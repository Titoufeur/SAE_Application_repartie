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

Insert INTO Restaurants (id, name, address, gps_coordinates) VALUES (restaurants_seq.NEXTVAL, 'Le Bistronome', '19 Rue Saint-Michel, Nancy', '48.69694894234496, 6.178183184660482');
Insert INTO Restaurants (id, name, address, gps_coordinates) VALUES (restaurants_seq.NEXTVAL, 'L''excelsior', '50 Rue Henri-Poincar?, Nancy', '48.69196510050854, 6.175805522648185');
Insert INTO Restaurants (id, name, address, gps_coordinates) VALUES (restaurants_seq.NEXTVAL, 'Le Dizneuf', '19 Pl. Henri Mengin, Nancy', '48.68969875199859, 6.182071161481133');

Insert INTO Restaurants (id, name, address, gps_coordinates) VALUES (restaurants_seq.NEXTVAL, 'Chez Gusto',  '1 Place Stanislas, Nancy', '48.692257980675705, 6.1836192117386');
Insert INTO Restaurants (id, name, address, gps_coordinates) VALUES (restaurants_seq.NEXTVAL, 'Le Vaudémont',  '2 Rue de la Grande Corvée, Nancy', '48.692221342724274, 6.182155993903978');
Insert INTO Restaurants (id, name, address, gps_coordinates) VALUES (restaurants_seq.NEXTVAL, 'La Grange aux Coqs',  '19 Rue de la Monnaie, Nancy', '48.694118693698875, 6.174874570104981');
Insert INTO Restaurants (id, name, address, gps_coordinates) VALUES (restaurants_seq.NEXTVAL, 'Le Léopard',  '16 Rue Saint-Léon, Nancy', '48.69273090772251, 6.185417084078476');
Insert INTO Restaurants (id, name, address, gps_coordinates) VALUES (restaurants_seq.NEXTVAL, 'Le P''tit Marcel',  '13 Rue du Pont Mouja, Nancy', '48.69389144269114, 6.182960171528195');
Insert INTO Restaurants (id, name, address, gps_coordinates) VALUES (restaurants_seq.NEXTVAL, 'La Petite Table',  '7 Rue Gustave Simon, Nancy', '48.692824, 6.177896');
Insert INTO Restaurants (id, name, address, gps_coordinates) VALUES (restaurants_seq.NEXTVAL, 'Le Dix',  '10 Rue Gustave Simon, Nancy', '48.6929475, 6.1781667');
Insert INTO Restaurants (id, name, address, gps_coordinates) VALUES (restaurants_seq.NEXTVAL, 'Le Petit Corail',  '5 Rue des Michottes, Nancy', '48.6909385, 6.1720135');
Insert INTO Restaurants (id, name, address, gps_coordinates) VALUES (restaurants_seq.NEXTVAL, 'La Table du Bon Roi Stanislas',  '10 Rue Sainte-Catherine, Nancy,', '48.6910313, 6.1735966');
Insert INTO Restaurants (id, name, address, gps_coordinates) VALUES (restaurants_seq.NEXTVAL, 'Le 19',  '19 Rue des Ponts, Nancy', '48.6958503, 6.1868724');
Insert INTO Restaurants (id, name, address, gps_coordinates) VALUES (restaurants_seq.NEXTVAL, 'Chez Suzette',  '24 Rue Saint-Dizier, Nancy', '48.6961544, 6.1732695');
Insert INTO Restaurants (id, name, address, gps_coordinates) VALUES (restaurants_seq.NEXTVAL, 'Le Square',  '5 Place Stanislas, Nancy', '48.692079, 6.184869');
Insert INTO Restaurants (id, name, address, gps_coordinates) VALUES (restaurants_seq.NEXTVAL, 'Le Caveau',  '18 Rue des Michottes, Nancy', '48.6911543, 6.1722601');
Insert INTO Restaurants (id, name, address, gps_coordinates) VALUES (restaurants_seq.NEXTVAL, 'La Maison dans le Parc',  '2 Rue de Mon Désert, Nancy', '48.6923835, 6.1866212');
Insert INTO Restaurants (id, name, address, gps_coordinates) VALUES (restaurants_seq.NEXTVAL, 'Le Bouche à Oreille',  '2 Rue des Chanoines, Nancy', '48.6923681, 6.183158');

SELECT * FROM RESTAURANTS

