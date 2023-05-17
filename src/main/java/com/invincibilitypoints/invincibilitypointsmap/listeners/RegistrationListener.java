package com.invincibilitypoints.invincibilitypointsmap.listeners;

import com.invincibilitypoints.invincibilitypointsmap.events.OnRegistrationCompleteEvent;
import com.invincibilitypoints.invincibilitypointsmap.security.models.User;
import com.invincibilitypoints.invincibilitypointsmap.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class RegistrationListener implements
        ApplicationListener<OnRegistrationCompleteEvent> {

    private final UserService userService;

    private final JavaMailSender mailSender;

    @Autowired
    public RegistrationListener(UserService userService, JavaMailSender mailSender) {
        this.userService = userService;
        this.mailSender = mailSender;
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
        userService.createVerificationToken(user, token);

        String recipientAddress = user.getEmail();
        String subject = "Мапа пунктів незламності";


        String confirmationUrl = event.getAppUrl() + "public/registrationConfirm?token=" + token;

        String message = "<div style=\"background-color: #f0f0f0; padding: 20px; border-radius: 5px;\">"
                + "<h1>Підтвердіть свою електронну адресу</h1>"
                + "<p>Для підтвердження своєї електронної адреси пошти перейдіть за посиланням. Посилання буде дійсне протягом 1 години</p>"
                + "<a href=\"http://localhost:8080/" + confirmationUrl + "\" style=\"background-color: #008CBA; color: white; padding: 10px 20px; border-radius: 5px; text-decoration: none;\">Підтвердити</a>"
                + "</div>";

        MimeMessage email = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(email, true, "UTF-8");
        InputStreamSource logoInputStreamSource = new ByteArrayResource(Files
                .readAllBytes(Paths.get("D:\\Idea Projects\\invincibility-points-map\\src\\main\\java\\com\\invincibilitypoints\\invincibilitypointsmap\\listeners\\img\\logo.png")));
        helper.addAttachment("logo.png", logoInputStreamSource, "image/svg+xml");
        try {
            helper.setTo(recipientAddress);
            helper.setSubject(subject);
            helper.setText(message, true);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        mailSender.send(email);
    }

}