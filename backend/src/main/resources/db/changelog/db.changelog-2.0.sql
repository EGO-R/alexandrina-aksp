--liquibase formatted sql

--changeset EGO-R:1
alter table video
    add column created_at  timestamp   not null,
    add column modified_at timestamp   not null;

--changeset EGO-R:2
alter table playlist
    add column created_at  timestamp   not null,
    add column modified_at timestamp   not null;

--changeset EGO-R:3
alter table playlist_video
    add column created_at  timestamp   not null,
    add column modified_at timestamp   not null;



