package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.email.EmailTemplateName;
import com.kamylo.Scrtly_backend.service.impl.EmailServiceImpl;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private SpringTemplateEngine templateEngine;

    @InjectMocks
    private EmailServiceImpl emailService;

    @Mock
    private MimeMessage mimeMessage;

    @Test
    void testSendEmail_withDefaultTemplate_whenEmailTemplateIsNull() throws MessagingException {
        String to = "test@example.com";
        String username = "testUser";
        EmailTemplateName emailTemplate = null;
        String confirmationURL = "http://example.com/confirm";
        String subject = "Potwierdzenie rejestracji";
        String expectedContent = "<html><body>Potwierdzenie</body></html>";

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("confirm-email"), any(Context.class))).thenReturn(expectedContent);

        emailService.sendEmail(to, username, emailTemplate, confirmationURL, subject);

        ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine).process(eq("confirm-email"), contextCaptor.capture());
        Context capturedContext = contextCaptor.getValue();
        assertThat(capturedContext.getVariable("username")).isEqualTo(username);
        assertThat(capturedContext.getVariable("confirmationUrl")).isEqualTo(confirmationURL);

        verify(mailSender).send(mimeMessage);
    }

    @Test
    void testSendEmail_withSpecificTemplate() throws MessagingException {
        String to = "test2@example.com";
        String username = "user2";
        EmailTemplateName emailTemplate = EmailTemplateName.RESET_PASSWORD;
        String confirmationURL = "http://example.com/reset";
        String subject = "Reset hasła";
        String expectedContent = "<html><body>Reset</body></html>";

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq(emailTemplate.name()), any(Context.class))).thenReturn(expectedContent);

        emailService.sendEmail(to, username, emailTemplate, confirmationURL, subject);

        ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine).process(eq(emailTemplate.name()), contextCaptor.capture());
        Context capturedContext = contextCaptor.getValue();
        assertThat(capturedContext .getVariable("username")).isEqualTo(username);
        assertThat(capturedContext .getVariable("confirmationUrl")).isEqualTo(confirmationURL);

        verify(mailSender).send(mimeMessage);
    }
}