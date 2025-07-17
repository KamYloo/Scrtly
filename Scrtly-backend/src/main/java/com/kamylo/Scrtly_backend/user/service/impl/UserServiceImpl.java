package com.kamylo.Scrtly_backend.user.service.impl;
import com.kamylo.Scrtly_backend.payment.domain.enums.SubscriptionStatus;
import com.kamylo.Scrtly_backend.payment.repository.SubscriptionRepository;
import com.kamylo.Scrtly_backend.user.service.UserService;
import com.kamylo.Scrtly_backend.user.web.dto.UserDto;
import com.kamylo.Scrtly_backend.artist.web.dto.ArtistVerificationRequest;
import com.kamylo.Scrtly_backend.email.EmailTemplateName;
import com.kamylo.Scrtly_backend.artist.domain.ArtistVerificationToken;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.common.handler.BusinessErrorCodes;
import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.common.mapper.Mapper;
import com.kamylo.Scrtly_backend.user.repository.UserRepository;
import com.kamylo.Scrtly_backend.user.web.dto.request.UserRequestDto;
import com.kamylo.Scrtly_backend.artist.service.ArtistVerificationTokenService;
import com.kamylo.Scrtly_backend.email.service.EmailService;
import com.kamylo.Scrtly_backend.common.service.FileService;
import com.kamylo.Scrtly_backend.common.utils.UserLikeChecker;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FileService fileService;
    private final Mapper<UserEntity, UserDto> mapper;
    private final UserLikeChecker userLikeChecker;
    private final ArtistVerificationTokenService artistVerificationTokenService;
    private final EmailService emailService;
    private final SubscriptionRepository subscriptionRepo;

    @Value("${mailing.backend.artistVerification-url}")
    private String artistVerificationUrl;

    @Override
    public UserEntity findUserByEmail(String email) {
        return  userRepository.findByEmail(email).orElseThrow(
                ()->new UsernameNotFoundException("User not found"));
    }

    @Override
    public UserEntity findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                ()->new UsernameNotFoundException("User not found"));
    }

    @Override
    public UserDto findUserByNickname(String nickname) {
        return  mapper.mapTo(userRepository.findByNickName(nickname).orElseThrow(
                ()->new UsernameNotFoundException("User not found")));
    }

    @Override
    public UserDto getUserProfile(String nickname, String reqUsername) {
        UserEntity user = userRepository.findByNickName(nickname).orElseThrow(
                ()->new UsernameNotFoundException("User not found"));
        UserEntity reqUser = (reqUsername != null) ? findUserByEmail(reqUsername) : null;

        UserDto userDto = mapper.mapTo(user);
        if (reqUser != null)
            userDto.setObserved(userLikeChecker.isUserFollowed(user,reqUser.getId()));
        return userDto;
    }

    @Override
    @Transactional
    public UserDto followUser(Long userId, String username) {
        UserEntity followToUser = findUserById(userId);
        UserEntity reqUser = findUserByEmail(username);

        if (reqUser.equals(followToUser)) {
            throw new CustomException(BusinessErrorCodes.FOLLOW_ERROR);
        }

        if (reqUser.getFollowings().contains(followToUser) && followToUser.getFollowers().contains(reqUser)) {
            reqUser.getFollowings().remove(followToUser);
            followToUser.getFollowers().remove(reqUser);
        }
        else {
            reqUser.getFollowings().add(followToUser);
            followToUser.getFollowers().add(reqUser);
        }

        userRepository.save(followToUser);
        userRepository.save(reqUser);
        UserDto userDto = mapper.mapTo(followToUser);
        userDto.setObserved(userLikeChecker.isUserFollowed(followToUser,reqUser.getId()));
        return userDto;
    }

    @Override
    @Transactional
    public UserDto updateUser(String username, UserRequestDto userRequestDto, MultipartFile userImage) {
        UserEntity user = findUserByEmail(username);

        if (userRequestDto.getFullName() != null) {
            user.setFullName(userRequestDto.getFullName());
        }
        if (userRequestDto.getDescription() != null) {
            user.setDescription(userRequestDto.getDescription());
        }

        if (userImage != null && !userImage.isEmpty()) {
            String imagePath = fileService.updateFile(userImage, user.getProfilePicture(), "userImages/");
            user.setProfilePicture(imagePath);
        }

        return mapper.mapTo(userRepository.save(user));
    }

    @Override
    public Set<UserDto> searchUser(String query) {
        return userRepository.searchUser(query).stream()
                .map(mapper::mapTo)
                .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    @Transactional
    public void requestArtistVerification(String username, ArtistVerificationRequest request) {
        UserEntity user = findUserByEmail(username);

        if (user.getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase("ARTIST"))) {
            throw new CustomException(BusinessErrorCodes.USER_ALREADY_ARTIST);
        }

        ArtistVerificationToken token = artistVerificationTokenService.getTokenByUser(user);

        if (token == null) {
            token = artistVerificationTokenService.createArtistVerificationToken(user, request.getRequestedArtistName());
        }

        List<UserEntity> adminList = userRepository.findAll().stream()
                .filter(admin -> admin.getRoles().stream()
                        .anyMatch(role -> role.getName().equalsIgnoreCase("ADMIN")))
                .toList();

        ArtistVerificationToken finalToken = token;
        adminList.forEach(admin -> {
            try {
                sendArtistVerificationEmail(user, admin, finalToken);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    @Cacheable(
            value = "premiumStatus",
            key = "#email",
            unless = "#result == false"
    )
    public boolean isPremium(String email) {
        UserEntity user = findUserByEmail(email);
        return subscriptionRepo.existsByUserIdAndStatusAndCurrentPeriodEndAfter(
                user.getId(),
                SubscriptionStatus.ACTIVE,
                LocalDateTime.now()
        );
    }

    private void sendArtistVerificationEmail(UserEntity user, UserEntity admin, ArtistVerificationToken artistVerificationToken) throws MessagingException {
        emailService.sendEmail(
                admin.getEmail(),
                admin.getFullName(),
                EmailTemplateName.ARTIST_VERIFICATION,
                generateURL(artistVerificationUrl, user.getId(), artistVerificationToken.getToken()),
                "Artist Verification",
                artistVerificationToken.getRequestedArtistName(),
                user
        );
    }

    private String generateURL(String baseUrl, Long user_id, String token){
        return String.format("%s/%s/%s", baseUrl ,user_id, token);
    }
}
