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
        String inDist = scanner.nextLine();
        if (inDist.equals("")) {
            inDist = "111.2";       // Default distance is 1 Degree of latitude
        }
        Double dist = Double.parseDouble(inDist);
        System.out.println("Rank by distance or price? (0: Distance; 1: Price): ");
        String rank = (scanner.nextLine().compareTo("0") == 0) ? "DISTANCE" : "PRICE";
        System.out.println("Ascending or descending? (0: Ascending; 1: Descending): ");
        String order = (scanner.nextLine().compareTo("0") == 0) ? "ASC" : "DESC";

        String distance = kmtoDegString(dist);
        String closestDistance = "SELECT price, address, POWER(latitude - "+ lat +", 2) + POWER(longitude - " + lon + ", 2) as DISTANCE FROM LISTINGS" +
        " WHERE POWER(latitude - "+ lat +", 2) + POWER(longitude - " + lon + ", 2) < POWER(" + distance + ", 2)"+
        " ORDER BY " + rank + " " + order;

        ResultSet rs = stmt.executeQuery(closestDistance);
        System.out.println("Closest listings to " + lat + ", " + lon + " within " + dist + " km:");
        int count = 1;
        System.out.println("----------------------------------");
        while (rs.next()) {
            System.out.println("Entry " + count + ":");
            System.out.println("Address: " + rs.getString("address"));
            System.out.println("Price: " + rs.getString("price"));
            System.out.println("Distance: " + Math.sqrt(rs.getDouble("DISTANCE")) * 111.2 + " km");
            System.out.println("----------------------------------");
            count++;
        }
    }

    static void searchByPostalCode (Scanner scanner, Statement stmt) throws Exception {
        System.out.println("Enter postal code: ");
        String postalCode = scanner.nextLine();
        String listing;
        if (postalCode.equals("")) {
            listing = "SELECT * FROM LISTINGS WHERE postal_code IS NULL";
        }
        else {
            listing = "SELECT * FROM LISTINGS WHERE postal_code = '" + postalCode + "'";
        }
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

        System.out.println("Input a start date for availability (mm/dd/yyyy): ");
        startDateString = scanner.nextLine();
        startDate = dateParseCheck(startDateString);

        System.out.println("Input a end date for availability (mm/dd/yyyy): ");
        endDateString = scanner.nextLine();
        endDate = dateParseCheck(endDateString);
        
        while (startDate.after(endDate)) {
            System.out.println("Start date must be before end date");
            System.out.println("Input a end date for availability (mm/dd/yyyy): ");
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

    static void searchFully(Scanner scanner, Statement stmt, Statement stmt2) throws Exception {
        String endDateString;
        String startDateString;
        Date startDate = null;
        Date endDate = null;
        String amenitySearch;

        System.out.println("Enter postal code: ");
        String postalCode = scanner.nextLine();
        System.out.println("Enter a list of amenities separated by commas: (Ex. Free Wi-Fi, Hot Tub, etc.)");
        String amenities = scanner.nextLine();

        System.out.println("Input a start date for availability (mm/dd/yyyy): ");
        startDateString = scanner.nextLine();
        startDate = dateParseCheck(startDateString);

        System.out.println("Input a end date for availability (mm/dd/yyyy): ");
        endDateString = scanner.nextLine();
        endDate = dateParseCheck(endDateString);
        
        while (startDate.after(endDate)) {
            System.out.println("Start date must be before end date");
            System.out.println("Input a end date for availability (mm/dd/yyyy): ");
            endDateString = scanner.nextLine();
            endDate = dateParseCheck(endDateString);
        }

        System.out.println("Enter a minimum price: ");
        String minPrice = scanner.nextLine();
        System.out.println("Enter a maximum price: ");
        String maxPrice = scanner.nextLine();

        // Search for listings with all amenities in the list
        if (amenities.equals("")) {
            amenitySearch = "SELECT lid FROM LISTINGS";
        }
        else {
            amenitySearch = "SELECT lid FROM LISTINGS WHERE lid IN (SELECT lid FROM LISTING_AMENITIES WHERE aid IN (SELECT aid FROM AMENITIES WHERE name IN (" +
            "SELECT * FROM (SELECT DISTINCT '" + amenities.replace(", ", "' UNION SELECT DISTINCT '") + "') AS T)) GROUP BY lid HAVING COUNT(DISTINCT aid) = " + amenities.split(",").length + ")";
        }
        ResultSet rs = stmt.executeQuery(amenitySearch);
        System.out.println("| lid | price | latitude | longitude | type | hid | address |");
        while (rs.next()) {
            String listing;
            // Search for listings with other filters
            if (postalCode.equals("")) {
                listing = "SELECT * FROM LISTINGS WHERE lid = " + rs.getString("lid") + 
                " AND postal_code IS NULL AND price >= " + minPrice + " AND price <= " + maxPrice + 
                " AND lid IN (SELECT lid FROM CALENDAR WHERE end_date >= '" + startDate + "' AND start_date <= '" + endDate + "' AND status = 'available')";
            }
            else {
                listing = "SELECT * FROM LISTINGS WHERE lid = " + rs.getString("lid") + 
                " AND postal_code = '" + postalCode + "' AND price >= " + minPrice + " AND price <= " + maxPrice + 
                " AND lid IN (SELECT lid FROM CALENDAR WHERE end_date >= '" + startDate + "' AND start_date <= '" + endDate + "' AND status = 'available')";
            }

            ResultSet sub_rs = stmt2.executeQuery(listing);
            while (sub_rs.next()) {
                String row = "";
                for (int i = 1; i <= 7; i++) {
                    row += sub_rs.getString(i) + " | ";
                }
                System.out.println(row);
            }
        }  
    }
    public static void main(String[] args) throws Exception {
        Class.forName(JDBC_DRIVER);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Queries:");
        while (true) {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                Statement stmt = conn.createStatement();
                Statement stmt2 = conn.createStatement();
                System.out.println("1: Search for listings");
                System.out.println("2: Search by postal code");
                System.out.println("3: Search by address");
                System.out.println("4: Search by availability");
                System.out.println("5: Search fully");
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
                        searchByPostalCode(scanner, stmt);
                        break;
                    case 3:
                        searchByAddress(scanner, stmt);
                        break;
                    case 4:
                        searchByAvailability(scanner, stmt);
                        break;
                    case 5:
                        searchFully(scanner, stmt, stmt2);
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                System.out.println(e);
                System.out.println("Invalid input or database error please try again");
            }
        }
    }    
}
