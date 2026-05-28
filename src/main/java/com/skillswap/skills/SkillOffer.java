package com.skillswap.skills;

import com.skillswap.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "skill_offer")
public class SkillOffer {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "owner_user_id", nullable = false)
  private User owner;

  @Column(nullable = false, length = 120)
  private String title;

  @Column(nullable = false, length = 2000)
  private String description;

  @Column(nullable = false, length = 30)
  private String category = "OTHER";

  @Column(nullable = false, length = 80)
  private String location = "Remote";

  @Column(nullable = false)
  private boolean active = true;

  @Column(nullable = false)
  private int minutesPerHour = 60;

  @Column(nullable = false)
  private Instant createdAt = Instant.now();
}

