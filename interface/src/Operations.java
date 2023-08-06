import java.sql.Connection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
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
        success = success + stmt.executeUpdate(sql);

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
        success = success + stmt.executeUpdate(sql2);
        scanner.close();
        return success == 2;    // Check if two rows in total have been added
    }

    public static String bookListing(String ren_id) throws ParseException {
        Scanner scanner = new Scanner(System.in);
        String startDateString;
        String listingId;
        String endDateString;
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
        while (startDate.before(endDate)) {
            System.out.println("Input a end date (mm/dd/yyyy): ");
            endDateString = scanner.nextLine();
            endDate = dateParseCheck(endDateString);
        }
        String insertReservation = "INSERT INTO RESERVATIONS (ren_id, lid, start_date, end_date) " +
        " SELECT " + ren_id + ", " + listingId + ", " + startDate + ", " + endDate +
        " WHERE NOT EXISTS (" +
            "SELECT * FROM RESERVATIONS" +
            " WHERE lid = " + listingId + 
            " AND start_date <= " + endDate +
            " AND end_date >= " + startDate + ");";
        scanner.close();
        return insertReservation;
    }

    public static void main(String[] args) throws Exception {
        Class.forName(JDBC_DRIVER);
        boolean failedReservation = true;
        Scanner scanner = new Scanner(System.in);
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        Statement stmt = conn.createStatement()) {
            System.out.println("Welcome to MyBnB!");
            while (true) {
                System.out.println("Choose an option:");
                System.out.println("1: Create an account");
                System.out.println("2: Login");
                System.out.println("3: Book a listing");
                System.out.println("0: Exit");
                int option = Integer.parseInt(scanner.nextLine());
                switch(option) {
                case 1:
                    if (createAccount(stmt))
                        System.out.println("Account created!");
                    else System.out.println("Account creation failed.");
                    break;
                case 3:
                    while (failedReservation) {
                        String ren_id = "1";
                        String tryReservation = bookListing(ren_id);
                        int count = stmt.executeUpdate(tryReservation);
                        if (count == 0) {
                            System.out.println("Please enter a valid reservation");
                        }
                        else {
                            System.out.println("Reservation Successful");
                            failedReservation = false;
                        }
                    }
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        scanner.close();
    }
}
