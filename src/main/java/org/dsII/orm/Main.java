package org.dsII.orm;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.dsII.orm.db.FoodGateway;
import org.dsII.orm.db.Gateway;
import org.dsII.orm.db.TableGateway;
import org.dsII.orm.db.UserGateway;
import org.dsII.orm.domain.Food;
import org.dsII.orm.domain.Table;
import org.dsII.orm.domain.User;

import java.util.LinkedList;

@Log4j2
public class Main {
    public static void main(String[] args) {
        boolean status = Gateway.DBConnection.setConnectionString(Files.readString("db-config.json"));

        if (!status) {
            log.error("Config Failed");
            return;
        }

        User user = new UserGateway().findByEmailAndPassword("manager@gmail.com", "1234");

        log.info("\033[1;32mUSER\033[0m");
        if(user != null) {
            log.info(user.toString());
        }

        LinkedList<Food> foods = new FoodGateway().findAllFoods();

        log.info("\033[1;32mFOOD\033[0m");
        for (Food food: foods) {
            log.info(food.toString());
        }


        log.info("\033[1;32mTABLE\033[0m");
//        new TableGateway().create(new Table(0, 8));
//        new TableGateway().create(new Table(0, 4));
        //new TableGateway().create(new Table(0, 2));

        LinkedList<Table> tables = new TableGateway().findAllTables();
        for (Table table: tables) {
            log.info(table.toString());
        }

        Gateway.DBConnection.close();
        log.info("Exiting ...");
    }
}