package com.payments.controller;

import com.payments.domain.payment.AbstractPaymentMethod;
import com.payments.dto.transaction.TransactionDTO;
import com.payments.repository.PaymentMethodRepository;
import com.payments.security.UserDetailsImpl;
import com.payments.service.PaymentProcessService;
import com.payments.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// TODO Pipeline Jenkins
@Controller
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentProcessService paymentProcessService;

    @GetMapping("/new")
    @PreAuthorize("isAuthenticated()")
    public String showForm(@AuthenticationPrincipal UserDetailsImpl user, Model model) {
        model.addAttribute("form", new TransactionDTO());
        model.addAttribute("paymentMethods", paymentMethodRepository.findByOwnerId(user.getId()));
        return "transaction/form";
    }

    @PostMapping("/new")
    public String submitForm(
            @AuthenticationPrincipal UserDetailsImpl user,
            @ModelAttribute("form") TransactionDTO form,
            RedirectAttributes redirectAttributes
    ) {
        AbstractPaymentMethod payment = transactionService.create(user.getId(), form.getAmount(), form.getPaymentMethodId()).getPayment();
        paymentProcessService.startPaymentTransaction(payment.getAccountId(), payment.getId(), payment.getType(), form.getAmount());
        if(payment.pay(form.getAmount())) {
            String paymentMessage = "Paid " + form.getAmount() + " with " + payment.getType() + " - " + payment.getAccountId();
            redirectAttributes.addFlashAttribute("successPaymentMessage", paymentMessage);
        }
        return "redirect:/transactions/new";
    }
}