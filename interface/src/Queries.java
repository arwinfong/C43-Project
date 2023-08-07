import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class Queries {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/c43";
    static final String DB_USER = "root";
    static final String DB_PASS ="pw";
    static final String DATE_FORMAT = "MM/dd/yy";

    static String kmtoDegString(double km) {
        return Double.toString(km / 111.2);
    }

    public static Date dateParseCheck(String dobString) throws ParseException {
        try {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            df.setLenient(false);
            java.util.Date _dob = df.parse(dobString);
            java.sql.Date dob = new java.sql.Date(_dob.getTime());
            return dob;
        }
        catch (ParseException e) {
            return null;
        }
    }

    static void searchListing (Scanner scanner, Statement stmt) throws Exception {
        System.out.println("Enter latitude of the listing: ");
        String lat = scanner.nextLine();
        System.out.println("Enter longitude of the listing: ");
        String lon = scanner.nextLine();
        System.out.println("Enter distance in km: ");
        Double dist = Double.parseDouble(scanner.nextLine());

        String distance = kmtoDegString(dist);
        String closestDistance = "SELECT address, POWER(latitude - "+ lat +", 2) + POWER(longitude - " + lon + ", 2) as DISTANCE FROM LISTINGS" +
        " WHERE POWER(latitude - "+ lat +", 2) + POWER(longitude - " + lon + ", 2) < POWER(" + distance + ", 2)"+
        " ORDER BY DISTANCE ASC;";

        // Create option to sort by price

        ResultSet rs = stmt.executeQuery(closestDistance);
        System.out.println("Closest listings:");
        int count = 1;
        System.out.println("----------------------------------");
        while (rs.next()) {
            System.out.println("Entry " + count + ":");
            System.out.println("Address: " + rs.getString("address"));
            System.out.println("Distance:" + rs.getString("DISTANCE"));
            System.out.println("----------------------------------");
            count++;
        }
    }

    // static void searchByPostalCode (Scanner scanner, Statement stmt) throws Exception {
    //     System.out.println("Enter postal code: ");
    //     String postalCode = scanner.nextLine();
    //     String samePostalCode = "SELECT * FROM LISTINGS WHERE address LIKE '%" + postalCode + "%'";
    //     ResultSet rs = stmt.executeQuery(samePostalCode);
    //     System.out.println("Same Postal Code:");
    //     while (rs.next()) {
    //         System.out.println(rs.getString("address"));
    //     }
    // }

    static void searchByAddress (Scanner scanner, Statement stmt) throws Exception {
        System.out.println("Enter address (Exact): ");
        String address = scanner.nextLine();
        String listing = "SELECT * FROM LISTINGS WHERE address = '" + address + "'";
        ResultSet rs = stmt.executeQuery(listing);
        System.out.println("Exact Search:");
        System.out.println("lid | price | latitude | longitude | type | hid | address |");
        while (rs.next()) {
            String row = "";
            for (int i = 1; i <= 7; i++) {
                row += rs.getString(i) + " | ";
            }
            System.out.println(row);
        }
    }

    static void searchByAvailability (Scanner scanner, Statement stmt) throws Exception {
        String endDateString;
        String startDateString;
        Date startDate = null;
        Date endDate = null;

        System.out.println("Input a start date (mm/dd/yyyy): ");
        startDateString = scanner.nextLine();
        startDate = dateParseCheck(startDateString);

        System.out.println("Input a end date (mm/dd/yyyy): ");
        endDateString = scanner.nextLine();
        endDate = dateParseCheck(endDateString);
        
        while (startDate.after(endDate)) {
            System.out.println("Start date must be before end date");
            System.out.println("Input a end date (mm/dd/yyyy): ");
            endDateString = scanner.nextLine();
            endDate = dateParseCheck(endDateString);
        }

        String available = "SELECT * FROM LISTINGS WHERE lid IN (SELECT lid FROM CALENDAR " +
        " WHERE end_date >= '" + startDate + "' AND start_date <= '" + endDate + "'" +
        " AND status = 'available');";
        ResultSet rs = stmt.executeQuery(available);
        System.out.println("Available Listings:");
        System.out.println("lid | price | latitude | longitude | type | hid | address |");
        while (rs.next()) {
            String row = "";
            for (int i = 1; i <= 7; i++) {
                row += rs.getString(i) + " | ";
            }
            System.out.println(row);
        }
    }
    public static void main(String[] args) throws Exception {
        Class.forName(JDBC_DRIVER);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Queries:");
        while (true) {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                Statement stmt = conn.createStatement();
                System.out.println("1: Search for listings");
                System.out.println("2: Search by postal code");
                System.out.println("3: Search by address");
                System.out.println("4: Search by availability");
                System.out.println("0: Exit");
                int option = Integer.parseInt(scanner.nextLine());
                switch (option) {
                    case 0:
                        System.out.println("Goodbye!");
                        scanner.close();
                        System.exit(0);
                        break;
                    case 1:
                        searchListing(scanner, stmt);
                        break;
                    case 2:
                        break;
                    case 3:
                        searchByAddress(scanner, stmt);
                        break;
                    case 4:
                        searchByAvailability(scanner, stmt);
                        break;
                    case 5:

                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                System.out.println("Invalid input or database error please try again");
            }
        }
    }    
}
