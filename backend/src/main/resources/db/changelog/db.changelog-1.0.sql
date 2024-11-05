--liquibase formatted sql

--changeset EGO-R:1
create table if not exists video (
    id bigserial primary key ,
    name varchar(256) not null
);

--changeset EGO-R:2
create table if not exists tag (
    id serial primary key ,
    name varchar(32)
);

--changeset EGO-R:3
create table if not exists playlist (
    id serial primary key ,
    name varchar(32) unique not null
);

--changeset EGO-R:4
create table if not exists playlist_video (
    id bigserial primary key ,
    playlist_id int not null references playlist(id) on delete cascade ,
    video_id bigint not null references video(id) on delete cascade
);

--changeset EGO-R:5
create table if not exists video_tag (
    id bigserial primary key ,
    video_id bigint not null references video(id) on delete cascade ,
    tag_id int not null references tag(id) on delete cascade
)