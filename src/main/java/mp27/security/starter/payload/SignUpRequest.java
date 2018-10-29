package mp27.security.starter.payload;

import lombok.Getter;
import lombok.Setter;
import mp27.security.starter.validation.PasswordMatches;
import mp27.security.starter.validation.ValidEmail;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@PasswordMatches
public class SignUpRequest {
    @NotBlank
    @Size(min=4, max=40)
    private String name;

    @NotBlank
    @Size(min = 3, max = 15)
    private String username;

    @NotBlank
    @Size(max = 40)
    @ValidEmail
    private String email;

    @NotBlank
    @Size(min = 6, max = 20)
    private String password;

    @NotBlank
    @Size(min = 6, max = 20)
    private String confirmPassword;

}
