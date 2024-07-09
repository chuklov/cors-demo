-- PostgreSQL initiation of the DB https://www.postgresql.org/docs/14/sql-createrole.html
DROP ROLE IF EXISTS demo_role;
CREATE ROLE demo_role WITH SUPERUSER;
GRANT demo_role TO demo;
GRANT ALL PRIVILEGES ON DATABASE abcd TO demo;

