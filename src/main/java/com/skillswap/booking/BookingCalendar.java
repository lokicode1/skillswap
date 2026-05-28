package com.skillswap.booking;

import com.skillswap.skills.SkillOffer;
import com.skillswap.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "booking_calendar")
public class BookingCalendar {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "offer_id", nullable = false)
  private SkillOffer offer;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "requester_user_id", nullable = false)
  private User requester;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "provider_user_id", nullable = false)
  private User provider;

  @Column(nullable = false)
  private Instant startAt;

  @Column(nullable = false)
  private Instant endAt;

  @Column(nullable = false)
  private long tokenMinutes;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private BookingStatus status = BookingStatus.REQUESTED;

  @Column(nullable = false)
  private Instant createdAt = Instant.now();
}

