package com.example.model;

public class Admin extends User{

    private String phoneNum;
    public Admin(String login, String password, String name, String surname, String phoneNum) {
        super(login, password, name, surname);
        this.phoneNum = phoneNum;
    }

    @Override
    public String toString() {
        return name + " " + surname;
    }
}
