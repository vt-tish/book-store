package com.vttish.bookstore.auth.service.impl;

import com.vttish.bookstore.auth.service.EmailService;
import com.vttish.bookstore.auth.config.FrontendProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final FrontendProperties frontendProps;

    @Override
    @Async
    public void sendVerificationEmail(String email, String token) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("verifyLink", frontendProps.getVerifyUrl() + "?token=" + token);

        sendEmail(email, "Verification", "verify-user-email", variables);
    }

    @Override
    @Async
    public void sendEmployeeVerificationEmail(String email, String token) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("verifyLink", frontendProps.getEmployeeVerifyUrl() + "?token=" + token);

        sendEmail(email, "Verification", "verify-user-email", variables);
    }

    @Override
    @Async
    public void sendPasswordResetEmail(String email, String token) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("resetLink", frontendProps.getResetPasswordUrl() + "?token=" + token);

        sendEmail(email, "Password Reset", "password-reset-email", variables);
    }

    @Override
    @Async
    public void sendSecurityAlert(String email) {
        sendEmail(email, "Security Alert", "security-alert-email", new HashMap<>());
    }

    private void sendEmail(String email, String subject, String templateName, Map<String, Object> variables) {
        Context thymleafContext = new Context();
        thymleafContext.setVariables(variables);

        String htmlBody = templateEngine.process(templateName, thymleafContext);

        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    true,
                    "UTF-8"
            );

            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
        } catch (MessagingException ex) {
            throw new RuntimeException(ex);
        }
    }
}
