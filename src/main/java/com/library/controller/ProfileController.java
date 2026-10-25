package com.library.controller;

import com.library.model.User;
import com.library.repository.TransactionRepository;
import com.library.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {

    private final UserService userService;
    private final TransactionRepository transactionRepository;

    public ProfileController(UserService userService, TransactionRepository transactionRepository) {
        this.userService = userService;
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/profile")
    public String viewProfile(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String userEmail = authentication.getName();
        User user = userService.findByEmailOrIdNumber(userEmail);

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        
        // Pass borrowed books list to profile view
        model.addAttribute("borrowedTransactions", transactionRepository.findByUserId(user.getId()));

        return "profile";
    }
}