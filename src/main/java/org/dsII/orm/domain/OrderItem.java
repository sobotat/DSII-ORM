package org.dsII.orm.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class OrderItem {
    private int orderItemId;
    private final Order order;
    private final MenuItem menuItem;
    @Setter private int count;
    @Setter private String state;
    private final User createdBy;

    public OrderItem(int orderItemId, Order order, MenuItem menuItem, int count, String state, User createdBy) {
        this.orderItemId = orderItemId;
        this.order = order;
        this.menuItem = menuItem;
        this.count = count;
        this.state = state;
        this.createdBy = createdBy;
    }

    // Setters
    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
    }
}
