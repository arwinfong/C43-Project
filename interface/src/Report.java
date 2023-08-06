import java.sql.*;
import java.util.Scanner;

public class Report {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/c43";
    static final String DB_USER = "root";
    static final String DB_PASS = "pw";

    static void reportBookings(Scanner scanner, Statement stmt) throws Exception {

    }

    static void reportListings(Scanner scanner, Statement stmt) throws Exception {

    }

    static void reportHosts(Scanner scanner, Statement stmt) throws Exception {

    }

    static void reportHostsByCity(Scanner scanner, Statement stmt) throws Exception {

    }

    static void reportRenters(Scanner scanner, Statement stmt) throws Exception {

    }

    static void reportRentersByCity(Scanner scanner, Statement stmt) throws Exception {

    }

    static void reportNounPhrases(Scanner scanner, Statement stmt) throws Exception {

    }
    public static void main(String[] args) throws Exception {
        Class.forName(JDBC_DRIVER);
        Scanner scanner = new Scanner(System.in);
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            Statement stmt = conn.createStatement();
            System.out.println("Choose an option:");
            System.out.println("1: Report number of bookings");
            System.out.println("2: Report total number of listings");
            System.out.println("3: Report hosts by number of listings");
            System.out.println("4: Report hosts that have >10% of listings in a city or country");
            System.out.println("5: Report renters by number of bookings");
            System.out.println("6: Report renters by number of bookings (later report)");
            System.out.println("7: Report popular noun phrases in comments");
            System.out.println("0: Exit");


            int option = Integer.parseInt(scanner.nextLine());
            switch (option) {
                case 0:
                    System.out.println("Goodbye!");
                    scanner.close();
                    System.exit(0);
                    break;
                case 1:
                    reportBookings(scanner, stmt);
                    break;
                case 2:
                    reportListings(scanner, stmt);
                    break;
                case 3:
                    reportHosts(scanner, stmt);
                    break;
                case 4:
                    reportHostsByCity(scanner, stmt);
                    break;
                case 5:
                    reportRenters(scanner, stmt);
                    break;
                case 6:
                    reportRentersByCity(scanner, stmt);
                    break;
                case 7:
                    reportNounPhrases(scanner, stmt);
                    break;
                default:
                    break;
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
}