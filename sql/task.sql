drop table task;

create table task
(
    id             serial8,
    title          varchar not null,
    description    text    not null,
    status         varchar not null,
    creation_date  date    not null,
    completed_date date,
    priority       varchar not null,
    assignee       varchar not null,
    primary key (id)
);


