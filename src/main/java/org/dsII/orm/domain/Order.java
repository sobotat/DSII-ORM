package org.dsII.orm.domain;

import lombok.Getter;
import lombok.ToString;
import org.dsII.orm.db.OrderItemGateway;
import org.dsII.orm.db.UserGateway;

import java.time.LocalDate;
import java.util.LinkedList;

@Getter
@ToString
public class Order {

    private int orderId;
    private Table table;
    private LocalDate createdDate;
    private String state;
    private User createdBy;

    public Order(int orderId, Table table, LocalDate createdDate, String state, User createdBy) {
        this.orderId = orderId;
        this.table = table;
        this.createdDate = createdDate;
        this.state = state;
        this.createdBy = createdBy;
    }

    public double getCost(){
        double cost = 0;

        OrderItemGateway orderItemGateway = new OrderItemGateway();
        LinkedList<OrderItem> orderItems = orderItemGateway.findAllForOrder(this);

        for (OrderItem item : orderItems){
            if(!item.getState().equals("Canceled"))
                cost += item.getMenuItem().getFood().getCost() * item.getCount();
        }
        return cost;
    }

    // Setters
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
}
