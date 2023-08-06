import java.sql.Connection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;
import java.util.List;

public class Operations {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/c43";
    static final String DB_USER = "root";
    static final String DB_PASS = "pw";
    static final String DATE_FORMAT = "MM/dd/yy";

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

    private static boolean isAdult(Date dob) {
        long diff = System.currentTimeMillis() - dob.getTime();
        long day_diff = diff / 86400000;
        return day_diff > 6574;
    }

    static boolean createAccount(Statement stmt) throws Exception {
        Scanner scanner = new Scanner(System.in);
        Date dob = null;
        int success = 0;

        System.out.println("Input your name: ");
        String name = scanner.nextLine();
        while (dob == null || !isAdult(dob)) {
            System.out.println("Input your date of birth (mm/dd/yyyy): ");
            String dobString = scanner.nextLine();
            dob = dateParseCheck(dobString);
        }
        System.out.println("Input your address: ");
        String addr = scanner.nextLine();
        System.out.println("Input your occupation: ");
        String occupation = scanner.nextLine();
        System.out.println("Input your SIN (e.g. 123456789):");
        int sin = Integer.parseInt(scanner.nextLine());
        System.out.println("Are you a renter or a host? (0: Renter; 1: Host)");
        int type = Integer.parseInt(scanner.nextLine());
        String sql = String.format("INSERT INTO Users (name, dob, address, sin, occupation) " +
        "VALUES ('%s', '%s', '%s', %d, '%s')",
        name, dob.toString(), addr, sin, occupation);
        success += stmt.executeUpdate(sql);

        String getUid = "SELECT uid FROM USERS WHERE sin = " + Integer.toString(sin);
        ResultSet rs = stmt.executeQuery(getUid);
        rs.next();
        String uid = rs.getString("uid"); 
        System.out.println(uid);

        String sql2 = (type == 0)     // type: 0 = renter; 1 = host
        ? String.format("INSERT INTO Renters (uid)" + "VALUES (%s) ",
        uid)
        : String.format("INSERT INTO Hosts (uid)" + "VALUES (%s) ",
        uid);
        success += stmt.executeUpdate(sql2);
        scanner.close();
        return success == 2;    // Check if two rows in total have been added
    }

    static List<String> bookListing(String ren_id, Scanner scanner) throws ParseException {
        String listingId;
        List<String> queries = new ArrayList<String>();
        String endDateString;
        String startDateString;
        Date startDate = null;
        Date endDate = null;
        
        System.out.println("Insert a Listing ID ");   
        listingId = scanner.nextLine();

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

        String insertReservation = "INSERT INTO RESERVATIONS (ren_id, lid, start_date, end_date) " +
        " SELECT " + ren_id + ", " + listingId + ", '" + startDate + "', '" + endDate + "'" +
        " WHERE '" + startDate + "' >= CURDATE()" +
        " AND '" + endDate + "' >= CURDATE()" +
        " AND NOT EXISTS (" +
            "SELECT * FROM RESERVATIONS" +
            " WHERE lid = " + listingId + 
            " AND start_date <= '" + endDate + "'" +
            " AND end_date >= '" + startDate + "')" +
        " AND NOT EXISTS (" +
            "SELECT * FROM CALENDAR" +
            " WHERE lid = " + listingId + 
            " AND status = 'booked' OR status = 'cancelled'" +
            " AND start_date <= '" + endDate + "'" +
            " AND end_date >= '" + startDate + "');";
        
        String insertCalendar = "INSERT INTO CALENDAR (lid, start_date, end_date, ren_id, status) " +
        " VALUES (" + listingId + ", '" + startDate + "', '" + endDate + "', " + ren_id + ", 'booked');";

        queries.add(insertReservation);
        queries.add(insertCalendar);
        return queries;
    }

    static String updatePricing (Scanner scanner, Statement stmt, String hid) throws Exception {
        // Check date

        boolean badLID = true;
        String listingId = "";
        while (badLID) {
            System.out.println("Insert a Listing ID ");
            listingId = scanner.nextLine();
    
            // Check if listing belongs to host
            String checkHost = "SELECT * FROM LISTINGS WHERE lid = " + listingId + " AND hid = " + hid + ";";
            ResultSet rs = stmt.executeQuery(checkHost);
            if (!rs.next()) {
                System.out.println("Listing does not belong to host or does not exist");
            }
            else {
                badLID = false;
            }
        }

        System.out.println("Insert a new price ");
        String newPrice = scanner.nextLine();

        String updatePrice = "UPDATE LISTINGS SET price = " + newPrice + " WHERE lid = " + listingId + ";";
        return updatePrice;
    }

