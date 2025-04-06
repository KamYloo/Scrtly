package com.kamylo.Scrtly_backend.service.impl;

import com.kamylo.Scrtly_backend.dto.UserDto;
import com.kamylo.Scrtly_backend.dto.request.LoginRequestDto;
import com.kamylo.Scrtly_backend.dto.request.RegisterRequestDto;
import com.kamylo.Scrtly_backend.email.EmailTemplateName;
import com.kamylo.Scrtly_backend.entity.ActivationToken;
import com.kamylo.Scrtly_backend.entity.ArtistEntity;
import com.kamylo.Scrtly_backend.entity.UserEntity;
import com.kamylo.Scrtly_backend.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.handler.CustomException;
import com.kamylo.Scrtly_backend.mappers.Mapper;
import com.kamylo.Scrtly_backend.repository.UserRepository;
import com.kamylo.Scrtly_backend.service.*;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final ActivationTokenService activationTokenService;
    private final Mapper<UserEntity, UserDto> mapper;

    @Value("${mailing.backend.activation-url}")
    private String activationUrl;

    @Override
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

        if ("Artist".equalsIgnoreCase(registerRequest.getRole())) {
            user = ArtistEntity.builder()
                    .fullName(registerRequest.getFullName())
                    .nickName(registerRequest.getNickName())
                    .email(registerRequest.getEmail())
                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .role(registerRequest.getRole())
                    .artistName(registerRequest.getArtistName())
                    .accountLocked(false)
                    .enable(false)
                    .build();
        } else {
            user = UserEntity.builder()
                    .fullName(registerRequest.getFullName())
                    .nickName(registerRequest.getNickName())
                    .email(registerRequest.getEmail())
                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .role(registerRequest.getRole())
                    .accountLocked(false)
                    .enable(false)
                    .build();
        }
        UserEntity savedUser = userRepository.save(user);
        activationTokenService.createActivationToken(savedUser);
        sendValidationEmail(savedUser);
        return mapper.mapTo(savedUser);
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
            objects.put("jwt", jwtService.generateToken(email));
            objects.put("refresh", refreshTokenService.createRefreshToken(email).getRefreshToken());
            return objects;
        }
        throw new CustomException(BusinessErrorCodes.BAD_CREDENTIALS);
    }

    @Override
    public void activateUser(Long id, String token) throws MessagingException {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if(userEntity.isEnabled())
            throw new CustomException(BusinessErrorCodes.USER_IS_ENABLE);

        ActivationToken activationToken = activationTokenService.getActivationTokenByUser(userEntity);
        if(!token.equals(activationToken.getToken())) {
            throw new MessagingException("Invalid activation token");
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

    private void sendValidationEmail(UserEntity user) throws MessagingException {
        ActivationToken activationToken = activationTokenService.getActivationTokenByUser(user);
        emailService.sendEmail(
                user.getEmail(),
                user.getFullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                generateURL(activationUrl, user.getId(), activationToken.getToken()),
                "Account activation"
        );
    }

    private String generateURL(String baseUrl, Long user_id, String token){
        return String.format("%s/%s/%s", baseUrl ,user_id, token);
    }
}
