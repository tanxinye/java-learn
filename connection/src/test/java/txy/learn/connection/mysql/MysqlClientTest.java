package txy.learn.connection.mysql;

import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class MysqlClientTest {

    private static MysqlClient mysqlClient;

    @BeforeClass
    public static void setUp() {
        mysqlClient = new MysqlClient("127.0.0.1", 3306, "root", "xinye", "xy_db");
    }

    @Test
    public void generateRandomData() {
        Connection conn = null;
        try {
            conn = mysqlClient.openConn();

            String sql = "insert into t_order (quantity,price,trade_date) values (?,?,?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            long now = System.currentTimeMillis();
            Random random = new Random();
            for (int i = 0; i < 1000000; i++) {
                int quantity = random.nextInt(1000);
                double price = random.nextDouble() * 1000;
                String tradeDate = "20210810";

                stmt.setInt(1, quantity);
                stmt.setDouble(2, price);
                stmt.setString(3, tradeDate);
                stmt.addBatch();
            }
            stmt.executeBatch();
            System.out.println(System.currentTimeMillis() - now);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                conn = null;
            }
        }

    }

    @Test
    public void updateData() {
        Connection conn = null;
        try {
            conn = mysqlClient.openConn();

            Statement stmt = conn.createStatement();
            Thread.sleep(1000);
            stmt.executeUpdate("update t_order set price = 300 where id = 100");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                conn = null;
            }
        }
    }

    @Test
    public void transaction() {
        Connection conn = null;
        try {
            conn = mysqlClient.openConn();
            conn.setAutoCommit(false);

            Statement stmt = conn.createStatement();
            stmt.execute("select * from t_order where id = 100 for update");
            stmt.execute("update t_order set price = 900 where id = 100");
            conn.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                conn = null;
            }
        }

    }

    @Test
    public void someUpdate() throws InterruptedException {
        new Thread(this::updateData).start();
        new Thread(this::transaction).start();
        Thread.sleep(15000);
    }

}