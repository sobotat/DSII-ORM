package org.dsII.orm.domain;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Table {
    private int tableId;
    private int capacity;

    public Table(int tableId){
        this.tableId = tableId;
    }

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

    @Getter
    @ToString
    public class TableReservation {
        private Table table;
        private Reservation reservation;

        public TableReservation(Table table, Reservation reservation) {
            this.table = table;
            this.reservation = reservation;
        }
    }

    @Getter
    @ToString
    public static class TableReservedForDay {
        private Table table;
        private int reservedThatDay;
        private int reservedThatMonth;

        public TableReservedForDay(Table table, int reservedThatDay, int reservedThatMonth) {
            this.table = table;
            this.reservedThatDay = reservedThatDay;
            this.reservedThatMonth = reservedThatMonth;
        }
    }
}
