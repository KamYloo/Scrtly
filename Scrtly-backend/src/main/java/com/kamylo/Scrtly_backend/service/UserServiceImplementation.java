package com.kamylo.Scrtly_backend.service;
import com.kamylo.Scrtly_backend.config.JwtProvider;
import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.repository.UserRepository;
import com.kamylo.Scrtly_backend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImplementation implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileServiceImplementation fileService;

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
    public User findUserById(Long userId) throws UserException{
        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            return user.get();
        }
        throw new UserException("User not found with id" + userId);
    }

    @Override
    public User followUser(Long userId, User user) throws UserException {
        User followToUser = findUserById(userId);

        if (user.getFollowings().contains(followToUser) && followToUser.getFollowers().contains(user)) {
            user.getFollowings().remove(followToUser);
            followToUser.getFollowers().remove(user);
        }
        else {
            user.getFollowings().add(followToUser);
            followToUser.getFollowers().add(user);
        }
        userRepository.save(followToUser);
        userRepository.save(user);
        return followToUser;
    }

    @Override
    public User updateUser(Long userId, String fullName, String description, MultipartFile userImage) throws UserException {
        User user = findUserById(userId);
        String currentProfilePicture = user.getProfilePicture();

        String imagePath = fileService.updateFile(userImage, "/"+currentProfilePicture, "/uploads/userImages");
        user.setProfilePicture("uploads/userImages/" + imagePath);

        user.setFullName(fullName);
        user.setDescription(description);
        return userRepository.save(user);
    }

    @Override
    public Set<User> searchUser(String query) {
        return userRepository.searchUser(query);
    }
}
