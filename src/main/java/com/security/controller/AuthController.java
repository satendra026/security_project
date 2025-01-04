package com.security.controller;
import com.security.DTO.AuthRequest;
import com.security.DTO.AuthResponseDTO;
import com.security.DTO.TokenRefreshRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import com.security.util.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody AuthRequest authRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

         UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        String accessToken = jwtUtil.generateAccessToken(userDetails.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());

        return new AuthResponseDTO(accessToken, refreshToken);
    }

    @PostMapping("/refresh-token")
    public AuthResponseDTO refreshToken(@RequestBody TokenRefreshRequest request) {
        if (!jwtUtil.validateToken(request.getRefreshToken())) {
            throw new RuntimeException("Invalid refresh token");
        }

        String username = jwtUtil.extractUsername(request.getRefreshToken());
        String newAccessToken = jwtUtil.generateAccessToken(username);

        return new AuthResponseDTO(newAccessToken, request.getRefreshToken());
    }

    @PostMapping("/logout")
    public String logout() {
        // Since tokens are stateless, "logout" is simply a client-side operation.
        return "Logged out successfully";
    }
}


