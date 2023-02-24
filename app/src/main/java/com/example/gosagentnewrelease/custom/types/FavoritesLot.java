package com.example.gosagentnewrelease.custom.types;

public class FavoritesLot {
    private final String _name;
    private final int _typeCard;

    public FavoritesLot(String nameLot, int typeCard) {
        _name = nameLot;
        _typeCard = typeCard;
    }

    public String get_name() {
        return _name;
    }

    public int getType() {
        return _typeCard;
    }
}
