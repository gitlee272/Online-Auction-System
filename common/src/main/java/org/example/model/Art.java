package org.example.model;

import org.example.enums.ItemCondition;

public class Art extends Item {
    private String artist;
    private int creationYear;

    public Art() {
        super();
    }

    public Art(int id, String name, String description, double startingPrice, ItemCondition condition, int sellerId, String artist, int creationYear) {
        super(id, name, description, startingPrice, condition, sellerId);
        this.artist = artist;
        this.creationYear = creationYear;
    }

    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }

    public int getCreationYear() { return creationYear; }
    public void setCreationYear(int creationYear) { this.creationYear = creationYear; }
}