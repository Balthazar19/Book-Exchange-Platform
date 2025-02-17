package com.example.model;

import com.example.model.enums.Demographic;

public class Manga extends Publication{

    private String illustrator;
    private String originalLanguage;
    private int volumeNumber;
    private Demographic demographic;
    private boolean isColor;

    public Manga(String title, String author, String illustrator, String originalLanguage, int volumeNumber, Demographic demographic, boolean isColor) {
        super(title, author);
        this.illustrator = illustrator;
        this.originalLanguage = originalLanguage;
        this.volumeNumber = volumeNumber;
        this.demographic = demographic;
        this.isColor = isColor;
    }

    @Override
    public String toString() {
        return author + ": " + title;
    }
}
