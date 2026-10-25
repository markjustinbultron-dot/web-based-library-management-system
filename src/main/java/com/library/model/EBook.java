package com.library.model;

import java.io.Serializable;

/**
 * A digital book title. Unlike {@link Book}, there is no copy count to manage —
 * any number of borrowers can hold an active access period for the same e-book
 * at the same time.
 */
public class EBook implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String title;
    private String author;
    private String isbn;

    public EBook(int id, String title, String author, String isbn) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
}
