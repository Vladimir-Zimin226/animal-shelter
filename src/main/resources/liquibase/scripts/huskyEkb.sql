-- liquibase formatted sql

-- changeset okrivenko:1

ALTER TABLE dogs ALTER COLUMN at_home SET DEFAULT FALSE;

ALTER TABLE users ALTER COLUMN is_volonter SET DEFAULT FALSE;




