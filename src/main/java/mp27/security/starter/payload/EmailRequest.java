package mp27.security.starter.payload;

import lombok.Getter;
import lombok.Setter;
import mp27.security.starter.validation.ValidEmail;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class EmailRequest {
    @NotBlank
    @ValidEmail
    private String email;
}
