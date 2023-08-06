    import java.sql.*;

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

            String drop_tables = "DROP TABLE IF EXISTS USERS, HOSTS, RENTERS, LISTINGS, AMENITIES, LISTING_AMENITIES, RESERVATIONS, COMMENTS, LISTING_COMMENTS, RENTER_COMMENTS;";
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
                            " FOREIGN KEY ( uid ) REFERENCES USERS ( uid ) ); ";
            stmt.executeUpdate(hosts_table);
            System.out.println("Hosts table created");

            String renters_table = "CREATE TABLE RENTERS" +
                            "(ren_id INTEGER not NULL AUTO_INCREMENT, " +
                            " uid INTEGER not NULL, " +
                            " PRIMARY KEY ( ren_id ), " +
                            " FOREIGN KEY ( uid ) REFERENCES USERS ( uid ) ); ";
            stmt.executeUpdate(renters_table);
            System.out.println("Renters table created");

            String listings_table =  "CREATE TABLE LISTINGS" +
                          "(lid INTEGER not NULL AUTO_INCREMENT, " +
                          " price INTEGER, " +
                          " latitude DECIMAL(9,6), " +
                          " longitude DECIMAL(9,6), " +
                          " type VARCHAR(20), " +
                          " hid INTEGER, " +
                          " address VARCHAR(50), " +
                          " PRIMARY KEY ( lid ), " +
                          " FOREIGN KEY ( hid ) REFERENCES HOSTS ( hid ));";
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
                          " FOREIGN KEY ( lid ) REFERENCES LISTINGS ( lid ), " +
                          " FOREIGN KEY ( aid ) REFERENCES AMENITIES ( aid )); ";
            stmt.executeUpdate(listing_amenities_table);
            System.out.println("Listing amenities table created");

            String reservations_table = "CREATE TABLE RESERVATIONS" +
                          "(res_id INTEGER not NULL AUTO_INCREMENT, " +
                          " ren_id INTEGER not NULL, " +
                          " lid INTEGER not NULL, " +
                          " start_date DATE, " +
                          " end_date DATE, " +
                          " PRIMARY KEY ( res_id ), " +
                          " FOREIGN KEY ( ren_id ) REFERENCES RENTERS ( ren_id ), " +
                          " FOREIGN KEY ( lid ) REFERENCES LISTINGS ( lid ), " +
                          " CHECK ( start_date < end_date )); ";
            stmt.executeUpdate(reservations_table);
            System.out.println("Reservations table created");

            String comments_table = "CREATE TABLE COMMENTS" +
                          "(rating INTEGER, " +
                          " comment VARCHAR(500), " +
                          " cid INTEGER AUTO_INCREMENT, " +
                          " PRIMARY KEY ( cid ), " +
                          " CHECK ( rating >= 0 AND rating <= 5 )); ";
            stmt.executeUpdate(comments_table);
            System.out.println("Comments table created");

            String listing_comments_table = "CREATE TABLE LISTING_COMMENTS" +
                          "(lid INTEGER not NULL, " +
                          " cid INTEGER not NULL, " +
                          " ren_id INTEGER not NULL, " +
                          " PRIMARY KEY ( lid, cid ), " +
                          " FOREIGN KEY ( lid ) REFERENCES LISTINGS ( lid ), " +
                          " FOREIGN KEY ( cid ) REFERENCES COMMENTS ( cid ), " +
                          " FOREIGN KEY ( ren_id ) REFERENCES RENTERS ( ren_id )); ";
            stmt.executeUpdate(listing_comments_table);
            System.out.println("Listing comments table created");

            String renter_comments_table = "CREATE TABLE RENTER_COMMENTS" +
                          "(ren_id INTEGER not NULL, " +
                          " cid INTEGER not NULL, " +
                          " hid INTEGER not NULL, " +
                          " PRIMARY KEY ( ren_id, cid ), " +
                          " FOREIGN KEY ( ren_id ) REFERENCES RENTERS ( ren_id ), " +
                          " FOREIGN KEY ( cid ) REFERENCES COMMENTS ( cid ), " +
                          " FOREIGN KEY ( hid ) REFERENCES HOSTS ( hid )); ";
            stmt.executeUpdate(renter_comments_table);
            System.out.println("Renter comments table created");
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}

// CREATE TABLE Reservations (
//     res_id INTEGER,
//     ren_id INTEGER,
//     lid INTEGER,
//     start_date DATE,
//     end_date DATE,
//     PRIMARY KEY (res_id),
//     FOREIGN KEY (ren_id) REFERENCES Renter,
//     FOREIGN KEY (lid) REFERENCES Listings,
//     CHECK (start_date < end_date)
// );


// CREATE TABLE Comments (
//     rating INTEGER,
//     comment VARCHAR(500),
//     cid INTEGER,
//     PRIMARY KEY (cid),
//     CHECK (rating >= 0 AND rating <= 5)
// );

// CREATE TABLE ListingComments (
//     lid INTEGER,
//     cid INTEGER,
//     ren_id INTEGER,
//     PRIMARY KEY (lid, cid),
//     FOREIGN KEY (lid) REFERENCES Listings,
//     FOREIGN KEY (cid) REFERENCES Comments,
//     FOREIGN KEY (ren_id) REFERENCES Renter
// );

// CREATE TABLE RenterComments (
//     ren_id INTEGER,
//     cid INTEGER,
//     hid INTEGER,
//     PRIMARY KEY (ren_id, cid),
//     FOREIGN KEY (ren_id) REFERENCES Renter,
//     FOREIGN KEY (cid) REFERENCES Comments,
//     FOREIGN KEY (hid) REFERENCES Hosts
// );