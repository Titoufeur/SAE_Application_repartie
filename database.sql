CREATE SEQUENCE restaurants_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE reservations_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE Restaurants (
    id INT DEFAULT restaurants_seq.NEXTVAL PRIMARY KEY,
    name VARCHAR2(255) NOT NULL,
    address VARCHAR2(255) NOT NULL,
    gps_coordinates VARCHAR2(255) NOT NULL
);

CREATE TABLE Reservations (
    id INT PRIMARY KEY,
    restaurant_id INT,
    first_name VARCHAR2(255) NOT NULL,
    last_name VARCHAR2(255) NOT NULL,
    num_guests INT NOT NULL,
    phone VARCHAR2(255) NOT NULL,
    FOREIGN KEY (restaurant_id) REFERENCES Restaurants(id)
);

INSERT ALL
    INTO Restaurants (id, name, address, gps_coordinates) VALUES (restaurants_seq.NEXTVAL, 'Le Bistronome', '19 Rue Saint-Michel, Nancy', '48.69694894234496, 6.178183184660482')
    INTO Restaurants (id, name, address, gps_coordinates) VALUES (restaurants_seq.NEXTVAL, 'L''excelsior', '50 Rue Henri-Poincaré, Nancy', '48.69196510050854, 6.175805522648185')
    INTO Restaurants (id, name, address, gps_coordinates) VALUES (restaurants_seq.NEXTVAL, 'Le Dizneuf', '19 Pl. Henri Mengin, Nancy', '48.68969875199859, 6.182071161481133')
SELECT 1 FROM DUAL;

SELECT * FROM RESTAURANTS

