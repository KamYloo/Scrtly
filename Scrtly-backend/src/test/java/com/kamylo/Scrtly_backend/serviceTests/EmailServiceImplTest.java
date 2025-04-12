package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.email.EmailTemplateName;
import com.kamylo.Scrtly_backend.entity.UserEntity;
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
    void testSendEmail_withDefaultTemplate_whenOptionalParamsAreNull() throws MessagingException {
        String to = "test@example.com";
        String username = "testUser";
        EmailTemplateName emailTemplate = null;
        String confirmationURL = "http://example.com/confirm";
        String subject = "Potwierdzenie rejestracji";
        String expectedContent = "<html><body>Potwierdzenie</body></html>";

        String artistName = null;
        UserEntity userEntity = null;

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("confirm-email"), any(Context.class))).thenReturn(expectedContent);

        emailService.sendEmail(to, username, emailTemplate, confirmationURL, subject, artistName, userEntity);

        ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine).process(eq("confirm-email"), contextCaptor.capture());
        Context capturedContext = contextCaptor.getValue();
        assertThat(capturedContext.getVariable("username")).isEqualTo(username);
        assertThat(capturedContext.getVariable("confirmationUrl")).isEqualTo(confirmationURL);
        assertThat(capturedContext.getVariable("artistName")).isNull();
        assertThat(capturedContext.getVariable("fullNameUser")).isNull();
        assertThat(capturedContext.getVariable("userEmail")).isNull();

        verify(mailSender).send(mimeMessage);
    }

    @Test
    void testSendEmail_withArtistVerificationData() throws MessagingException {
        String to = "admin@example.com";
        String username = "Admin";
        EmailTemplateName emailTemplate = EmailTemplateName.ARTIST_VERIFICATION;
        String confirmationURL = "http://example.com/verify?token=123";
        String subject = "Weryfikacja artysty";
        String expectedContent = "<html><body>Weryfikacja artysty</body></html>";

        String artistName = "Desired Artist";
        UserEntity userEntity = UserEntity.builder()
                .fullName("Jan Kowalski")
                .email("artistuser@example.com")
                .build();

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq(emailTemplate.name()), any(Context.class))).thenReturn(expectedContent);

        emailService.sendEmail(to, username, emailTemplate, confirmationURL, subject, artistName, userEntity);

        ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine).process(eq(emailTemplate.name()), contextCaptor.capture());
        Context capturedContext = contextCaptor.getValue();
        assertThat(capturedContext.getVariable("username")).isEqualTo(username);
        assertThat(capturedContext.getVariable("confirmationUrl")).isEqualTo(confirmationURL);
        assertThat(capturedContext.getVariable("artistName")).isEqualTo(artistName);
        assertThat(capturedContext.getVariable("fullNameUser")).isEqualTo(userEntity.getFullName());
        assertThat(capturedContext.getVariable("userEmail")).isEqualTo(userEntity.getEmail());

        verify(mailSender).send(mimeMessage);
    }
}
