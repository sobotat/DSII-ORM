package org.dsII.orm.db;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dsII.orm.domain.Order;
import org.dsII.orm.domain.Table;
import org.dsII.orm.domain.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedList;

public class OrderGateway implements Gateway<Order> {
    private static final Logger logger = LogManager.getLogger(OrderGateway.class.getName());

    @Override
    public Order find(int id) {
        Order order = null;

        // Database
        try (PreparedStatement statement = Gateway.DBConnection.getConnection().prepareStatement("SELECT TABLE_ID, CREATED_BY, STATE, CREATED_BY FROM \"ORDER\" WHERE ORDER_ID = ?;")){
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()){

                if(resultSet.next()) {
                    // Order
                    int tableId = resultSet.getInt(2);
                    Date createdDate = resultSet.getDate(3);
                    String state = resultSet.getString(4);
                    User createdBy = null;//resultSet.getInt(5);

                    Table table = new TableGateway().find(tableId);

                    order = new Order( id, table, createdDate.toLocalDate(), state, createdBy);
                }
                statement.close();
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, "Order DB exception :> " + e.getSQLState());
        }

        return order;
    }

    public LinkedList<Order> findAllForDay(LocalDate date) {
        LinkedList<Order> orders = new LinkedList<>();

        try (PreparedStatement statement = Gateway.DBConnection.getConnection().prepareStatement("SELECT ORDER_ID, TABLE_ID, STATE, CREATED_BY FROM \"ORDER\" WHERE CREATED_DATE = ?;")){
            Date dateTmp = Date.valueOf(date);
            statement.setDate(1, dateTmp);
            try(ResultSet resultSet = statement.executeQuery()){

                while (resultSet.next()) {
                    // Order
                    int orderId = resultSet.getInt(1);
                    int tableId = resultSet.getInt(2);
                    String state = resultSet.getString(4);
                    User createdBy = null;//resultSet.getInt(5);

                    Table table = new TableGateway().find(tableId);

                    orders.add( new Order( orderId, table, LocalDate.now(), state, createdBy));
                }
                statement.close();
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, "Order DB exception :> " + e.getSQLState());
        }

        return orders;
    }

    public LinkedList<Order> findAllOrders() {
        LinkedList<Order> orders = new LinkedList<>();

        try (Statement statement = Gateway.DBConnection.getConnection().createStatement()){
            try(ResultSet resultSet = statement.executeQuery("SELECT ORDER_ID, TABLE_ID, CREATED_DATE, STATE, CREATED_BY FROM \"ORDER\";")){

                while (resultSet.next()) {
                    // Order
                    int orderId = resultSet.getInt(1);
                    int tableId = resultSet.getInt(2);
                    Date date = resultSet.getDate(3);
                    String state = resultSet.getString(4);
                    User createdBy = null;//resultSet.getInt(5);

                    Table table = new TableGateway().find(tableId);
                    orders.add( new Order( orderId, table, date.toLocalDate(), state, createdBy));
                }
                statement.close();
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, "Order DB exception :> " + e.getSQLState());
        }

        return orders;
    }

    @Override
    public boolean create(Order obj) {

        try (PreparedStatement preparedStatement = Gateway.DBConnection.getConnection().prepareStatement("INSERT INTO \"ORDER\" ( ORDER_ID, TABLE_ID, CREATED_DATE, STATE, CREATED_BY) VALUES ((SELECT COALESCE(MAX(ORDER_ID),0) + 1 FROM \"ORDER\"), ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setInt( 1, obj.getTable().getTableId());
            preparedStatement.setDate(2, Date.valueOf(obj.getCreatedDate()));
            preparedStatement.setString( 3, obj.getState());
            preparedStatement.setInt(4, obj.getCreatedBy().getUserId());

            preparedStatement.execute();
            try(ResultSet resultSet = preparedStatement.getGeneratedKeys()){

                if (resultSet.next()) {
                    // Order
                    obj.setOrderId(resultSet.getInt(1));
                }
                preparedStatement.close();
            }
            return true;
        } catch (SQLException e) {
            logger.log(Level.ERROR, "Order DB exception :> " + e.getSQLState());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(Order obj) {
        try (PreparedStatement preparedStatement = Gateway.DBConnection.getConnection().prepareStatement("UPDATE \"ORDER\" SET TABLE_ID = ?, CREATED_BY = ?, STATE = ? WHERE ORDER_ID = ?;")){
            preparedStatement.setInt( 1, obj.getTable().getTableId());
            preparedStatement.setDate(2, Date.valueOf(obj.getCreatedDate()));
            preparedStatement.setString( 3, obj.getState());
            preparedStatement.setInt(4, obj.getOrderId());

            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            logger.log(Level.ERROR, "Order DB exception :> " + e.getSQLState());
        }
        return false;
    }

    @Override
    public boolean delete(Order obj) {
        try (PreparedStatement preparedStatement = Gateway.DBConnection.getConnection().prepareStatement("DELETE FROM \"ORDER\" WHERE ORDER_ID = ?")){
            preparedStatement.setInt(1, obj.getOrderId());
            preparedStatement.execute();

            return true;
        } catch (SQLException e) {
            logger.log(Level.ERROR, "Order DB exception :> " + e.getSQLState());
        }
        return false;
    }
}
