CREATE TABLE IF NOT EXISTS users
(
    id       uuid primary key,
    email    varchar(64) not null unique,
    password varchar(32) not null,
    role     varchar(64) not null
);

CREATE TABLE IF NOT EXISTS refresh_tokens
(
    id            serial primary key,
    user_id       uuid not null,
    refresh_token varchar(300) not null,
    expires_at    bigint  not null
);

