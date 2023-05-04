package org.dsII.orm.db;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dsII.orm.domain.User;

import java.sql.*;
import java.util.LinkedList;

public class UserGateway implements Gateway<User> {
    private static final Logger logger = LogManager.getLogger(UserGateway.class.getName());

    @Override
    public User find(int id) {
        User user = null;

        // Database
        try (PreparedStatement statement = Gateway.DBConnection.getConnection().prepareStatement("SELECT U.FIRST_NAME, U.LAST_NAME, U.BORN_DATE, U.EMAIL, ROLE.ROLE_ID, ROLE.NAME, ROLE.DESCRIPTION FROM \"USER\" U JOIN ROLE ON ROLE.ROLE_ID = U.ROLE_ID WHERE U.user_id = ?")){
            statement.setInt(1, id);
            try(ResultSet resultSet = statement.executeQuery()){

                if(resultSet.next()) {
                    // User
                    String first_name = resultSet.getString(1);
                    String last_name = resultSet.getString(2);
                    Date born_date = resultSet.getDate(3);
                    String email = resultSet.getString(4);
                    String password = null;

                    // Role
                    int roleId = resultSet.getInt(5);
                    String roleName = resultSet.getString(6);
                    String roleDescription = resultSet.getString(7);

                    user = new User( id, first_name, last_name, born_date.toLocalDate(), email, password, new User.UserRole(roleId, roleName, roleDescription));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, "User DB exception :> " + e.getSQLState());
        }

        return user;
    }

    public User findByEmailAndPassword(String email, String password){
        User user = null;

        // Database
        try (PreparedStatement statement = Gateway.DBConnection.getConnection().prepareStatement("SELECT U.USER_ID, U.FIRST_NAME, U.LAST_NAME, U.BORN_DATE, ROLE.ROLE_ID, ROLE.NAME, ROLE.DESCRIPTION FROM \"USER\" U JOIN ROLE ON ROLE.ROLE_ID = U.ROLE_ID WHERE U.EMAIL = ? AND U.PASSWORD = ?")){
            statement.setString( 1, email);
            statement.setString( 2, password);
            try(ResultSet resultSet = statement.executeQuery()){

                if(resultSet.next()) {
                    // User
                    int userId = resultSet.getInt(1);
                    String first_name = resultSet.getString(2);
                    String last_name = resultSet.getString(3);
                    Date born_date = resultSet.getDate(4);

                    // Role
                    int roleId = resultSet.getInt(5);
                    String roleName = resultSet.getString(6);
                    String roleDescription = resultSet.getString(7);

                    user = new User(userId, first_name, last_name, born_date.toLocalDate(), email, password, new User.UserRole(roleId, roleName, roleDescription));

                }
                statement.close();
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, "User DB exception :> " + e.getSQLState());
            e.printStackTrace();
        }

        return user;
    }

    public LinkedList<User> findAllUsers(){
        LinkedList<User> users = new LinkedList<>();

        // Database
        try (Statement statement = Gateway.DBConnection.getConnection().createStatement()){
            try(ResultSet resultSet = statement.executeQuery("SELECT U.USER_ID, U.FIRST_NAME, U.LAST_NAME, U.BORN_DATE, U.EMAIL, ROLE.ROLE_ID, ROLE.NAME, ROLE.DESCRIPTION FROM \"USER\" U JOIN ROLE ON ROLE.ROLE_ID = U.ROLE_ID")){

                while (resultSet.next()) {
                    // User
                    int userId = resultSet.getInt(1);
                    String first_name = resultSet.getString(2);
                    String last_name = resultSet.getString(3);
                    Date born_date = resultSet.getDate(4);
                    String email = resultSet.getString(5);

                    // Role
                    int roleId = resultSet.getInt(6);
                    String roleName = resultSet.getString(7);
                    String roleDescription = resultSet.getString(8);

                    User user = new User(userId, first_name, last_name, born_date.toLocalDate(), email, null, new User.UserRole(roleId, roleName, roleDescription));
                    users.add(user);
                }
                statement.close();
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, "User DB exception :> " + e.getSQLState());
        }

        return users;
    }

    public User.UserRole findUserRoleByName(String roleName){
        User.UserRole role = null;

        try (PreparedStatement statement = Gateway.DBConnection.getConnection().prepareStatement("SELECT ROLE_ID, DESCRIPTION FROM ROLE WHERE NAME = ?")){
            statement.setString(1, roleName);
            try(ResultSet resultSet = statement.executeQuery()){

                if(resultSet.next()) {
                    // Role
                    int roleId = resultSet.getInt(1);
                    String roleDescription = resultSet.getString(2);

                    role = new User.UserRole(roleId, roleName, roleDescription);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, "User DB exception :> " + e.getSQLState());
        }

        return role;
    }

    public LinkedList<User.UserRole> findAllUserRoles() {
        LinkedList<User.UserRole> userRoles = new LinkedList<>();

        try (Statement statement = Gateway.DBConnection.getConnection().createStatement()){
            try(ResultSet resultSet = statement.executeQuery("SELECT ROLE_ID, NAME, DESCRIPTION FROM ROLE")){

                while (resultSet.next()) {
                    // Role
                    int roleId = resultSet.getInt(1);
                    String roleName = resultSet.getString(2);
                    String roleDescription = resultSet.getString(3);

                    userRoles.add( new User.UserRole(roleId, roleName, roleDescription));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, "User DB exception :> " + e.getSQLState());
        }

        return userRoles;
    }

    @Override
    public boolean create(User obj) {
        try (PreparedStatement preparedStatement = Gateway.DBConnection.getConnection().prepareStatement("INSERT INTO \"USER\" ( USER_ID, FIRST_NAME, LAST_NAME, BORN_DATE, EMAIL, PASSWORD, ROLE_ID, ACTIVE) VALUES ((SELECT COALESCE(MAX(USER_ID), 0) + 1 FROM \"USER\"), ?, ?, ?, ?, ?, ?, 1);", Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setString( 1, obj.getFirstName());
            preparedStatement.setString( 2, obj.getLastName());
            preparedStatement.setDate(3, Date.valueOf(obj.getBornDate()));
            preparedStatement.setString( 4, obj.getEmail());
            preparedStatement.setString( 5, obj.getPassword());
            preparedStatement.setInt( 6, obj.getUserRole().getRoleId());

            preparedStatement.execute();
            try(ResultSet resultSet = preparedStatement.getGeneratedKeys()){

                if (resultSet.next()) {
                    // Order
                    obj.setUserId(resultSet.getInt(1));
                }
                preparedStatement.close();
            }
            return true;
        } catch (SQLException e) {
            logger.log(Level.ERROR, "User DB exception :> " + e.getSQLState());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(User obj) {
        try (PreparedStatement preparedStatement = Gateway.DBConnection.getConnection().prepareStatement("UPDATE \"USER\" U SET U.FIRST_NAME = ?, U.LAST_NAME = ?, U.BORN_DATE = ?, U.EMAIL = ?, U.PASSWORD = ?, U.ROLE_ID = ? WHERE U.USER_ID = ?;")){
            preparedStatement.setString( 1, obj.getFirstName());
            preparedStatement.setString( 2, obj.getLastName());
            preparedStatement.setDate(3, Date.valueOf(obj.getBornDate()));
            preparedStatement.setString( 4, obj.getEmail());
            preparedStatement.setString( 5, obj.getPassword());
            preparedStatement.setInt( 6, obj.getUserRole().getRoleId());
            preparedStatement.setInt(7, obj.getUserId());

            preparedStatement.execute();

            return true;
        } catch (SQLException e) {
            logger.log(Level.ERROR, "User DB exception :> " + e.getSQLState());
        }
        return true;
    }

    public boolean updateWithoutPassword(User obj) {
        try (PreparedStatement preparedStatement = Gateway.DBConnection.getConnection().prepareStatement("UPDATE \"USER\" U SET U.FIRST_NAME = ?, U.LAST_NAME = ?, U.BORN_DATE = ?, U.EMAIL = ?, U.ROLE_ID = ? WHERE U.USER_ID = ?;")){
            preparedStatement.setString( 1, obj.getFirstName());
            preparedStatement.setString( 2, obj.getLastName());
            preparedStatement.setDate(3, Date.valueOf(obj.getBornDate()));
            preparedStatement.setString( 4, obj.getEmail());
            preparedStatement.setInt( 5, obj.getUserRole().getRoleId());
            preparedStatement.setInt(6, obj.getUserId());

            preparedStatement.execute();

            return true;
        } catch (SQLException e) {
            logger.log(Level.ERROR, "User DB exception :> " + e.getSQLState());
        }
        return true;
    }

    public boolean updatePassword(String email, String password) {
        try (PreparedStatement preparedStatement = Gateway.DBConnection.getConnection().prepareStatement("UPDATE \"USER\" U SET U.PASSWORD = ? WHERE U.EMAIL = ?;")){

            preparedStatement.setString( 1, password);
            preparedStatement.setString( 2, email);

            preparedStatement.execute();

            return true;
        } catch (SQLException e) {
            logger.log(Level.ERROR, "User DB exception :> " + e.getSQLState());
        }
        return false;
    }

    @Override
    public boolean delete(User obj) {
        try (PreparedStatement preparedStatement = Gateway.DBConnection.getConnection().prepareStatement("DELETE FROM \"USER\" U WHERE U.USER_ID = ?")){
            preparedStatement.setInt(1, obj.getUserId());

            preparedStatement.execute();

            return true;
        } catch (SQLException e) {
            logger.log(Level.ERROR, "User DB exception :> " + e.getSQLState());
        }
        return false;
    }
}
