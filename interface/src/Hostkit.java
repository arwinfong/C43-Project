import java.sql.*;
import java.util.Scanner;

public class Hostkit {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/c43";
    static final String DB_USER = "root";
    static final String DB_PASS = "pw";

    static void suggestPrice(Scanner scanner, Statement stmt) throws Exception {
        System.out.println("Enter a city: ");
        String city = scanner.nextLine();
        System.out.println("Enter a country: ");
        String country = scanner.nextLine();
        System.out.println("Enter a type of listing: ");
        String type = scanner.nextLine();

        String query = "SELECT AVG(price) as Average FROM LISTINGS WHERE";
        if (!city.equals("")) {
            query += " city = '" + city + "'";
            if (!country.equals("") || !type.equals("")) {
                query += " AND";
            }
        }
        if (!country.equals("")) {
            query += " country = '" + country + "'";
            if (!type.equals("")) {
                query += " AND";
            }
        }
        if (!type.equals("")) {
            query += " type = '" + type + "';";
        }
        ResultSet rs = stmt.executeQuery(query);
        rs.next();
        System.out.println("Suggested price: $" + rs.getInt("Average"));
    }

    static void suggestAmenities(Scanner scanner, Statement stmt) throws Exception {
        System.out.println("Enter a city: ");
        String city = scanner.nextLine();
        System.out.println("Enter a country: ");
        String country = scanner.nextLine();
        System.out.println("Enter a type of listing: ");
        String type = scanner.nextLine();

        // Suggest amenities
        String query = "SELECT DISTINCT a.name FROM AMENITIES a, LISTINGS l, LISTING_AMENITIES la WHERE a.aid = la.aid AND l.lid = la.lid AND l.lid IN (SELECT lid FROM LISTINGS WHERE";
        if (!city.equals("")) {
            query += " city = '" + city + "'";
            query += (!country.equals("") || !type.equals("")) ? " AND" : ")";
        }
        if (!country.equals("")) {
            query += " country = '" + country + "'";
            query += !type.equals("") ? " AND" : ")";
        }
        if (!type.equals("")) {
            query += " type = '" + type + "');";
        }
        ResultSet rs = stmt.executeQuery(query);
        System.out.println("Suggested amenities:");
        while (rs.next()) {
            System.out.println(rs.getString("name"));
        }
    }

    public static void main(String[] args) throws Exception {
        Class.forName(JDBC_DRIVER);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Host toolkit:");
        while (true) {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                Statement stmt = conn.createStatement();

                System.out.println("Enter an option:");
                System.out.println("1: Suggest a price for a listing");
                System.out.println("2: Suggest amenities for a listing");
                System.out.println("0: Exit");

                int option = Integer.parseInt(scanner.nextLine());
                switch (option) {
                    case 0:
                        System.out.println("Goodbye!");
                        scanner.close();
                        System.exit(0);
                        break;
                    case 1:
                        suggestPrice(scanner, stmt);
                        break;
                    case 2:
                        suggestAmenities(scanner, stmt);
                        break;
                    default:
                        break;
                }
            }
            catch (SQLException e) {
                System.out.println("Invalid input or database error please try again");
            }
        }
    }
}
