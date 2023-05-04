package org.dsII.orm.db;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dsII.orm.domain.Food;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

public class FoodGateway implements Gateway<Food> {
    private static final Logger logger = LogManager.getLogger(FoodGateway.class.getName());

    @Override
    public Food find(int id) {
        Food food = null;

        try (PreparedStatement statement = Gateway.DBConnection.getConnection().prepareStatement("SELECT F.name, F.description, F.allergens, F.cost, T.type_id, T.name "
                                                                                                   + "FROM FOOD F JOIN TYPE T on F.TYPE_ID = T.TYPE_ID WHERE F.food_id = ?;")){
            statement.setInt(1, id);
            try(ResultSet resultSet = statement.executeQuery()){

                if(resultSet.next()) {
                    // Food
                    String name = resultSet.getString(1);
                    String description = resultSet.getString(2);
                    String allergens = resultSet.getString(3);
                    double cost = resultSet.getDouble(4);

                    // Type
                    int typeId = resultSet.getInt(5);
                    String typeName = resultSet.getString(6);

                    food = new Food(id, new Food.FoodType(typeId, typeName), name, description, allergens, cost);
                    statement.close();
                }
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, "Food DB exception :> " + e.getSQLState());
        }

        return food;
    }

    public LinkedList<Food> findAllFoods(){
        LinkedList<Food> foods = new LinkedList<>();

        try (Statement statement = Gateway.DBConnection.getConnection().createStatement()){
            try(ResultSet resultSet = statement.executeQuery("SELECT F.FOOD_ID, F.name, F.description, F.allergens, F.cost, T.type_id, T.name FROM FOOD F JOIN TYPE T ON F.TYPE_ID = T.TYPE_ID ")){

                while (resultSet.next()) {
                    // Food
                    int foodId = resultSet.getInt(1);
                    String name = resultSet.getString(2);
                    String description = resultSet.getString(3);
                    String allergens = resultSet.getString(4);
                    double cost = resultSet.getDouble(5);

                    // Type
                    int typeId = resultSet.getInt(6);
                    String typeName = resultSet.getString(7);

                    foods.add( new Food(foodId, new Food.FoodType(typeId, typeName), name, description, allergens, cost));
                }
                statement.close();
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, "Food DB exception :> " + e.getSQLState());
        }

        return foods;
    }

    @Override
    public boolean create(Food obj) {
        try (PreparedStatement preparedStatement = Gateway.DBConnection.getConnection().prepareStatement("INSERT INTO FOOD ( FOOD_ID, TYPE_ID, NAME, DESCRIPTION, ALLERGENS, COST) VALUES ((SELECT COALESCE(MAX(FOOD_ID), 0) + 1 FROM FOOD),?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setInt( 1, obj.getFoodType().getTypeId());
            preparedStatement.setString( 2, obj.getName());
            preparedStatement.setString( 3, obj.getDescription());
            preparedStatement.setString( 4, obj.getAllergens());
            preparedStatement.setDouble( 5, obj.getCost());

            preparedStatement.execute();
            try(ResultSet resultSet = preparedStatement.getGeneratedKeys()){

                if (resultSet.next()) {
                    // Food
                    obj.setFoodId(resultSet.getInt(1));
                }
                preparedStatement.close();
                return true;
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, "Food DB exception :> " + e.getSQLState());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(Food obj) {
        try (PreparedStatement preparedStatement = Gateway.DBConnection.getConnection().prepareStatement("UPDATE FOOD SET TYPE_ID = ?, NAME = ?, DESCRIPTION = ?, ALLERGENS = ?, COST = ? WHERE FOOD_ID = ?;")){
            preparedStatement.setInt(1, obj.getFoodType().getTypeId());
            preparedStatement.setString( 2, obj.getName());
            preparedStatement.setString( 3, obj.getDescription());
            preparedStatement.setString(4, obj.getAllergens());
            preparedStatement.setDouble(5, obj.getCost());
            preparedStatement.setInt(6, obj.getFoodId());

            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            logger.log(Level.ERROR, "Food DB exception :> " + e.getSQLState());
        }
        return false;
    }

    @Override
    public boolean delete(Food obj) {
        try (PreparedStatement preparedStatement = Gateway.DBConnection.getConnection().prepareStatement("DELETE FROM FOOD WHERE FOOD_ID = ?")){
            preparedStatement.setInt(1, obj.getFoodId());

            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            logger.log(Level.ERROR, "Food DB exception :> " + e.getSQLState());
        }
        return false;
    }
}
