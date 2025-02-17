package com.example.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ExchangePlatform implements Serializable {
    private List<User> allUsers;
    private List<Publication> allPublications;

    public ExchangePlatform(List<User> allUsers, List<Publication> allPublications) {
        this.allUsers = allUsers;
        this.allPublications = allPublications;
    }

    public ExchangePlatform() {
        this.allUsers = new ArrayList<>();
        this.allPublications = new ArrayList<>();
    }
}
