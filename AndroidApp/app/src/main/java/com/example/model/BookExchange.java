package com.example.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BookExchange implements Serializable {
    private List<User> users;
    private List<Publication> publications;

    public BookExchange() {
        this.users = new ArrayList<>();
        this.publications = new ArrayList<>();
    }
}
