package com.kamylo.Scrtly_backend.auth.service.impl;

import com.kamylo.Scrtly_backend.auth.domain.ActivationToken;
import com.kamylo.Scrtly_backend.auth.domain.PasswordResetToken;
import com.kamylo.Scrtly_backend.auth.service.*;
import com.kamylo.Scrtly_backend.user.mapper.UserMapper;
import com.kamylo.Scrtly_backend.user.web.dto.UserDto;
import com.kamylo.Scrtly_backend.auth.web.dto.request.LoginRequestDto;
import com.kamylo.Scrtly_backend.auth.web.dto.request.RegisterRequestDto;
import com.kamylo.Scrtly_backend.auth.web.dto.request.RestPasswordRequest;
import com.kamylo.Scrtly_backend.email.EmailTemplateName;
import com.kamylo.Scrtly_backend.email.service.EmailService;
import com.kamylo.Scrtly_backend.common.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.user.repository.RolesRepository;
import com.kamylo.Scrtly_backend.user.repository.UserRepository;
import com.kamylo.Scrtly_backend.user.domain.RoleEntity;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final ActivationTokenService activationTokenService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final UserMapper userMapper;

    @Value("${mailing.backend.activation-url}")
    private String activationUrl;

    @Value("${mailing.backend.reset-password-url}")
    private String resetPasswordUrl;

    @Override
    @Transactional
    public UserDto createUser(RegisterRequestDto registerRequest) throws MessagingException{
        userRepository.findByEmail(registerRequest.getEmail())
                .ifPresent(email -> {
                    throw new CustomException(BusinessErrorCodes.EMAIL_IS_USED);
                });

        userRepository.findByNickName(registerRequest.getNickName())
                .ifPresent(email -> {
                    throw new CustomException(BusinessErrorCodes.NICKNAME_IS_USED);
                });
        UserEntity user;

        RoleEntity role = rolesRepository.findByName("USER").orElseThrow(
                () -> new RuntimeException("Role not found"));

        user = UserEntity.builder()
                .fullName(registerRequest.getFullName())
                .nickName(registerRequest.getNickName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .roles(Set.of(role))
                .accountLocked(false)
                .enable(false)
                .build();

        UserEntity savedUser = userRepository.save(user);
        activationTokenService.createActivationToken(savedUser);
        sendValidationEmail(savedUser);
        return userMapper.toDto(savedUser);
    }

    @Override
    public Map<String, String> verify(LoginRequestDto loginRequest) throws MessagingException {
        UserEntity user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if(!user.isEnabled())
            throw new CustomException(BusinessErrorCodes.ACCOUNT_DISABLED);

        if(user.isAccountLocked())
            throw new CustomException(BusinessErrorCodes.ACCOUNT_LOCKED);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        if (authentication.isAuthenticated()) {
            String email = loginRequest.getEmail();
            Map<String, String> objects = new HashMap<>();
            objects.put("jwt_zuvoria_v1", jwtService.generateToken(email));
            objects.put("refresh_zuvoria_v1", refreshTokenService.createRefreshToken(email).getRefreshToken());
            return objects;
        }
        throw new CustomException(BusinessErrorCodes.BAD_CREDENTIALS);
    }

    @Override
    @Transactional
    public void activateUser(Long id, String token) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if(userEntity.isEnabled())
            throw new CustomException(BusinessErrorCodes.USER_IS_ENABLE);

        ActivationToken activationToken = activationTokenService.getActivationTokenByUser(userEntity);
        if(!token.equals(activationToken.getToken())) {
            throw new CustomException(BusinessErrorCodes.INVALID_TOKEN);
        }
        else if(activationTokenService.verifyExpiration(activationToken)){
            activationTokenService.deleteActivationToken(activationToken);
            throw new CustomException(BusinessErrorCodes.TOKEN_EXPIRED);
        }
        else{
            userEntity.setEnable(true);
            userRepository.save(userEntity);
            activationTokenService.deleteActivationToken(activationToken);
        }
    }

    @Override
    public void forgotPassword(String email) throws MessagingException {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User not found"));
        passwordResetTokenService.createToken(user);
        sendPasswordRestEmail(user);
    }

    @Override
    @Transactional
    public void restPassword(Long userId, String token, RestPasswordRequest restPasswordRequest) {
        UserEntity user = userRepository.findById(userId).orElseThrow(
                () -> new UsernameNotFoundException("User not found"));

        if(!restPasswordRequest.getPassword().equals(restPasswordRequest.getPasswordConfirmation())){
            throw new CustomException(BusinessErrorCodes.NEW_PASSWORD_DOES_NOT_MATCH);
        }

        PasswordResetToken resetToken = passwordResetTokenService.getTokenByUser(user);

        if(!resetToken.getToken().equals(token)){
            throw new CustomException(BusinessErrorCodes.INVALID_TOKEN);
        }

        if(passwordResetTokenService.tokenExpired(resetToken)){
            passwordResetTokenService.deleteToken(resetToken);
            throw new CustomException(BusinessErrorCodes.TOKEN_EXPIRED);
        }

        user.setPassword(passwordEncoder.encode(restPasswordRequest.getPassword()));
        userRepository.save(user);
        passwordResetTokenService.deleteToken(resetToken);
    }

    private void sendValidationEmail(UserEntity user) throws MessagingException {
        ActivationToken activationToken = activationTokenService.getActivationTokenByUser(user);
        emailService.sendEmail(
                user.getEmail(),
                user.getFullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                generateURL(activationUrl, user.getId(), activationToken.getToken()),
                "Account activation",
                null,
                null
        );
    }

    private void sendPasswordRestEmail(UserEntity user) throws MessagingException {
        PasswordResetToken passwordResetToken = passwordResetTokenService.getTokenByUser(user);
        emailService.sendEmail(
                user.getEmail(),
                user.getFullName(),
                EmailTemplateName.RESET_PASSWORD,
                generateURL(resetPasswordUrl, user.getId(), passwordResetToken.getToken()),
                "Reset password",
                null,
                null
        );
    }

    private String generateURL(String baseUrl, Long user_id, String token){
        return String.format("%s/%s/%s", baseUrl ,user_id, token);
    }
}
