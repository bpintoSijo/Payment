package com.payments.controller;

import com.payments.domain.payment.AbstractPaymentMethod;
import com.payments.dto.transaction.TransactionDTO;
import com.payments.repository.PaymentMethodRepository;
import com.payments.service.PaymentProcessService;
import com.payments.service.TransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// TODO workflow de la transactions jusqu'a OK ou NON. Penser à un process => camunda.
// TODO Pipeline Jenkins
@Controller
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentProcessService paymentProcessService;

    public TransactionController(TransactionService transactionService,
                                 PaymentMethodRepository paymentMethodRepository,
                                 PaymentProcessService paymentProcessService
    ) {
        this.transactionService = transactionService;
        this.paymentMethodRepository = paymentMethodRepository;
        this.paymentProcessService = paymentProcessService;
    }

    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("form", new TransactionDTO());
        model.addAttribute("paymentMethods", paymentMethodRepository.findAll());
        return "transaction/form";
    }

    @PostMapping("/new")
    public String submitForm(@ModelAttribute("form") TransactionDTO form, RedirectAttributes redirectAttributes) {
        AbstractPaymentMethod payment = transactionService.create(form.getAmount(), form.getPaymentMethodId()).getPayment();
        paymentProcessService.startPaymentTransaction(payment.getAccountId(), payment.getId(), payment.getType(), form.getAmount());
        if(payment.pay(form.getAmount())) {
            String paymentMessage = "Paid " + form.getAmount() + " with " + payment.getType() + " - " + payment.getAccountId();
            redirectAttributes.addFlashAttribute("successPaymentMessage", paymentMessage);
        }
        return "redirect:/transactions/new";
    }
}