--liquibase formatted sql

--changeset EGO-R:1
create table if not exists users (
    id bigserial primary key ,
    username varchar(32) not null unique,
    password varchar(128) not null,
    role varchar(16) not null
);

