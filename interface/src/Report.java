import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.HashMap;

public class Report {
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

    static void reportBookings(Scanner scanner, Statement stmt) throws Exception {
        // Get date range
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

        // Join reservations and listings
        String bookings = "SELECT L.city, COUNT(*) AS BOOKINGS FROM CALENDAR C, LISTINGS L WHERE C.lid = L.lid AND C.start_date >= '" + startDate + "' AND C.end_date <= '" + endDate + "'" +
        " AND C.status = 'booked'" +
        " GROUP BY city";
        ResultSet rs = stmt.executeQuery(bookings);
        System.out.println("Total bookings between " + startDate + " and " + endDate + ":");
        System.out.println("----------------------------------");
        while (rs.next()) {
            System.out.println("City: " + rs.getString("city"));
            System.out.println("Bookings: " + rs.getString("BOOKINGS"));
            System.out.println("----------------------------------");
        }

        // By postal code
        String bookingsByZip = "SELECT L.postal_code, COUNT(*) AS BOOKINGS FROM CALENDAR C, LISTINGS L WHERE C.lid = L.lid AND C.start_date >= '" + startDate + "' AND C.end_date <= '" + endDate + "'" +
        " AND C.status = 'booked'" +
        " GROUP BY postal_code";
        rs = stmt.executeQuery(bookingsByZip);
        System.out.println("Total bookings by postal code between" + startDate + " and " + endDate + ":");
        System.out.println("----------------------------------");
        while (rs.next()) {
            System.out.println("Postal Code: " + rs.getString("postal_code"));
            System.out.println("Bookings: " + rs.getString("BOOKINGS"));
            System.out.println("----------------------------------");
        }
    }

    static void reportListings(Scanner scanner, Statement stmt) throws Exception {
        System.out.println("Choose an option:");
        System.out.println("1: Report number of listings by city");
        System.out.println("2: Report number of listings by country");
        System.out.println("3: Report number of listings by postal code");
        int option = Integer.parseInt(scanner.nextLine());
        switch (option) {
            case 1:
                String listingsByCity = "SELECT city, COUNT(*) AS LISTINGS FROM LISTINGS GROUP BY city";
                ResultSet rs = stmt.executeQuery(listingsByCity);
                System.out.println("Listings by city:");
                System.out.println("----------------------------------");
                while (rs.next()) {
                    System.out.println("City: " + rs.getString("city"));
                    System.out.println("Listing(s): " + rs.getString("LISTINGS"));
                    System.out.println("----------------------------------");
                }
                break;
            case 2:
                String listingsByCountry = "SELECT country, COUNT(*) AS LISTINGS FROM LISTINGS GROUP BY country";
                rs = stmt.executeQuery(listingsByCountry);
                System.out.println("Listings by country:");
                System.out.println("----------------------------------");
                while (rs.next()) {
                    System.out.println("Country: " + rs.getString("country"));
                    System.out.println("Listing(s): " + rs.getString("LISTINGS"));
                    System.out.println("----------------------------------");
                }
                break;
            case 3:
                String listingsByPostalCode = "SELECT postal_code, COUNT(*) AS LISTINGS FROM LISTINGS GROUP BY postal_code";
                rs = stmt.executeQuery(listingsByPostalCode);
                System.out.println("Listings by postal code:");
                System.out.println("----------------------------------");
                while (rs.next()) {
                    System.out.println("Postal Code: " + rs.getString("postal_code"));
                    System.out.println("Listing(s): " + rs.getString("LISTINGS"));
                    System.out.println("----------------------------------");
                }
                break;
            default:
                break;
        }
    }

