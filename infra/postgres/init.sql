CREATE DATABASE authdb;
CREATE DATABASE orderdb;

\connect authdb;
CREATE USER authuser WITH ENCRYPTED PASSWORD 'authpass';
GRANT ALL PRIVILEGES ON DATABASE authdb TO authuser;

\connect orderdb;
CREATE USER orderuser WITH ENCRYPTED PASSWORD 'orderpass';
GRANT ALL PRIVILEGES ON DATABASE orderdb TO orderuser;


