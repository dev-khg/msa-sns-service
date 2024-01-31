package hg.userservice.presentation.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @Email(message = "email field's is invalid form.")
    @NotEmpty(message = "email field must not empty.")
    @Size(min = 8, max = 50, message = "email length must bigger than 8 and must smaller than 50.")
    private String email;

    @NotBlank(message = "password must be not blank")
    @Size(min = 8, max = 20, message = "password length must bigger than 8 and must smaller than 20.")
    private String password;
}
