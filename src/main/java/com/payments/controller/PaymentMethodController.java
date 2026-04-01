package com.payments.controller;

import com.payments.dto.payment.PaymentMethodFormDTO;
import com.payments.service.PaymentMethodService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/payment-methods")
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    public PaymentMethodController(PaymentMethodService paymentMethodService) {
        this.paymentMethodService = paymentMethodService;
    }

    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("form", new PaymentMethodFormDTO());
        model.addAttribute("paymentMethods", paymentMethodService.getAvailablePaymentMethod());
        return "paymentMethod/form";
    }

    @PostMapping("/new")
    public String submitForm(@ModelAttribute("form") PaymentMethodFormDTO form, RedirectAttributes redirectAttributes) {
        paymentMethodService.create(form.toDTO());
        redirectAttributes.addFlashAttribute("successMessage",
                "Payment method " + form.getType() + " — " + form.getAccountId() + " created successfully.");
        return "redirect:/payment-methods/new";
    }
}
