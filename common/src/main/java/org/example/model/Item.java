package org.example.model;

import org.example.enums.ItemCondition;

public class Item extends Entity {
    private String name;
    private String description;
    private double startingPrice;
    private ItemCondition condition;
    private int sellerId;

    public Item() {}

    public Item(int id, String name, String description, double startingPrice, ItemCondition condition, int sellerId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startingPrice = startingPrice;
        this.condition = condition;
        this.sellerId = sellerId;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getStartingPrice() { return startingPrice; }
    public void setStartingPrice(double startingPrice) { this.startingPrice = startingPrice; }

    public ItemCondition getCondition() { return condition; }
    public void setCondition(ItemCondition condition) { this.condition = condition; }

    public int getSellerId() { return sellerId; }
    public void setSellerId(int sellerId) { this.sellerId = sellerId; }
}
