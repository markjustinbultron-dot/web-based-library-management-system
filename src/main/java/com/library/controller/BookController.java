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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final TransactionService transactionService;
    private final UserService userService;

    public BookController(BookService bookService, TransactionService transactionService, UserService userService) {
        this.bookService = bookService;
        this.transactionService = transactionService;
        this.userService = userService;
    }



@GetMapping
public String listBooks(
        @RequestParam(value = "keyword", required = false) String keyword,
        @RequestParam(value = "genre", required = false) String genre,
        @RequestParam(value = "sortBy", defaultValue = "title") String sortBy,
        Model model) {

	List<Book> books = new java.util.ArrayList<>(
		    bookService.getBooksByKeywordAndGenre(keyword, genre)
		);

    if (!sortBy.equals("title")
            && !sortBy.equals("author")
            && !sortBy.equals("genre")) {
        sortBy = "title";
    }

    switch (sortBy) {
        case "author":
            books.sort(java.util.Comparator.comparing(
                    Book::getAuthor,
                    java.util.Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));
            break;

        case "genre":
            books.sort(java.util.Comparator.comparing(
                    Book::getGenre,
                    java.util.Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));
            break;

        default:
            books.sort(java.util.Comparator.comparing(
                    Book::getTitle,
                    java.util.Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));
            break;
    }

    List<String> genres = bookService.getAllBooks().stream()
            .map(Book::getGenre)
            .filter(g -> g != null && !g.isBlank())
            .distinct()
            .sorted()
            .toList();

    model.addAttribute("books", books);
    model.addAttribute("keyword", keyword);
    model.addAttribute("genres", genres);
    model.addAttribute("selectedGenre", genre);
    model.addAttribute("sortBy", sortBy);

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