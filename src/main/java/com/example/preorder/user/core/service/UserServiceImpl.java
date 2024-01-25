package com.example.preorder.user.core.service;

import com.example.preorder.common.exception.BadRequestException;
import com.example.preorder.common.utils.HttpServletUtils;
import com.example.preorder.common.utils.RandomGenerator;
import com.example.preorder.global.jwt.TokenProvider;
import com.example.preorder.global.mail.EmailService;
import com.example.preorder.global.redis.RedisManager;
import com.example.preorder.global.storage.service.StorageService;
import com.example.preorder.user.core.entity.UserEntity;
import com.example.preorder.user.core.repository.UserRepository;
import com.example.preorder.user.core.resources.request.UserChangePasswordDTO;
import com.example.preorder.user.core.resources.request.UserInfoEditDTO;
import com.example.preorder.user.core.resources.request.UserSignUpDTO;
import com.example.preorder.user.core.resources.response.TokenDTO;
import com.example.preorder.user.core.resources.response.UserInfoDTO;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final RedisManager redisManager;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final StorageService storageService;
    private final TokenProvider tokenProvider;
    private final HttpServletUtils servletUtils;

    @Override
    @Transactional
    public TokenDTO signUp(UserSignUpDTO signUpDTO) {
        checkEmailDuplication(signUpDTO.getEmail());
        checkUsernameDuplication(signUpDTO.getUsername());
        checkAuthCode(signUpDTO.getEmail(), signUpDTO.getAuthCode());

        String profileImageUrl = uploadFile(signUpDTO.getFile());

        // TODO : need to password encryption
        UserEntity userEntity = createUser(
                signUpDTO.getEmail(),
                signUpDTO.getUsername(),
                signUpDTO.getPassword(),
                profileImageUrl
        );

        userRepository.save(userEntity);

        return generateTokenDTOByEmail(userEntity.getEmail());
    }

    @Override
    public TokenDTO reissueToken(String refreshToken) {
        String email = tokenProvider.getSubject(refreshToken);
        Optional<String> value = redisManager.getValue(REFRESH_TOKEN, email);

        if (value.isEmpty() || !value.get().equals(refreshToken)) {
            throw new BadRequestException("로그인 후에 진행해주세요.");
        }

        redisManager.deleteKey(REFRESH_TOKEN, email);

        return generateTokenDTOByEmail(email);
    }

    @Override
    public UserInfoDTO getUserInfo(Long userId) {
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
    public void editPassword(UserChangePasswordDTO changePasswordDTO) {
        String accessToken = getAccessToken();
        String email = tokenProvider.getSubject(accessToken);

        userRepository.findByEmail(email).ifPresent((user) -> {
            user.changePassword(changePasswordDTO.getCurrentPassword(), changePasswordDTO.getNewPassword());
        });
    }

    @Override
    @Transactional
    public void editInfo(UserInfoEditDTO infoEditDTO) {
        String accessToken = getAccessToken();
        String email = tokenProvider.getSubject(accessToken);
        String uploadUrl = uploadFile(infoEditDTO.getFile());

        userRepository.findByEmail(email).ifPresent((user) -> {
            user.changeInfo(infoEditDTO.getUsername(), uploadUrl, infoEditDTO.getDescription());
        });
    }

    private String getAccessToken() {
        String accessToken = servletUtils.getHeader(HeaderType.ACCESS_TOKEN)
                .orElseThrow(() -> new BadRequestException("Please do login."));

        return accessToken;
    }

    private UserInfoDTO entityToInfoDTO(UserEntity userEntity) {
        UserInfoDTO userInfoDTO = new UserInfoDTO(userEntity.getUsername(), userEntity.getProfileImage(), userEntity.getDescription());
        return userInfoDTO;
    }

    private TokenDTO generateTokenDTOByEmail(String email) {
        String accessToken = tokenProvider.createAccessToken(email);
        String refreshToken = tokenProvider.createRefreshToken(email);

        redisManager.putValue(REFRESH_TOKEN, email, refreshToken);

        return new TokenDTO(accessToken, refreshToken);
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
