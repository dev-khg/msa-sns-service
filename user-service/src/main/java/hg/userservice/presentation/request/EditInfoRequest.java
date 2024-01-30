package hg.userservice.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EditInfoRequest {
    @NotBlank(message = "username must be not blank")
    @Size(min = 2, max = 50, message = "username length must bigger than 2 and must smaller than 50.")
    private String username;

    private String description;
}
