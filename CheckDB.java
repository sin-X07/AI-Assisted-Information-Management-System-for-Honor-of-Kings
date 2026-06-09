import java.sql.*;
public class CheckDB {
    public static void main(String[] args) throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:hok_data.db");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' ORDER BY name");
        while (rs.next()) {
            String table = rs.getString("name");
            stmt.execute("SELECT COUNT(*) FROM " + table);
            ResultSet r2 = stmt.getResultSet();
            r2.next();
            System.out.println(table + ": " + r2.getInt(1) + " rows");
        }
        conn.close();
    }
}
