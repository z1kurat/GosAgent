package com.example.gosagentnewrelease.adapter;

import android.graphics.drawable.Drawable;

import com.example.gosagentnewrelease.custom.types.LotData;

import java.util.List;

public class InformationPages {
    private LotData selectedLot;
    public int type;
    private Drawable imageLot;
    private List<String> imageLink;
    private String nameOfLot;
    private String textLot;
    private String description;
    private String linkOnLot;
    private String priceLot;
    private String addressesLot;

    public List<String> getImageLink() { return imageLink; }

    public void setImageLinks(List<String> imageLinks) { this.imageLink = imageLinks; }

    public Drawable getImage() { return imageLot; }

    public void setImage(Drawable image) { this.imageLot = image; }

    public String getNameOfLot() { return nameOfLot; }

    public void setNameOfLot(String nameOfLot) { this.nameOfLot = nameOfLot; }

    public int getType() { return type; }

    public void setType(int type) { this.type = type; }

    public String getTextLot() {
        return textLot;
    }

    public void setTextLot(String textLot) {
        this.textLot = textLot;
    }

    public LotData getSelectedLot() { return selectedLot; }

    public void setSelectedLot(LotData selectedLot) {
        this.selectedLot = selectedLot;
    }

    public String getAddresses() { return addressesLot; }

    public void setAddresses(String addresses) {
        this.addressesLot = addresses;
    }

    public String getPrice() {
        return priceLot;
    }

    public void setPrice(String price) {
        this.priceLot = price;
    }

    public String getLink() {
        return linkOnLot;
    }

    public void setLink(String link) {
        this.linkOnLot = link;
    }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }
}
