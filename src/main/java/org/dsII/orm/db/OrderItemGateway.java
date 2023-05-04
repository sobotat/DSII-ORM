package org.dsII.orm.db;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dsII.orm.domain.MenuItem;
import org.dsII.orm.domain.Order;
import org.dsII.orm.domain.OrderItem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.LinkedList;

public class OrderItemGateway implements Gateway<OrderItem> {
    private static final Logger logger = LogManager.getLogger(OrderItemGateway.class.getName());

    @Override
    public OrderItem find(int id) {
        OrderItem orderItem = null;

        // Database
        try (PreparedStatement statement = Gateway.DBConnection.getConnection().prepareStatement("SELECT ORDER_ITEM_ID, ORDER_ID, MENU_ITEM_ID, COUNT, STATE, CREATED_BY FROM ORDER_ITEM WHERE order_item_id = ?")){
            statement.setInt(1, id);
            try(ResultSet resultSet = statement.executeQuery()){

                if(resultSet.next()) {
                    // Order Item
                    int orderItemId = resultSet.getInt(1);
                    int order_id = resultSet.getInt(2);
                    MenuItem menuItem = new MenuItemGateway().find(resultSet.getInt(3));
                    int count = resultSet.getInt(4);
                    String state = resultSet.getString(5);
                    Integer cookedById = resultSet.getInt(6);

                    Order order = new OrderGateway().find(order_id);

                    orderItem = new OrderItem( orderItemId, order, menuItem, count, state, cookedById);
                }
                statement.close();
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, "OrderItem DB exception :> " + e.getSQLState());
        }

        return orderItem;
    }

    public LinkedList<OrderItem> findAllForOrder(Order order){
        LinkedList<OrderItem> orderItems = new LinkedList<>();

        // Database
        try (PreparedStatement statement = Gateway.DBConnection.getConnection().prepareStatement("SELECT ORDER_ITEM_ID, MENU_ITEM_ID, COUNT, STATE, CREATED_BY FROM ORDER_ITEM WHERE ORDER_ID = ?")){
            statement.setInt(1, order.getOrderId());
            try(ResultSet resultSet = statement.executeQuery()){

                while (resultSet.next()) {
                    // Order Item
                    int orderItemId = resultSet.getInt(1);
                    MenuItem menuItem = new MenuItemGateway().find(resultSet.getInt(2));
                    int count = resultSet.getInt(3);
                    String state = resultSet.getString(4);
                    Integer cookedById = resultSet.getInt(5);

                    OrderItem orderItem = new OrderItem( orderItemId, order, menuItem, count, state, cookedById);
                    orderItems.add(orderItem);
                }
                statement.close();
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, "OrderItem DB exception :> " + e.getSQLState());
        }

        return orderItems;
    }

    @Override
    public boolean create(OrderItem obj) {

        try (PreparedStatement preparedStatement = Gateway.DBConnection.getConnection().prepareStatement("INSERT INTO ORDER_ITEM ( ORDER_ITEM_ID, ORDER_ID, MENU_ITEM_ID, COUNT, CREATED_BY) VALUES ((SELECT MAX(ORDER_ITEM_ID) + 1 FROM ORDER_ITEM), ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setInt( 1, obj.getOrder().getOrderId());
            preparedStatement.setInt(2, obj.getMenuItem().getMenuItemId());
            preparedStatement.setInt( 3, obj.getCount());
            preparedStatement.setInt(4, obj.getCreatedBy().getUserId());

            preparedStatement.execute();
            try(ResultSet resultSet = preparedStatement.getGeneratedKeys()){

                if (resultSet.next()) {
                    // Order
                    obj.setOrderItemId(resultSet.getInt(1));
                }
                preparedStatement.close();
            }

            return true;
        } catch (SQLException e) {
            logger.log(Level.ERROR, "OrderItem DB exception :> " + e.getSQLState());
        }
        return false;
    }

    @Override
    public boolean update(OrderItem obj) {

        try (PreparedStatement preparedStatement = Gateway.DBConnection.getConnection().prepareStatement("UPDATE ORDER_ITEM SET COUNT = ?, STATE = ? WHERE ORDER_ITEM_ID = ?;")){
            preparedStatement.setInt( 1, obj.getCount());
            preparedStatement.setString(2, obj.getState());

            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            logger.log(Level.ERROR, "OrderItem DB exception :> " + e.getSQLState());
        }
        return false;
    }

    @Override
    public boolean delete(OrderItem obj) {
        try (PreparedStatement preparedStatement = Gateway.DBConnection.getConnection().prepareStatement("DELETE FROM ORDER_ITEM WHERE ORDER_ITEM_ID = ?")){
            preparedStatement.setInt(1, obj.getOrderItemId());
            preparedStatement.execute();

            return true;
        } catch (SQLException e) {
            logger.log(Level.ERROR, "OrderItem DB exception :> " + e.getSQLState());
        }
        return false;
    }
}
