package com.skillswap.booking;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingCalendarRepository extends JpaRepository<BookingCalendar, Long> {
  List<BookingCalendar> findByRequesterIdOrderByStartAtDesc(Long requesterId);
  List<BookingCalendar> findByProviderIdOrderByStartAtDesc(Long providerId);
  List<BookingCalendar> findByRequesterIdOrProviderIdOrderByStartAtDesc(Long requesterId, Long providerId);
}

