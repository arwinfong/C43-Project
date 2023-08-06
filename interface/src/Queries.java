import java.sql.*;

public class Queries {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/c43";
    static final String DB_USER = "root";
    static final String DB_PASS = /* ur pw */;

    static String kmtoDegString(double km) {
        return Double.toString(km / 111.2);
    }

    public static void main(String[] args) throws Exception {
        Class.forName(JDBC_DRIVER);
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            System.out.println("Connected to database");
            Statement stmt = conn.createStatement();

            // User input
            double user_dist = 666.6; // in km
            String distance = kmtoDegString(user_dist);
            String latitude = "49";
            String longitude = "-119";

            String closestDistance = "SELECT address, POWER(latitude - "+ latitude +", 2) + POWER(longitude - " + longitude + ", 2) as DISTANCE FROM LISTINGS" +
            " WHERE POWER(latitude - "+ latitude +", 2) + POWER(longitude - " + longitude + ", 2) < POWER(" + distance + ", 2)"+
            " ORDER BY DISTANCE ASC;";
            
            // Create option to sort by price

            ResultSet rs = stmt.executeQuery(closestDistance);
            System.out.println("Closest listings:");
            while (rs.next()) {
                System.out.println(rs.getString("address"));
                System.out.println(rs.getString("DISTANCE"));
            }

            // Same/Adjecent Postal code
            // String postalCode = "V0N 1B4";
            // String samePostalCode = "SELECT * FROM LISTINGS WHERE address LIKE '%" + postalCode + "%'";
            // rs = stmt.executeQuery(samePostalCode);
            // System.out.println("Same Postal Code:");
            // while (rs.next()) {
            //     System.out.println(rs.getString("address"));
            // }

            String address = "4573 Chateau Blvd, BC V0N 1B4";
            String listing = "SELECT * FROM LISTINGS WHERE address = '" + address + "'";
            rs = stmt.executeQuery(listing);
            System.out.println("Exact Search:");
            System.out.println("lid | price | latitude | longitude | type | hid | address |");
            while (rs.next()) {
                String row = "";
                for (int i = 1; i <= 7; i++) {
                    row += rs.getString(i) + " | ";
                }
                System.out.println(row);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }    
}
