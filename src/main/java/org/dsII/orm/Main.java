package org.dsII.orm;

import lombok.extern.log4j.Log4j2;
import org.dsII.orm.db.*;
import org.dsII.orm.domain.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;

@Log4j2
public class Main {
    public static void main(String[] args){
        boolean status = Gateway.DBConnection.setConnectionString(Files.readString("db-config.json"));

        if (!status) {
            log.error("Config Failed");
            return;
        }
        try {
            log.info("\033[1;32m\nTABLE\033[0m");
            TableGateway tableGateway = new TableGateway();
            //tableGateway.create(new Table(0, 4));

            LocalDateTime today = LocalDateTime.now();
            LocalDateTime todayIn2359 = LocalDateTime.of(today.getYear(), today.getMonth(), today.getDayOfMonth(), 23, 59);
            Reservation reservation = new Reservation(0, "Tomas Sobota", "123456789", today, todayIn2359, today, new User(1));
            LinkedList<Table> reservedTables = new LinkedList<>();
            reservedTables.add(new Table(1));
            reservedTables.add(new Table(3));
            tableGateway.changeReserveTable(reservation, reservedTables);

            LinkedList<Table> tables = tableGateway.listOfTables();
            for (Table table: tables) {
                log.info(table);
                log.info("Has Reservation " + tableGateway.isReserved(table));
                log.info("Reservations " + tableGateway.listOfReservation(table) + "\n");
            }

            log.info(tableGateway.listOfReservedTablesForDay(LocalDate.now()));

            log.info("\033[1;32m\nORDER ITEM\033[0m");
            OrderItemGateway orderItemGateway = new OrderItemGateway();

            try {
                orderItemGateway.create(new OrderItem(0, new Order(2), new MenuItem(2), 10, "Ordered", new User(1)));
                log.info("\033[1;32mAdded Order Item\033[0m");
            } catch (SQLException e) {
                if (e.getSQLState().equals("65000"))
                    log.error("\033[1;33mNot Enough Food in Menu\033[0m");
            }

            log.info("\033[1;33mOrder 1 Items \033[0m" + orderItemGateway.listOfOrder_ItemsOfOrder(1) + "\n");
            log.info("\033[1;33mOrder 2 Items \033[0m" + orderItemGateway.listOfOrder_ItemsOfOrder(2) + "\n");
            log.info("\033[1;33mOrder 3 Items \033[0m" + orderItemGateway.listOfOrder_ItemsOfOrder(3) + "\n");
        }catch (SQLException e){
            e.printStackTrace();
        }

        Gateway.DBConnection.close();
        log.info("Exiting ...");
    }
}