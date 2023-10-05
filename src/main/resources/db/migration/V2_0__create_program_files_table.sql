create table if not exists program_files
(
    id uuid primary key,
    lst_file int references file_table on delete cascade,
    preview_file int references file_table on delete cascade,
    tmt_file int references file_table on delete cascade
);
