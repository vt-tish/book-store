package com.vttish.bookstore.auth.service.impl;

import com.vttish.bookstore.auth.config.FrontendProperties;
import com.vttish.bookstore.auth.exception.EmailDeliveryException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private FrontendProperties frontendProps;

    @InjectMocks
    private EmailServiceImpl emailService;

    @Test
    void sendVerificationEmail_ShouldProcessTemplateAndSend() {
        when(frontendProps.getVerifyUrl()).thenReturn("http://localhost:3000/verify");
        when(templateEngine.process(eq("verify-user-email"), any(Context.class)))
                .thenReturn("<html>Test</html>");
        
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendVerificationEmail("test@example.com", "my-token");

        verify(templateEngine).process(eq("verify-user-email"), any(Context.class));
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendEmployeeVerificationEmail_ShouldProcessTemplateAndSend() {
        when(frontendProps.getEmployeeVerifyUrl()).thenReturn("http://localhost:3000/emp-verify");
        when(templateEngine.process(eq("verify-user-email"), any(Context.class)))
                .thenReturn("<html>Test</html>");
        
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendEmployeeVerificationEmail("emp@example.com", "emp-token");

        verify(templateEngine).process(eq("verify-user-email"), any(Context.class));
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendResetPasswordEmail_ShouldProcessTemplateAndSend() {
        when(frontendProps.getResetPasswordUrl()).thenReturn("http://localhost:3000/reset");
        when(templateEngine.process(eq("password-reset-email"), any(Context.class)))
                .thenReturn("<html>Test</html>");
        
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendResetPasswordEmail("test@example.com", "reset-token");

        verify(templateEngine).process(eq("password-reset-email"), any(Context.class));
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendSecurityAlert_ShouldProcessTemplateAndSend() {
        when(templateEngine.process(eq("security-alert-email"), any(Context.class)))
                .thenReturn("<html>Test</html>");
        
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendSecurityAlert("test@example.com");

        verify(templateEngine).process(eq("security-alert-email"), any(Context.class));
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendEmail_WhenMailExceptionOccurs_ShouldThrowEmailDeliveryException() {
        when(templateEngine.process(eq("security-alert-email"), any(Context.class)))
                .thenReturn("<html>Test</html>");
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        doThrow(new MailSendException("Fail")).when(mailSender).send(any(MimeMessage.class));

        assertThrows(EmailDeliveryException.class, () -> emailService.sendSecurityAlert("test@example.com"));
    }
}
