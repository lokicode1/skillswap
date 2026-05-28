package com.skillswap.wallet;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeTransactionRepository extends JpaRepository<TimeTransaction, Long> {
  List<TimeTransaction> findByFromUserIdOrToUserIdOrderByCreatedAtDesc(Long fromId, Long toId);
}

