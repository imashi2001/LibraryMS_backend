package com.imashi.lms.backend.service;

import com.imashi.lms.backend.dto.request.LoginRequest;
import com.imashi.lms.backend.dto.request.RegisterRequest;
import com.imashi.lms.backend.dto.response.AuthResponse;
import com.imashi.lms.backend.entity.User;
import com.imashi.lms.backend.exception.ResourceAlreadyExistsException;
import com.imashi.lms.backend.repository.UserRepository;
import com.imashi.lms.backend.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already exists: " + request.getEmail());
        }

        // Create new user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setIsBlacklisted(false);

        // Save user
        User savedUser = userRepository.save(user);

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(savedUser.getId(), savedUser.getEmail(), savedUser.getRole());

        // Return response
        return new AuthResponse(token, savedUser.getEmail(), savedUser.getRole(), savedUser.getId());
    }

    public AuthResponse login(LoginRequest request) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Get user from database
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

            // Check if user is blacklisted
            if (user.getIsBlacklisted()) {
                throw new BadCredentialsException("Account has been blacklisted");
            }

            // Generate JWT token
            String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole());

            // Return response
            return new AuthResponse(token, user.getEmail(), user.getRole(), user.getId());
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid email or password", e);
        }
    }
}