    static String updateAvailability(Scanner scanner, Statement stmt, String hid) throws Exception {
        boolean badLID = true;
        boolean badEntry = true;
        String listingId = "";
        String endDateString;
        String startDateString;
        Date startDate = null;
        Date endDate = null;
        ResultSet rs;
        
        while (badEntry) {
            while (badLID) {
                System.out.println("Insert a Listing ID ");
                listingId = scanner.nextLine();
        
                // Check if listing belongs to host
                String checkHost = "SELECT * FROM LISTINGS WHERE lid = " + listingId + " AND hid = " + hid + ";";
                rs = stmt.executeQuery(checkHost);
                if (!rs.next()) {
                    System.out.println("Listing does not belong to host or does not exist");
                }
                else {
                    badLID = false;
                }
            }
    
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
            
            // Check if list is in calendar
            String checkListing = "SELECT * FROM CALENDAR WHERE lid = " + listingId +
            " AND start_date <= '" + endDate + "'" +
            " AND end_date >= '" + startDate + "';";
            rs = stmt.executeQuery(checkListing);
            if (!rs.next()) {
                System.out.println("This listing is not in the calendar");
                System.out.println("Would you like to add it? (0: No; 1: Yes)");
                String addListing = scanner.nextLine();
                if (addListing.compareTo("1") == 0) {
                    System.out.println("Insert a new availability (0: unavailable; 1: available)");
                    String newAvailabilityIn = scanner.nextLine();
                    String newAvailability = (newAvailabilityIn.compareTo("0") == 0) ? "unavailable" : "available";
                    String insertCalendar = "INSERT INTO CALENDAR (lid, start_date, end_date, hid, status) " +
                    " VALUES (" + listingId + ", '" + startDate + "', '" + endDate + "', " + hid + ", '"+ newAvailability +"');";
                    return insertCalendar;
                }
                else {
                    System.out.println("Enter a valid Calendar entry");
                }
            }
            else {
                // Check if listing is booked
                String checkBooked = "SELECT * FROM CALENDAR WHERE lid = " + listingId + " AND status != 'available'" + 
                                    " AND start_date = '" + startDate + "' AND end_date = '" + endDate + "';";
                rs = stmt.executeQuery(checkBooked);
                if (rs.next()) {
                    System.out.println("Listing is booked, cancelled or unavailable");
                }
                else {
                    badEntry = false;
                }
            }

        }

        System.out.println("Insert a new availability (0: unavailable; 1: available)");
        String newAvailabilityIn = scanner.nextLine();
        String newAvailability = (newAvailabilityIn.compareTo("0") == 0) ? "unavailable" : "available";

        String updateAvailability = "UPDATE CALENDAR SET status = '" + newAvailability + "' WHERE lid = " + listingId + ";";

        return updateAvailability;
    }

    public static void main(String[] args) throws Exception {
        Class.forName(JDBC_DRIVER);
        boolean failedReservation = true;
        String hid = "1";
        String ren_id = "1"; 
        int count = 0;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to MyBnB!");
        while (true) {
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                Statement stmt = conn.createStatement()) {
                System.out.println("Choose an option:");
                System.out.println("1: Create an account"); 
                System.out.println("2: Login");                         // TODO
                System.out.println("3: Delete account");                // TODO
                System.out.println("4: Cancel Booking");                // TODO
                /* Renter operations */
                System.out.println("5: Book a listing");             
                System.out.println("6: Comment on a listing");          // TODO
                /* Host Operations */
                System.out.println("5: Update a Listing Price");        // TODO
                System.out.println("6: Update a Listing Availability"); // TODO
                System.out.println("7: Comment on a renter");           // TODO

                System.out.println("0: Exit");
                int option = Integer.parseInt(scanner.nextLine());
                switch(option) {
                case 0:
                    System.out.println("Goodbye!");
                    scanner.close();
                    System.exit(0);
                    break;
                case 1:
                    if (createAccount(stmt))
                        System.out.println("Account created!");
                    else System.out.println("Account creation failed.");
                    break;
                case 5:
                    while (failedReservation) {
                        List<String> tryQueries = bookListing(ren_id, scanner);
                        count = 0;
                        for (String query : tryQueries) {
                            count += stmt.executeUpdate(query);
                        }
                        if (count == 0) {
                            System.out.println("Please enter a valid reservation");
                        }
                        else {
                            System.out.println("Reservation Successful");
                            failedReservation = false;
                        }
                    }
                    break;
                case 6:
                    count = stmt.executeUpdate(updatePricing(scanner, stmt, hid));
                    if (count == 0) {
                        System.out.println("Please enter a valid listing");
                    }
                    else {
                        System.out.println("Price Updated");
                    }
                    break;
                case 7:
                    count = stmt.executeUpdate(updateAvailability(scanner, stmt, hid));
                    if (count == 0) {
                        System.out.println("Please enter a valid listing");
                    }
                    else {
                        System.out.println("Availability Updated");
                    }
                    break;
                }
            }
            catch (Exception e) {
                System.out.println(e);
            }
        } 
    }
}
