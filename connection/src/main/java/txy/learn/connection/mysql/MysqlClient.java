package txy.learn.connection.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MysqlClient {

    private String URL;
    private String DB_NAME;
    private volatile static MysqlClient client;

    public static MysqlClient getInstance(String host, int port, String username, String password, String db) {
        if (client == null) {
            synchronized (MysqlClient.class) {
                if (client == null) {
                    client = new MysqlClient(host, port, username, password, db);
                }
            }
        }
        return client;
    }

    public MysqlClient(String host, int port, String username, String password, String db) {
        DB_NAME = db;
        URL = String.format("jdbc:mysql://%s:%d/%s?" +
                        "user=%s&password=%s&characterEncoding=utf-8&useUnicode=true&useSSL=false",
                host, port, db, username, password);
    }

    public Connection openConn() throws SQLException {
        return DriverManager.getConnection(URL);
    }

}
