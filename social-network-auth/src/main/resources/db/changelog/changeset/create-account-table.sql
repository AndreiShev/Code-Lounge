--liquibase formatted sql
--changeset liquibase-docs:insert-data
--preconditions onFail:CONTINUE onError:CONTINUE
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM auth_schema.account

CREATE TABLE IF NOT EXISTS auth_schema.account (
     id uuid NOT NULL,
     email varchar(50) NULL,
     first_name varchar(30) NULL,
     last_name varchar(30) NULL,
     "password" varchar NULL,
     "token" varchar(100) NULL,
     CONSTRAINT account_pkey PRIMARY KEY (id)
);