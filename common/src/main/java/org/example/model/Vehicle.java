package org.example.model;

import org.example.enums.ItemCondition;

public class Vehicle extends Item {
    private String modelName;
    private double mileage;

    public Vehicle() {
        super();
    }

    public Vehicle(int id, String name, String description, double startingPrice, ItemCondition condition, int sellerId, String modelName, double mileage) {
        super(id, name, description, startingPrice, condition, sellerId);
        this.modelName = modelName;
        this.mileage = mileage;
    }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public double getMileage() { return mileage; }
    public void setMileage(double mileage) { this.mileage = mileage; }
}
