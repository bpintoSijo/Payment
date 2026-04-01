package com.payments.controller.rest;

import com.payments.dto.transaction.TransactionDTO;
import com.payments.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
public class TransactionRestController {
    private final TransactionService transactionService;

    public TransactionRestController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionDTO> create(@RequestBody TransactionDTO transactionDTO) {
        return ResponseEntity.ok(TransactionDTO.fromEntity(transactionService.create(transactionDTO)));
    }
}
