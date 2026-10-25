package com.library.model;

import jakarta.persistence.*;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    private String isbn;

    private String genre;

    @Enumerated(EnumType.STRING)
    private BookType bookType = BookType.PHYSICAL;

    private String pdfFileName; // Stores the filename of the uploaded E-Book PDF

    private int totalCopies;

    private int availableCopies;

    public Book() {
    }

    public Book(String title, String author, String isbn, String genre, BookType bookType, String pdfFileName, int totalCopies, int availableCopies) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.genre = genre;
        this.bookType = bookType;
        this.pdfFileName = pdfFileName;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public BookType getBookType() { return bookType; }
    public void setBookType(BookType bookType) { this.bookType = bookType; }

    public String getPdfFileName() { return pdfFileName; }
    public void setPdfFileName(String pdfFileName) { this.pdfFileName = pdfFileName; }

    public int getTotalCopies() { return totalCopies; }
    public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }

    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }
}