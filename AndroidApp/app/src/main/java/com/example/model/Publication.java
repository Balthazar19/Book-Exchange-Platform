package com.example.model;

import java.io.Serializable;

public class Publication implements Serializable {
    protected int id;
    protected String title;
    protected String author;

    public Publication() {
        // Default constructor needed for deserialization
    }

    public Publication(String title, String author) {
        this.title = title;
        this.author = author;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return title + " by " + author;  // Customize how the list item will be displayed
    }
}