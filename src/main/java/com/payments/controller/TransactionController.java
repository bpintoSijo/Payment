package com.payments.controller;

import com.payments.domain.payment.AbstractPaymentMethod;
import com.payments.domain.transaction.Transaction;
import com.payments.dto.transaction.TransactionDTO;
import com.payments.repository.PaymentMethodRepository;
import com.payments.repository.TransactionRepository;
import com.payments.security.UserDetailsImpl;
import com.payments.service.PaymentProcessService;
import com.payments.service.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// TODO Pipeline Jenkins
@Controller
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentProcessService paymentProcessService;

    @ModelAttribute
    public void addActiveLink(Model model) {
        model.addAttribute("activeLink", "transactions");
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public String showForm(
            @AuthenticationPrincipal UserDetailsImpl user, Model model,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size
    ) {

        model.addAttribute("form", new TransactionDTO());
        model.addAttribute("paymentMethods",
                paymentMethodRepository.findByOwnerId(user.getId()));

        Page<Transaction> transactionsPage =
                transactionService.getPageFromUserSortByCreationDate(page, size, user.getId());

        model.addAttribute("transactionsPage", transactionsPage);
        model.addAttribute("transactions", transactionsPage.getContent());

        return "transactions";
    }

    @PostMapping
    public String submitForm(
            @AuthenticationPrincipal UserDetailsImpl user,
            @ModelAttribute("form") TransactionDTO form,
            RedirectAttributes redirectAttributes
    ) {
        AbstractPaymentMethod payment = paymentMethodRepository.findById(form.getPaymentMethodId()).orElseThrow(() -> new EntityNotFoundException("Could not find payment method."));
        paymentProcessService.startPaymentTransaction(payment.getAccountId(), payment.getId(), payment.getType(), form.getAmount());
        if(payment.pay(form.getAmount())) {
            String paymentMessage = "Paid " + form.getAmount() + " with " + payment.getType() + " - " + payment.getAccountId();
            redirectAttributes.addFlashAttribute("successPaymentMessage", paymentMessage);
        }
        return "redirect:/transactions";
    }
}