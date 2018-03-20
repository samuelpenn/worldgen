
#CREATE DATABASE worldgen;
#GRANT ALL ON worldgen.* TO 'worldgen' IDENTIFIED BY 'worldgen';

/* Singleton table. */
DROP TABLE IF EXISTS universe;
CREATE TABLE universe (
  id INT NOT NULL DEFAULT 1,
  name VARCHAR(64) NOT NULL,
  created_date DATETIME NOT NULL,
  last_date DATETIME NOT NULL,
  sim_time BIGINT NOT NULL DEFAULT 0,
  running BOOLEAN NOT NULL DEFAULT FALSE,
  configured BOOLEAN NOT NULL DEFAULT FALSE,
  locked BOOLEAN NOT NULL DEFAULT FALSE,
  min_x INT NOT NULL DEFAULT 0,
  max_x INT NOT NULL DEFAULT 0,
  min_y INT NOT NULL DEFAULT 0,
  max_y INT NOT NULL DEFAULT 0,
  PRIMARY KEY(id)
);
INSERT INTO universe VALUES(1, "Untitled", NOW(), NOW(), FALSE, FALSE, FALSE, 0, -5, +5, -3, +3);


/* List of numerical constants. */
DROP TABLE IF EXISTS constants;
CREATE TABLE constants (
  name VARCHAR(64) NOT NULL,
  value BIGINT DEFAULT 0,
  PRIMARY KEY (name)
);
INSERT INTO constants VALUES("speed", 10);


DROP TABLE IF EXISTS sectors;
CREATE TABLE sectors (
  id INT AUTO_INCREMENT,
  name VARCHAR(64) NOT NULL,
  x INT NOT NULL,
  y INT NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY (name),
  UNIQUE KEY (x, y)
);

DROP TABLE IF EXISTS blobs;
CREATE TABLE blobs (
  id INT AUTO_INCREMENT,
  name VARCHAR(64) NOT NULL,
  data LONGBLOB NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY (name)
);

DROP TABLE IF EXISTS stars;
CREATE TABLE stars (
  id INT AUTO_INCREMENT,
  system_id INT NOT NULL,
  name VARCHAR(32) NOT NULL,
  parent_id INT NOT NULL,
  distance INT NOT NULL,
  luminosity VARCHAR(4) NOT NULL,
  type VARCHAR(4) NOT NULL,
  mass DOUBLE NOT NULL DEFAULT 1.0,
  radius INT NOT NULL DEFAULT 0,
  period BIGINT NOT NULL DEFAULT 0,
  PRIMARY KEY(id),
  KEY(system_id),
  UNIQUE KEY(system_id, name)
);

DROP TABLE IF EXISTS systems;
CREATE TABLE systems (
  id INT AUTO_INCREMENT,
  sector_id INT NOT NULL,
  name VARCHAR(32) NOT NULL,
  x INT NOT NULL,
  y INT NOT NULL,
  type VARCHAR(24) NOT NULL,
  zone VARCHAR(8) NOT NULL,
  planets int NOT NULL,
  port VARCHAR(8) NOT NULL,
  tech INT NOT NULL,
  population BIGINT NOT NULL,
  codes VARCHAR(32) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY(sector_id, x, y),
  UNIQUE KEY(sector_id, name)
);
ALTER TABLE systems AUTO_INCREMENT = 1;

DROP TABLE IF EXISTS resources;
DROP TABLE IF EXISTS commodities;
DROP TABLE IF EXISTS planets;
CREATE TABLE planets (
  id INT AUTO_INCREMENT,
  system_id INT NOT NULL,
  name VARCHAR(64) NOT NULL,
  parent_id INT NOT NULL,
  moon_of INT NOT NULL DEFAULT 0,
  distance INT NOT NULL DEFAULT 0,
  radius INT NOT NULL DEFAULT 0,
  day BIGINT NOT NULL DEFAULT 0,
  type VARCHAR(24) NOT NULL,
  temperature INT NOT NULL DEFAULT 0,
  atmosphere VARCHAR(24) NOT NULL,
  pressure int NOT NULL DEFAULT 0,
  field VARCHAR(24) NOT NULL,
  hydro INT NOT NULL DEFAULT 0,
  port VARCHAR(4) NOT NULL DEFAULT 'X',
  population BIGINT NOT NULL,
  government VARCHAR(24) NOT NULL,
  tech INT NOT NULL,
  law INT NOT NULL,
  description TEXT,
  PRIMARY KEY (id),
  KEY (system_id)
);
ALTER TABLE planets AUTO_INCREMENT = 1;

