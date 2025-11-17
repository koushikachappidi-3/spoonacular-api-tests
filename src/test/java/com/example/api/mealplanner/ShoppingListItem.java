package com.example.api.mealplanner;

// A simple POJO (Plain Old Java Object)
// Jackson will automatically convert this into a JSON object:
// { "item": "...", "aisle": "...", "parse": true }
public class ShoppingListItem {

    private String item;
    private String aisle;
    private boolean parse;

    // Default constructor (needed by Jackson)
    public ShoppingListItem() {}

    public ShoppingListItem(String item, String aisle, boolean parse) {
        this.item = item;
        this.aisle = aisle;
        this.parse = parse;
    }

    // Getters (needed by Jackson)
    public String getItem() {
        return item;
    }

    public String getAisle() {
        return aisle;
    }

    public boolean isParse() {
        return parse;
    }
}
