package util;
import java.sql.*;
public class DbQuickCheck {
    public static void main(String[] args) throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:hok_data.db");
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT count(*), (SELECT count(*) FROM equipments), (SELECT count(*) FROM teams) FROM heroes");
        if (rs.next()) System.out.println("heroes=" + rs.getInt(1) + " equipments=" + rs.getInt(2) + " teams=" + rs.getInt(3));
        conn.close();
    }
}