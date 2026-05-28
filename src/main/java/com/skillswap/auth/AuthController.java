package com.skillswap.auth;

import com.skillswap.user.User;
import com.skillswap.user.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final UserRepository users;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  @Transactional
  public AuthDtos.AuthResponse register(@Valid @RequestBody AuthDtos.RegisterRequest req) {
    if (users.existsByEmailIgnoreCase(req.email())) {
      throw new IllegalArgumentException("Email already registered");
    }
    User u = new User();
    u.setEmail(req.email().trim().toLowerCase());
    u.setDisplayName(req.displayName().trim());
    u.setPasswordHash(passwordEncoder.encode(req.password()));
    u.setRole(User.Role.USER);
    u.setTokenMinutesBalance(120); // small starter balance for demo
    users.save(u);

    String token = jwtService.issueAccessToken(u.getId(), u.getEmail(), u.getRole().name());
    return new AuthDtos.AuthResponse(token);
  }

  @PostMapping("/login")
  public AuthDtos.AuthResponse login(@Valid @RequestBody AuthDtos.LoginRequest req) {
    User u = users.findByEmailIgnoreCase(req.email().trim())
        .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
    if (!passwordEncoder.matches(req.password(), u.getPasswordHash())) {
      throw new IllegalArgumentException("Invalid credentials");
    }
    String token = jwtService.issueAccessToken(u.getId(), u.getEmail(), u.getRole().name());
    return new AuthDtos.AuthResponse(token);
  }
}

