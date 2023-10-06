CREATE TABLE parts(
    id serial primary key,
    dimensions varchar(256) not null,
    quantity integer not null,
    geo_filename varchar(256) not null
);

CREATE TABLE program__part(

    program uuid references programs on delete cascade,
    part integer references parts on delete cascade
)
