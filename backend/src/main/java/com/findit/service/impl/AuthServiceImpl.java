package com.findit.service.impl;

import com.findit.dto.auth.AuthResponse;
import com.findit.dto.auth.ForgotPasswordRequest;
import com.findit.dto.auth.LoginRequest;
import com.findit.dto.auth.RegisterRequest;
import com.findit.dto.auth.ResetPasswordRequest;
import com.findit.entity.User;
import com.findit.enums.Role;
import com.findit.exception.BadRequestException;
import com.findit.exception.DuplicateResourceException;
import com.findit.exception.ResourceNotFoundException;
import com.findit.repository.UserRepository;
import com.findit.security.JwtTokenProvider;
import com.findit.service.AuditService;
import com.findit.service.AuthService;
import com.findit.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final AuditService auditService;
    private final NotificationService notificationService;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtTokenProvider tokenProvider,
                           AuditService auditService,
                           NotificationService notificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.auditService = auditService;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new DuplicateResourceException("An account with email " + request.getEmail() + " already exists.");
        }

        User user = User.builder()
                .email(request.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .phoneNumber(request.getPhoneNumber() != null ? request.getPhoneNumber().trim() : null)
                .role(Role.ROLE_USER)
                .active(true)
                .verified(true)
                .build();

        User savedUser = userRepository.save(user);

        auditService.logAction(savedUser, "USER_REGISTER", "User", savedUser.getId(),
                "Registered new user: " + savedUser.getEmail(), null);

        notificationService.createNotification(savedUser,
                "Welcome to FindIt!",
                "Welcome to FindIt – Smart Lost & Found Platform. You can now report lost or found items and submit ownership claims.",
                "SYSTEM",
                "/dashboard");

        String token = tokenProvider.generateTokenFromEmail(
                savedUser.getEmail(),
                savedUser.getId(),
                savedUser.getRole().name(),
                savedUser.getFullName()
        );

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .role(savedUser.getRole())
                .build();
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail().trim().toLowerCase(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmailIgnoreCase(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.isActive()) {
            throw new BadRequestException("Your account has been deactivated. Please contact an administrator.");
        }

        String token = tokenProvider.generateToken(authentication);

        auditService.logAction(user, "USER_LOGIN", "User", user.getId(), "User logged in", null);

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .build();
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmailIgnoreCase(request.getEmail().trim().toLowerCase()).ifPresent(user -> {
            String token = UUID.randomUUID().toString();
            user.setResetPasswordToken(token);
            user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
            userRepository.save(user);

            auditService.logAction(user, "FORGOT_PASSWORD_REQUESTED", "User", user.getId(),
                    "Password reset token requested", null);

            notificationService.createNotification(user,
                    "Password Reset Request",
                    "A password reset was requested for your account. Use reset token: " + token,
                    "SYSTEM",
                    "/reset-password?token=" + token);

            log.info("Password reset token generated for user {}: {}", user.getEmail(), token);
        });
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByResetPasswordToken(request.getToken())
                .orElseThrow(() -> new BadRequestException("Invalid or expired password reset token"));

        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Password reset token has expired");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetPasswordToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);

        auditService.logAction(user, "PASSWORD_RESET_SUCCESS", "User", user.getId(),
                "Password was successfully reset", null);

        notificationService.createNotification(user,
                "Password Reset Successful",
                "Your account password was successfully updated.",
                "SYSTEM",
                "/login");
    }
}
