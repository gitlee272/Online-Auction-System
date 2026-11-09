package org.example.dto;

import org.example.enums.ItemCondition;
import java.io.Serializable;

public class CreateAuctionDTO implements Serializable {
    private String itemName;
    private String itemDescription;
    private double startingPrice;
    private ItemCondition condition;
    private String category;
    private int sellerId;
    private String extraInfo1; // brand / artist / modelName
    private double extraInfo2; // warrantyMonths / creationYear / mileage
    private String startTime;
    private String endTime;

    public CreateAuctionDTO() {}

    public CreateAuctionDTO(
            String itemName,
            String itemDescription,
            double startingPrice,
            ItemCondition condition,
            String category,
            int sellerId,
            String extraInfo1,
            double extraInfo2,
            String startTime,
            String endTime
    ) {
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.startingPrice = startingPrice;
        this.condition = condition;
        this.category = category;
        this.sellerId = sellerId;
        this.extraInfo1 = extraInfo1;
        this.extraInfo2 = extraInfo2;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getItemDescription() { return itemDescription; }
    public void setItemDescription(String itemDescription) { this.itemDescription = itemDescription; }

    public double getStartingPrice() { return startingPrice; }
    public void setStartingPrice(double startingPrice) { this.startingPrice = startingPrice; }

    public ItemCondition getCondition() { return condition; }
    public void setCondition(ItemCondition condition) { this.condition = condition; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getSellerId() { return sellerId; }
    public void setSellerId(int sellerId) { this.sellerId = sellerId; }

    public String getExtraInfo1() { return extraInfo1; }
    public void setExtraInfo1(String extraInfo1) { this.extraInfo1 = extraInfo1; }

    public double getExtraInfo2() { return extraInfo2; }
    public void setExtraInfo2(double extraInfo2) { this.extraInfo2 = extraInfo2; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
}

