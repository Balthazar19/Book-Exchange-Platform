package com.example.model;

import com.example.model.enums.PublicationStatus;

import java.time.LocalDate;

public class PeriodicRecord {
    private int id;
    private Client user;
    private Book book;
    private LocalDate transactionDate;
    private LocalDate returnDate;
    private PublicationStatus status;

    public PeriodicRecord(int id, Client user, Book book, LocalDate transactionDate, LocalDate returnDate, PublicationStatus status) {
        this.id = id;
        this.user = user;
        this.book = book;
        this.transactionDate = transactionDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    public PeriodicRecord(Client user, Book book, LocalDate transactionDate, LocalDate returnDate, PublicationStatus status) {
        this.user = user;
        this.book = book;
        this.transactionDate = transactionDate;
        this.returnDate = returnDate;
        this.status = status;
    }
}
