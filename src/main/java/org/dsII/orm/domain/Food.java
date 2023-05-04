package org.dsII.orm.domain;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class Food {
    private int foodId;
    private FoodType foodType;
    private String name;
    private String description;
    private String allergens;
    private double cost;

    public Food(int foodId, FoodType foodType, String name, String description, String allergens, double cost) {
        this.foodId = foodId;
        this.foodType = foodType;
        this.name = name;
        this.description = description;
        this.allergens = allergens;
        this.cost = cost;
    }

    // Setters
    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    @Getter
    @ToString
    public static class FoodType {
        private final int typeId;
        private final String name;

        public FoodType(int typeId, String name) {
            this.typeId = typeId;
            this.name = name;
        }
    }
}
