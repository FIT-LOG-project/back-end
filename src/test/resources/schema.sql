drop table if exists members cascade;
create table members (
    id bigint auto_increment,
    email varchar(50) not null unique,
    password char(255) not null,
    nick_name varchar(20) unique,
    status varchar(10) not null,
    primary key (id)
)