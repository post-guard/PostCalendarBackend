use post_calendar;

create table if not exists `users` (
    `id` int not null auto_increment,
    `emailAddress` text not null ,
    `username` text not null ,
    `password` varchar(64) not null ,
    primary key (`id`)
);

create table if not exists `groups` (
    `id` int not null auto_increment,
    `name` varchar(20) not null ,
    `details` text,
    primary key (`id`)
);

create table if not exists `user_group_links` (
    `id` int not null auto_increment,
    `userId` int not null ,
    `groupId` int not null ,
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
    `length` float not null ,
    primary key (`id`)
);

create table if not exists `time_span_events` (
    `id` int not null auto_increment,
    `name` text not null ,
    `details` text not null ,
    `userId` int ,
    `groupId` int ,
    `placeId` int not null ,
    `beginDateTime` DATETIME not null ,
    `endDateTime` DATETIME not null ,
    primary key (`id`)
);




