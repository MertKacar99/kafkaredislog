package com.mertkacar.businiess.concretes;

import com.mertkacar.businiess.abstracts.AuthService;
import com.mertkacar.businiess.security.JwtService;
import com.mertkacar.dtos.requests.AuthRequest;
import com.mertkacar.dtos.requests.RegisterRequest;
import com.mertkacar.dtos.responses.AuthResponse;
import com.mertkacar.kafka.producer.AuthLogProducer;
import com.mertkacar.kafka.producer.LogProducer;
import com.mertkacar.model.enums.Role;
import com.mertkacar.model.User;
import com.mertkacar.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthManager implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthLogProducer  authLogProducer;
    private final LogProducer logProducer;
    @Override
    public AuthResponse register(RegisterRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(u -> {
            logProducer.sendLog(request.getEmail(),"User  Email already exists");
            throw new IllegalArgumentException("Email already in use");


        });
        userRepository.findByUsername(request.getUsername()).ifPresent(u -> {
           logProducer.sendLog(u.getUsername(),"User  Username already exists");
            throw new IllegalArgumentException("Username already in use");
        });

        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        userRepository.save(user);
        String token = jwtService.generateToken(user);
        authLogProducer.sendRegisterEvent(user.getUsername(),user.getEmail());
        return new AuthResponse(token);
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        // Kimlik doğrulama (Spring Security)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // DB’den kullanıcıyı çek
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // JWT üret
        String token = jwtService.generateToken(user);

        authLogProducer.sendLoginEvent(user.getRealUsername(),user.getEmail(),true);
        return new AuthResponse(token);
    }
}
