package org.dsII.orm.db;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dsII.orm.domain.Menu;

import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedList;

public class MenuGateway implements Gateway<Menu> {
    private static final Logger logger = LogManager.getLogger(MenuGateway.class.getName());

    @Override
    public Menu find(int id) {
        Menu menu = null;

        try (PreparedStatement statement = Gateway.DBConnection.getConnection().prepareStatement("SELECT MENU_ID, MENU.DATE, CREATED_DATE FROM MENU WHERE MENU_ID = ?")){
            statement.setInt(1, id);
            try(ResultSet resultSet = statement.executeQuery()){

                if(resultSet.next()) {
                    // Menu
                    int menuId = resultSet.getInt(1);
                    Date date = resultSet.getDate(2);
                    Date createdDate = resultSet.getDate(3);

                    menu = new Menu( menuId, date.toLocalDate(), createdDate.toLocalDate());
                }
                statement.close();
            }

        } catch (SQLException e) {
            logger.log(Level.ERROR, "Order DB exception :> " + e.getSQLState());
        }

        return menu;
    }

    public Menu findForDay(LocalDate date){
        Menu menu = null;

        try (PreparedStatement statement = Gateway.DBConnection.getConnection().prepareStatement("SELECT MENU_ID, CREATED_DATE FROM MENU WHERE MENU.DATE = ?")){
            statement.setDate(1, Date.valueOf(date));
            try(ResultSet resultSet = statement.executeQuery()){

                if(resultSet.next()) {
                    // Menu
                    int menuId = resultSet.getInt(1);
                    Date createdDate = resultSet.getDate(2);

                    menu = new Menu( menuId, date, createdDate.toLocalDate());
                }
                statement.close();
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, "Menu DB exception :> " + e.getSQLState());
        }

        return menu;
    }

    public LinkedList<Menu> findAllMenus(){
        LinkedList<Menu> menus = new LinkedList<>();

        try (Statement statement = Gateway.DBConnection.getConnection().createStatement()){
            try(ResultSet resultSet = statement.executeQuery("SELECT MENU_ID, MENU.DATE, CREATED_DATE FROM MENU")){

                while (resultSet.next()) {
                    // Menu
                    int menuId = resultSet.getInt(1);
                    Date date = resultSet.getDate(2);
                    Date createdDate = resultSet.getDate(3);

                    menus.add( new Menu( menuId, date.toLocalDate(), createdDate.toLocalDate()));
                }
                statement.close();
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, "Menu DB exception :> " + e.getSQLState());
        }

        return menus;
    }

    @Override
    public boolean create(Menu obj) {
        try (PreparedStatement preparedStatement = Gateway.DBConnection.getConnection().prepareStatement("INSERT INTO MENU ( MENU_ID, MENU.DATE, CREATED_DATE) VALUES ((SELECT COALESCE(MAX(MENU_ID),0) + 1 FROM MENU),?, ?);", Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setDate( 1, Date.valueOf(obj.getDate()));
            preparedStatement.setDate(2, Date.valueOf(obj.getCreatedDate()));

            preparedStatement.execute();
            try(ResultSet resultSet = preparedStatement.getGeneratedKeys()){

                if (resultSet.next()) {
                    // Menu
                    obj.setMenuId(resultSet.getInt(1));
                }
                preparedStatement.close();
                return true;
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, "Menu DB exception :> " + e.getSQLState());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(Menu obj) {
        try (PreparedStatement preparedStatement = Gateway.DBConnection.getConnection().prepareStatement("UPDATE `restaurantos-db`.`menu` SET `date` = ?, `created_date` = ? WHERE `menu_id` = ?;")){
            preparedStatement.setDate( 1, Date.valueOf(obj.getDate()));
            preparedStatement.setDate(2, Date.valueOf(obj.getCreatedDate()));
            preparedStatement.setInt(3, obj.getMenuId());

            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            logger.log(Level.ERROR, "Menu DB exception :> " + e.getSQLState());
        }
        return false;
    }

    @Override
    public boolean delete(Menu obj) {
        try (PreparedStatement preparedStatement = Gateway.DBConnection.getConnection().prepareStatement("DELETE FROM `restaurantos-db`.`menu` WHERE `menu_id` = ?")){
            preparedStatement.setInt(1, obj.getMenuId());
            preparedStatement.execute();

            return true;
        } catch (SQLException e) {
            logger.log(Level.ERROR, "Menu DB exception :> " + e.getSQLState());
        }
        return false;
    }
}
