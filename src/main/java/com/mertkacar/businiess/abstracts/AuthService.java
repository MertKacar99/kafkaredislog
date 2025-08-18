package com.mertkacar.businiess.abstracts;


import com.mertkacar.dtos.requests.AuthRequest;
import com.mertkacar.dtos.requests.RegisterRequest;
import com.mertkacar.dtos.responses.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(AuthRequest request);
}
