--changeset chris:1
-- auto-generated definition
create schema matrix_animator;
alter schema matrix_animator owner to postgres;

create table matrix_animator.animations
(
    id      serial
        primary key,
    title   varchar not null,
    frames  jsonb   not null,
    user_id integer not null,
    height  integer,
    width   integer,
    speed   integer
);

alter table matrix_animator.animations
    owner to postgres;