    static void reportHosts(Scanner scanner, Statement stmt, Statement stmt2) throws Exception {
        // All listing countries
        String allCountries = "SELECT DISTINCT country FROM LISTINGS";
        ResultSet rs = stmt.executeQuery(allCountries);
        System.out.println("Countries:");
        System.out.println("----------------------------------");
        while (rs.next()) {
            String country = rs.getString("country");
            // Rank hosts by total listings per country
            String hostsByCountry = "SELECT U.name, L.country, COUNT(*) AS LISTINGS FROM USERS U, HOSTS H, LISTINGS L WHERE U.uid = H.uid AND H.hid = L.hid" +
            " AND L.country = '" + country + "'" +
            " GROUP BY U.name, L.country ORDER BY LISTINGS DESC";

            ResultSet sub_rs = stmt2.executeQuery(hostsByCountry);
            System.out.println("Hosts with listings in " + country + ":");
            System.out.println("----------------------------------");
            int count = 1;
            while (sub_rs.next()) {
                System.out.println("Rank: " + count);
                System.out.println("Name: " + sub_rs.getString("name"));
                System.out.println("Country: " + sub_rs.getString("country"));
                System.out.println("Listing(s): " + sub_rs.getString("LISTINGS"));
                System.out.println("----------------------------------");
                count++;
            }
        }
        
        // By city
        // All listing cities
        String allCities = "SELECT DISTINCT city FROM LISTINGS";
        rs = stmt.executeQuery(allCities);
        System.out.println("Cities:");
        System.out.println("----------------------------------");
        while (rs.next()) {
            String city = rs.getString("city");
            // Rank hosts by total listings per city
            String hostsByCity = "SELECT U.name, L.city, COUNT(*) AS LISTINGS FROM USERS U, HOSTS H, LISTINGS L WHERE U.uid = H.uid AND H.hid = L.hid" +
            " AND L.city = '" + city + "'" +
            " GROUP BY U.name, L.city ORDER BY LISTINGS DESC";

            ResultSet sub_rs = stmt2.executeQuery(hostsByCity);
            System.out.println("Hosts with listings in " + city + ":");
            System.out.println("----------------------------------");
            int count = 1;
            while (sub_rs.next()) {
                System.out.println("Rank: " + count);
                System.out.println("Name: " + sub_rs.getString("name"));
                System.out.println("City: " + sub_rs.getString("city"));
                System.out.println("Listing(s): " + sub_rs.getString("LISTINGS"));
                System.out.println("----------------------------------");
                count++;
            }
        }
    }

    static void reportHostsGT10P(Scanner scanner, Statement stmt, Statement stmt2) throws Exception {
        // Total number of listings per city
        String totalListings = "SELECT city, COUNT(*) AS LISTINGS FROM LISTINGS GROUP BY city";
        ResultSet rs = stmt.executeQuery(totalListings);
        System.out.println("Total listings by city:");
        System.out.println("----------------------------------");
        while (rs.next()) {
            // Hosts with more than 10% of listings by city
            String hostsByCity = "SELECT U.name, L.city, COUNT(*) AS LISTINGS FROM USERS U, HOSTS H, LISTINGS L WHERE U.uid = H.uid AND H.hid = L.hid" +
            " AND L.city = '" + rs.getString("city") + "'" +
            " GROUP BY U.name, L.city HAVING COUNT(*) >= " + (rs.getInt("LISTINGS") * 0.1) +
            " ORDER BY LISTINGS DESC";
            double total = rs.getDouble("LISTINGS");
            ResultSet sub_rs = stmt2.executeQuery(hostsByCity);
            System.out.println("Hosts with more than 10% of listings in " + rs.getString("city") + ":");
            System.out.println("----------------------------------");
            while (sub_rs.next()) {
                System.out.println("Name: " + sub_rs.getString("name"));
                System.out.println("City: " + sub_rs.getString("city"));
                System.out.println("Listing(s): " + sub_rs.getString("LISTINGS"));
                System.out.println("Percentage: " + (sub_rs.getDouble("LISTINGS") / total) * 100 + "%");
                System.out.println("----------------------------------");
            }
        }

        // Total number of listings per country
        String totalListingsByCountry = "SELECT country, COUNT(*) AS LISTINGS FROM LISTINGS GROUP BY country";
        rs = stmt.executeQuery(totalListingsByCountry);
        System.out.println("Total listings by country:");
        System.out.println("----------------------------------");
        while (rs.next()) {
            // Hosts with more than 10% of listings by country
            String hostsByCountry = "SELECT U.name, L.country, COUNT(*) AS LISTINGS FROM USERS U, HOSTS H, LISTINGS L WHERE U.uid = H.uid AND H.hid = L.hid" +
            " AND L.country = '" + rs.getString("country") + "'" +
            " GROUP BY U.name, L.country HAVING COUNT(*) >= " + (rs.getInt("LISTINGS") * 0.1) +
            " ORDER BY LISTINGS DESC";
            double total = rs.getDouble("LISTINGS");
            ResultSet sub_rs = stmt2.executeQuery(hostsByCountry);
            System.out.println("Hosts with more than 10% of listings in " + rs.getString("country") + ":");
            System.out.println("----------------------------------");
            while (sub_rs.next()) {
                System.out.println("Name: " + sub_rs.getString("name"));
                System.out.println("Country: " + sub_rs.getString("country"));
                System.out.println("Listing(s): " + sub_rs.getString("LISTINGS"));
                System.out.println("Percentage: " + (sub_rs.getDouble("LISTINGS") / total) * 100 + "%");
                System.out.println("----------------------------------");
            }
        }
    }

