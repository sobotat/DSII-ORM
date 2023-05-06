package org.dsII.orm.db;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dsII.orm.domain.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedList;

public class OrderItemGateway implements Gateway<OrderItem> {
    private static final Logger logger = LogManager.getLogger(OrderItemGateway.class.getName());

    @Override
    public OrderItem find(int id) throws SQLException {
        OrderItem orderItem = null;

        // Database
        try (PreparedStatement statement = Gateway.DBConnection.getConnection().prepareStatement("SELECT OI.ORDER_ITEM_ID OI_ORDER_ITEM_ID, OI.COUNT OI_COUNT, OI.STATE OI_STATE, " +
                                                                                                            "O.ORDER_ID O_ORDER_ID, O.CREATED_DATE O_CREATED_DATE, O.STATE O_STATE, " +
                                                                                                            "T.TABLE_ID T_TABLE_ID, T.CAPACITY T_CAPACITY, " +
                                                                                                            "MI.MENU_ITEM_ID MI_MENU_ITEM_ID, MI.COUNT MI_COUNT, MI.COST MI_COST, " +
                                                                                                            "M.MENU_ID M_MENU_ID,M.\"DATE\" M_DATE, M.CREATED_DATE M_CREATED_DATE," +
                                                                                                            "F.FOOD_ID F_FOOD_ID, F.NAME F_NAME, F.DESCRIPTION F_DESCRIPTION, F.ALLERGENS F_ALLERGENS, F.COST F_COST, " +
                                                                                                            "TY.TYPE_ID TY_TYPE_ID, TY.NAME TY_NAME, " +
                                                                                                            "U1.USER_ID U1_USER_ID, U1.FIRST_NAME U1_FN, U1.LAST_NAME U1_LN, U1.BORN_DATE U1_BORN_DATE, U1.EMAIL U1_EMAIL, U1.ACTIVE U1_ACTIVE, " +
                                                                                                            "R1.ROLE_ID R1_ROLE_ID, R1.NAME R1_NAME, R1.DESCRIPTION R1_DESCRIPTION, " +
                                                                                                            "U2.USER_ID U2_USER_ID, U2.FIRST_NAME U2_FN, U2.LAST_NAME U2_LN, U2.BORN_DATE U2_BORN_DATE, U2.EMAIL U2_EMAIL, U2.ACTIVE U2_ACTIVE, " +
                                                                                                            "R2.ROLE_ID R2_ROLE_ID, R2.NAME R2_NAME, R2.DESCRIPTION R2_DESCRIPTION " +
                                                                                                      "FROM ORDER_ITEM OI " +
                                                                                                      "JOIN \"ORDER\" O on OI.ORDER_ID = O.ORDER_ID " +
                                                                                                      "JOIN \"TABLE\" T ON T.TABLE_ID = O.TABLE_ID " +
                                                                                                      "JOIN MENU_ITEM MI on OI.MENU_ITEM_ID = MI.MENU_ITEM_ID " +
                                                                                                      "JOIN MENU M on M.MENU_ID = MI.MENU_ID " +
                                                                                                      "JOIN FOOD F on MI.FOOD_ID = F.FOOD_ID " +
                                                                                                      "JOIN TYPE TY on F.TYPE_ID = TY.TYPE_ID " +
                                                                                                      "JOIN \"USER\" U1 ON OI.CREATED_BY = U1.USER_ID " +
                                                                                                      "JOIN ROLE R1 ON R1.ROLE_ID = U1.ROLE_ID " +
                                                                                                      "JOIN \"USER\" U2 ON O.CREATED_BY = U2.USER_ID " +
                                                                                                      "JOIN ROLE R2 ON R2.ROLE_ID = U2.ROLE_ID " +
                                                                                                      "WHERE ORDER_ITEM_ID = ?")){
            statement.setInt(1, id);
            try(ResultSet resultSet = statement.executeQuery()){
                if(resultSet.next()) {
                    // Order Item
                    int orderItemId = resultSet.getInt("OI_ORDER_ITEM_ID");
                    int count = resultSet.getInt("OI_COUNT");
                    String state = resultSet.getString("OI_STATE");

                    // Order
                    int orderId = resultSet.getInt("O_ORDER_ID");
                    LocalDate orderCreatedDate = resultSet.getDate("O_CREATED_DATE").toLocalDate();
                    String orderState = resultSet.getString("O_STATE");

                    int tableId = resultSet.getInt("T_TABLE_ID");
                    int tableCapacity = resultSet.getInt("T_CAPACITY");
                    Table table = new Table(tableId, tableCapacity);

                    int u2Id = resultSet.getInt("U2_USER_ID");
                    String u2FN = resultSet.getString("U2_FN");
                    String u2LN = resultSet.getString("U2_LN");
                    LocalDate u2BornDate = resultSet.getDate("U2_BORN_DATE").toLocalDate();
                    String u2Email = resultSet.getString("U2_EMAIL");
                    boolean u2Active = resultSet.getBoolean("U2_ACTIVE");

                    int u2RoleId = resultSet.getInt("R2_ROLE_ID");
                    String u2RoleName = resultSet.getString("R2_NAME");
                    String u2RoleDesc = resultSet.getString("R2_DESCRIPTION");
                    User.UserRole u2Role = new User.UserRole(u2RoleId, u2RoleName, u2RoleDesc);

                    User orderCreatedBy = new User(u2Id, u2FN, u2LN, u2BornDate, u2Email, "", u2Role, u2Active);


                    Order order = new Order( orderId, table, orderCreatedDate, orderState, orderCreatedBy);

                    // Menu
                    int menuId = resultSet.getInt("M_MENU_ID");
                    LocalDate menuDate = resultSet.getDate("M_DATE").toLocalDate();
                    LocalDate menuCreatedDate = resultSet.getDate("M_CREATED_DATE").toLocalDate();
                    Menu menu = new Menu(menuId, menuDate, menuCreatedDate);

                    // Food
                    int foodId = resultSet.getInt("F_FOOD_ID");
                    String foodName = resultSet.getString("F_NAME");
                    String foodDesc = resultSet.getString("F_DESCRIPTION");
                    String foodAllergens = resultSet.getString("F_ALLERGENS");
                    double foodCost = resultSet.getDouble("F_COST");

                    int foodTypeId = resultSet.getInt("TY_TYPE_ID");
                    String foodTypeName = resultSet.getString("TY_NAME");
                    Food.FoodType foodType = new Food.FoodType(foodTypeId, foodTypeName);

                    Food food = new Food(foodId, foodType, foodName, foodDesc, foodAllergens, foodCost);

                    // Menu Item
                    int menuItemId = resultSet.getInt("MI_MENU_ITEM_ID");
                    int menuItemCount = resultSet.getInt("MI_COUNT");
                    int menuItemCost = resultSet.getInt("MI_COST");
                    MenuItem menuItem = new MenuItem(menuItemId, menu, food, menuItemCount, menuItemCost);

                    // User ORDER ITEM
                    int u1Id = resultSet.getInt("U1_USER_ID");
                    String u1FN = resultSet.getString("U1_FN");
                    String u1LN = resultSet.getString("U1_LN");
                    LocalDate u1BornDate = resultSet.getDate("U1_BORN_DATE").toLocalDate();
                    String u1Email = resultSet.getString("U1_EMAIL");
                    boolean u1Active = resultSet.getBoolean("U1_ACTIVE");

                    int u1RoleId = resultSet.getInt("R1_ROLE_ID");
                    String u1RoleName = resultSet.getString("R1_NAME");
                    String u1RoleDesc = resultSet.getString("R1_DESCRIPTION");
                    User.UserRole u1Role = new User.UserRole(u1RoleId, u1RoleName, u1RoleDesc);

                    User orderItemCreatedBy = new User(u1Id, u1FN, u1LN, u1BornDate, u1Email, "", u1Role, u1Active);


                    orderItem = new OrderItem( orderItemId, order, menuItem, count, state, orderItemCreatedBy);
                }
                statement.close();
            }
        } catch (SQLException e) {
            //logger.log(Level.ERROR, "OrderItem DB exception :> " + e.getSQLState());
            throw e;
        }
        return orderItem;
    }

