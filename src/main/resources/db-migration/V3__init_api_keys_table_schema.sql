-- Create Movies Table
create table if not exists api_keys
(
    id       serial
        constraint api_keys_pk
            primary key,
    api_key  varchar(100) not null,
    username varchar(100) not null
);
