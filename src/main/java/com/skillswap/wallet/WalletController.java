package com.skillswap.wallet;

import com.skillswap.common.CurrentUserService;
import java.time.Instant;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

  private final TimeTransactionRepository transactions;
  private final CurrentUserService currentUserService;

  public WalletController(TimeTransactionRepository transactions, CurrentUserService currentUserService) {
    this.transactions = transactions;
    this.currentUserService = currentUserService;
  }

  @GetMapping("/transactions")
  public List<TransactionResponse> myTransactions() {
    Long userId = currentUserService.requireUserId();
    return transactions.findByFromUserIdOrToUserIdOrderByCreatedAtDesc(userId, userId).stream()
        .map(t -> new TransactionResponse(
            t.getId(),
            t.getFromUser().getId(),
            t.getFromUser().getDisplayName(),
            t.getToUser().getId(),
            t.getToUser().getDisplayName(),
            t.getMinutes(),
            t.getMemo(),
            t.getCreatedAt()
        ))
        .toList();
  }

  public record TransactionResponse(
      Long id,
      Long fromUserId,
      String fromUserName,
      Long toUserId,
      String toUserName,
      long minutes,
      String memo,
      Instant createdAt
  ) {}
}

