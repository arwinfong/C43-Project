    import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Schema {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/c43";
    static final String DB_USER = "java";
    static final String DB_PASS = "password";

    public static void main(String[] args) throws Exception {
        Class.forName(JDBC_DRIVER);
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            System.out.println("Connected to database");
            Statement stmt = conn.createStatement();

            String drop_tables = "DROP TABLE IF EXISTS USERS, HOSTS, RENTERS, LISTINGS, AMENITIES, LISTING_AMENITIES, RESERVATIONS, COMMENTS, LISTING_COMMENTS, RENTER_COMMENTS, CALENDAR;";
            stmt.executeUpdate(drop_tables);
            System.out.println("Tables dropped");

            String users_table = "CREATE TABLE USERS" +
                          "(uid INTEGER not NULL AUTO_INCREMENT, " +
                          " name VARCHAR(50), " +
                          " dob DATE, " +
                          " address VARCHAR(50), " +
                          " sin CHAR(9), " +
                          " occupation VARCHAR(30), " +
                          " PRIMARY KEY ( uid )); ";
            stmt.executeUpdate(users_table);
            System.out.println("Users table created");

            String hosts_table = "CREATE TABLE HOSTS" +
                            "(hid INTEGER not NULL AUTO_INCREMENT, " +
                            " uid INTEGER not NULL, " +
                            " PRIMARY KEY ( hid ), " +
                            " FOREIGN KEY ( uid ) REFERENCES USERS ( uid ) " +
                            " ON DELETE CASCADE ); ";
            stmt.executeUpdate(hosts_table);
            System.out.println("Hosts table created");

            String renters_table = "CREATE TABLE RENTERS" +
                            "(ren_id INTEGER not NULL AUTO_INCREMENT, " +
                            " uid INTEGER not NULL, " +
                            " payment_info VARCHAR(50)," +
                            " PRIMARY KEY ( ren_id ), " +
                            " FOREIGN KEY ( uid ) REFERENCES USERS ( uid ) " +
                            " ON DELETE CASCADE ); ";
            stmt.executeUpdate(renters_table);
            System.out.println("Renters table created");

            String listings_table =  "CREATE TABLE LISTINGS" +
                          "(lid INTEGER not NULL AUTO_INCREMENT, " +
                          " price INTEGER, " +
                          " latitude DECIMAL(9,6), " +
                          " longitude DECIMAL(9,6), " +
                          " type VARCHAR(20), " +
                          " hid INTEGER, " +
                          " city VARCHAR(50), " +
                          " country VARCHAR(50), " +
                          " postal_code VARCHAR(50), " +
                          " address VARCHAR(50), " +
                          " PRIMARY KEY ( lid ), " +
                          " FOREIGN KEY ( hid ) REFERENCES HOSTS ( hid ) " +
                          " ON DELETE CASCADE ); ";
            stmt.executeUpdate(listings_table);
            System.out.println("Listings table created");

            String amenities_table = "CREATE TABLE AMENITIES" +
                          "(aid INTEGER not NULL AUTO_INCREMENT, " +
                          " name VARCHAR(40), " +
                          " PRIMARY KEY ( aid )); ";
            stmt.executeUpdate(amenities_table);
            System.out.println("Amenities table created");

            String listing_amenities_table = "CREATE TABLE LISTING_AMENITIES" +
                          "(lid INTEGER not NULL AUTO_INCREMENT, " +
                          " aid INTEGER not NULL, " +
                          " PRIMARY KEY ( lid, aid ), " +
                          " FOREIGN KEY ( lid ) REFERENCES LISTINGS ( lid ) " +
                          " ON DELETE CASCADE, " +
                          " FOREIGN KEY ( aid ) REFERENCES AMENITIES ( aid ) " +
                          " ON DELETE CASCADE ); ";
            stmt.executeUpdate(listing_amenities_table);
            System.out.println("Listing amenities table created");

            String reservations_table = "CREATE TABLE RESERVATIONS" +
                          "(res_id INTEGER not NULL AUTO_INCREMENT, " +
                          " ren_id INTEGER not NULL, " +
                          " lid INTEGER not NULL, " +
                          " start_date DATE, " +
                          " end_date DATE, " +
                          " availability BOOLEAN, " +
                          " PRIMARY KEY ( res_id ), " +
                          " FOREIGN KEY ( ren_id ) REFERENCES RENTERS ( ren_id ) " +
                          " ON DELETE CASCADE, " +
                          " FOREIGN KEY ( lid ) REFERENCES LISTINGS ( lid ) " +
                          " ON DELETE CASCADE, " +
                          " CHECK ( start_date < end_date )); ";
            stmt.executeUpdate(reservations_table);
            System.out.println("Reservations table created");

            String calendar_table = "CREATE TABLE Calendar" +
                            "(lid INTEGER not NULL, " +
                            " start_date DATE, " +
                            " end_date DATE, " +
                            " PRIMARY KEY (lid, start_date, end_date), " +
                            " hid INTEGER, " +
                            " ren_id INTEGER, " +
                            " status VARCHAR(20), " +
                            " FOREIGN KEY (lid) REFERENCES Listings ( lid ) " +
                            " ON DELETE CASCADE, " +
                            " FOREIGN KEY (hid) REFERENCES Hosts ( hid ) " +
                            " ON DELETE CASCADE, " +
                            " FOREIGN KEY (ren_id) REFERENCES Renters ( ren_id ) " +
                            " ON DELETE CASCADE) ";
            stmt.executeUpdate(calendar_table);
            System.out.println("Calendar table created");

            String comments_table = "CREATE TABLE COMMENTS" +
                          "(rating INTEGER, " +
                          " comment VARCHAR(500), " +
                          " cid INTEGER AUTO_INCREMENT, " +
                          " PRIMARY KEY ( cid ), " +
                          " CHECK ( rating >= 1 AND rating <= 5 )); ";
            stmt.executeUpdate(comments_table);
            System.out.println("Comments table created");

            String listing_comments_table = "CREATE TABLE LISTING_COMMENTS" +
                          "(lid INTEGER not NULL, " +
                          " cid INTEGER not NULL, " +
                          " ren_id INTEGER not NULL, " +
                          " PRIMARY KEY ( lid, cid ), " +
                          " FOREIGN KEY ( lid ) REFERENCES LISTINGS ( lid ) " +
                          " ON DELETE CASCADE, " +
                          " FOREIGN KEY ( cid ) REFERENCES COMMENTS ( cid ) " +
                          " ON DELETE CASCADE, " +
                          " FOREIGN KEY ( ren_id ) REFERENCES RENTERS ( ren_id ) " +
                          " ON DELETE CASCADE ); ";
            stmt.executeUpdate(listing_comments_table);
            System.out.println("Listing comments table created");

            String renter_comments_table = "CREATE TABLE RENTER_COMMENTS" +
                          "(ren_id INTEGER not NULL, " +
                          " cid INTEGER not NULL, " +
                          " hid INTEGER not NULL, " +
                          " PRIMARY KEY ( ren_id, cid ), " +
                          " FOREIGN KEY ( ren_id ) REFERENCES RENTERS ( ren_id ) " +
                          " ON DELETE CASCADE, " +
                          " FOREIGN KEY ( cid ) REFERENCES COMMENTS ( cid ) " +
                          " ON DELETE CASCADE, " +
                          " FOREIGN KEY ( hid ) REFERENCES HOSTS ( hid ) " +
                          " ON DELETE CASCADE ); ";
            stmt.executeUpdate(renter_comments_table);
            System.out.println("Renter comments table created");

            String ignore = "SET FOREIGN_KEY_CHECKS = 0;";
            stmt.executeUpdate(ignore);

            List<String> drops = new ArrayList<String>();
            drops.add("TRUNCATE TABLE HOSTS;"); 
            drops.add("TRUNCATE TABLE RENTERS;");
            drops.add("TRUNCATE TABLE RESERVATIONS;");
            drops.add("TRUNCATE TABLE LISTINGS;"); 
            drops.add("TRUNCATE TABLE LISTING_AMENITIES;");
            drops.add("TRUNCATE TABLE AMENITIES;");
            drops.add("TRUNCATE TABLE CALENDAR;");
            drops.add("TRUNCATE TABLE USERS;");
            drops.add("TRUNCATE TABLE LISTING_COMMENTS");
            for (String drop:drops) {
                stmt.executeUpdate(drop);
            }
            System.out.println("Database reset");
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}