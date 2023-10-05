CREATE TABLE IF NOT EXISTS file_table
(
    id          serial primary key,
    path        varchar(256) not null,
    last_update timestamp  not null
);
