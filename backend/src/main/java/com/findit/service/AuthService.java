package com.findit.service;

import com.findit.dto.auth.AuthResponse;
import com.findit.dto.auth.ForgotPasswordRequest;
import com.findit.dto.auth.LoginRequest;
import com.findit.dto.auth.RegisterRequest;
import com.findit.dto.auth.ResetPasswordRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);
}
