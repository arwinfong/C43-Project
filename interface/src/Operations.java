import java.sql.Connection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
// import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Operations {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/c43";
    static final String DB_USER = "java";
    static final String DB_PASS = "password";
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

    static int createAccount(Statement stmt, Scanner scanner) throws Exception {
        Date dob = null;
        int success = 0;
        String pay_info = "";

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
        if (type == 0) {
            Boolean badMethod = true;
            while (badMethod) {
                System.out.println("Please choose a payment method (0: Credit Card, 1: Debit Card)");
                String payMethod = scanner.nextLine();
                if (payMethod == "0") {
                    pay_info = "Credit Card";
                    badMethod = false;
                }
                else if (payMethod == "1") {
                    pay_info = "Debit Card";
                    badMethod = false;
                }
                else
                    System.out.println("Invalid input");
            }
        }
        String sql = String.format("INSERT INTO Users (name, dob, address, sin, occupation) " +
        "VALUES ('%s', '%s', '%s', %d, '%s')",
        name, dob.toString(), addr, sin, occupation);
        success = stmt.executeUpdate(sql);
        if (success == 0)
            return -1;

        String getUid = "SELECT uid FROM USERS WHERE sin = " + Integer.toString(sin);
        ResultSet rs = stmt.executeQuery(getUid);
        rs.next();
        String uid = rs.getString("uid"); 
        System.out.println(uid);

        String sql2 = (type == 0)     // type: 0 = renter; 1 = host
        ? String.format("INSERT INTO Renters (uid, payment_info)" + "VALUES (%s, %s) ",
        uid, pay_info)
        : String.format("INSERT INTO Hosts (uid)" + "VALUES (%s) ",
        uid);
        success = stmt.executeUpdate(sql2);
        if (success == 0)
            return -1;
        return type;    // Check if two rows in total have been added
    }

    public static List<Integer> Login(Statement stmt, Scanner scanner) throws Exception {
        int type;
        int sin;
        List<Integer> res = new ArrayList<Integer>();
        System.out.println("Are you logging in as a renter or host? (0: renter; 1: host)");
        type = Integer.parseInt(scanner.nextLine());
        System.out.println("Input your SIN: ");
        sin = Integer.parseInt(scanner.nextLine());
        String uidQuery = String.format("SELECT uid FROM USERS WHERE sin = %d", sin);
        String uid;
        ResultSet rs = stmt.executeQuery(uidQuery);     // Throws exception if user not found
        if (rs.next()) {
            uid = rs.getString("uid");
        }
        else {
            uid = "-1";
            System.out.println("User not found");
        }
        res.add(Integer.parseInt(uid));
        String _type = type == 0 ? "RENTERS" : "HOSTS";
        String typeQuery = String.format("SELECT * FROM %s WHERE uid = %s", _type, uid);
        rs = stmt.executeQuery(typeQuery);
        if (rs.next())
            res.add(type);  // User correct type
        else {
            System.out.println("User not found");
            res.add(-1);
        }
        // System.out.println(String.format("%d %d", res.get(0), res.get(1)));
        return res;
    }

    public static int deleteAccount(int type, int uid, Statement stmt, Scanner scanner) {
        System.out.println("Are you sure you want to delete your account?");
        System.out.println("Enter \"y\" to confirm, enter any other key to cancel.");
        String confirm = scanner.nextLine();
        if (confirm.equalsIgnoreCase("y")) {
            try {
                System.out.println("Deleting account...");
                // Delete user from Users
                String sql = String.format("DELETE FROM Users WHERE uid = %d", uid);
                stmt.executeUpdate(sql);
                System.out.println("Deletion complete.");
                return -1;
            }
            catch (Exception e) {
                System.out.println("A problem occurred during deletion.");
                return type;
            }
        }
        else {
            System.out.println("Operation cancelled");
            return type;
        }
    }

    static int cancelBooking(int type, int uid, Statement stmt, Scanner scanner) throws Exception{
        String endDateString;
        String startDateString;
        Date startDate = null;
        Date endDate = null;
        String checkListing;
        String ren_id = "";
        String hid = "";
        ResultSet rs;
        int count = 0;

        // Get ren_id
        if (type == 0) {
            String getRenID = "SELECT ren_id FROM RENTERS WHERE uid = " + uid + ";";
            rs = stmt.executeQuery(getRenID);
            rs.next();
            ren_id = rs.getString("ren_id");
        }
        else {
            String getHID = "SELECT hid FROM HOSTS WHERE uid = " + uid + ";";
            rs = stmt.executeQuery(getHID);
            rs.next();
            hid = rs.getString("hid");
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

        System.out.println("Insert a Listing ID ");
        String listingId = scanner.nextLine();

        if (type == 0) {
            // Check if list is booked in calendar by renter
            checkListing = "SELECT * FROM CALENDAR WHERE lid = " + listingId +
            " AND start_date = '" + startDate + "'" +
            " AND end_date = '" + endDate + "'" +
            " AND status = 'booked'" +
            " AND ren_id = " + ren_id + ";";
            rs = stmt.executeQuery(checkListing);
            if (!rs.next()) {
                System.out.println("You do not have a booking for this listing");
                return 0;
            }
        }
        else {
            // Check if listing is by host
            checkListing = "SELECT * FROM LISTINGS WHERE lid = " + listingId + " AND hid = " + hid + ";";
            rs = stmt.executeQuery(checkListing);
            if (!rs.next()) {
                System.out.println("Listing does not belong to you or does not exist");
                return 0;
            }
        }

        // Update calendar
        if (type == 0) {
            String updateCalendar = "UPDATE CALENDAR SET status = 'cancelled', ren_id = "+ ren_id + ", hid = NULL WHERE lid = " + listingId +
            " AND start_date = '" + startDate + "'" +
            " AND end_date = '" + endDate + "'";
            count = stmt.executeUpdate(updateCalendar);
        }
        else {
            String updateCalendar = "UPDATE CALENDAR SET status = 'available', hid = " + hid + ", ren_id = NULL WHERE lid = " + listingId +
            " AND start_date = '" + startDate + "'" +
            " AND end_date = '" + endDate + "';";
            count = stmt.executeUpdate(updateCalendar);
        }
    
        return count;
    }

    static List<String> bookListing(int type, int uid, Scanner scanner, Statement stmt) throws Exception {
        String listingId;
        List<String> queries = new ArrayList<String>();
        String endDateString;
        String startDateString;
        Date startDate = null;
        Date endDate = null;
        
        // get ren_id
        String getRenID = "SELECT ren_id FROM RENTERS WHERE uid = " + uid + ";";
        ResultSet rs = stmt.executeQuery(getRenID);
        rs.next();
        String ren_id = rs.getString("ren_id");

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

    static void addComment(Scanner scanner, Statement stmt, Integer type, Integer curUID) throws Exception {
        ResultSet rs = null;
        boolean badEntry = true;
        String id = "";
        String subject = type == 0 ? "user" : "listing";
        java.util.Date dateNow = new java.util.Date();
        java.sql.Date curDate = new java.sql.Date(dateNow.getTime());
        while (badEntry) {
            System.out.println(String.format("Input %s's ID: ", subject));
            id = scanner.nextLine();
            String subExist = type == 0
            ? String.format("SELECT ren_id FROM Renters WHERE uid = %s", id)
            : String.format("SELECT * FROM Listings WHERE lid = %s", id);
            
            rs = stmt.executeQuery(subExist);
            rs.next();
            if (type == 0) {
                String rid = rs.getString("ren_id");
                if (rid == null) {
                    System.out.println(String.format("The %s does not exist", subject));
                    continue;
                }
                // Checks if renter has rented in host's listings
                String checkUser =  "SELECT * FROM CALENDAR WHERE ren_id = " + rid +
                " OR hid = " + curUID + 
                " AND status = 'booked' " +
                " AND (start_date <= '" + curDate + "' <= end_date" +
                " OR end_date + interval 2 week >= '" + curDate + "');";
                rs = stmt.executeQuery(checkUser);
                if (!rs.next()) {
                    System.out.println("User not found.");
                    System.out.println(badEntry);
                    continue;
                }
                else {
                    badEntry = false;
                }
            }
            else {
                // Check if listing is in calendar
                String checkListing = "SELECT * FROM CALENDAR WHERE lid = " + id +
                " AND ren_id = " + curUID +
                " AND status = 'booked' " +
                " AND (start_date <= '" + curDate + "' <= end_date" +
                " OR end_date + interval 2 week >= '" + curDate + "');";
                rs = stmt.executeQuery(checkListing);
                if (!rs.next()) {
                    System.out.println("Listing not found");
                    continue;
                }
                else {
                    badEntry = false;
                }
            }
        }
        // Found subject
        Boolean badRating = true;
        Integer rating = 0;
        while (badRating) {
            System.out.println(String.format("Rate this %s from 1 - 5", subject));
            rating = Integer.parseInt(scanner.nextLine());
            badRating = (rating < 1 && rating > 5);
        }
        System.out.println("Add a short comment to your rating (optional): ");
        String textComment = scanner.nextLine();
        String insertComment = String.format(
            "INSERT INTO Comments (rating, comment) VALUES (%d, '%s');",
            rating, textComment
        );
        System.out.println(insertComment);
        stmt.executeUpdate(insertComment);
        String getCID = String.format("SELECT LAST_INSERT_ID();");
        rs = stmt.executeQuery(getCID);
        rs.next();
        String cid = rs.getString("last_insert_id()");
        String idConvert = type == 0
        ? String.format("SELECT hid FROM Hosts WHERE uid = %d", curUID)
        : String.format("SELECT ren_id FROM Renters WHERE uid = %d", curUID);
        rs = stmt.executeQuery(idConvert);
        rs.next();
        String idConverted = type == 0 ? rs.getString("hid") : rs.getString("ren_id");
        String insertLR = type == 0
        ? String.format("INSERT INTO Renter_Comments (ren_id, cid, hid) VALUES ('%s', '%s', '%s')", id, cid, idConverted)
        : String.format("INSERT INTO Listing_Comments (lid, cid, ren_id) VALUES ('%s', '%s', '%s')", id, cid, idConverted);
        stmt.executeUpdate(insertLR);
        System.out.println("Rating saved!");
    }

    static String updatePricing (Scanner scanner, Statement stmt, int type, int uid) throws Exception {
        // Get hid
        String getHID = "SELECT hid FROM HOSTS WHERE uid = " + uid + ";";
        ResultSet rs = stmt.executeQuery(getHID);
        rs.next();
        String hid = rs.getString("hid");

        boolean badLID = true;
        String listingId = "";
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

        System.out.println("Insert a new price ");
        String newPrice = scanner.nextLine();

        String updatePrice = "UPDATE LISTINGS SET price = " + newPrice + " WHERE lid = " + listingId + ";";
        return updatePrice;
    }

    static String updateAvailability(Scanner scanner, Statement stmt, int type, int uid) throws Exception {
        boolean badLID = true;
        boolean badEntry = true;
        String listingId = "";
        String endDateString;
        String startDateString;
        Date startDate = null;
        Date endDate = null;
        ResultSet rs;
        
        // get hid
        String getHID = "SELECT hid FROM HOSTS WHERE uid = " + uid + ";";
        rs = stmt.executeQuery(getHID);
        rs.next();
        String hid = rs.getString("hid");

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

    static String insertListing (Scanner scanner, int uid, Statement stmt) throws Exception {
        String getHID = "SELECT hid FROM HOSTS WHERE uid = " + uid;
        ResultSet rs = stmt.executeQuery(getHID);
        rs.next();
        String hid = rs.getString("hid");

        boolean existingListing = true;
        String address = "";
        String city = "";
        String country = "";
        String postal_code = "";
        String latitude = "";
        String longitude = "";
        String type = "";

        System.out.println("Input a price:");
        int price = Integer.parseInt(scanner.nextLine());
        while (price < 0) {
            System.out.println("Input a price:");
            price = Integer.parseInt(scanner.nextLine());
        }

        while (existingListing) {
            System.out.println("Input a address (e.g. 123 Fake Street):");
            address = scanner.nextLine();
    
            System.out.println("Input a city:");
            city = scanner.nextLine();
    
            System.out.println("Input a country:");
            country = scanner.nextLine();
    
            System.out.println("Input a postal code (e.g. M5S 1A5):");
            postal_code = scanner.nextLine();
    
            System.out.println("Input a latitude (e.g. 43.6532):");
            latitude = scanner.nextLine();
    
            System.out.println("Input a longitude (e.g. -79.3832):");
            longitude = scanner.nextLine();
    
            System.out.println("Input a type (e.g. apartment, house, condo):");
            type = scanner.nextLine();
    
            // Check if listing exists
            String checkListing = "SELECT * FROM LISTINGS WHERE latitude = " + latitude + " AND longitude = " + longitude + 
            " AND type = '" + type + "' AND city = '" + city + "' AND country = '" + country + 
            "' AND postal_code = '" + postal_code + "' AND address = '" + address + "'";
            rs = stmt.executeQuery(checkListing);
            if (rs.next()) {
                System.out.println("Listing already exists");
            }
            else {
                existingListing = false;
            }
        }

        String insertListing = "INSERT INTO LISTINGS (price, latitude, longitude, type, hid, city, country, postal_code, address) " +
        " VALUES (" + price + ", " + latitude + ", " + longitude + ", '" + type + "', " + hid + ", '" + city + "', '" + country + "', '" + postal_code + "', '" + address + "');";

        return insertListing;
    }

    static String deleteListing (Scanner scanner, int uid, Statement stmt) throws Exception {
        boolean badLID = true;
        String getHID = "SELECT hid FROM HOSTS WHERE uid = " + uid;
        ResultSet rs = stmt.executeQuery(getHID);
        rs.next();
        String hid = rs.getString("hid");
        String lid = "";
        while (badLID) {
            System.out.println("Input a listing ID:");
            lid = scanner.nextLine();
    
            // Check if listing belongs to host
            String checkHost = "SELECT * FROM LISTINGS WHERE lid = " + lid + " AND hid = " + hid + ";";
            rs = stmt.executeQuery(checkHost);
            if (!rs.next()) {
                System.out.println("Listing does not belong to host or does not exist");
                continue;
            }
            // Check if listing is booked
            String checkBooked = "SELECT * FROM CALENDAR WHERE lid = " + lid + " AND status = 'booked';";
            rs = stmt.executeQuery(checkBooked);
            if (rs.next()) {
                System.out.println("Listing is booked");
            }
            else {
                badLID = false;
            }
        }

        String deleteListing = "DELETE FROM LISTINGS WHERE lid = " + lid + " AND hid = " + hid + ";";

        return deleteListing;
    }

    public static void main(String[] args) throws Exception {
        Class.forName(JDBC_DRIVER);
        Integer userType = -1;              // -1: Not logged in; 0: Renter; 1: Host
        Integer uid = -1;
        boolean looping = true;
        boolean failedReservation = true;
        int count = 0;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to MyBnB!");
        while (looping) {
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                Statement stmt = conn.createStatement()) {
                System.out.println("Choose an option:");
                System.out.println("1: Create an account"); 
                System.out.println("2: Login");
                System.out.println("3: Delete account");
                System.out.println("4: Cancel Booking");                
                /* Renter operations */
                System.out.println("5: Book a listing");             
                System.out.println("6: Comment on a listing");          
                /* Host Operations */
                System.out.println("7: Update a Listing Price");
                System.out.println("8: Update a Listing Availability");
                System.out.println("9: Comment on a renter");           
                System.out.println("10: Insert a Listing");
                System.out.println("11: Delete: Delete a Listing");

                System.out.println("0: Exit");
                int option = Integer.parseInt(scanner.nextLine());
                switch(option) {
                case 0:
                    System.out.println("Goodbye!");
                    looping = false;
                    break;
                case 1:
                    userType = createAccount(stmt, scanner);
                    if (userType != -1) {
                        System.out.println("Account created!");
                        System.out.println("Logged In!");
                    }
                    else System.out.println("Account creation failed.");
                    break;
                case 2:
                    if (userType != -1) {
                        System.out.println("Invalid operation: Already logged in.");
                        break;
                    }
                    List<Integer> loginResult = Login(stmt, scanner);
                    uid = loginResult.get(0);
                    userType = loginResult.get(1);
                    if (userType != -1)
                        System.out.println("Logged In!");
                    break;
                case 3:
                    if (userType == -1) {
                        System.out.println("Invalid operation: Not logged in.");
                        break;
                    }
                    userType = deleteAccount(userType, uid, stmt, scanner);
                    break;
                case 4:
                    if (userType == -1) {
                        System.out.println("Invalid operation: Not logged in.");
                        break;
                    }
                    int cancelled = cancelBooking(userType, uid, stmt, scanner);
                    if (cancelled == 0) {
                        System.out.println("Please enter a valid reservation");
                    }
                    else {
                        System.out.println("Reservation Cancelled");
                    }
                    break;
                case 5:
                    while (failedReservation) {
                        if (userType != 0) {
                            System.out.println("Invalid operation. Only renters may book listings");
                            break;
                        }
                        List<String> tryQueries = bookListing(0, uid, scanner, stmt);
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
                    if (userType != 0) {
                        System.out.println("Invalid operation. Only renters may leave comments on listings");
                        break;
                    }
                    addComment(scanner, stmt, 1, uid);
                    break;
                case 7:
                    if (userType != 1) {
                        System.out.println("Invalid operation. Only hosts may update listing prices");
                        break;
                    }
                    count = stmt.executeUpdate(updatePricing(scanner, stmt, 1, uid));
                    if (count == 0) {
                        System.out.println("Please enter a valid listing");
                    }
                    else {
                        System.out.println("Price Updated");
                    }
                    break;
                case 8:
                    if (userType != 1) {
                        System.out.println("Invalid operation. Only hosts may update listing availability");
                        break;
                    }
                    count = stmt.executeUpdate(updateAvailability(scanner, stmt, 1, uid));
                    if (count == 0) {
                        System.out.println("Please enter a valid listing");
                    }
                    else {
                        System.out.println("Availability Updated");
                    }
                    break;
                case 9:
                    if (userType != 1) {
                        System.out.println("Invalid operation. Only hosts may leave comments on renters");
                        break;
                    }
                    addComment(scanner, stmt, 0, uid);
                    break;
                case 10:
                    if (userType != 1) {
                        System.out.println("Invalid operation. Only hosts may insert listings");
                        break;
                    }
                    count = stmt.executeUpdate(insertListing(scanner, uid, stmt));
                    if (count == 0) {
                        System.out.println("Please enter a valid listing");
                    }
                    else {
                        System.out.println("Listing Inserted");
                    }
                    break;
                case 11:
                    if (userType != 1) {
                        System.out.println("Invalid operation. Only hosts may delete listings");
                        break;
                    }
                    count = stmt.executeUpdate(deleteListing(scanner, uid, stmt));
                    if (count == 0) {
                        System.out.println("Please enter a valid listing");
                    }
                    else {
                        System.out.println("Listing Deleted");
                    }
                    break;
                default:
                    break;
                }
            }
            catch (Exception e) {
                System.out.println(e);
            }
        }
        scanner.close();
        System.exit(0);
    }
}
