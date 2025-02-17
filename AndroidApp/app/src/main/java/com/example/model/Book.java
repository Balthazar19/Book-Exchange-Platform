package com.example.model;

import com.example.model.enums.Genre;
import com.example.model.enums.Language;

//You can generate getters and setters, here Lombok lib is used. They are generated for you
public class Book extends Publication{

    //Variables should be private and their values retrieved and changed using getters and setters respectively

    private String publisher;
    private String isbn;
    private Genre genre;
    private int pageCount;
    private Language language;
    private int publicationYear;
    private Format format; // Hardcover, Paperback, Digital, etc.
    private String summary;

    //Define constructors - how to create objects and assign values to attributes

    public Book(String title, String author, String publisher, String isbn, Genre genre, int pageCount, Language language, int publicationYear, Format format, String summary) {
        super(title, author);
        this.publisher = publisher;
        this.isbn = isbn;
        this.genre = genre;
        this.pageCount = pageCount;
        this.language = language;
        this.publicationYear = publicationYear;
        this.format = format;
        this.summary = summary;
    }

    @Override
    public String toString() {
        return author + ": " + title;
    }
}
