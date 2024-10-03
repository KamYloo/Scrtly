package com.kamylo.Scrtly_backend.controller;

import com.kamylo.Scrtly_backend.model.Artist;
import com.kamylo.Scrtly_backend.model.PlayList;
import com.kamylo.Scrtly_backend.model.User;
import com.kamylo.Scrtly_backend.repository.UserRepository;
import com.kamylo.Scrtly_backend.request.UserRequest;
import com.kamylo.Scrtly_backend.service.CustomUserServiceImplementation;
import com.kamylo.Scrtly_backend.response.AuthResponse;
import com.kamylo.Scrtly_backend.config.JwtProvider;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserServiceImplementation customUserDetails;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, CustomUserServiceImplementation customUserDetails) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.customUserDetails = customUserDetails;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody UserRequest userRequest) throws Exception {
        String role = userRequest.getRole();
        User isEmailExist = userRepository.findByEmail(userRequest.getEmail());
        if (isEmailExist != null) {
            throw new Exception("Email Is Already Used With Another Account");
        }
        User createdUser;
        if ("Artist".equalsIgnoreCase(role)) {
            Artist artist = new Artist();
            artist.setEmail(userRequest.getEmail());
            artist.setFullName(userRequest.getFullName());
            artist.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            artist.setArtistName(userRequest.getArtistName());
            artist.setRole("ARTIST");
            createdUser = artist;
        } else {
            createdUser = new User();
            createdUser.setEmail(userRequest.getEmail());
            createdUser.setFullName(userRequest.getFullName());
            createdUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            createdUser.setRole("USER");
        }
        User savedUser = userRepository.save(createdUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userRequest.getEmail(), userRequest.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = JwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Register Success");
        authResponse.setStatus(true);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);

    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> signin(@RequestBody User loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        System.out.println(email + "-------" + password);

        Authentication authentication = authenticate(email, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = JwtProvider.generateToken(authentication);
        AuthResponse authResponse = new AuthResponse();

        authResponse.setMessage("Login success");
        authResponse.setJwt(token);
        authResponse.setStatus(true);

        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    private Authentication authenticate(String email, String password) {

        System.out.println(email + "---++----" + password);

        UserDetails userDetails = customUserDetails.loadUserByUsername(email);

        System.out.println("Sig in in user details" + userDetails);

        if (userDetails == null) {
            System.out.println("Sign in details - null" + userDetails);

            throw new BadCredentialsException("Invalid email and password");
        }
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            System.out.println("Sign in userDetails - password mismatch" + userDetails);

            throw new BadCredentialsException("Invalid password");

        }
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

    }
}
