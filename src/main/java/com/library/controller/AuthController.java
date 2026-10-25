package com.library.controller;

import com.library.model.User;
import com.library.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }


@PostMapping("/register")
public String registerUser(
        @ModelAttribute("user") User user,
        @RequestParam("confirmPassword") String confirmPassword,
        Model model) {

    if (user.getPassword() == null
            || !user.getPassword().equals(confirmPassword)) {
        model.addAttribute("errorMessage", "Registration failed: Passwords do not match.");
        return "register";
    }

    try {
        userService.registerUser(user);
        return "redirect:/login?success";
    } catch (Exception e) {
        model.addAttribute("errorMessage", "Registration failed: " + e.getMessage());
        return "register";
    }
}

}