package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DB Connection helper.
 * 修改這裡的 url/user/password 即可連接你的 MySQL。
 */
public class Tool {

    private static final String URL = "jdbc:mysql://localhost:3306/travel_agency_shop?useSSL=false&serverTimezone=Asia/Taipei&characterEncoding=utf8";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";

    public static Connection getDb() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("找不到 MySQL Driver", e);
        } catch (SQLException e) {
            throw new RuntimeException("MySQL 連線失敗，請確認資料庫/帳密/Port", e);
        }
    }
}
