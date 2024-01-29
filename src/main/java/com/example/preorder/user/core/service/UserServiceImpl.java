package com.example.preorder.user.core.service;

import com.example.preorder.common.event.EventPublisher;
import com.example.preorder.common.exception.BadRequestException;
import com.example.preorder.common.exception.InternalErrorException;
import com.example.preorder.common.utils.HttpServletUtils;
import com.example.preorder.common.utils.RandomGenerator;
import com.example.preorder.global.jwt.TokenProvider;
import com.example.preorder.global.mail.EmailService;
import com.example.preorder.global.redis.RedisManager;
import com.example.preorder.global.storage.service.StorageService;
import com.example.preorder.user.core.entity.UserEntity;
import com.example.preorder.user.core.repository.UserRepository;
import com.example.preorder.user.presentation.resources.request.UserChangePasswordRequest;
import com.example.preorder.user.presentation.resources.request.UserInfoEditRequest;
import com.example.preorder.user.presentation.resources.request.UserSignUpRequest;
import com.example.preorder.user.presentation.resources.response.TokenResponse;
import com.example.preorder.user.presentation.resources.response.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static com.example.preorder.common.utils.HttpServletUtils.*;
import static com.example.preorder.common.utils.RedisKeyGenerator.RedisKeyType.*;
import static com.example.preorder.common.utils.RedisKeyGenerator.RedisKeyType.REFRESH_TOKEN;
import static com.example.preorder.global.storage.service.StorageService.Bucket.*;
import static com.example.preorder.user.core.entity.UserEntity.*;

@Service
public class UserServiceImpl extends EventPublisher implements UserService {
    private final RedisManager redisManager;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final StorageService storageService;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(ApplicationEventPublisher publisher,
                           RedisManager redisManager,
                           UserRepository userRepository,
                           EmailService emailService,
                           StorageService storageService,
                           TokenProvider tokenProvider,
                           HttpServletUtils servletUtils,
                           PasswordEncoder passwordEncoder) {
        super(publisher);
        this.redisManager = redisManager;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.storageService = storageService;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public TokenResponse signUp(UserSignUpRequest signUpDTO, MultipartFile file) {
        checkEmailDuplication(signUpDTO.getEmail());
        checkUsernameDuplication(signUpDTO.getUsername());
        checkAuthCode(signUpDTO.getEmail(), signUpDTO.getAuthCode());

        String profileImageUrl = uploadFile(file);

        UserEntity userEntity = createUser(
                signUpDTO.getEmail(),
                signUpDTO.getUsername(),
                passwordEncoder.encode(signUpDTO.getPassword()),
                profileImageUrl
        );

        userRepository.save(userEntity);

        return generateTokenDTOByEmail(userEntity.getEmail());
    }

    @Override
    public TokenResponse reissueToken(String refreshToken) {
        String email = tokenProvider.getSubject(refreshToken);
        Optional<String> value = redisManager.getValue(REFRESH_TOKEN, email);

        if (value.isEmpty() || !value.get().equals(refreshToken)) {
            throw new BadRequestException("로그인 후에 진행해주세요.");
        }

        redisManager.deleteKey(REFRESH_TOKEN, email);

        return generateTokenDTOByEmail(email);
    }

    @Override
    public UserInfoResponse getUserInfo(Long userId) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException("Not found User.")
        );

        return entityToInfoDTO(userEntity);
    }

    @Override
    public void sendAuthCode(String email) {
        String randomCode = String.valueOf(RandomGenerator.generateSixDigitsRandomCode());
        redisManager.putValue(EMAIL_CERT, email, randomCode);
        emailService.sendEmail(email, "[PRE-ORDER] 회원가입 인증 코드 입니다.", randomCode);
    }

    @Override
    public void logout(String accessToken, String refreshToken) {
        if (accessToken != null)
            accessToken = accessToken.substring(7);
        String email = tokenProvider.getSubject(accessToken);

        if (tokenProvider.isValidateToken(accessToken)) {
            redisManager.putValue(BLACKLIST_TOKEN, accessToken, "");
        }

        if (tokenProvider.isValidateToken(refreshToken)) {
            redisManager.deleteKey(REFRESH_TOKEN, email);
        }
    }

    @Override
    @Transactional
    public void editPassword(UserEntity userEntity, UserChangePasswordRequest changePasswordDTO) {
        if (userEntity == null) {
            throw new BadRequestException("please do login.");
        }

        Optional<UserEntity> userOptional = userRepository.findById(userEntity.getId());

        if (userOptional.isEmpty()) {
            throw new InternalErrorException("not found user");
        }

        UserEntity foundUser = userOptional.get();
        foundUser.changePassword(
                passwordEncoder,
                changePasswordDTO.getCurrentPassword(),
                changePasswordDTO.getNewPassword()
        );
    }

    @Override
    @Transactional
    public void editInfo(UserEntity userEntity, UserInfoEditRequest infoEditDTO, MultipartFile file) {
        String uploadUrl = uploadFile(file);

        Optional<UserEntity> optionalUser = userRepository.findById(userEntity.getId());

        if (optionalUser.isEmpty()) {
            throw new InternalErrorException("Sorry. Server has problem");
        }

        UserEntity foundUser = optionalUser.get();
        foundUser.changeInfo(infoEditDTO.getUsername(), uploadUrl, infoEditDTO.getDescription());


        userRepository.save(userEntity);
    }

    private UserInfoResponse entityToInfoDTO(UserEntity userEntity) {
        UserInfoResponse userInfoResponse = new UserInfoResponse(userEntity.getUsername(), userEntity.getProfileImage(), userEntity.getDescription());
        return userInfoResponse;
    }

    private TokenResponse generateTokenDTOByEmail(String email) {
        String accessToken = tokenProvider.createAccessToken(email);
        String refreshToken = tokenProvider.createRefreshToken(email);

        redisManager.putValue(REFRESH_TOKEN, email, refreshToken);

        return new TokenResponse(accessToken, refreshToken);
    }

    private String uploadFile(MultipartFile file) {
        String profileImageUrl = null;

        if (file != null && !file.isEmpty()) {
            profileImageUrl = storageService.uploadFile(file, IMAGE);
        }

        return profileImageUrl;
    }

    private void checkAuthCode(String email, String code) {
        Optional<String> optionalCode = redisManager.getValue(EMAIL_CERT, email);

        if (optionalCode.isEmpty()) {
            throw new BadRequestException("Code is not matched.");
        } else if (!optionalCode.get().equals(code)) {
            throw new BadRequestException("Email is not certificated.");
        }

        redisManager.deleteKey(EMAIL_CERT, email);
    }

    private void checkEmailDuplication(String email) {
        if (userRepository.existsEmail(email)) {
            throw new BadRequestException("Email is already used.");
        }
    }

    private void checkUsernameDuplication(String username) {
        if (userRepository.existsUsername(username)) {
            throw new BadRequestException("Username is already used.");
        }
    }
}
