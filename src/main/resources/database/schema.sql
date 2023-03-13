use post_calendar;

create table if not exists `users` (
    `id` int not null auto_increment,
    `emailAddress` varchar(20) not null ,
    `username` varchar(20) not null ,
    `password` varchar(64) not null ,
    primary key (`id`)
);




