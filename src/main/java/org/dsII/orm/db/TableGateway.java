package org.dsII.orm.db;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dsII.orm.domain.Table;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

public class TableGateway implements Gateway<Table> {
    private static final Logger logger = LogManager.getLogger(TableGateway.class.getName());

    @Override
    public Table find(int id) {
        Table table = null;

        try (PreparedStatement statement = Gateway.DBConnection.getConnection().prepareStatement("SELECT T.TABLE_ID, T.CAPACITY FROM \"TABLE\" T WHERE T.TABLE_ID = ?;")){
            statement.setInt(1, id);
            try(ResultSet resultSet = statement.executeQuery()){

                if(resultSet.next()) {
                    // Table
                    int tableId = resultSet.getInt(1);
                    int capacity = resultSet.getInt(2);

                    table = new Table(tableId, capacity);
                }
                statement.close();
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, "Table DB exception :> " + e.getSQLState());
        }

        return table;
    }

    public LinkedList<Table> findAllTables(){
        LinkedList<Table> tables = new LinkedList<>();

        try (Statement statement = Gateway.DBConnection.getConnection().createStatement()){
            try(ResultSet resultSet = statement.executeQuery("SELECT T.TABLE_ID, T.CAPACITY FROM \"TABLE\" T ")){

                while (resultSet.next()) {
                    // Table
                    int tableId = resultSet.getInt(1);
                    int capacity = resultSet.getInt(2);

                    tables.add( new Table(tableId, capacity));
                }
                statement.close();
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, "Table DB exception :> " + e.getSQLState());
            e.printStackTrace();
        }

        return tables;
    }

    @Override
    public boolean create(Table obj) {

        try (PreparedStatement preparedStatement = Gateway.DBConnection.getConnection().prepareStatement("INSERT INTO \"TABLE\" (TABLE_ID, CAPACITY) VALUES ((SELECT COALESCE(MAX(TABLE_ID), 0) + 1 FROM \"TABLE\"), ?)", Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setInt( 1, obj.getCapacity());

            preparedStatement.execute();
            try(ResultSet resultSet = preparedStatement.getGeneratedKeys()){

                if (resultSet.next()) {
                    // Order
                    obj.setTableId(resultSet.getInt(1));
                }
                preparedStatement.close();
            }
            return true;
        } catch (SQLException e) {
            logger.log(Level.ERROR, "Table DB exception :> " + e.getSQLState());
        }
        return false;
    }

    @Override
    public boolean update(Table obj) {
        try (PreparedStatement preparedStatement = Gateway.DBConnection.getConnection().prepareStatement("UPDATE \"TABLE\" T SET T.CAPACITY = ? WHERE T.TABLE_ID = ?;")){
            preparedStatement.setInt( 1, obj.getCapacity());
            preparedStatement.setInt(3, obj.getTableId());

            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            logger.log(Level.ERROR, "Table DB exception :> " + e.getSQLState());
        }
        return false;
    }

    @Override
    public boolean delete(Table obj) {
        try (PreparedStatement preparedStatement = Gateway.DBConnection.getConnection().prepareStatement("DELETE FROM \"TABLE\" T WHERE T.TABLE_ID = ?")){
            preparedStatement.setInt(1, obj.getTableId());
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            logger.log(Level.ERROR, "Table DB exception :> " + e.getSQLState());
        }
        return false;
    }
}
