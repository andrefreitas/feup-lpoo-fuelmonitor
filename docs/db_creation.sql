CREATE TABLE "android_metadata" ("locale" TEXT DEFAULT 'en_US');
INSERT INTO "android_metadata" VALUES ('pt_PT');


--
-- TABLE: Fueling
-- 
--  

CREATE OR TABLE Fueling (
  _id number NOT NULL PRIMARY KEY,
  date date NOT NULL ,
  kmsAtFueling number NOT NULL ,
  quantity number NOT NULL ,
  cost float NOT NULL ,
  idVehicle number NOT NULL ,
  idGasStation number NOT NULL ,
  FOREIGN KEY (idVehicle) REFERENCES Vehicle(idVehicle) ,
  FOREIGN KEY (idGasStation) REFERENCES GasStation(idGasStation)
);


--
-- TABLE: FuelType
-- 
--  

CREATE TABLE FuelType (
  _id number NOT NULL PRIMARY KEY,
  name nvarchar2 NOT NULL UNIQUE
);


--
-- TABLE: GasStation
-- 
--  

CREATE TABLE GasStation (
  _id number NOT NULL PRIMARY KEY,
  name nvarchar2 NOT NULL ,
  location nvarchar2 NOT NULL 
);


--
-- TABLE: Make
-- 
--  

CREATE TABLE Make (
  _id number NOT NULL PRIMARY KEY,
  name nvarchar2 NOT NULL UNIQUE
);


--
-- TABLE: Model
-- 
--  

CREATE TABLE Model (
  _id number NOT NULL PRIMARY KEY,
  name nvarchar2 NOT NULL UNIQUE,
  idMake number NOT NULL,
  FOREIGN KEY (idModel) REFERENCES Make(idMake)
);


--
-- TABLE: Vehicle
-- 
--  

CREATE TABLE Vehicle (
  _id number NOT NULL PRIMARY KEY,
  kms number NOT NULL ,
  date date NOT NULL ,
  photoPath nvarchar2 NOT NULL UNIQUE,
  fuelCapacity number NOT NULL ,
  registration nvarchar2 NOT NULL UNIQUE,
  idModel number NOT NULL ,
  idMake number NOT NULL ,
  idFuelType number NOT NULL,
  FOREIGN KEY (idModel) REFERENCES Model(idModel),
  FOREIGN KEY (idFuelType) REFERENCES FuelType(idFuelType)
);
