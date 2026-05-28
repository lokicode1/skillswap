package com.skillswap.user;

import com.skillswap.common.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MeController {

  private final UserRepository users;
  private final CurrentUserService currentUserService;

  @GetMapping
  public MeResponse me() {
    Long userId = currentUserService.requireUserId();
    User u = users.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
    return new MeResponse(u.getId(), u.getEmail(), u.getDisplayName(), u.getRole().name(), u.getTokenMinutesBalance());
  }

  public record MeResponse(
      Long id,
      String email,
      String displayName,
      String role,
      long tokenMinutesBalance
  ) {}
}

