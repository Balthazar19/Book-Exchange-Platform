package com.example.model;

import java.util.List;

public class Chat {
    private int id;
    private Book book;
    private List<Comment> messages;

    public Chat(Book book, List<Comment> messages) {
        this.book = book;
        this.messages = messages;
    }
}
