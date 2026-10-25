package com.library.controller;

import com.library.model.Book;
import com.library.model.BookType;
import com.library.model.User;
import com.library.repository.BookRepository;
import com.library.repository.TransactionRepository;
import com.library.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;

@Controller
@RequestMapping("/admin")
public class AdminController {

	private final BookRepository bookRepository;
	private final TransactionRepository transactionRepository;
	private final UserService userService;
	
    // Folder location on server where PDFs will be stored
    private static final String UPLOAD_DIR = "uploads/ebooks/";

    public AdminController(BookRepository bookRepository,
            TransactionRepository transactionRepository,
            UserService userService) {
this.bookRepository = bookRepository;
this.transactionRepository = transactionRepository;
this.userService = userService;
}


@GetMapping("/dashboard")
public String adminDashboard(Model model) {
    model.addAttribute("books", bookRepository.findAll());
    model.addAttribute("transactions", transactionRepository.findAll());
    model.addAttribute("users", userService.getManageableUsers());
    return "admin-dashboard";
}


@GetMapping("/books/removal")
public String bookRemovalPage(Model model) {
    model.addAttribute("books", bookRepository.findAll());
    return "book-removal";
}


    
    @GetMapping("/teachers/add")
    public String showAddTeacherForm(Model model) {
        model.addAttribute("user", new User());
        return "add-teacher";
    }
    

@PostMapping("/teachers/add")
public String createTeacher(@ModelAttribute("user") User user, Model model) {
    try {
        userService.createTeacher(user);
        return "redirect:/admin/dashboard?teacherCreated";
    } catch (IllegalArgumentException e) {
        model.addAttribute("errorMessage", e.getMessage());
        return "add-teacher";
    }
}


    @GetMapping("/books/add")
    public String showAddBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "add-book";
    }

    @PostMapping("/books/add")
    public String saveBook(@ModelAttribute("book") Book book,
                           @RequestParam("pdfFile") MultipartFile pdfFile) {
        
        // Handle PDF Upload if the book is an E-Book and file is not empty
        if (book.getBookType() == BookType.EBOOK && pdfFile != null && !pdfFile.isEmpty()) {
            try {
                // Ensure upload directory exists
                File uploadDir = new File(UPLOAD_DIR);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                // Standardize unique filename to prevent overwrites
                String fileName = System.currentTimeMillis() + "_" + pdfFile.getOriginalFilename();
                Path filePath = Paths.get(UPLOAD_DIR + fileName);

                // Copy file to server directory
                Files.copy(pdfFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Save filename to database entity
                book.setPdfFileName(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (book.getAvailableCopies() == 0 && book.getTotalCopies() > 0) {
            book.setAvailableCopies(book.getTotalCopies());
        }

        bookRepository.save(book);
        return "redirect:/admin/dashboard";
    }
    

@PostMapping("/books/delete/{id}")
public String deleteBook(@PathVariable("id") Long bookId) {
    if (!bookRepository.existsById(bookId)) {
        return "redirect:/admin/books/removal?bookError=notFound";
    }

    if (transactionRepository.existsByBookId(bookId)) {
        return "redirect:/admin/books/removal?bookError=hasTransactions";
    }

    bookRepository.deleteById(bookId);
    return "redirect:/admin/books/removal?bookDeleted";
}


    @PostMapping("/transactions/return/{id}")
    public String returnBook(@PathVariable("id") Long transactionId) {
        var transaction = transactionRepository.findById(transactionId).orElse(null);
        if (transaction != null && !transaction.isReturned()) {
            transaction.setReturned(true);
            transaction.setReturnDate(LocalDate.now());

            Book book = transaction.getBook();
            if (book != null && (book.getBookType() == null || book.getBookType() == BookType.PHYSICAL)) {
                book.setAvailableCopies(book.getAvailableCopies() + 1);
                bookRepository.save(book);
            }

            transactionRepository.save(transaction);
        }
        return "redirect:/admin/dashboard";
    }
    

@GetMapping("/accounts")
public String manageAccounts(Model model) {
    model.addAttribute("users", userService.getManageableUsers());
    return "manage-accounts";
}


@PostMapping("/accounts/delete/{id}")
public String deleteAccount(@PathVariable("id") Long userId) {
    try {
        userService.deleteUser(userId);
        return "redirect:/admin/accounts?deleted";
    } catch (IllegalArgumentException e) {
        return "redirect:/admin/accounts?error="
                + java.net.URLEncoder.encode(
                        e.getMessage(),
                        java.nio.charset.StandardCharsets.UTF_8);
    }
}


}