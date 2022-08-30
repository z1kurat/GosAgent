package com.example.gosagentnewrelease;

public class Card {
    private final int imageID;
    private final int type;
    private final String description;

    public Card(int image, String description, int type) {
        this.imageID = image;
        this.type = type;
        this.description = description;
    }

    public int getImageID() { return imageID; }
    public int getType() {return  type; }
    public String getDescription() { return description; }
}