    static void reportRenters(Scanner scanner, Statement stmt, Statement stmt2) throws Exception {
        // Get date range
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

        // Number of bookings within date range
        String bookings = "SELECT R.ren_id, COUNT(*) AS BOOKINGS FROM RESERVATIONS R WHERE R.start_date >= '" + startDate + "' AND R.end_date <= '" + endDate + "'" +
        " GROUP BY R.ren_id";
        ResultSet rs = stmt.executeQuery(bookings);
        System.out.println("Number of Bookings between " + startDate + " and " + endDate + ":");
        System.out.println("----------------------------------");
        int count = 1;
        while (rs.next()) {
            System.out.println("Rank: " + count);
            System.out.println("Renter ID: " + rs.getString("ren_id"));
            System.out.println("Bookings: " + rs.getString("BOOKINGS"));
            System.out.println("----------------------------------");
            count++;
        }

        // By city
        // All listing cities
        String allCities = "SELECT DISTINCT city FROM LISTINGS l WHERE l.lid IN (SELECT lid FROM RESERVATIONS WHERE start_date >= '" + startDate + "' AND end_date <= '" + endDate + "')";
        rs = stmt.executeQuery(allCities);
        System.out.println("Cities:");
        System.out.println("----------------------------------");
        while (rs.next()) {
            String city = rs.getString("city");
            // Rank renters by total bookings per city
            String rentersByCity = "SELECT U.name, L.city, COUNT(*) AS BOOKINGS FROM USERS U, RENTERS R, LISTINGS L, RESERVATIONS RE WHERE U.uid = R.uid AND R.ren_id = RE.ren_id AND RE.lid = L.lid" +
            " AND L.city = '" + city + "'" +
            " GROUP BY U.name, L.city ORDER BY BOOKINGS DESC";

            ResultSet sub_rs = stmt2.executeQuery(rentersByCity);
            System.out.println("Renters with bookings in " + city + ":");
            System.out.println("----------------------------------");
            count = 1;
            while (sub_rs.next()) {
                System.out.println("Rank: " + count);
                System.out.println("Name: " + sub_rs.getString("name"));
                System.out.println("City: " + sub_rs.getString("city"));
                System.out.println("Booking(s): " + sub_rs.getString("BOOKINGS"));
                System.out.println("----------------------------------");
                count++;
            }
        }
    }

