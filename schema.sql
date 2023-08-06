DROP TABLE IF EXISTS ListingComments;
DROP TABLE IF EXISTS ListingAmenities;
DROP TABLE IF EXISTS Listings;
DROP TABLE IF EXISTS Hosts;
DROP TABLE IF EXISTS Renters;
DROP TABLE IF EXISTS Reservations;
DROP TABLE IF EXISTS Comments;
DROP TABLE IF EXISTS Amenities;
DROP TABLE IF EXISTS Users; 
DROP TABLE IF EXISTS RenterComments;

CREATE TABLE Listings (
    lid INTEGER,
    price INTEGER,
    latitude DECIMAL(9,6),
    longitude DECIMAL(9,6),
    type VARCHAR(20),
    hid INTEGER,
    address VARCHAR(20),
    PRIMARY KEY (lid),
    FOREIGN KEY (hid) REFERENCES Hosts
);

CREATE TABLE Amenities (
    aid INTEGER,
    name VARCHAR(40),
    PRIMARY KEY (aid)
);

CREATE TABLE ListingAmenities (
    lid INTEGER,
    aid INTEGER,
    PRIMARY KEY (lid, aid),
    FOREIGN KEY (lid) REFERENCES Listings,
    FOREIGN KEY (aid) REFERENCES Amenities
);

CREATE TABLE Users (
    uid INTEGER,
    name VARCHAR(50),
    dob DATE,
    address VARCHAR(50),
    sin VARCHAR(11),
    occupation VARCHAR(30),
    PRIMARY KEY (uid)
);

CREATE TABLE Hosts (
    hid INTEGER,
    uid INTEGER,
    PRIMARY KEY (hid),
    FOREIGN KEY (uid) REFERENCES Users
);

CREATE TABLE Renters (
    ren_id INTEGER,
    uid INTEGER,
    PRIMARY KEY (ren_id),
    FOREIGN KEY (uid) REFERENCES Users
);

CREATE TABLE Reservations (
    res_id INTEGER,
    ren_id INTEGER,
    lid INTEGER,
    start_date DATE,
    end_date DATE,
    PRIMARY KEY (res_id),
    FOREIGN KEY (ren_id) REFERENCES Renter,
    FOREIGN KEY (lid) REFERENCES Listings,
    CHECK (start_date < end_date)
);

CREATE TABLE Calendar (
    lid INTEGER,
    start_date DATE,
    end_date DATE,
    PRIMARY KEY (lid, start_date, end_date),
    hid INTEGER,
    ren_id INTEGER,
    status VARCHAR(20),
    FOREIGN KEY (lid) REFERENCES Listings,
    FOREIGN KEY (hid) REFERENCES Hosts,
    FOREIGN KEY (ren_id) REFERENCES Renters
);

CREATE TABLE Comments (
    rating INTEGER,
    comment VARCHAR(500),
    cid INTEGER,
    PRIMARY KEY (cid),
    CHECK (rating >= 0 AND rating <= 5)
);

CREATE TABLE ListingComments (
    lid INTEGER,
    cid INTEGER,
    ren_id INTEGER,
    PRIMARY KEY (lid, cid),
    FOREIGN KEY (lid) REFERENCES Listings,
    FOREIGN KEY (cid) REFERENCES Comments,
    FOREIGN KEY (ren_id) REFERENCES Renter
);

CREATE TABLE RenterComments (
    ren_id INTEGER,
    cid INTEGER,
    hid INTEGER,
    PRIMARY KEY (ren_id, cid),
    FOREIGN KEY (ren_id) REFERENCES Renter,
    FOREIGN KEY (cid) REFERENCES Comments,
    FOREIGN KEY (hid) REFERENCES Hosts
);