    // Function	O006 – List of Order_Items of Order
    public LinkedList<OrderItem> listOfOrder_ItemsOfOrder(int orderId) throws SQLException {
        LinkedList<OrderItem> orderItems = new LinkedList<>();

        // Database
        try (PreparedStatement statement = Gateway.DBConnection.getConnection().prepareStatement("SELECT OI.ORDER_ITEM_ID OI_ORDER_ITEM_ID, OI.COUNT OI_COUNT, OI.STATE OI_STATE, " +
                                                                                                            "O.CREATED_DATE O_CREATED_DATE, O.STATE O_STATE, " +
                                                                                                            "T.TABLE_ID T_TABLE_ID, T.CAPACITY T_CAPACITY, " +
                                                                                                            "MI.MENU_ITEM_ID MI_MENU_ITEM_ID, MI.COUNT MI_COUNT, MI.COST MI_COST, " +
                                                                                                            "M.MENU_ID M_MENU_ID,M.\"DATE\" M_DATE, M.CREATED_DATE M_CREATED_DATE," +
                                                                                                            "F.FOOD_ID F_FOOD_ID, F.NAME F_NAME, F.DESCRIPTION F_DESCRIPTION, F.ALLERGENS F_ALLERGENS, F.COST F_COST, " +
                                                                                                            "TY.TYPE_ID TY_TYPE_ID, TY.NAME TY_NAME, " +
                                                                                                            "U1.USER_ID U1_USER_ID, U1.FIRST_NAME U1_FN, U1.LAST_NAME U1_LN, U1.BORN_DATE U1_BORN_DATE, U1.EMAIL U1_EMAIL, U1.ACTIVE U1_ACTIVE, " +
                                                                                                            "R1.ROLE_ID R1_ROLE_ID, R1.NAME R1_NAME, R1.DESCRIPTION R1_DESCRIPTION, " +
                                                                                                            "U2.USER_ID U2_USER_ID, U2.FIRST_NAME U2_FN, U2.LAST_NAME U2_LN, U2.BORN_DATE U2_BORN_DATE, U2.EMAIL U2_EMAIL, U2.ACTIVE U2_ACTIVE, " +
                                                                                                            "R2.ROLE_ID R2_ROLE_ID, R2.NAME R2_NAME, R2.DESCRIPTION R2_DESCRIPTION " +
                                                                                                     "FROM ORDER_ITEM OI " +
                                                                                                     "JOIN \"ORDER\" O on OI.ORDER_ID = O.ORDER_ID " +
                                                                                                     "JOIN \"TABLE\" T ON T.TABLE_ID = O.TABLE_ID " +
                                                                                                     "JOIN MENU_ITEM MI on OI.MENU_ITEM_ID = MI.MENU_ITEM_ID " +
                                                                                                     "JOIN MENU M on M.MENU_ID = MI.MENU_ID " +
                                                                                                     "JOIN FOOD F on MI.FOOD_ID = F.FOOD_ID " +
                                                                                                     "JOIN TYPE TY on F.TYPE_ID = TY.TYPE_ID " +
                                                                                                     "JOIN \"USER\" U1 ON OI.CREATED_BY = U1.USER_ID " +
                                                                                                     "JOIN ROLE R1 ON R1.ROLE_ID = U1.ROLE_ID " +
                                                                                                     "JOIN \"USER\" U2 ON O.CREATED_BY = U2.USER_ID " +
                                                                                                     "JOIN ROLE R2 ON R2.ROLE_ID = U2.ROLE_ID " +
                                                                                                     "WHERE OI.ORDER_ID = ?")){
            statement.setInt(1, orderId);
            try(ResultSet resultSet = statement.executeQuery()){

                while (resultSet.next()) {
                    // Order Item
                    int orderItemId = resultSet.getInt("OI_ORDER_ITEM_ID");
                    int count = resultSet.getInt("OI_COUNT");
                    String state = resultSet.getString("OI_STATE");

                    // Order
                    LocalDate orderCreatedDate = resultSet.getDate("O_CREATED_DATE").toLocalDate();
                    String orderState = resultSet.getString("O_STATE");

                    int tableId = resultSet.getInt("T_TABLE_ID");
                    int tableCapacity = resultSet.getInt("T_CAPACITY");
                    Table table = new Table(tableId, tableCapacity);

                    int u2Id = resultSet.getInt("U2_USER_ID");
                    String u2FN = resultSet.getString("U2_FN");
                    String u2LN = resultSet.getString("U2_LN");
                    LocalDate u2BornDate = resultSet.getDate("U2_BORN_DATE").toLocalDate();
                    String u2Email = resultSet.getString("U2_EMAIL");
                    boolean u2Active = resultSet.getBoolean("U2_ACTIVE");

                    int u2RoleId = resultSet.getInt("R2_ROLE_ID");
                    String u2RoleName = resultSet.getString("R2_NAME");
                    String u2RoleDesc = resultSet.getString("R2_DESCRIPTION");
                    User.UserRole u2Role = new User.UserRole(u2RoleId, u2RoleName, u2RoleDesc);

                    User orderCreatedBy = new User(u2Id, u2FN, u2LN, u2BornDate, u2Email, "", u2Role, u2Active);

                    Order order = new Order( orderId, table, orderCreatedDate, orderState, orderCreatedBy);

                    // Menu
                    int menuId = resultSet.getInt("M_MENU_ID");
                    LocalDate menuDate = resultSet.getDate("M_DATE").toLocalDate();
                    LocalDate menuCreatedDate = resultSet.getDate("M_CREATED_DATE").toLocalDate();
                    Menu menu = new Menu(menuId, menuDate, menuCreatedDate);

                    // Food
                    int foodId = resultSet.getInt("F_FOOD_ID");
                    String foodName = resultSet.getString("F_NAME");
                    String foodDesc = resultSet.getString("F_DESCRIPTION");
                    String foodAllergens = resultSet.getString("F_ALLERGENS");
                    double foodCost = resultSet.getDouble("F_COST");

                    int foodTypeId = resultSet.getInt("TY_TYPE_ID");
                    String foodTypeName = resultSet.getString("TY_NAME");
                    Food.FoodType foodType = new Food.FoodType(foodTypeId, foodTypeName);

                    Food food = new Food(foodId, foodType, foodName, foodDesc, foodAllergens, foodCost);

                    // Menu Item
                    int menuItemId = resultSet.getInt("MI_MENU_ITEM_ID");
                    int menuItemCount = resultSet.getInt("MI_COUNT");
                    int menuItemCost = resultSet.getInt("MI_COST");
                    MenuItem menuItem = new MenuItem(menuItemId, menu, food, menuItemCount, menuItemCost);

                    // User ORDER ITEM
                    int u1Id = resultSet.getInt("U1_USER_ID");
                    String u1FN = resultSet.getString("U1_FN");
                    String u1LN = resultSet.getString("U1_LN");
                    LocalDate u1BornDate = resultSet.getDate("U1_BORN_DATE").toLocalDate();
                    String u1Email = resultSet.getString("U1_EMAIL");
                    boolean u1Active = resultSet.getBoolean("U1_ACTIVE");

                    int u1RoleId = resultSet.getInt("R1_ROLE_ID");
                    String u1RoleName = resultSet.getString("R1_NAME");
                    String u1RoleDesc = resultSet.getString("R1_DESCRIPTION");
                    User.UserRole u1Role = new User.UserRole(u1RoleId, u1RoleName, u1RoleDesc);

                    User orderItemCreatedBy = new User(u1Id, u1FN, u1LN, u1BornDate, u1Email, "", u1Role, u1Active);

                    OrderItem orderItem = new OrderItem( orderItemId, order, menuItem, count, state, orderItemCreatedBy);
                    orderItems.add(orderItem);
                }
                statement.close();
            }
        } catch (SQLException e) {
            //logger.log(Level.ERROR, "OrderItem DB exception :> " + e.getSQLState());
            throw e;
        }

        return orderItems;
    }

