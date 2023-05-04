package org.dsII.orm.db;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dsII.orm.domain.Food;
import org.dsII.orm.domain.Menu;
import org.dsII.orm.domain.MenuItem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

public class MenuItemGateway implements Gateway<MenuItem> {
    private static final Logger logger = LogManager.getLogger(MenuItemGateway.class.getName());

    @Override
    public MenuItem find(int id) {
        MenuItem menuItem = null;

        try (PreparedStatement statement = Gateway.DBConnection.getConnection().prepareStatement("SELECT MENU_ITEM_ID, MENU_ID, FOOD_ID, COUNT, COST FROM MENU_ITEM WHERE MENU_ITEM_ID = ?;")){
            statement.setInt(1, id);
            try(ResultSet resultSet = statement.executeQuery()){

                if(resultSet.next()) {
                    // Menu Item
                    int menuItemId = resultSet.getInt(1);
                    int menu_id = resultSet.getInt(2);
                    Food food = new FoodGateway().find(resultSet.getInt(3));
                    int count = resultSet.getInt(4);
                    double cost = resultSet.getDouble(5);

                    Menu menu = new MenuGateway().find(menu_id);

                    menuItem = new MenuItem( menuItemId, menu, food, count, cost);
                }
                statement.close();
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, "MenuItem DB exception :> " + e.getSQLState());
        }

        return menuItem;
    }

    public LinkedList<MenuItem> findAllForMenu(Menu menu){
        LinkedList<MenuItem> menuItems = new LinkedList<>();

        try (PreparedStatement statement = Gateway.DBConnection.getConnection().prepareStatement("SELECT MENU_ITEM_ID, FOOD_ID, COUNT, COST FROM MENU_ITEM WHERE MENU_ID = ?;")){
            statement.setInt(1, menu.getMenuId());
            try(ResultSet resultSet = statement.executeQuery()){

                while (resultSet.next()) {
                    // Menu Item
                    int menuItemId = resultSet.getInt(1);
                    Food food = new FoodGateway().find(resultSet.getInt(2));
                    int count = resultSet.getInt(3);
                    double cost = resultSet.getDouble(4);

                    menuItems.add( new MenuItem( menuItemId, menu, food, count, cost));
                }
                statement.close();
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, "MenuItem DB exception :> " + e.getSQLState());
        }

        return menuItems;
    }

    @Override
    public boolean create(MenuItem obj) {
        try (PreparedStatement preparedStatement = Gateway.DBConnection.getConnection().prepareStatement("INSERT INTO MENU_ITEM ( MENU_ITEM_ID, MENU_ID, FOOD_ID, COUNT, COST) VALUES ((SELECT COALESCE(MAX(MENU_ITEM_ID), 0) + 1 FROM MENU_ITEM), ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setInt( 1, obj.getMenu().getMenuId());
            preparedStatement.setInt(2, obj.getFood().getFoodId());
            preparedStatement.setInt( 3, obj.getCount());
            preparedStatement.setDouble( 4, obj.getCost());

            preparedStatement.execute();
            try(ResultSet resultSet = preparedStatement.getGeneratedKeys()){

                if (resultSet.next()) {
                    // Order
                    obj.setMenuItemId(resultSet.getInt(1));
                }
                preparedStatement.close();
                return true;
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, "MenuItem DB exception :> " + e.getSQLState());
        }
        return false;
    }

    @Override
    public boolean update(MenuItem obj) {
        try (PreparedStatement preparedStatement = Gateway.DBConnection.getConnection().prepareStatement("UPDATE MENU_ITEM SET FOOD_ID = ?, COUNT = ?, COST = ? WHERE MENU_ITEM_ID = ?;")){
            preparedStatement.setInt(2, obj.getFood().getFoodId());
            preparedStatement.setInt( 3, obj.getCount());
            preparedStatement.setDouble( 4, obj.getCost());
            preparedStatement.setInt(5, obj.getMenuItemId());

            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            logger.log(Level.ERROR, "MenuItem DB exception :> " + e.getSQLState());
        }
        return false;
    }

    @Override
    public boolean delete(MenuItem obj) {
        try (PreparedStatement preparedStatement = Gateway.DBConnection.getConnection().prepareStatement("DELETE FROM MENU_ITEM WHERE MENU_ITEM_ID = ?")){
            preparedStatement.setInt(1, obj.getMenuItemId());

            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            logger.log(Level.ERROR, "MenuItem DB exception :> " + e.getSQLState());
        }
        return false;
    }
}
