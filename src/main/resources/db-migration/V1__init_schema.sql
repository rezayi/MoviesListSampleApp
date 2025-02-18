-- Create Movies Table
create table if not exists movies
(
    id              serial
        constraint movies_pk
            primary key,
    title           varchar(100)                 not null,
    release_date    date                         not null,
    poster_url      varchar(500)                 not null,
    overview        varchar                      not null,
    genres          varchar(50)                  not null,
    runtime_minutes integer                      not null,
    language        varchar(20)                  not null,
    rating_score    double precision default 0.0 not null,
    rating_count    integer          default 0   not null
);
