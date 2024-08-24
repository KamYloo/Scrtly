package com.kamylo.Scrtly_backend.service;
import com.kamylo.Scrtly_backend.config.JwtProvider;
import com.kamylo.Scrtly_backend.exception.UserException;
import com.kamylo.Scrtly_backend.repository.UserRepository;
import com.kamylo.Scrtly_backend.model.User;

import com.kamylo.Scrtly_backend.request.UpdateUserRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;


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
    public User updateUser(Long userId, UpdateUserRequest updateUserRequest) throws UserException {
        User user = findUserById(userId);

        if (updateUserRequest.getFullName() != null) {
            user.setFullName(updateUserRequest.getFullName());
        }
        if (updateUserRequest.getProfilePicture() != null) {
            user.setProfilePicture(updateUserRequest.getProfilePicture());
        }

        return userRepository.save(user);
    }

    @Override
    public List<User> searchUser(String query) {
        return userRepository.searchUser(query);
    }
}
