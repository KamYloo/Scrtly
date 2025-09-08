package com.kamylo.Scrtly_backend.serviceTests;

import com.kamylo.Scrtly_backend.common.handler.CustomException;
import com.kamylo.Scrtly_backend.common.service.FileService;
import com.kamylo.Scrtly_backend.common.utils.UserLikeChecker;
import com.kamylo.Scrtly_backend.email.EmailTemplateName;
import com.kamylo.Scrtly_backend.email.service.EmailService;
import com.kamylo.Scrtly_backend.payment.domain.enums.SubscriptionStatus;
import com.kamylo.Scrtly_backend.payment.repository.SubscriptionRepository;
import com.kamylo.Scrtly_backend.artist.domain.ArtistVerificationToken;
import com.kamylo.Scrtly_backend.artist.service.ArtistVerificationTokenService;
import com.kamylo.Scrtly_backend.artist.web.dto.request.ArtistVerificationRequest;
import com.kamylo.Scrtly_backend.user.domain.RoleEntity;
import com.kamylo.Scrtly_backend.user.domain.UserEntity;
import com.kamylo.Scrtly_backend.user.mapper.UserMapper;
import com.kamylo.Scrtly_backend.user.repository.UserRepository;
import com.kamylo.Scrtly_backend.user.service.impl.UserServiceImpl;
import com.kamylo.Scrtly_backend.user.web.dto.UserDto;
import com.kamylo.Scrtly_backend.user.web.dto.request.UserRequestDto;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private FileService fileService;
    @Mock private UserMapper mapper;
    @Mock private UserLikeChecker userLikeChecker;
    @Mock private MultipartFile multipartFile;
    @Mock private ArtistVerificationTokenService artistVerificationTokenService;
    @Mock private EmailService emailService;
    @Mock private SubscriptionRepository subscriptionRepo;

    @InjectMocks private UserServiceImpl userService;

    private UserEntity user;
    private UserEntity anotherUser;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setNickName("User1");
        user.setFullName("Test User");
        user.setRoles(new HashSet<>());

        anotherUser = new UserEntity();
        anotherUser.setId(2L);
        anotherUser.setEmail("another@example.com");
        anotherUser.setNickName("User2");

        userDto = new UserDto();
        userDto.setNickName("User1");
        userDto.setFullName("Test User");
    }

    @Test
    void findUserByEmail_whenExists_shouldReturnEntity() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        assertThat(userService.findUserByEmail("user@example.com")).isEqualTo(user);
    }

    @Test
    void findUserByEmail_whenNotExists_shouldThrow() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.findUserByEmail("unknown@example.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void findUserById_whenExists_shouldReturnEntity() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        assertThat(userService.findUserById(1L)).isEqualTo(user);
    }

    @Test
    void findUserById_whenNotExists_shouldThrow() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.findUserById(999L))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void findUserByNickname_whenExists_shouldReturnDto() {
        when(userRepository.findByNickName("User1")).thenReturn(Optional.of(user));
        when(mapper.toDto(user)).thenReturn(userDto);
        assertThat(userService.findUserByNickname("User1")).isEqualTo(userDto);
    }

    @Test
    void findUserByNickname_whenNotExists_shouldThrow() {
        when(userRepository.findByNickName("unknownNick")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.findUserByNickname("unknownNick"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void getUserProfile_whenNicknameNotExists_shouldThrow() {
        when(userRepository.findByNickName("noNick")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.getUserProfile("noNick", "user@example.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void getUserProfile_whenReqUserEmailNotExists_shouldThrow() {
        when(userRepository.findByNickName("User1")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("bad@example.com")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.getUserProfile("User1", "bad@example.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void getUserProfile_whenObserved_shouldSetObservedTrue() {
        when(userRepository.findByEmail("another@example.com")).thenReturn(Optional.of(anotherUser));
        when(userRepository.findByNickName("User1")).thenReturn(Optional.of(user));
        when(userLikeChecker.isUserFollowed(user, anotherUser.getId())).thenReturn(true);
        when(mapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.getUserProfile("User1", "another@example.com");
        assertThat(result.isObserved()).isTrue();
    }

    @Test
    void getUserProfile_whenNotObserved_shouldSetObservedFalse() {
        when(userRepository.findByEmail("another@example.com")).thenReturn(Optional.of(anotherUser));
        when(userRepository.findByNickName("User1")).thenReturn(Optional.of(user));
        when(userLikeChecker.isUserFollowed(user, anotherUser.getId())).thenReturn(false);
        when(mapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.getUserProfile("User1", "another@example.com");
        assertThat(result.isObserved()).isFalse();
    }

    @Test
    void getUserProfile_whenRequesterNull_shouldSetObservedFalse() {
        when(userRepository.findByNickName("User1")).thenReturn(Optional.of(user));
        when(mapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.getUserProfile("User1", null);
        assertThat(result.isObserved()).isFalse();
    }

    @Test
    void followUser_whenNotFollowing_shouldFollowAndReturnDto() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(userRepository.findById(2L)).thenReturn(Optional.of(anotherUser));
        when(mapper.toDto(anotherUser)).thenReturn(userDto);

        userService.followUser(2L, "user@example.com");
        assertThat(user.getFollowings()).contains(anotherUser);
        assertThat(anotherUser.getFollowers()).contains(user);
    }

    @Test
    void followUser_whenFollowingAlready_shouldUnfollow() {
        user.getFollowings().add(anotherUser);
        anotherUser.getFollowers().add(user);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(userRepository.findById(2L)).thenReturn(Optional.of(anotherUser));
        when(mapper.toDto(anotherUser)).thenReturn(userDto);

        userService.followUser(2L, "user@example.com");
        assertThat(user.getFollowings()).doesNotContain(anotherUser);
        assertThat(anotherUser.getFollowers()).doesNotContain(user);
    }

    @Test
    void followUser_whenFollowingSelf_shouldThrow() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        assertThatThrownBy(() -> userService.followUser(1L, "user@example.com"))
                .isInstanceOf(CustomException.class);
    }

    @Test
    void updateUser_whenValidRequest_shouldUpdateFieldsAndSave() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        UserRequestDto req = new UserRequestDto();
        req.setFullName("Updated Name");
        req.setDescription("New Description");

        when(userRepository.save(any(UserEntity.class))).thenReturn(user);
        when(mapper.toDto(user)).thenReturn(userDto);

        userService.updateUser("user@example.com", req, null);

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());
        UserEntity saved = captor.getValue();
        assertThat(saved.getFullName()).isEqualTo("Updated Name");
        assertThat(saved.getDescription()).isEqualTo("New Description");
    }

    @Test
    void updateUser_whenImageEmptyOrNull_shouldNotChangeProfilePicture() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);

        userService.updateUser("user@example.com", new UserRequestDto(), null);
        assertThat(user.getProfilePicture()).isNull();

        when(multipartFile.isEmpty()).thenReturn(true);
        userService.updateUser("user@example.com", new UserRequestDto(), multipartFile);
        assertThat(user.getProfilePicture()).isNull();
    }

    @Test
    void updateUser_whenImageProvided_shouldUpdateProfilePicture() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(multipartFile.isEmpty()).thenReturn(false);
        when(fileService.updateFile(multipartFile, user.getProfilePicture(), "userImages/")).thenReturn("newImagePath");
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);
        when(mapper.toDto(user)).thenReturn(userDto);

        userService.updateUser("user@example.com", new UserRequestDto(), multipartFile);
        assertThat(user.getProfilePicture()).isEqualTo("newImagePath");
    }

    @Test
    void searchUser_whenMatches_shouldReturnDtoSet() {
        when(userRepository.searchUser("User")).thenReturn(Set.of(user));
        when(mapper.toDto(user)).thenReturn(userDto);

        Set<UserDto> result = userService.searchUser("User");
        assertThat(result).hasSize(1).extracting(UserDto::getNickName).containsExactly("User1");
    }

    @Test
    void searchUser_whenNoMatches_shouldReturnEmptySet() {
        when(userRepository.searchUser("Nobody")).thenReturn(Set.of());

        Set<UserDto> result = userService.searchUser("Nobody");
        assertThat(result).isEmpty();
    }

    @Test
    void isPremium_whenActiveSubscriptionExists_shouldReturnTrue() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(subscriptionRepo.existsByUserIdAndStatusAndCurrentPeriodEndAfter(
                eq(1L), eq(SubscriptionStatus.ACTIVE), any(LocalDateTime.class)))
                .thenReturn(true);

        assertThat(userService.isPremium("user@example.com")).isTrue();
    }

    @Test
    void isPremium_whenNoActiveSubscription_shouldReturnFalse() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(subscriptionRepo.existsByUserIdAndStatusAndCurrentPeriodEndAfter(
                eq(1L), eq(SubscriptionStatus.ACTIVE), any(LocalDateTime.class)))
                .thenReturn(false);

        assertThat(userService.isPremium("user@example.com")).isFalse();
    }

    @Test
    void requestArtistVerification_whenFirstRequest_andAdminsExist_shouldSendEmails() throws MessagingException {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        ArtistVerificationRequest req = new ArtistVerificationRequest();
        req.setRequestedArtistName("artist");

        when(artistVerificationTokenService.getTokenByUser(user)).thenReturn(null);
        ArtistVerificationToken token = new ArtistVerificationToken();
        token.setToken("token");
        token.setRequestedArtistName("artist");
        when(artistVerificationTokenService.createArtistVerificationToken(user, "artist")).thenReturn(token);

        RoleEntity adminRole = new RoleEntity(); adminRole.setName("ADMIN");
        UserEntity admin = new UserEntity(); admin.setId(100L); admin.setEmail("admin@example.com"); admin.setFullName("Admin"); admin.setRoles(Set.of(adminRole));
        when(userRepository.findAll()).thenReturn(List.of(admin));

        userService.requestArtistVerification("user@example.com", req);

        verify(emailService).sendEmail(eq("admin@example.com"), eq("Admin"), eq(EmailTemplateName.ARTIST_VERIFICATION), anyString(), eq("Artist Verification"), eq("artist"), eq(user));
    }

    @Test
    void requestArtistVerification_whenTokenExists_shouldReuseAndSendEmail() throws MessagingException {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        ArtistVerificationRequest req = new ArtistVerificationRequest();
        req.setRequestedArtistName("artist");

        ArtistVerificationToken existing = new ArtistVerificationToken(); existing.setToken("existing"); existing.setRequestedArtistName("artist");
        when(artistVerificationTokenService.getTokenByUser(user)).thenReturn(existing);

        RoleEntity adminRole = new RoleEntity(); adminRole.setName("ADMIN");
        UserEntity admin = new UserEntity(); admin.setId(100L); admin.setEmail("admin@example.com"); admin.setFullName("Admin"); admin.setRoles(Set.of(adminRole));
        when(userRepository.findAll()).thenReturn(List.of(admin));

        userService.requestArtistVerification("user@example.com", req);
        verify(emailService).sendEmail(anyString(), anyString(), eq(EmailTemplateName.ARTIST_VERIFICATION), anyString(), anyString(), anyString(), eq(user));
    }

    @Test
    void requestArtistVerification_whenNoAdmins_shouldNotThrowOrSend() throws MessagingException {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        ArtistVerificationRequest req = new ArtistVerificationRequest();
        req.setRequestedArtistName("artist");

        when(artistVerificationTokenService.getTokenByUser(user)).thenReturn(null);
        ArtistVerificationToken token = new ArtistVerificationToken(); token.setToken("t"); token.setRequestedArtistName("artist");
        when(artistVerificationTokenService.createArtistVerificationToken(user, "artist")).thenReturn(token);

        when(userRepository.findAll()).thenReturn(List.of());
        userService.requestArtistVerification("user@example.com", req);
        verifyNoInteractions(emailService);
    }

    @Test
    void requestArtistVerification_whenUserIsArtist_shouldThrow() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        RoleEntity artistRole = new RoleEntity(); artistRole.setName("ARTIST");
        user.getRoles().add(artistRole);

        ArtistVerificationRequest req = new ArtistVerificationRequest();
        req.setRequestedArtistName("artist");
        assertThatThrownBy(() -> userService.requestArtistVerification("user@example.com", req))
                .isInstanceOf(CustomException.class);
    }

    @Test
    void requestArtistVerification_whenEmailFails_shouldNotThrow() throws MessagingException {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        ArtistVerificationRequest req = new ArtistVerificationRequest();
        req.setRequestedArtistName("artist");

        when(artistVerificationTokenService.getTokenByUser(user)).thenReturn(null);
        ArtistVerificationToken token = new ArtistVerificationToken(); token.setToken("t"); token.setRequestedArtistName("artist");
        when(artistVerificationTokenService.createArtistVerificationToken(user, "artist")).thenReturn(token);

        RoleEntity adminRole = new RoleEntity(); adminRole.setName("ADMIN");
        UserEntity admin = new UserEntity(); admin.setId(100L); admin.setEmail("admin@example.com"); admin.setFullName("Admin"); admin.setRoles(Set.of(adminRole));
        when(userRepository.findAll()).thenReturn(List.of(admin));
        doThrow(new MessagingException("fail")).when(emailService)
                .sendEmail(anyString(), anyString(), any(), anyString(), anyString(), anyString(), any(UserEntity.class));

        userService.requestArtistVerification("user@example.com", req);
    }
}
