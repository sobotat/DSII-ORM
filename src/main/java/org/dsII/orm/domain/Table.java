package org.dsII.orm.domain;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Table {
    private int tableId;
    private int capacity;

    public Table(int tableId, int capacity) {
        this.tableId = tableId;
        this.capacity = capacity;
    }

    // Setters
    public void setTableId(int tableId) {
        this.tableId = tableId;
    }
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
