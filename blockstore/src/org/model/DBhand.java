package org.model;



import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Created by Stan on 6/14/2017.
 */
public class DBhand {
    private static final Logger logger = Logger.getLogger(DBhand.class.getName());
    public static DBhand instance;
    //private static String url = "jdbc:postgresql://localhost/XLAnalysis";
    private static String url = "jdbc:postgresql://localhost/ibd";

    private static String user = "mangesh";
    private static String password = "mangesh";
    private static String driver = "org.postgresql.Driver";
    private static boolean autoCommit = true;
    private static Connection con;

    public boolean getAutoCommit() {
        return autoCommit;
    }

    public Connection getConnection() {
        return con;
    }

    public void disableAutoCommit() {
        autoCommit=false;
        try {
            con.setAutoCommit(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void enableAutoCommit() {
        autoCommit=true;
        try {
            con.setAutoCommit(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void connectDB() {
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(
                    url, user, password);
            logger.info("Connected to Database");
            instance = this;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void commit() {
        try {
            if (!autoCommit)
                con.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
