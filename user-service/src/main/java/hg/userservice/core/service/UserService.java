package hg.userservice.core.service;

import com.example.commonproject.exception.BadRequestException;
import com.example.commonproject.exception.InternalServerException;
import hg.userservice.core.entity.UserEntity;
import hg.userservice.core.repository.KeyValueStorage;
import hg.userservice.core.repository.UserRepository;
import hg.userservice.core.repository.dto.UserNameInfoDTO;
import hg.userservice.infrastructure.storage.UploadService;
import hg.userservice.presentation.request.EditInfoRequest;
import hg.userservice.presentation.request.EditPasswordRequest;
import hg.userservice.presentation.request.SignUpRequest;
import hg.userservice.presentation.response.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static hg.userservice.core.entity.UserEntity.*;
import static hg.userservice.core.vo.KeyType.EMAIL_CERT;

@Service
@RequiredArgsConstructor
public class UserService {
    private final KeyValueStorage keyValueStorage;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final UploadService uploadService;

    @Transactional
    public void signUp(SignUpRequest signUpRequest) {
        checkEmailDuplication(signUpRequest.getEmail());
        checkUsernameDuplication(signUpRequest.getUsername());
        checkAuthCode(signUpRequest.getEmail(), signUpRequest.getAuthCode());

        String encodedPassword = passwordEncoder.encode(signUpRequest.getPassword());
        UserEntity userEntity = create(signUpRequest.getEmail(), signUpRequest.getUsername(), encodedPassword);
        userEntity = userRepository.save(userRepository.save(userEntity));

        authService.issueToken(userEntity.getId());
    }

    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfo(Long userId) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException("Not exists user.")
        );
        return new UserInfoResponse(userEntity);
    }

    @Transactional
    public void editUserInfo(Long userId, EditInfoRequest request) {
        checkUsernameDuplication(request.getUsername());

        UserEntity userEntity = getUserEntity(userId);
        userEntity.editInfo(request.getUsername(), request.getDescription());
    }

    @Transactional
    public void changePassword(Long userId, EditPasswordRequest request) {
        UserEntity userEntity = getUserEntity(userId);
        userEntity.changePassword(passwordEncoder, request.getCurrentPassword(), request.getNewPassword());
    }

    @Transactional(readOnly = true)
    public List<UserNameInfoDTO> getActivities(List<Long> userIdList) {
        return userRepository.findUsername(userIdList);
    }

    @Transactional
    public void changeProfile(Long userId, MultipartFile file) {
        UserEntity userEntity = getUserEntity(userId);
        String fileName = null;

        if(userEntity.getProfileImage() != null) {
            uploadService.deleteAsync(userEntity.getProfileImage());
        }

        if (!file.isEmpty()) {
            fileName = uploadService.uploadAsync(file);
        }

        userEntity.changeProfileImage(fileName);
    }

    private UserEntity getUserEntity(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new InternalServerException("Sorry! Server has problem.")
        );
    }

    private void checkAuthCode(String email, String code) {
        Optional<String> optionalCode = keyValueStorage.getValue(EMAIL_CERT, email);

        if (optionalCode.isEmpty()) {
            throw new BadRequestException("Code is not matched.");
        } else if (!optionalCode.get().equals(code)) {
            throw new BadRequestException("Email is not certificated.");
        }

        keyValueStorage.deleteKey(EMAIL_CERT, email);
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
