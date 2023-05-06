package org.dsII.orm.domain;

import lombok.Getter;
import lombok.ToString;
import org.dsII.orm.db.OrderItemGateway;

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

    public Order(int orderId){
        this.orderId = orderId;
    }

    public Order(int orderId, int tableId, int createdById){
        this.orderId = orderId;
        this.table = new Table(tableId);
        this.createdBy = new User(createdById);
    }

    public Order(int orderId, Table table, LocalDate createdDate, String state, User createdBy) {
        this.orderId = orderId;
        this.table = table;
        this.createdDate = createdDate;
        this.state = state;
        this.createdBy = createdBy;
    }

    // Setters
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
}
