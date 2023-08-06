-- Active: 1691267127016@@127.0.0.1@3306@c43
INSERT INTO USERS (name, sin, address, occupation, dob) VALUES
    ('Liam Johnson', '123-456-789', '5 Pin Oak Lane Blackville, NB E9B 1Y5', 'Software Engineer', '1990-01-15'),
    ('Olivia Smith', '987-654-321', '700 Rocky River Ave. Mercier, QC J6R 1M8', 'Nurse Practitioner', '1985-02-28'),
    ('Noah Williams', '456-789-123', '876 Lees Creek Lane Brantville, NB E9H 0X9', 'Marketing Manager', '1998-03-12'),
    ('Emma Brown', '789-123-456', '9296 Old Addison Dr. Tantallon, NS B3Z 1M5', 'Electrician', '2001-04-04'),
    ('Elijah Jones', '321-654-987', '119 Taylor St. Innisfil, ON L9S 4H7', 'Graphic Designer', '1993-05-22'),
    ('Ava Garcia', '987-123-654', '91 East Pennsylvania Street Kitimat, BC V8C 0K7', 'Accountant', '1982-06-10'),
    ('James Miller', '654-321-987', '638 N. Cedarwood Lane Strathmore, AB T1P 4S3', 'Teacher', '1995-07-06'),
    ('Iseblla Williams', '258-369-147', '203 Sierra St. Christmas Island, NS B1T 4R4', 'Chef', '1997-08-17'),
    ('Benjamin Anderson', '963-852-741', '9690 S. County St. Sioux Lookout, ON P8T 7T4', 'Sales Representative', '1989-09-30'),
    ('Sophia Taylor', '741-852-963', '32 Walnutwood Ave. Innisfail, AB T4G 9C1', 'Mechanical Engineer', '2003-10-08');

INSERT INTO HOSTS (uid) VALUES
    (2),
    (7),
    (6);

INSERT INTO RENTERS (uid) VALUES
    (1),
    (3),
    (4),
    (5),
    (8),
    (9),
    (10);

INSERT INTO LISTINGS (price, longitude, latitude, type, hid, address) VALUES
    (410, -122.947106, 50.117276, 'House', 1, '4573 Chateau Blvd, BC V0N 1B4'),
    (200, -114.015641, 50.990937, 'Apartment', 2, '7005 18 St SE, AB T2C 1K1'),
    (300, -80.52287, 43.466432, 'Apartment', 3, '47 King St N, ON N2J 2W9');

INSERT INTO amenities (name) VALUES
    ('Free Wi-Fi'),
    ('Air Conditioning'),
    ('Fully Equipped Kitchen'),
    ('Swimming Pool'),
    ('Private Balcony'),
    ('Gym Access'),
    ('Pet-Friendly'),
    ('Free Parking'),
    ('Washer and Dryer'),
    ('Smart TV'),
    ('Fireplace'),
    ('Ocean View'),
    ('Hot Tub'),
    ('Outdoor BBQ Area'),
    ('Child-Friendly'),
    ('Bicycles for Guest Use'),
    ('24/7 Concierge Service'),
    ('Central Heating'),
    ('Garden or Patio'),
    ('Spa and Wellness Facilities');

INSERT INTO LISTING_AMENITIES (lid, aid) VALUES
    (1, 3),
    (1, 4),
    (1, 6),
    (1, 8),
    (1, 11),
    (2, 1),
    (2, 2),
    (2, 3),
    (2, 4),
    (2, 5),
    (2, 6),
    (2, 7),
    (2, 8),
    (2, 9),
    (2, 10),
    (2, 11),
    (2, 12),
    (2, 13),
    (2, 14),
    (2, 15),
    (2, 16),
    (2, 17),
    (2, 18),
    (2, 19),
    (2, 20);

INSERT INTO RESERVATIONS (ren_id, lid, start_date, end_date) 
SELECT 4, 1, '2019-01-05', '2019-01-07' 
WHERE '2019-01-05' >= CURDATE()
AND '2019-01-07' >= CURDATE()
AND NOT EXISTS (SELECT * FROM RESERVATIONS WHERE lid = 1 AND start_date <= '2019-01-04' AND end_date >= '2019-01-01');