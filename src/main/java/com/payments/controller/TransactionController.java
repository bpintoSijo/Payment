package com.payments.controller;

import com.payments.domain.payment.Payment;
import com.payments.dto.transaction.TransactionDTO;
import com.payments.repository.PaymentMethodRepository;
import com.payments.service.TransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

//TODO FAIRE TESTS implémenter tests fonctionnels automatisé (Cucumber) commité tout sur git creéer branches features + 80% de couverture de code
// TODO workflow de la transactions jusqu'a OK ou NON. Penser à un process => camunda.
// TODO Pipeline Jenkins
@Controller
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final PaymentMethodRepository paymentMethodRepository;

    public TransactionController(TransactionService transactionService, PaymentMethodRepository paymentMethodRepository) {
        this.transactionService = transactionService;
        this.paymentMethodRepository = paymentMethodRepository;
    }

    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("form", new TransactionDTO());
        model.addAttribute("paymentMethods", paymentMethodRepository.findAll());
        return "transaction/form";
    }

    @PostMapping("/new")
    public String submitForm(@ModelAttribute("form") TransactionDTO form, RedirectAttributes redirectAttributes) {
        Payment payment = transactionService.create(form).getPayment();

        redirectAttributes.addFlashAttribute("successPayment", payment.pay(form.getAmount()));
        return "redirect:/transactions/new";
    }
}