package com.library.controller;

import com.library.model.Book;
import com.library.model.User;
import com.library.service.BookService;
import com.library.service.TransactionService;
import com.library.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class LibraryController {

    private final BookService bookService;
    private final TransactionService transactionService;
    private final UserService userService;

    public LibraryController(BookService bookService, TransactionService transactionService, UserService userService) {
        this.bookService = bookService;
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<Book> books = bookService.getAllBooks();
        model.addAttribute("books", books);
        return "books";
    }

    @PostMapping("/borrow/{id}")
    public String borrowBook(@PathVariable Long id, 
                             @AuthenticationPrincipal UserDetails userDetails, 
                             RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        try {
            User user = userService.findByEmailOrIdNumber(userDetails.getUsername());
            transactionService.borrowBook(user.getId(), id);
            redirectAttributes.addFlashAttribute("successMessage", "Book borrowed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/books";
    }

    @PostMapping("/return/{transactionId}")
    public String returnBook(@PathVariable Long transactionId, RedirectAttributes redirectAttributes) {
        try {
            transactionService.returnBook(transactionId);
            redirectAttributes.addFlashAttribute("successMessage", "Book returned successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/profile";
    }
}