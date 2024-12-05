--liquibase formatted sql
--changeset liquibase-docs:insert-data
CREATE TABLE IF NOT EXISTS auth_schema.user_roles (
                                        account_id uuid NOT NULL,
                                        roles varchar(255) NOT NULL,
                                        CONSTRAINT user_roles_pkey PRIMARY KEY (account_id, roles),
                                        CONSTRAINT user_roles_roles_check CHECK (((roles)::text = ANY ((ARRAY['ROLE_USER'::character varying, 'ROLE_MODERATOR'::character varying, 'ROLE_ADMIN'::character varying])::text[])))
);


--preconditions onFail:CONTINUE  onError:CONTINUE
--precondition-sql-check expectedResult:0 SELECT count(*) FROM pg_catalog.pg_constraint con WHERE con.conname = 'fk5mym6jm1tu8ls8ca92gq4c44v';
ALTER TABLE auth_schema.user_roles ADD CONSTRAINT fk5mym6jm1tu8ls8ca92gq4c44v FOREIGN KEY (account_id) REFERENCES auth_schema.account(id);