package com.kamylo.Scrtly_backend.service;
import com.kamylo.Scrtly_backend.config.JwtProvider;
import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.repository.UserRepository;
import com.kamylo.Scrtly_backend.model.User;

import com.kamylo.Scrtly_backend.request.UpdateUserRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;

    public UserServiceImplementation(UserRepository userRepository) {
        this.userRepository=userRepository;
    }

    @Override
    public List<User> getAllUser() {
        return List.of();
    }

    @Override
    public User findUserProfileByJwt(String jwt) throws UserException{
        String email = JwtProvider.getEmailFromJwtToken(jwt);

        if (email==null) {
            throw new BadCredentialsException("Invalid JWT");
        }
        User user = userRepository.findByEmail(email);

        if(user==null) {
            throw new UserException("User not found with this email"+email);
        }
        return user;
    }

    @Override
    public User findUserByEmail(String email) {
        return null;
    }

    @Override
    public User findUserById(Long userId) throws UserException{
        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            return user.get();
        }
        throw new UserException("User not found with id" + userId);
    }

    @Override
    public User updateUser(Long userId, String fullName, String profilePicturePath, String decription) throws UserException {
        User user = findUserById(userId);

        String currentProfilePicture = user.getProfilePicture();

        if (currentProfilePicture != null && !currentProfilePicture.isEmpty()) {
            Path oldFilePath = Paths.get("src/main/resources/static").resolve(currentProfilePicture);
            File oldFile = oldFilePath.toFile();
            if (oldFile.exists()) {
                oldFile.delete();
            }
        }

        user.setFullName(fullName);
        user.setProfilePicture(profilePicturePath);
        user.setDescription(decription);
        return userRepository.save(user);
    }

    @Override
    public List<User> searchUser(String query) {
        return userRepository.searchUser(query);
    }
}
