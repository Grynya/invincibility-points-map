package com.invincibilitypoints.invincibilitypointsmap.events;

import com.invincibilitypoints.invincibilitypointsmap.security.models.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

@Getter
@Setter
public class OnPasswordRecoveryEvent extends ApplicationEvent {
    private String appUrl;
    private Locale locale;
    private User user;

    public OnPasswordRecoveryEvent(
            User user, Locale locale, String appUrl) {
        super(user);

        this.user = user;
        this.locale = locale;
        this.appUrl = appUrl;
    }
}