    // Function O002 – New Order Item
    @Override
    public boolean create(OrderItem obj) throws SQLException {

        try (PreparedStatement preparedStatement = Gateway.DBConnection.getConnection().prepareStatement("CALL NEWORDERITEM(?, ?, ?, ?)")){
            Gateway.DBConnection.getConnection().setAutoCommit(false);

            preparedStatement.setInt( 1, obj.getOrder().getOrderId());
            preparedStatement.setInt(2, obj.getMenuItem().getMenuItemId());
            preparedStatement.setInt( 3, obj.getCount());
            preparedStatement.setInt(4, obj.getCreatedBy().getUserId());

            preparedStatement.execute();
            Gateway.DBConnection.getConnection().commit();
        } catch (SQLException e) {
            //logger.log(Level.ERROR, "OrderItem DB exception :> " + e.getSQLState());
            Gateway.DBConnection.getConnection().rollback();
            throw e;
        }
        return true;
    }

    // Function	O004 – Update Order Item
    // Následně trigger by zkontroloval a upravil objednávku na zaplacenou
    @Override
    public boolean update(OrderItem obj) throws SQLException {

        try (PreparedStatement preparedStatement = Gateway.DBConnection.getConnection().prepareStatement("UPDATE ORDER_ITEM SET COUNT = ?, STATE = ? WHERE ORDER_ITEM_ID = ?;")){
            Gateway.DBConnection.getConnection().setAutoCommit(false);

            preparedStatement.setInt( 1, obj.getCount());
            preparedStatement.setString(2, obj.getState());
            preparedStatement.setInt(3, obj.getOrderItemId());

            preparedStatement.execute();
            Gateway.DBConnection.getConnection().commit();
        } catch (SQLException e) {
            //logger.log(Level.ERROR, "OrderItem DB exception :> " + e.getSQLState());
            Gateway.DBConnection.getConnection().rollback();
            throw e;
        }
        return true;
    }

