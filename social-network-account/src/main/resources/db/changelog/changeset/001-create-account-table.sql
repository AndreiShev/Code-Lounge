-- liquibase formatted sql
-- changeset Anastasia Martova:create-account-table
CREATE TABLE accounts (
  id UUID PRIMARY KEY,
  email VARCHAR(255) NOT NULL,
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  blocked BOOLEAN NOT NULL,
  deleted BOOLEAN NOT NULL,
  online BOOLEAN NOT NULL,
  city VARCHAR(50),
  country VARCHAR(50),
  phone VARCHAR(255),
  about VARCHAR(255),
  birth_date TIMESTAMP,
  emoji_status VARCHAR(100),
  profile_cover VARCHAR(255),
  photo VARCHAR(255),
  created_on TIMESTAMP NOT NULL,
  updated_on TIMESTAMP NOT NULL,
  last_online_time TIMESTAMP
);