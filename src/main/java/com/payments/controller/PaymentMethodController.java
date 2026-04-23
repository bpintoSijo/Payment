package com.payments.controller;

import com.payments.dto.payment.PaymentMethodFormDTO;
import com.payments.security.UserDetailsImpl;
import com.payments.service.PaymentMethodService;
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

@Controller
@RequestMapping("/payment-methods")
@RequiredArgsConstructor
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    @ModelAttribute
    public void addActiveLink(Model model) {
        model.addAttribute("activeLink", "payments");
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public String showForm(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        model.addAttribute("form", new PaymentMethodFormDTO());
        model.addAttribute("paymentMethods", paymentMethodService.getAvailablePaymentMethod(userDetails.getId()));
        return "paymentMethod/form";
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public String submitForm(@AuthenticationPrincipal UserDetailsImpl userDetails,
                             @ModelAttribute("form") PaymentMethodFormDTO form,
                             RedirectAttributes redirectAttributes
    ) {
        paymentMethodService.create(userDetails.getId(), form.toDTO());
        redirectAttributes.addFlashAttribute("successMessage",
                "Payment method " + form.getType() + " - " + form.getAccountId() + " created successfully.");
        return "redirect:/payment-methods";
    }
}
