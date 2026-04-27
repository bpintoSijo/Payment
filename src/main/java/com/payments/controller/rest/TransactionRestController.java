package com.payments.controller.rest;

import com.payments.dto.transaction.TransactionDTO;
import com.payments.security.UserDetailsImpl;
import com.payments.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionRestController {
    private final TransactionService transactionService;

    public TransactionRestController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionDTO> create(@AuthenticationPrincipal UserDetailsImpl user, @RequestBody TransactionDTO transactionDTO) {
        return ResponseEntity.ok(
                TransactionDTO.fromEntity(transactionService.create(user.getId(), transactionDTO.getAmount(), transactionDTO.getPaymentMethodId()))
        );
    }

    @GetMapping
    public ResponseEntity<Page<TransactionDTO>> getPageFromUser(@AuthenticationPrincipal UserDetailsImpl user,
                                                                @PageableDefault(page = 1, size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
                                                                Pageable pageable) {
        Page<TransactionDTO> transactionsPage =
                transactionService.getPageFromUserSortByCreationDate(
                        pageable.getPageNumber() - 1,
                        pageable.getPageSize(),
                        user.getId()
                ).map(TransactionDTO::fromEntity);
        return ResponseEntity.ok(transactionsPage);

    }
}
