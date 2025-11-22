BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS "pc" (
	"IdPc"	INTEGER,
	"codigo"	TEXT,
	"nombre"	TEXT,
	"disco"	TEXT,
	"procesador"	TEXT,
	"sistemaOperativo"	TEXT,
	"ram"	TEXT,
	"video"	TEXT,
	"imagen"	BLOB,
	PRIMARY KEY("IdPc" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "ps2" (
	"IdPs2"	INTEGER,
	"codigo"	TEXT,
	"nombre"	TEXT,
	"region"	TEXT,
	"lenguaje"	TEXT,
	"jugadores"	NUMERIC,
	"disco"	TEXT,
	"imagen"	BLOB,
	PRIMARY KEY("IdPs2" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "ps3" (
	"IdPs3"	INTEGER,
	"codigo"	TEXT,
	"nombre"	TEXT,
	"region"	TEXT,
	"lenguaje"	TEXT,
	"jugadores"	NUMERIC,
	"disco"	TEXT,
	"actualizacion"	INTEGER,
	"dlc"	INTEGER,
	"imagen"	BLOB,
	PRIMARY KEY("IdPs3" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "ps4" (
	"IdPs4"	INTEGER,
	"codigo"	TEXT,
	"nombre"	TEXT,
	"region"	TEXT,
	"lenguaje"	TEXT,
	"jugadores"	NUMERIC,
	"disco"	TEXT,
	"imagen"	BLOB,
	"actualizacion"	INTEGER,
	"patch"	TEXT,
	"dlc"	INTEGER,
	PRIMARY KEY("IdPs4" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "psp" (
	"IdPsp"	INTEGER,
	"codigo"	TEXT,
	"nombre"	TEXT,
	"region"	TEXT,
	"lenguaje"	TEXT,
	"jugadores"	NUMERIC,
	"disco"	TEXT,
	"imagen"	BLOB,
	PRIMARY KEY("IdPsp" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "psx" (
	"IdPsx"	INTEGER,
	"codigo"	TEXT,
	"nombre"	TEXT,
	"region"	TEXT,
	"lenguaje"	TEXT,
	"jugadores"	INTEGER,
	"disco"	TEXT,
	"imagen"	BLOB,
	PRIMARY KEY("IdPsx" AUTOINCREMENT)
);
COMMIT;
