package com.skillswap.booking;

import com.skillswap.common.CurrentUserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Instant;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

  private final BookingCalendarRepository bookings;
  private final BookingService bookingService;
  private final CurrentUserService currentUserService;

  public BookingController(
      BookingCalendarRepository bookings,
      BookingService bookingService,
      CurrentUserService currentUserService
  ) {
    this.bookings = bookings;
    this.bookingService = bookingService;
    this.currentUserService = currentUserService;
  }

  @GetMapping
  public List<BookingResponse> myBookings() {
    Long userId = currentUserService.requireUserId();
    return bookings.findByRequesterIdOrProviderIdOrderByStartAtDesc(userId, userId).stream()
        .map(this::toResponse)
        .toList();
  }

  @PostMapping
  public BookingResponse create(@Valid @RequestBody CreateBookingRequest req) {
    Long userId = currentUserService.requireUserId();
    BookingCalendar b = bookingService.createBooking(userId, req.offerId(), req.startAt(), req.endAt(), req.tokenMinutes());
    return toResponse(b);
  }

  @PostMapping("/{id}/confirm")
  public BookingResponse confirm(@PathVariable Long id) {
    Long userId = currentUserService.requireUserId();
    return toResponse(bookingService.confirmBooking(id, userId));
  }

  @PostMapping("/{id}/complete")
  public BookingResponse complete(@PathVariable Long id) {
    Long userId = currentUserService.requireUserId();
    return toResponse(bookingService.completeBooking(id, userId));
  }

  @PostMapping("/{id}/cancel")
  public BookingResponse cancel(@PathVariable Long id) {
    Long userId = currentUserService.requireUserId();
    return toResponse(bookingService.cancelBooking(id, userId));
  }

  private BookingResponse toResponse(BookingCalendar b) {
    return new BookingResponse(
        b.getId(),
        b.getOffer().getId(),
        b.getOffer().getTitle(),
        b.getRequester().getId(),
        b.getRequester().getDisplayName(),
        b.getProvider().getId(),
        b.getProvider().getDisplayName(),
        b.getStartAt(),
        b.getEndAt(),
        b.getTokenMinutes(),
        b.getStatus().name()
    );
  }

  public record CreateBookingRequest(
      @NotNull Long offerId,
      @NotNull @Future Instant startAt,
      @NotNull @Future Instant endAt,
      @Positive long tokenMinutes
  ) {}

  public record BookingResponse(
      Long id,
      Long offerId,
      String offerTitle,
      Long requesterId,
      String requesterName,
      Long providerId,
      String providerName,
      Instant startAt,
      Instant endAt,
      long tokenMinutes,
      String status
  ) {}
}

