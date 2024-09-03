\connect postgres

CREATE USER "user" WITH PASSWORD '1234';
CREATE DATABASE countrycode OWNER "user";

\connect countrycode "user"
CREATE TABLE if not exists countryCodes
(
    id      SERIAL PRIMARY KEY,
    serving VARCHAR(255),
    code    VARCHAR(255)
);
