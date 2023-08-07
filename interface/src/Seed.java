import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Seed {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/c43";
    static final String DB_USER = "root";
    static final String DB_PASS = "pw";

    public static void main(String[] args) throws Exception {
        Class.forName(JDBC_DRIVER);
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            System.out.println("Connected to database");
            Statement stmt = conn.createStatement();

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
            for (String drop:drops) {
                stmt.executeUpdate(drop);
            }
            System.out.println("Database reset");

            String unignore = "SET FOREIGN_KEY_CHECKS = 1;";
            stmt.executeUpdate(unignore);
            
            String users = "INSERT INTO USERS (name, sin, address, occupation, dob) VALUES " +
            "('Liam Johnson', '123456789', '5 Pin Oak Lane Blackville, NB E9B 1Y5', 'Software Engineer', '1990-01-15')," +
            "('Olivia Smith', '987654321', '700 Rocky River Ave. Mercier, QC J6R 1M8', 'Nurse Practitioner', '1985-02-28')," +
            "('Noah Williams', '456789123', '876 Lees Creek Lane Brantville, NB E9H 0X9', 'Marketing Manager', '1998-03-12')," +
            "('Emma Brown', '789123456', '9296 Old Addison Dr. Tantallon, NS B3Z 1M5', 'Electrician', '2001-04-04')," +
            "('Elijah Jones', '321654987', '119 Taylor St. Innisfil, ON L9S 4H7', 'Graphic Designer', '1993-05-22')," +
            "('Ava Garcia', '987123654', '91 East Pennsylvania Street Kitimat, BC V8C 0K7', 'Accountant', '1982-06-10')," +
            "('James Miller', '654321987', '638 N. Cedarwood Lane Strathmore, AB T1P 4S3', 'Teacher', '1995-07-06')," +
            "('Iseblla Williams', '258369147', '203 Sierra St. Christmas Island, NS B1T 4R4', 'Chef', '1997-08-17')," +
            "('Benjamin Anderson', '963852741', '9690 S. County St. Sioux Lookout, ON P8T 7T4', 'Sales Representative', '1989-09-30')," +
            "('Sophia Taylor', '741852963', '32 Walnutwood Ave. Innisfail, AB T4G 9C1', 'Mechanical Engineer', '2003-10-08')," +
            "('David Wilson', '321987654', '4567 Pine Road', 'Accountant', '1992-02-10');";
            stmt.executeUpdate(users);
            System.out.println("Users seeded");

            String hosts = "INSERT INTO HOSTS (uid) VALUES" +
            "(2)," +
            "(6)," +
            "(7)," +
            "(11);";
            stmt.executeUpdate(hosts);
            System.out.println("Hosts seeded");

            String renters = "INSERT INTO RENTERS (uid) VALUES" +
            "(1)," +
            "(3)," +
            "(4)," +
            "(5)," +
            "(8)," +
            "(9)," +
            "(10);";
            stmt.executeUpdate(renters);
            System.out.println("Renters seeded");

            String listings = "INSERT INTO LISTINGS (price, longitude, latitude, type, hid, address, postal_code, city, country) VALUES" +
            "(410, -122.947106, 50.117276, 'House', 1, '4573 Chateau Blvd', 'BC V0N 1B4', 'Whistler', 'Canada')," +
            "(200, -114.015641, 50.990937, 'Apartment', 2, '7005 18 St SE', 'AB T2C 1K1', 'Calgary', 'Canada')," +
            "(300, -80.52287, 43.466432, 'Single Room', 3, '47 King St N', 'ON N2J 2W9', 'Waterloo', 'Canada')," +
            "(600, -80.72176, 35.11555, 'Apartment', 3, '4535 St. John Street', 'SK S4P 3Y2', 'Bruno', 'Canada')," +
            "(300, 107.996300 , 13.993440, 'House', 1, '131-1, Gamjeon-dong', NULL, 'Sasang-gu', 'Korea')," + 
            "(200, -122.4194, 37.7749, 'Apartment', 1, '123 Main Street', '12345', 'San Francisco', 'USA')," +
            "(150, -118.2437, 34.0522, 'House', 2, '456 Elm Avenue', '67890', 'San Francisco', 'USA')," +
            "(300, -0.128, 51.5074, 'Condo', 2, '789 Park Lane', 'SW1A 1AA', 'London', 'UK')," +
            "(180, -74.006, 40.7128, 'Apartment', 2, '101 Broadway Avenue', '54321', 'New York', 'USA')," +
            "(250, -42.877960, -22.756750, 'Townhouse', 3, '248-1225, Atago', NULL, 'Minato-ku', 'Japan')," +
            "(400, -44.291300, -23.008780, 'House', 4, '239-1024, Iijima', NULL, 'Hokota-shi', 'Japan'); ";
            stmt.executeUpdate(listings);

            String amenities = "INSERT INTO amenities (name) VALUES" +
            "('Free Wi-Fi')," +
            "('Air Conditioning')," +
            "('Fully Equipped Kitchen')," +
            "('Swimming Pool')," +
            "('Private Balcony')," +
            "('Gym Access')," +
            "('Pet-Friendly')," +
            "('Free Parking')," +
            "('Washer and Dryer')," +
            "('Smart TV')," +
            "('Fireplace')," +
            "('Ocean View')," +
            "('Hot Tub')," +
            "('Outdoor BBQ Area')," +
            "('Child-Friendly')," +
            "('Bicycles for Guest Use')," +
            "('24/7 Concierge Service')," +
            "('Central Heating')," +
            "('Garden or Patio')," +
            "('Spa and Wellness Facilities');";
            stmt.executeUpdate(amenities);
            System.out.println("Amenities seeded");

            String listing_amenities = "INSERT INTO LISTING_AMENITIES (lid, aid) VALUES" +
            "(1, 3)," +
            "(1, 4)," +
            "(1, 6)," +
            "(1, 8)," +
            "(1, 11)," +
            "(2, 1)," +
            "(2, 2)," +
            "(2, 3)," +
            "(2, 4)," +
            "(2, 5)," +
            "(2, 6)," +
            "(2, 7)," +
            "(2, 8)," +
            "(2, 9)," +
            "(2, 10)," +
            "(2, 11)," +
            "(2, 12)," +
            "(2, 13)," +
            "(2, 14)," +
            "(2, 15)," +
            "(2, 16)," +
            "(2, 17)," +
            "(2, 18)," +
            "(2, 19)," +
            "(2, 20);";
            stmt.executeUpdate(listing_amenities);
            System.out.println("Listing amenities seeded");

            System.out.println("Listings seeded");

            String reservations = "INSERT INTO RESERVATIONS (ren_id, lid, start_date, end_date) VALUES " +
            "(1, 1, '2019-01-01', '2019-01-02'),"+
            "(3, 1, '2019-01-04', '2019-01-07'),"+
            "(3, 2, '2020-03-13', '2020-03-21');";
            stmt.executeUpdate(reservations);
            System.out.println("Reservations seeded");

            String calendar = "INSERT INTO CALENDAR (lid, start_date, end_date, hid, ren_id, status) VALUES " +
            "(1, '2019-01-01', '2019-01-02', NULL, 1, 'booked')," +
            "(2, '2019-01-04', '2019-01-07', NULL, 3, 'booked')," +
            "(1, '2021-03-10', '2021-03-23', NULL, 5, 'cancelled')," +
            "(3, '2021-11-04', '2021-11-08', NULL, 4, 'cancelled')," +
            "(3, '2022-12-04', '2022-12-25', NULL, 2, 'cancelled')," +
            "(2, '2022-12-25', '2023-01-03', NULL, 2, 'cancelled')," +
            "(1, '2021-07-10', '2021-07-15', 1, NULL, 'cancelled')," +
            "(2, '2021-08-09', '2021-08-12', 2, NULL, 'cancelled')," +
            "(3, '2021-12-29', '2022-01-07', 3, NULL, 'cancelled');";
            stmt.executeUpdate(calendar);
            System.out.println("Calendar seeded");

            String comments = "INSERT INTO COMMENTS (rating, comment) VALUES " +
            "(5, 'We absolutely loved our stay at this charming house in Whistler! The location was perfect, and the cozy atmosphere made us feel right at home.')," +
            "(4, 'The apartment in Calgary was a great find. The modern amenities and convenient location made our trip enjoyable and hassle-free.')," +
            "(4.5, 'Our stay in the single room in Waterloo was fantastic. The host was incredibly welcoming, and the room had everything we needed for a comfortable stay.')," +
            "(5, 'The apartment in Bruno was simply stunning. The spacious layout and beautiful views made it a perfect retreat for our vacation.')," +
            "(4.7, 'We had an amazing time at the house in Sasang-gu, Korea. The unique design and local charm added a special touch to our trip.')," +
            "(4.8, 'The apartment on Main Street in San Francisco was fantastic. The central location made it easy for us to explore the city, and the amenities were top-notch.')," +
            "(4.9, 'The house on Elm Avenue was a dream come true. The spacious layout and beautiful garden provided a relaxing and enjoyable getaway.')," +
            "(5, 'Our stay in the condo in London was exceptional. The luxurious amenities and prime location allowed us to experience the best of the city.')," +
            "(4.5, 'The apartment in New York exceeded our expectations. The modern decor and stunning city views made it a memorable and comfortable stay.')," +
            "(4.6, 'We had a wonderful time at the townhouse in Minato-ku, Japan. The traditional design and convenient amenities offered a unique and delightful experience.')," +
            "(4.8, 'The house in Hokota-shi, Japan, was the perfect retreat. The peaceful surroundings and thoughtful touches made it a truly relaxing and enjoyable stay.');";
            stmt.executeUpdate(comments);
            System.out.println("Comments seeded");

            String listing_comments = "INSERT INTO LISTING_COMMENTS (lid, cid, ren_id) VALUES " +
            "(1, 1, 1)," +
            "(2, 2, 2)," +
            "(3, 3, 4)," +
            "(4, 4, 4)," +
            "(5, 5, 2)," +
            "(6, 6, 1)," +
            "(7, 7, 5)," +
            "(8, 8, 4)," +
            "(9, 9, 2)," +
            "(10, 10, 3)," +
            "(11, 11, 6);";
            stmt.executeUpdate(listing_comments);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}