    // Function O012 – Set Order_Item Status
    public boolean setOrder_ItemStatus(OrderItem obj, String state) throws SQLException {

        try (PreparedStatement preparedStatement = Gateway.DBConnection.getConnection().prepareStatement("UPDATE ORDER_ITEM SET STATE = ? WHERE ORDER_ITEM_ID = ?;")){
            Gateway.DBConnection.getConnection().setAutoCommit(false);

            obj.setState(state);
            preparedStatement.setString(1, obj.getState());
            preparedStatement.setInt(2, obj.getOrderItemId());

            preparedStatement.execute();
            Gateway.DBConnection.getConnection().commit();
        } catch (SQLException e) {
            //logger.log(Level.ERROR, "OrderItem DB exception :> " + e.getSQLState());
            Gateway.DBConnection.getConnection().rollback();
            throw e;
        }
        return true;
    }

    // Function O007 –  Pay Order_Items
    public boolean payOrder_Items(Order order) throws SQLException {
        try (PreparedStatement preparedStatement = Gateway.DBConnection.getConnection().prepareStatement("UPDATE ORDER_ITEM SET STATE = 'Paid' WHERE ORDER_ID = ? AND (STATE = 'Served' OR STATE = 'Canceled')")){
            Gateway.DBConnection.getConnection().setAutoCommit(false);

            preparedStatement.setInt(1, order.getOrderId());

            preparedStatement.execute();
            Gateway.DBConnection.getConnection().commit();
        } catch (SQLException e) {
            //logger.log(Level.ERROR, "OrderItem DB exception :> " + e.getSQLState());
            Gateway.DBConnection.getConnection().rollback();
            throw e;
        }
        return true;
    }

    // Function O008 – Cancel Order_Item
    @Override
    public boolean delete(OrderItem obj) throws SQLException {
        try (PreparedStatement preparedStatement = Gateway.DBConnection.getConnection().prepareStatement("UPDATE ORDER_ITEM SET STATE = 'Canceled'  WHERE ORDER_ITEM_ID = ?;")){
            Gateway.DBConnection.getConnection().setAutoCommit(false);

            preparedStatement.setInt( 1, obj.getOrderItemId());

            preparedStatement.execute();
            Gateway.DBConnection.getConnection().commit();
        } catch (SQLException e) {
            //logger.log(Level.ERROR, "OrderItem DB exception :> " + e.getSQLState());
            Gateway.DBConnection.getConnection().rollback();
            throw e;
        }
        return true;
    }
}
