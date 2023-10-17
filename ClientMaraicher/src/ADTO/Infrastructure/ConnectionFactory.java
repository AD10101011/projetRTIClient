package ADTO.Infrastructure;

import java.sql.*;
public class ConnectionFactory {

    private static String mdp;
    private static String user;
    private static String url;


    public ConnectionFactory(String DriverName,String mdp, String user, String url) {
        try {
            Class.forName(DriverName);
            this.mdp = mdp;
            this.user = user;
            this.url = url;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static Connection createConnection() {
        try {
            return DriverManager.getConnection(url, user, mdp);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
