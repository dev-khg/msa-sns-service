package hg.userservice.presentation;

import hg.userservice.core.service.AuthService;
import hg.userservice.core.service.UserService;
import hg.userservice.infrastructure.email.EmailService;
import hg.userservice.infrastructure.security.annotation.AuthorizationRequired;
import hg.userservice.presentation.request.SignUpRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.*;

@RestController
@RequiredArgsConstructor
public class AccountController {
    private final AuthService authService;
    private final UserService userService;
    private final EmailService emailService;

    @DeleteMapping("/signout")
    public ResponseEntity<Void> logout() {
        authService.logout();

        return noContent().build();
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@Valid @RequestBody SignUpRequest request) {
        userService.signUp(request);

        return noContent().build();
    }

    @PostMapping("/signup/email")
    public ResponseEntity<Void> sendEmail(@RequestParam("email") String email) {
        emailService.sendCode(email);

        return noContent().build();
    }

    @PostMapping("/reissue")
    public ResponseEntity<Void> reissueToken() {
        authService.reissueToken();

        return noContent().build();
    }
}
