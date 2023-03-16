use post_calendar;

create table if not exists `users` (
    `id` int not null auto_increment,
    `emailAddress` varchar(20) not null ,
    `username` varchar(20) not null ,
    `password` varchar(64) not null ,
    primary key (`id`)
);

create table if not exists `organization` (
    `id` int not null auto_increment,
    `name` varchar(20) not null ,
    `details` text,
    primary key (`id`)
);

create table if not exists `user-organ-link` (
    `id` int not null auto_increment,
    `user-id` int not null ,
    `organization-link` int not null ,
    primary key (`id`)
);




