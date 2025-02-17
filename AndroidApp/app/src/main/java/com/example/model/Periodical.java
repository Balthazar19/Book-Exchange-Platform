package com.example.model;

import com.example.model.enums.Frequency;

import java.time.LocalDate;
public class Periodical extends Publication{

    private int issueNumber;
    private LocalDate publicationDate;
    private String editor;
    private Frequency frequency;
    private String publisher;

    public Periodical(String title, String author, int issueNumber, LocalDate publicationDate, String editor, Frequency frequency, String publisher) {
        super(title, author);
        this.issueNumber = issueNumber;
        this.publicationDate = publicationDate;
        this.editor = editor;
        this.frequency = frequency;
        this.publisher = publisher;
    }

    @Override
    public String toString() {
        return author + ": " + title;
    }
}