DROP VIEW IF EXISTS p;
CREATE VIEW p (id, name, distance, type, radius, temperature, atmosphere, pressure, hydro) AS
  SELECT planets.id, planets.name, planets.distance, planets.type, planets.radius, planets.temperature,
         planets.atmosphere, planets.pressure, planets.hydro
  FROM planets WHERE planets.moon_of = 0
  ORDER BY planets.system_id, planets.distance;

DROP VIEW IF EXISTS sss;
create view sss (sector, system, x, y, luminosity, type) as
  select sectors.name, systems.name, systems.x, systems.y, stars.luminosity, stars.type
  from sectors, systems, stars
  where sectors.id = systems.sector_id and stars.system_id = systems.id
  order by x, y;

DROP TABLE IF EXISTS planet_maps;
CREATE TABLE planet_maps (
  id INT AUTO_INCREMENT,
  planet_id INT NOT NULL,
  name VARCHAR(64) NOT NULL,
  data LONGBLOB NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY (planet_id, name)
);

CREATE TABLE commodities (
  id INT AUTO_INCREMENT,
  name VARCHAR(32) NOT NULL,
  frequency VARCHAR(16) NOT NULL,
  image VARCHAR(64) NOT NULL,
  PRIMARY KEY(id)
);

INSERT INTO commodities VALUES(0, "Hydrogen", "COMMON", "chemicals/hydrogen");
INSERT INTO commodities VALUES(0, "Helium", "RARE", "chemicals/helium");
INSERT INTO commodities VALUES(0, "Organic Gases", "UNCOMMON", "chemicals/organic_gases");
INSERT INTO commodities VALUES(0, "Corrosive Gases", "RARE", "chemicals/corrosive_gases");
INSERT INTO commodities VALUES(0, "Exotic Gases", "VERYRARE", "chemicals/exotic_gases");
INSERT INTO commodities VALUES(0, "Silicate Ore", "COMMON", "minerals/silicate_ore");
INSERT INTO commodities VALUES(0, "Carbonic Ore", "UNCOMMON", "minerals/carbonic_ore");
INSERT INTO commodities VALUES(0, "Ferric Ore", "UNCOMMON", "minerals/ferric_ore");
INSERT INTO commodities VALUES(0, "Heavy Metals", "RARE", "minerals/heavy_metals");
INSERT INTO commodities VALUES(0, "Radioactives", "VERYRARE", "minerals/radioactives");
INSERT INTO commodities VALUES(0, "Rare Metals", "VERYRARE", "minerals/rare_metals");
INSERT INTO commodities VALUES(0, "Silicate Crystals", "RARE", "minerals/silicate_crystals");
INSERT INTO commodities VALUES(0, "Carbonic Crystals", "RARE", "minerals/carbonic_crystals");
INSERT INTO commodities VALUES(0, "Exotic Crystals", "TRACE", "minerals/exotic_crystals");
INSERT INTO commodities VALUES(0, "Precious Metals", "TRACE", "minerals/precious_metals");
INSERT INTO commodities VALUES(0, "Water", "COMMON", "chemicals/water");
INSERT INTO commodities VALUES(0, "Oxygen", "COMMON", "chemicals/oxygen");
INSERT INTO commodities VALUES(0, "Organic Chemicals", "COMMON", "chemicals/organic_chemicals");
INSERT INTO commodities VALUES(0, "Protobionts", "COMMON", "organics/protobionts");
INSERT INTO commodities VALUES(0, "Prokaryotes", "COMMON", "organics/prokaryotes");
INSERT INTO commodities VALUES(0, "Metazoa", "COMMON", "organics/metazoa");
INSERT INTO commodities VALUES(0, "Cyanobacteria", "COMMON", "organics/cyanobacteria");
INSERT INTO commodities VALUES(0, "Algae", "COMMON", "organics/algae");
INSERT INTO commodities VALUES(0, "Plankton", "COMMON", "organics/plankton");
INSERT INTO commodities VALUES(0, "Echinoderms", "COMMON", "organics/echinoderms");

CREATE TABLE resources (
  id INT AUTO_INCREMENT,
  planet_id INT NOT NULL,
  commodity_id INT NOT NULL,
  density INT NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (planet_id) REFERENCES planets(id) ON DELETE CASCADE,
  FOREIGN KEY (commodity_id) REFERENCES commodities(id) ON DELETE CASCADE
);
