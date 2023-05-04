package org.dsII.orm.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.dsII.orm.db.FoodGateway;

@Getter
@ToString
public class MenuItem {
    private int menuItemId;
    private Menu menu;
    private Food food;
    @Setter private int count;
    @Setter private double cost;

    public MenuItem(int menuItemId, Menu menu, Food food, int count, double cost) {
        this.menuItemId = menuItemId;
        this.menu = menu;
        this.food = food;
        this.count = count;
        this.cost = cost;
    }

    // Setters
    public void setMenuItemId(int menuItemId) {
        this.menuItemId = menuItemId;
    }
}
