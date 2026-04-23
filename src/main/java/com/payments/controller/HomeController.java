package com.payments.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

    @ModelAttribute
    public void addActiveLink(Model model) {
        model.addAttribute("activeLink", "home");
    }

    @GetMapping
    public String homePage(Model model) {
        return "home";
    }
}
