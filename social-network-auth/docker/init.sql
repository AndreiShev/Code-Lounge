DO
$do$
DECLARE
  _db TEXT := 'social-network-auth';
  _user TEXT := 'postgres';
  _password TEXT := 'postgres';
BEGIN
  CREATE EXTENSION IF NOT EXISTS dblink; -- enable extension
  IF EXISTS (SELECT 1 FROM pg_database WHERE datname = _db) THEN
    RAISE NOTICE 'Database already exists';
ELSE
    PERFORM dblink_connect('host=localhost user=' || _user || ' password=' || _password || ' dbname=' || current_database());
    PERFORM dblink_exec('CREATE DATABASE ' || _db);
END IF;
END
$do$