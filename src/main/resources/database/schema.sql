use post_calendar;

create table if not exists `users` (
    `id` int not null auto_increment,
    `emailAddress` varchar(20) not null ,
    `username` varchar(20) not null ,
    `password` varchar(64) not null ,
    primary key (`id`)
);

create table if not exists `groups` (
    `id` int not null auto_increment,
    `name` varchar(20) not null ,
    `details` text,
    primary key (`id`)
);

create table if not exists `user-group-links` (
    `id` int not null auto_increment,
    `user-id` int not null ,
    `group-id` int not null ,
    `permission` int not null,
    primary key (`id`)
);

create table if not exists `places` (
    `id` int not null auto_increment,
    `name` text not null ,
    `x` int not null ,
    `y` int not null ,
    `placeType` int not null ,
    primary key (`id`)
);

create table if not exists `roads` (
    `id` int not null auto_increment,
    `name` text not null ,
    `startPlaceId` int not null ,
    `endPlaceId` int not null ,
    `length` int not null ,
    primary key (`id`)
);




