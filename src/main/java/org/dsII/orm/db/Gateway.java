package org.dsII.orm.db;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

public interface Gateway<T> {

    T find(int id);
    boolean create(T obj);
    boolean update(T obj);
    boolean delete(T obj);

    class DBConnection{
        private static final Logger logger = LogManager.getLogger(DBConnection.class.getName());
        private static String connectionStr;
        private static String userStr;
        private static String passwordStr;
        private static Connection connection = null;
        private static Timer timer;

        public static Connection getConnection() throws SQLException {
            resetTimer();

            if(connectionStr.equals(""))
                throw new SQLException("Connection String is Empty");

            if(connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(connectionStr, userStr, passwordStr);
                logger.log(Level.INFO, "DB Connection Opened");
                return connection;
            }

            return connection;
        }

        public static void close(){
            try {
                if(connection != null) {
                    connection.close();
                    connection = null;

                    logger.log(Level.INFO, "DB Connection Closed");
                }

                timer = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private static void resetTimer(){
            if(timer != null)
                timer.cancel();
            timer = new Timer(true);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    close();
                }
            }, 30000);
        }

        public static boolean setConnectionString(String config) {
            try {
                JSONObject jsonObject = new JSONObject(config);

                String host = jsonObject.getString("host");
                String port = jsonObject.getString("port");
                String sid = jsonObject.getString("sid");

                connectionStr = "jdbc:oracle:thin:@" + host + ":" + port + ":" + sid;

                userStr = jsonObject.getString("user");
                passwordStr = jsonObject.getString("password");
            }catch (Exception e) {
                connectionStr = "";
                userStr = "";
                passwordStr = "";
                return false;
            }
            return true;
        }
    }
}
