package mp27.security.starter.event;

import lombok.Getter;
import lombok.Setter;
import mp27.security.starter.model.User;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

@Getter
@Setter
public class OnResendConfirmationEvent extends ApplicationEvent {
    private String appUrl;
    private Locale locale;
    private User user;

    public OnResendConfirmationEvent(User user, Locale locale, String appUrl) {
        super(user);
        this.appUrl = appUrl;
        this.locale = locale;
        this.user = user;
    }
}