    static void reportRentersGT2B(Scanner scanner, Statement stmt) throws Exception {
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

        // Number of bookings within date range
        String bookings = "SELECT R.ren_id, COUNT(*) AS BOOKINGS FROM RESERVATIONS R WHERE R.start_date >= '" + startDate + "' AND R.end_date <= '" + endDate + "'" +
        " GROUP BY R.ren_id" + 
        " HAVING COUNT(*) >= 2";
        ResultSet rs = stmt.executeQuery(bookings);
        System.out.println("Number of Bookings between " + startDate + " and " + endDate + " (with at least 2 bookings):");
        System.out.println("----------------------------------");
        int count = 1;
        while (rs.next()) {
            System.out.println("Rank: " + count);
            System.out.println("Renter ID: " + rs.getString("ren_id"));
            System.out.println("Bookings: " + rs.getString("BOOKINGS"));
            System.out.println("----------------------------------");
            count++;
        }
    }

    static void reportCancellations(Scanner scanner, Statement stmt, Statement stmt2) throws Exception {
        System.out.println("----------------------------------");
        // Get all years with cancellations for hosts
        String allYears = "SELECT DISTINCT YEAR(start_date) AS YEAR FROM CALENDAR WHERE status = 'cancelled' AND ren_id IS NULL" +
        " UNION SELECT DISTINCT YEAR(end_date) AS YEAR FROM CALENDAR WHERE status = 'cancelled' AND ren_id IS NULL";
        ResultSet rs = stmt.executeQuery(allYears);
        while (rs.next()) {
            int year = rs.getInt("YEAR");
            // Rank hosts by total cancellations per year
            String hostsByYear = "SELECT U.name, YEAR(C.start_date) AS YEAR, COUNT(*) AS CANCELLATIONS FROM USERS U, HOSTS H, CALENDAR C WHERE U.uid = H.uid AND H.hid = C.hid" +
            " AND YEAR(C.start_date) = " + year +
            " AND C.status = 'cancelled'" +
            " GROUP BY U.name, YEAR(C.start_date) UNION" +
            " SELECT U.name, YEAR(C.end_date) AS YEAR, COUNT(*) AS CANCELLATIONS FROM USERS U, HOSTS H, CALENDAR C WHERE U.uid = H.uid AND H.hid = C.hid" +
            " AND YEAR(C.end_date) = " + year +
            " AND C.status = 'cancelled'" +
            " GROUP BY U.name, YEAR(C.end_date) ORDER BY YEAR, CANCELLATIONS DESC LIMIT 1";

            ResultSet sub_rs = stmt2.executeQuery(hostsByYear);
            System.out.println("The Host with the most cancellations in " + year + ":");
            while (sub_rs.next()) {
                System.out.println("Name: " + sub_rs.getString("name"));
                System.out.println("Cancellation(s): " + sub_rs.getString("CANCELLATIONS"));
                System.out.println("----------------------------------");
            }
        }

        // Get all years with cancellations for renters
        allYears = "SELECT DISTINCT YEAR(start_date) AS YEAR FROM CALENDAR WHERE status = 'cancelled' AND hid IS NULL" +
        " UNION SELECT DISTINCT YEAR(end_date) AS YEAR FROM CALENDAR WHERE status = 'cancelled' AND hid IS NULL";
        rs = stmt.executeQuery(allYears);
        while(rs.next()) {
            int year = rs.getInt("YEAR");
            // Rank renters by total cancellations per year
            String rentersByYear = "SELECT U.name, YEAR(C.start_date) AS YEAR, COUNT(*) AS CANCELLATIONS FROM USERS U, RENTERS R, CALENDAR C WHERE U.uid = R.uid AND R.ren_id = C.ren_id" +
            " AND YEAR(C.start_date) = " + year +
            " AND C.status = 'cancelled'" +
            " GROUP BY U.name, YEAR(C.start_date) UNION" +
            " SELECT U.name, YEAR(C.end_date) AS YEAR, COUNT(*) AS CANCELLATIONS FROM USERS U, RENTERS R, CALENDAR C WHERE U.uid = R.uid AND R.ren_id = C.ren_id" +
            " AND YEAR(C.end_date) = " + year +
            " AND C.status = 'cancelled'" +
            " GROUP BY U.name, YEAR(C.end_date) ORDER BY YEAR, CANCELLATIONS DESC LIMIT 1";

            ResultSet sub_rs = stmt2.executeQuery(rentersByYear);
            System.out.println("The Renter with the most cancellations in " + year + ":");
            while (sub_rs.next()) {
                System.out.println("Name: " + sub_rs.getString("name"));
                System.out.println("Cancellation(s): " + sub_rs.getString("CANCELLATIONS"));
                System.out.println("----------------------------------");
            }
        }
    }

