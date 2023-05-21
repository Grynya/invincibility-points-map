package com.invincibilitypoints.invincibilitypointsmap.listeners;

import com.invincibilitypoints.invincibilitypointsmap.events.OnPasswordRecoveryEvent;
import com.invincibilitypoints.invincibilitypointsmap.security.model.User;
import com.invincibilitypoints.invincibilitypointsmap.security.repository.UserRepository;
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
import java.util.Random;

@Component
public class PasswordRecoveryListener implements
        ApplicationListener<OnPasswordRecoveryEvent> {

    private final JavaMailSender mailSender;

    private final UserRepository userRepository;

    @Autowired
    public PasswordRecoveryListener(JavaMailSender mailSender, UserRepository userRepository) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
    }

    @Override
    public void onApplicationEvent(OnPasswordRecoveryEvent event) {
        try {
            this.confirmPasswordRecovery(event);
        } catch (MessagingException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void confirmPasswordRecovery(OnPasswordRecoveryEvent event) throws MessagingException, IOException {
        User user = event.getUser();
        String recipientAddress = user.getEmail();
        String subject = "Мапа пунктів незламності";
        int code = generateCode();

        String message =
                "<div style=\"background-color: #f0f0f0; padding: 20px; border-radius: 5px;\">"
                + "<h1>Підтвердіть свою електронну адресу</h1>"
                + "<p>Код для підтвердження зміни паролю:</p>"
                + "<h1>"+code+"</h1>"
                + "</div>";

        MimeMessage email = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(email, true, "UTF-8");
        InputStreamSource logoInputStreamSource = new ByteArrayResource(Files
                .readAllBytes(Paths.get("D:\\Idea Projects\\invincibility-points-map\\src\\main\\java\\com\\invincibilitypoints\\invincibilitypointsmap\\listeners\\img\\logo.png")));
        helper.addAttachment("logo.png", logoInputStreamSource, "image/svg+xml");

        user.setCode(code);
        userRepository.save(user);
        try {
            helper.setTo(recipientAddress);
            helper.setSubject(subject);
            helper.setText(message, true);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        mailSender.send(email);
    }

    private int generateCode() {
        int min = 100000;
        int max = 999999;
        return new Random().nextInt(max - min + 1) + min;
    }
}