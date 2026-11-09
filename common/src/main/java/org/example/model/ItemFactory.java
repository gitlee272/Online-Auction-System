package org.example.model;

import org.example.enums.ItemCondition;

public class ItemFactory {

    public static Item createItem(
            String category,
            int id,
            String name,
            String description,
            double startingPrice,
            ItemCondition condition,
            int sellerId,
            String extra1,
            double extra2
    ) {
        if (category == null) {
            return new Item(
                    id, name, description,
                    startingPrice, condition, sellerId
            );
        }

        String cat = category.trim().toUpperCase();

        if ("ELECTRONICS".equals(cat)) {
            return new Electronics(
                    id, name, description,
                    startingPrice, condition, sellerId,
                    extra1, (int) extra2
            );
        } else if ("ART".equals(cat)) {
            return new Art(
                    id, name, description,
                    startingPrice, condition, sellerId,
                    extra1, (int) extra2
            );
        } else if ("VEHICLE".equals(cat)) {
            return new Vehicle(
                    id, name, description,
                    startingPrice, condition, sellerId,
                    extra1, extra2
            );
        } else {
            return new Item(
                    id, name, description,
                    startingPrice, condition, sellerId
            );
        }
    }
}