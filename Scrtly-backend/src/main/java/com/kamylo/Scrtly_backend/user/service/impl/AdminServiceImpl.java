package com.kamylo.Scrtly_backend.user.service.impl;

import com.kamylo.Scrtly_backend.artist.domain.ArtistEntity;
import com.kamylo.Scrtly_backend.artist.domain.ArtistVerificationToken;
import com.kamylo.Scrtly_backend.common.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.user.repository.RolesRepository;
import com.kamylo.Scrtly_backend.user.repository.UserRepository;
import com.kamylo.Scrtly_backend.artist.service.ArtistVerificationTokenService;
import com.kamylo.Scrtly_backend.user.domain.RoleEntity;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.user.service.AdminService;
import com.kamylo.Scrtly_backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final ArtistVerificationTokenService artistVerificationTokenService;
    private final RolesRepository rolesRepository;

    @Override
    public void verifyUserAsArtist(Long userId, String token) {

        UserEntity user = userService.findUserById(userId);

        if (user.getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase("ARTIST"))) {
            throw new CustomException(BusinessErrorCodes.USER_ALREADY_ARTIST);
        }

        ArtistVerificationToken artistVerificationToken = artistVerificationTokenService.getTokenByUser(user);
        if(!token.equals(artistVerificationToken.getToken())) {
            throw new CustomException(BusinessErrorCodes.INVALID_TOKEN);
        }
        else if(artistVerificationTokenService.tokenExpired(artistVerificationToken)){
            artistVerificationTokenService.deleteToken(artistVerificationToken);
            throw new CustomException(BusinessErrorCodes.TOKEN_EXPIRED);
        }
        else{
            RoleEntity role = rolesRepository.findByName("ARTIST").orElseThrow(
                    () -> new RuntimeException("Role not found"));

            user.getRoles().add(role);
            ArtistEntity artistEntity = ArtistEntity.builder()
                    .user(user)
                    .pseudonym(artistVerificationToken.getRequestedArtistName())
                    .artistBio(null)
                    .bannerImg(null)
                    .build();

            user.setArtistEntity(artistEntity);
            userRepository.save(user);
            artistVerificationTokenService.deleteToken(artistVerificationToken);
        }
    }
}
