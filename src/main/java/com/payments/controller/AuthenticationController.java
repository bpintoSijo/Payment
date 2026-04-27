package com.payments.controller;

import com.payments.dto.authentication.AuthenticationDTO;
import com.payments.security.JwtUtils;
import com.payments.security.UserDetailsImpl;
import com.payments.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserService userService;

    private static final String LOGIN_REDIRECT = "redirect:/auth/login";

    /**
     * GET /auth/login
     */
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginRequest", new AuthenticationDTO.LoginRequest());
        return "auth/login";
    }

    /**
     * POST /auth/login
     */
    @PostMapping("/login")
    public String login(
            @Valid @ModelAttribute("loginRequest") AuthenticationDTO.LoginRequest loginRequest,
            BindingResult bindingResult,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "auth/login";
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            Cookie jwtCookie = jwtUtils.generateCookie(authentication);

            response.addCookie(jwtCookie);
            return "redirect:/";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Invalid username or password.");
            return LOGIN_REDIRECT;
        }
    }

    /**
     * GET /auth/signup
     */
    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("signupRequest", new AuthenticationDTO.SignupRequest());
        return "auth/signup";
    }

    /**
     * POST /auth/signup
     */
    @PostMapping("/signup")
    public String signup(
            @Valid @ModelAttribute("signupRequest") AuthenticationDTO.SignupRequest signupRequest,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "auth/signup";
        }

        try {
            userService.register(signupRequest);
            redirectAttributes.addFlashAttribute("success", "Account created. Please log in.");
            return LOGIN_REDIRECT;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/auth/signup";
        }
    }

    /**
     * POST /auth/logout
     */
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public String logout(HttpServletResponse response) {
        SecurityContextHolder.clearContext();
        response.addCookie(jwtUtils.generateEmptyCookie());
        return LOGIN_REDIRECT;
    }

    /**
     * GET /auth/profile
     */
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public String getCurrentUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            Model model) {
        model.addAttribute("userInfo", userService.getCurrentUserInfo(userDetails));
        return "auth/profile";
    }
}