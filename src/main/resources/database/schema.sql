use post_calendar;

create table if not exists `users` (
    `id` int not null auto_increment,
    `username` varchar(20) not null ,
    `password` varchar(64) not null ,
    primary key (`id`)
);




