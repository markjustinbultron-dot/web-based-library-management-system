
package com.library.service;

import com.library.model.Book;
import com.library.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    public List<Book> searchBooks(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            return bookRepository
                    .findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(
                            keyword.trim(), keyword.trim());
        }
        return getAllBooks();
    }

    public List<Book> getBooksByGenre(String genre) {
        if (genre != null && !genre.trim().isEmpty()) {
            return bookRepository.findByGenreIgnoreCase(genre.trim());
        }
        return getAllBooks();
    }

    public List<Book> getBooksByKeywordAndGenre(String keyword, String genre) {
        List<Book> books = searchBooks(keyword);

        if (genre == null || genre.trim().isEmpty()) {
            return books;
        }

        String selectedGenre = genre.trim();

        return books.stream()
                .filter(book -> book.getGenre() != null
                        && book.getGenre().equalsIgnoreCase(selectedGenre))
                .toList();
    }
}
