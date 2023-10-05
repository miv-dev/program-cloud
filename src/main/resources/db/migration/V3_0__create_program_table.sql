create table if not exists programs
(
    id             uuid primary key,
    program_id     varchar(16)  not null,
    name           varchar(256) not null,
    blank          varchar(256) not null,
    machining_time integer default 0,
    files          uuid references program_files on delete cascade
)
