package com.invincibilitypoints.invincibilitypointsmap.listeners;

import com.invincibilitypoints.invincibilitypointsmap.events.OnRegistrationCompleteEvent;
import com.invincibilitypoints.invincibilitypointsmap.security.model.User;
import com.invincibilitypoints.invincibilitypointsmap.service.AuthService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.UUID;

@Component
public class RegistrationListener implements
        ApplicationListener<OnRegistrationCompleteEvent> {

    private final AuthService authService;

    private final JavaMailSender mailSender;
    ResourceBundle messages = ResourceBundle.getBundle("messages", new Locale("ua"));

    private final ResourceLoader resourceLoader;

    @Autowired
    public RegistrationListener(AuthService authService, JavaMailSender mailSender, ResourceLoader resourceLoader) {
        this.authService = authService;
        this.mailSender = mailSender;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        try {
            this.confirmRegistration(event);
        } catch (MessagingException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) throws MessagingException, IOException {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        authService.createVerificationToken(user, token);

        String recipientAddress = user.getEmail();
        String subject = "Мапа пунктів незламності";

        String confirmationHeader = messages.getString("email.confirmation.header");
        String confirmationDesc = messages.getString("email.confirmation.desc");

        String confirmationUrl = event.getAppUrl() + "/public/registrationConfirm?token=" + token;
        String message =
                "<div style=\"background-color: #f0f0f0; padding: 20px; border-radius: 5px;\">"
                + "<h1>"+confirmationHeader+"</h1>"
                + "<p>"+confirmationDesc+"</p>"
                + "<a href=\"http://" + confirmationUrl + "\" style=\"background-color: #008CBA; color: white; padding: 10px 20px; border-radius: 5px; text-decoration: none;\">Підтвердити</a>"
                + "</div>";

        MimeMessage email = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(email, true, "UTF-8");

        try (InputStream inputStream = resourceLoader.getResource("classpath:logo.png").getInputStream()) {
            InputStreamSource logoInputStreamSource = new ByteArrayResource(inputStream.readAllBytes());
            helper.addAttachment("logo.png", logoInputStreamSource, "image/svg+xml");
            helper.setTo(recipientAddress);
            helper.setSubject(subject);
            helper.setText(message, true);
        } catch (IOException | MessagingException  e) {
            e.printStackTrace();
        }

        mailSender.send(email);
    }

}