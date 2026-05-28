package com.skillswap.booking;

import com.skillswap.skills.SkillOffer;
import com.skillswap.skills.SkillOfferRepository;
import com.skillswap.user.User;
import com.skillswap.user.UserRepository;
import com.skillswap.wallet.TimeTransaction;
import com.skillswap.wallet.TimeTransactionRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookingService {

  private final BookingCalendarRepository bookings;
  private final SkillOfferRepository offers;
  private final UserRepository users;
  private final TimeTransactionRepository transactions;

  @Transactional
  public BookingCalendar createBooking(Long requesterId, Long offerId, Instant startAt, Instant endAt, long tokenMinutes) {
    SkillOffer offer = offers.findById(offerId).orElseThrow(() -> new IllegalArgumentException("Offer not found"));
    if (offer.getOwner().getId().equals(requesterId)) {
      throw new IllegalArgumentException("You cannot book your own offer");
    }
    User requester = users.findById(requesterId).orElseThrow(() -> new IllegalArgumentException("Requester not found"));
    User provider = users.findById(offer.getOwner().getId()).orElseThrow(() -> new IllegalArgumentException("Provider not found"));

    BookingCalendar b = new BookingCalendar();
    b.setOffer(offer);
    b.setRequester(requester);
    b.setProvider(provider);
    b.setStartAt(startAt);
    b.setEndAt(endAt);
    b.setTokenMinutes(tokenMinutes);
    b.setStatus(BookingStatus.REQUESTED);
    return bookings.save(b);
  }

  @Transactional
  public BookingCalendar confirmBooking(Long bookingId, Long actorUserId) {
    BookingCalendar booking = bookings.findById(bookingId).orElseThrow(() -> new IllegalArgumentException("Booking not found"));
    if (!booking.getProvider().getId().equals(actorUserId)) {
      throw new IllegalArgumentException("Only provider can confirm booking");
    }
    if (booking.getStatus() != BookingStatus.REQUESTED) {
      throw new IllegalArgumentException("Only requested bookings can be confirmed");
    }
    booking.setStatus(BookingStatus.CONFIRMED);
    return booking;
  }

  @Transactional
  public BookingCalendar completeBooking(Long bookingId, Long actorUserId) {
    BookingCalendar booking = bookings.findById(bookingId).orElseThrow(() -> new IllegalArgumentException("Booking not found"));
    if (!booking.getProvider().getId().equals(actorUserId)) {
      throw new IllegalArgumentException("Only provider can complete booking");
    }
    if (booking.getStatus() != BookingStatus.CONFIRMED) {
      throw new IllegalArgumentException("Only confirmed bookings can be completed");
    }

    // Lock both user rows before balance mutation to avoid concurrent transfer race.
    User payer = users.findByIdForUpdate(booking.getRequester().getId())
        .orElseThrow(() -> new IllegalArgumentException("Requester not found"));
    User payee = users.findByIdForUpdate(booking.getProvider().getId())
        .orElseThrow(() -> new IllegalArgumentException("Provider not found"));

    if (payer.getTokenMinutesBalance() < booking.getTokenMinutes()) {
      throw new IllegalArgumentException("Insufficient Time Tokens");
    }
    payer.setTokenMinutesBalance(payer.getTokenMinutesBalance() - booking.getTokenMinutes());
    payee.setTokenMinutesBalance(payee.getTokenMinutesBalance() + booking.getTokenMinutes());

    TimeTransaction t = new TimeTransaction();
    t.setFromUser(payer);
    t.setToUser(payee);
    t.setBooking(booking);
    t.setMinutes(booking.getTokenMinutes());
    t.setMemo("Booking #" + booking.getId() + " completed");
    transactions.save(t);

    booking.setStatus(BookingStatus.COMPLETED);
    return booking;
  }

  @Transactional
  public BookingCalendar cancelBooking(Long bookingId, Long actorUserId) {
    BookingCalendar booking = bookings.findById(bookingId).orElseThrow(() -> new IllegalArgumentException("Booking not found"));
    boolean allowed = booking.getProvider().getId().equals(actorUserId) || booking.getRequester().getId().equals(actorUserId);
    if (!allowed) {
      throw new IllegalArgumentException("Only requester or provider can cancel");
    }
    if (booking.getStatus() == BookingStatus.COMPLETED) {
      throw new IllegalArgumentException("Completed bookings cannot be cancelled");
    }
    booking.setStatus(BookingStatus.CANCELLED);
    return booking;
  }
}

