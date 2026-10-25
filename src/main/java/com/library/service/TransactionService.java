package com.library.service;

import com.library.model.Book;
import com.library.model.BookType;
import com.library.model.Transaction;
import com.library.model.User;
import com.library.repository.BookRepository;
import com.library.repository.TransactionRepository;
import com.library.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              BookRepository bookRepository,
                              UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Transaction borrowBook(Long userId, Long bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (user.isBanned()) {
            throw new RuntimeException("Account is banned from borrowing books.");
        }

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setBook(book);
        transaction.setBorrowDate(LocalDate.now());

        // Check if the book is an E-Book
        boolean isEbook = book.getBookType() != null && book.getBookType() == BookType.EBOOK;

        if (isEbook) {
            // E-Books do not decrease inventory & auto-complete immediately
            transaction.setReturned(true);
            transaction.setReturnDate(LocalDate.now());
            transaction.setDueDate(LocalDate.now());
        } else {
            // Physical Books require inventory check & 14-day due date
            if (book.getAvailableCopies() <= 0) {
                throw new RuntimeException("No available copies for this physical book.");
            }
            transaction.setReturned(false);
            transaction.setDueDate(LocalDate.now().plusDays(14));

            // Decrement physical stock counter
            book.setAvailableCopies(book.getAvailableCopies() - 1);
            bookRepository.save(book);
        }

        return transactionRepository.save(transaction);
    }

    @Transactional
    public void returnBook(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!transaction.isReturned()) {
            transaction.setReturned(true);
            transaction.setReturnDate(LocalDate.now());

            // Restock physical copies when returned
            Book book = transaction.getBook();
            if (book != null && (book.getBookType() == null || book.getBookType() == BookType.PHYSICAL)) {
                book.setAvailableCopies(book.getAvailableCopies() + 1);
                bookRepository.save(book);
            }

            transactionRepository.save(transaction);
        }
    }
}