package com.example.model;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import java.time.LocalDate; import java.util.List;

public class Client extends User implements Comparable<Client> {

    private String address;
    private Date birthDate;
    private List<Comment> commentList = new ArrayList<>();  // Initialize list to prevent null refs
    private List<Publication> ownedPublications = new ArrayList<>();
    private List<Publication> borrowedPublications = new ArrayList<>();

    public Client() {
        super();
        // Default constructor needed for Gson
    }

    public Client(String login, String password, String name, String surname, String address) {
        super(login, password, name, surname);
        this.address = address;
    }

    public Client(String login, String password, String name, String surname, String address, Date birthDate) {
        super(login, password, name, surname);
        this.address = address;
        this.birthDate = birthDate;
    }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Date getBirthDate() { return birthDate; }
    public void setBirthDate(Date birthDate) { this.birthDate = birthDate; }

    @Override
    public String toString() {
        return name + " " + surname;
    }

    @Override
    public int compareTo(Client o) {

        return 0;
    }
}