package com.skillswap.skills;

import com.skillswap.common.CurrentUserService;
import com.skillswap.user.User;
import com.skillswap.user.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SkillsController {

  private final SkillOfferRepository offers;
  private final SkillNeedRepository needs;
  private final UserRepository users;
  private final CurrentUserService currentUserService;

  public SkillsController(
      SkillOfferRepository offers,
      SkillNeedRepository needs,
      UserRepository users,
      CurrentUserService currentUserService
  ) {
    this.offers = offers;
    this.needs = needs;
    this.users = users;
    this.currentUserService = currentUserService;
  }

  @GetMapping("/offers")
  public List<OfferResponse> offers(@RequestParam(defaultValue = "") String q) {
    return offers.searchActive(q).stream().map(this::toOfferResponse).toList();
  }

  @PostMapping("/offers")
  @Transactional
  public OfferResponse createOffer(@Valid @RequestBody CreateSkillRequest req) {
    Long userId = currentUserService.requireUserId();
    User owner = users.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
    SkillOffer s = new SkillOffer();
    s.setOwner(owner);
    s.setTitle(req.title().trim());
    s.setDescription(req.description().trim());
    s.setCategory(req.category().trim().toUpperCase());
    s.setLocation(req.location().trim());
    s.setMinutesPerHour(req.minutesPerHour());
    offers.save(s);
    return toOfferResponse(s);
  }

  @GetMapping("/needs")
  public List<NeedResponse> needs(@RequestParam(defaultValue = "") String q) {
    return needs.searchActive(q).stream().map(this::toNeedResponse).toList();
  }

  @PostMapping("/needs")
  @Transactional
  public NeedResponse createNeed(@Valid @RequestBody CreateSkillRequest req) {
    Long userId = currentUserService.requireUserId();
    User requester = users.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
    SkillNeed n = new SkillNeed();
    n.setRequester(requester);
    n.setTitle(req.title().trim());
    n.setDescription(req.description().trim());
    n.setCategory(req.category().trim().toUpperCase());
    n.setLocation(req.location().trim());
    needs.save(n);
    return toNeedResponse(n);
  }

  private OfferResponse toOfferResponse(SkillOffer o) {
    return new OfferResponse(
        o.getId(),
        o.getOwner().getId(),
        o.getOwner().getDisplayName(),
        o.getTitle(),
        o.getDescription(),
        o.getCategory(),
        o.getLocation(),
        o.getMinutesPerHour(),
        o.isActive()
    );
  }

  private NeedResponse toNeedResponse(SkillNeed n) {
    return new NeedResponse(
        n.getId(),
        n.getRequester().getId(),
        n.getRequester().getDisplayName(),
        n.getTitle(),
        n.getDescription(),
        n.getCategory(),
        n.getLocation(),
        n.isActive()
    );
  }

  public record CreateSkillRequest(
      @NotBlank @Size(max = 120) String title,
      @NotBlank @Size(max = 2000) String description,
      @NotBlank @Size(max = 30) String category,
      @NotBlank @Size(max = 80) String location,
      int minutesPerHour
  ) {
    public int minutesPerHour() {
      return minutesPerHour <= 0 ? 60 : minutesPerHour;
    }
  }

  public record OfferResponse(
      Long id,
      Long ownerId,
      String ownerName,
      String title,
      String description,
      String category,
      String location,
      int minutesPerHour,
      boolean active
  ) {}

  public record NeedResponse(
      Long id,
      Long requesterId,
      String requesterName,
      String title,
      String description,
      String category,
      String location,
      boolean active
  ) {}
}

