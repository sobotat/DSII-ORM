package org.dsII.orm.db;

import oracle.jdbc.proxy.annotation.Pre;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dsII.orm.domain.Reservation;
import org.dsII.orm.domain.Table;
import org.dsII.orm.domain.User;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;

public class TableGateway implements Gateway<Table> {
    private static final Logger logger = LogManager.getLogger(TableGateway.class.getName());

    @Override
    public Table find(int id) throws SQLException {
        Table table = null;

        try (PreparedStatement statement = Gateway.DBConnection.getConnection().prepareStatement("SELECT T.TABLE_ID, T.CAPACITY FROM \"TABLE\" T WHERE T.TABLE_ID = ?")){
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
            //logger.log(Level.ERROR, "Table DB exception :> " + e.getSQLState());
            throw e;
        }

        return table;
    }

    // Function T003 – List of Tables
    public LinkedList<Table> listOfTables() throws SQLException {
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
            //logger.log(Level.ERROR, "Table DB exception :> " + e.getSQLState());
            throw e;
        }

        return tables;
    }

    // Function T007 – List of Reservation
    public LinkedList<Reservation> listOfReservation(Table obj) throws SQLException {
        LinkedList<Reservation> reservations = new LinkedList<>();

        try (PreparedStatement preparedStatement = Gateway.DBConnection.getConnection().prepareStatement("SELECT DISTINCT R.RESERVATION_ID, R.ON_NAME, R.TELEPHONE, R.TIME_START, R.TIME_END, R.CREATED_DATE, " +
                                                                                                "U.USER_ID, U.FIRST_NAME, U.LAST_NAME, U.BORN_DATE, U.EMAIL, UR.ROLE_ID, UR.NAME, UR.DESCRIPTION, U.ACTIVE " +
                                                                                             "FROM RESERVATION R " +
                                                                                             "JOIN TABLE_RESERVATION TR on R.RESERVATION_ID = TR.RESERVATION_ID " +
                                                                                             "JOIN \"USER\" U ON U.USER_ID = R.CREATED_BY " +
                                                                                             "JOIN ROLE UR on U.ROLE_ID = UR.ROLE_ID " +
                                                                                             "WHERE TR.TABLE_ID = ? ")){

            preparedStatement.setInt(1, obj.getTableId());
            try(ResultSet resultSet = preparedStatement.executeQuery()){

                if (resultSet.next()) {
                    int reservationId = resultSet.getInt(1);
                    String onName = resultSet.getString(2);
                    String telephone = resultSet.getString(3);
                    LocalDateTime timeStart = resultSet.getObject(4, LocalDateTime.class);
                    LocalDateTime timeEnd = resultSet.getObject(5, LocalDateTime.class);
                    LocalDateTime createdDate = resultSet.getObject(6, LocalDateTime.class);

                    int userId = resultSet.getInt("USER_ID");
                    String userFN = resultSet.getString("FIRST_NAME");
                    String userLN = resultSet.getString("LAST_NAME");
                    LocalDate userBornDate = resultSet.getDate("BORN_DATE").toLocalDate();
                    String userEmail = resultSet.getString("EMAIL");
                    boolean userActive = resultSet.getBoolean("ACTIVE");

                    int userRoleId = resultSet.getInt("ROLE_ID");
                    String userRoleName = resultSet.getString("NAME");
                    String userRoleDesc = resultSet.getString("DESCRIPTION");
                    User.UserRole userRole = new User.UserRole(userRoleId, userRoleName, userRoleDesc);
                    User createdBy = new User(userId, userFN, userLN, userBornDate, userEmail, "", userRole, userActive);

                    reservations.add(new Reservation(reservationId, onName, telephone, timeStart, timeEnd, createdDate, createdBy));
                }
                preparedStatement.close();
            }
        } catch (SQLException e) {
            //logger.log(Level.ERROR, "Table DB exception :> " + e.getSQLState());
            throw e;
        }

        return reservations;
    }

    // Function T006 – Is Reserved
    public boolean isReserved(Table obj) throws SQLException {

        try (PreparedStatement preparedStatement = Gateway.DBConnection.getConnection().prepareStatement("SELECT COUNT(TABLE_RESERVATION.RESERVATION_ID) " +
                                                                                                             "FROM TABLE_RESERVATION " +
                                                                                                             "JOIN RESERVATION ON TABLE_RESERVATION.RESERVATION_ID = RESERVATION.RESERVATION_ID " +
                                                                                                             "WHERE TABLE_ID = ? AND CURRENT_DATE BETWEEN RESERVATION.TIME_START AND RESERVATION.TIME_END ")){
            preparedStatement.setInt(1, obj.getTableId());
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                if(resultSet.next()){
                    return (resultSet.getInt(1) > 0 ? true : false);
                }
            }
        } catch (SQLException e) {
            //logger.log(Level.ERROR, "Table DB exception :> " + e.getSQLState());
            throw e;
        }
        return false;
    }

    // Function T008 – List of Reserved Tables for day
    public LinkedList<Table.TableReservedForDay> listOfReservedTablesForDay(LocalDate date) throws SQLException {
        LinkedList<Table.TableReservedForDay> output = new LinkedList<>();

        try (PreparedStatement preparedStatement = Gateway.DBConnection.getConnection().prepareStatement("SELECT T.TABLE_ID, T.CAPACITY, LIST.RESERVED_THAT_DAY, LIST.RESERVED_THAT_MONTH " +
                                                                                                             "FROM TABLE(LISTOFRESERVEDTABLESFORDAY(?)) LIST " +
                                                                                                             "JOIN \"TABLE\" T ON T.TABLE_ID = LIST.TABLE_ID")){

            preparedStatement.setDate(1, Date.valueOf(date));
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                if(resultSet.next()){
                    int tableId = resultSet.getInt("TABLE_ID");
                    int tableCapacity = resultSet.getInt("CAPACITY");
                    Table table = new Table(tableId, tableCapacity);

                    int thatDay = resultSet.getInt("RESERVED_THAT_DAY");
                    int thatMonth = resultSet.getInt("RESERVED_THAT_MONTH");

                    Table.TableReservedForDay tableReservedForDay = new Table.TableReservedForDay(table, thatDay, thatMonth);
                    output.add(tableReservedForDay);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, "Table DB exception :> " + e.getSQLState());
            throw e;
        }
        return output;
    }

    // Function T001 – New Table
    @Override
    public boolean create(Table obj) throws SQLException {

        try (PreparedStatement preparedStatement = Gateway.DBConnection.getConnection().prepareStatement("INSERT INTO \"TABLE\" (TABLE_ID, CAPACITY) VALUES ((SELECT COALESCE(MAX(TABLE_ID), 0) + 1 FROM \"TABLE\"), ?)")){
            Gateway.DBConnection.getConnection().setAutoCommit(false);

            preparedStatement.setInt( 1, obj.getCapacity());

            preparedStatement.execute();
            Gateway.DBConnection.getConnection().commit();
        } catch (SQLException e) {
            //logger.log(Level.ERROR, "Table DB exception :> " + e.getSQLState());
            Gateway.DBConnection.getConnection().rollback();
            throw e;
        }
        return true;
    }

    // Function T002 – Update Table
    @Override
    public boolean update(Table obj) throws SQLException {
        try (PreparedStatement preparedStatement = Gateway.DBConnection.getConnection().prepareStatement("UPDATE \"TABLE\" T SET T.CAPACITY = ? WHERE T.TABLE_ID = ?;")){
            Gateway.DBConnection.getConnection().setAutoCommit(false);

            preparedStatement.setInt( 1, obj.getCapacity());
            preparedStatement.setInt(3, obj.getTableId());

            preparedStatement.execute();
            Gateway.DBConnection.getConnection().commit();
        } catch (SQLException e) {
            //logger.log(Level.ERROR, "Table DB exception :> " + e.getSQLState());
            Gateway.DBConnection.getConnection().rollback();
            throw e;
        }
        return true;
    }

    // Function T004 – Change Reserve Table
    public boolean changeReserveTable(Reservation reservation, LinkedList<Table> tables) throws SQLException {

        try (PreparedStatement preparedStatement = Gateway.DBConnection.getConnection().prepareStatement("INSERT INTO RESERVATION (RESERVATION_ID, ON_NAME, TELEPHONE, TIME_START, TIME_END, CREATED_DATE, CREATED_BY) VALUES ((SELECT COALESCE(MAX(RESERVATION_ID), 0) + 1 FROM RESERVATION),?,?,?,?,?,?)")){
            Gateway.DBConnection.getConnection().setAutoCommit(false);

            preparedStatement.setString(1, reservation.getOnName());
            preparedStatement.setString(2, reservation.getTelephone());
            preparedStatement.setObject(3, reservation.getTimeStart());
            preparedStatement.setObject(4, reservation.getTimeEnd());
            preparedStatement.setObject(5, reservation.getCreatedDate());
            preparedStatement.setInt(6, reservation.getCreatedBy().getUserId());

            preparedStatement.execute();
        } catch (SQLException e) {
            //logger.log(Level.ERROR, "Table DB exception :> " + e.getSQLState());
            Gateway.DBConnection.getConnection().rollback();
            throw e;
        }

        if(tables.isEmpty())
            return true;

        try (PreparedStatement preparedStatement = Gateway.DBConnection.getConnection().prepareStatement("INSERT INTO TABLE_RESERVATION (TABLE_ID, RESERVATION_ID) VALUES (?, (SELECT MAX(RESERVATION.RESERVATION_ID) FROM RESERVATION))")){
            for (Table table: tables) {
                preparedStatement.setInt(1, table.getTableId());
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
        }catch (SQLException e){
            //logger.log(Level.ERROR, "Table DB exception :> " + e.getSQLState());
            Gateway.DBConnection.getConnection().rollback();
            throw e;
        }
        return true;
    }

    // Function	T005 – Delete Table
    @Override
    public boolean delete(Table obj) throws SQLException {
        try (PreparedStatement preparedStatement = Gateway.DBConnection.getConnection().prepareStatement("UPDATE \"ORDER\" SET TABLE_ID = null WHERE TABLE_ID = ?")){
            Gateway.DBConnection.getConnection().setAutoCommit(false);
            preparedStatement.setInt(1, obj.getTableId());
            preparedStatement.execute();
        } catch (SQLException e) {
            //logger.log(Level.ERROR, "Delete 1 Table DB exception :> " + e.getSQLState());
            Gateway.DBConnection.getConnection().rollback();
            throw  e;
        }

        try (PreparedStatement preparedStatement = Gateway.DBConnection.getConnection().prepareStatement("DELETE FROM TABLE_RESERVATION WHERE TABLE_ID = ?")){
            preparedStatement.setInt(1, obj.getTableId());
            preparedStatement.execute();
        }catch (SQLException e){
            //logger.log(Level.ERROR, "Delete 2 Table DB exception :> " + e.getSQLState());
            Gateway.DBConnection.getConnection().rollback();
            throw e;
        }

        try (PreparedStatement preparedStatement = Gateway.DBConnection.getConnection().prepareStatement("DELETE FROM \"TABLE\" T WHERE T.TABLE_ID = ?")){
            preparedStatement.setInt(1, obj.getTableId());
            preparedStatement.execute();
            Gateway.DBConnection.getConnection().commit();
        } catch (SQLException e) {
            //logger.log(Level.ERROR, "Delete 3 Table DB exception :> " + e.getSQLState());
            Gateway.DBConnection.getConnection().rollback();
            throw e;
        }
        return true;
    }
}