    static void reportNounPhrases(Scanner scanner, Statement stmt, Statement stmt2) throws Exception {
        
        // Get all listings with comments
        String allListings = "SELECT DISTINCT LC.lid FROM COMMENTS C, LISTING_COMMENTS LC WHERE C.cid = LC.cid";
        ResultSet rs = stmt.executeQuery(allListings);
        while (rs.next()) {
            HashMap<String, Integer> nounPhrases = new HashMap<String, Integer>();
            int lid = rs.getInt("lid");
            System.out.println("Getting noun phrases for listing id " + lid);
            System.out.println("----------------------------------");
            // Get all comments for listing
            String comments = "SELECT comment FROM COMMENTS C, LISTING_COMMENTS LC WHERE C.cid = LC.cid AND lid = " + lid;
            ResultSet sub_rs = stmt2.executeQuery(comments);
            while (sub_rs.next()) {
                String comment = sub_rs.getString("comment");
                // Get rid of punctuation
                comment = comment.replaceAll("[^a-zA-Z0-9 ]", "");
                // Split comment into words
                String[] words = comment.split(" ");

                for (int i = 0; i < words.length; i++) {
                    // If noun phrase is already in hashmap
                    if (nounPhrases.containsKey(words[i])) {
                        nounPhrases.put(words[i], nounPhrases.get(words[i]) + 1);
                    }
                    else {
                        nounPhrases.put(words[i], 1);
                    }
                }
            }
            // Print noun phrases
            for (String key : nounPhrases.keySet()) {
                System.out.println("Noun Phrase: " + key);
                System.out.println("Count: " + nounPhrases.get(key));
                System.out.println("----------------------------------");
            }
        }
    }
    public static void main(String[] args) throws Exception {
        Class.forName(JDBC_DRIVER);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Reports:");
        while (true) {
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                    Statement stmt = conn.createStatement();
                    Statement stmt2 = conn.createStatement();   // For nested Result Sets
                    System.out.println("Choose an option:");
                    System.out.println("1: Report number of bookings");                                         
                    System.out.println("2: Report total number of listings");
                    System.out.println("3: Report hosts by number of listings");
                    System.out.println("4: Report hosts that have >10% of listings in a city or country");     
                    System.out.println("5: Report renters by number of bookings");                             
                    System.out.println("6: Report renters by number of bookings (later report)");               
                    System.out.println("7: Report hosts and renters with the most cancellations within a year");
                    System.out.println("8: Report popular noun phrases in comments");                           // Implemented all words instead of just noun phrases
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
                            reportHosts(scanner, stmt, stmt2);
                            break;
                        case 4:
                            reportHostsGT10P(scanner, stmt, stmt2);
                            break;
                        case 5:
                            reportRenters(scanner, stmt, stmt2);
                            break;
                        case 6:
                            reportRentersGT2B(scanner, stmt);
                            break;
                        case 7:
                            reportCancellations(scanner, stmt, stmt2);
                            break;
                        case 8:
                            reportNounPhrases(scanner, stmt, stmt2);
                            break;
                        default:
                            break;
                    }
            }
            catch (Exception e) {
                System.out.println(e);
                System.out.println("Invalid input or database error please try again");
            }
        }
    }
}