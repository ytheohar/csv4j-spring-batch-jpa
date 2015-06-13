DROP SEQUENCE IF EXISTS hibernate_sequence;
DROP TABLE IF EXISTS csvdata;
CREATE SEQUENCE hibernate_sequence START WITH 1 INCREMENT BY 1 NO CYCLE;
CREATE TABLE csvdata (
id serial NOT NULL,
field0 integer,
att1 VARCHAR(30),
att2 double,
PRIMARY KEY (id)
);

