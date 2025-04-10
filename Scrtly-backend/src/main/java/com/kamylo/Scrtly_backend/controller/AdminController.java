package com.kamylo.Scrtly_backend.controller;

import com.kamylo.Scrtly_backend.service.AdminService;
import com.kamylo.Scrtly_backend.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @Value("${mailing.frontend.redirect-url}")
    private String redirectUrl;

    @GetMapping("/artist/verify/{userId}/{token}")
    public ResponseEntity<?> active_account(@PathVariable Long userId, @PathVariable String token, HttpServletResponse response)
            throws IOException {
        adminService.verifyUserAsArtist(userId, token);
        response.sendRedirect(redirectUrl);
        return new ResponseEntity<>("User successfully verified as an artist", HttpStatus.OK);
    }

}
