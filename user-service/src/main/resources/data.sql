SET foreign_key_checks = 0;
DROP TABLE IF EXISTS follow;
DROP TABLE IF EXISTS users;

show tables;

create table follow
(
    created_at    datetime(6),
    deleted_at    datetime(6),
    followee_id   bigint not null,
    follower_id   bigint not null,
    follow_number bigint not null auto_increment,
    updated_at    datetime(6),
    primary key (follow_number)
) engine = InnoDB;

create table users
(
    user_number   bigint       not null auto_increment,
    password      varchar(80)  not null,
    email         varchar(100) not null,
    username      varchar(100) not null,
    description   varchar(200),
    profile_image varchar(200),
    created_at    datetime(6),
    deleted_at    datetime(6),
    updated_at    datetime(6),
    primary key (user_number)
) engine = InnoDB;

alter table follow
    add constraint follow_pair_unique unique (follower_id, followee_id);
alter table users
    add constraint user_name_unique unique (username);
alter table users
    add constraint user_email_unique unique (email);
alter table follow
    add constraint followee_user_fk
        foreign key (followee_id)
            references users (user_number);
alter table follow
    add constraint follower_user_fk
        foreign key (follower_id)
            references users (user_number);

insert into users(email, username, password, description, created_at, updated_at)
values ('test1234@test.com', 'tester1234', 'test1234', 'tester1234-description', CURRENT_TIME, CURRENT_TIME),
       ('tester-1@test.com', 'tester1', 'test1234', 'tester1-description', CURRENT_TIME, CURRENT_TIME),
       ('tester-2@test.com', 'tester2', 'test1234', 'tester2-description', CURRENT_TIME, CURRENT_TIME),
       ('tester-3@test.com', 'tester3', 'test1234', 'tester3-description', CURRENT_TIME, CURRENT_TIME),
       ('tester-4@test.com', 'tester4', 'test1234', 'tester4-description', CURRENT_TIME, CURRENT_TIME),
       ('tester-5@test.com', 'tester5', 'test1234', 'tester5-description', CURRENT_TIME, CURRENT_TIME),
       ('tester-6@test.com', 'tester6', 'test1234', 'tester6-description', CURRENT_TIME, CURRENT_TIME),
       ('tester-7@test.com', 'tester7', 'test1234', 'tester7-description', CURRENT_TIME, CURRENT_TIME);

insert into follow(follower_id, followee_id, created_at, updated_at)
values (1, 2, DATE_SUB(NOW(), INTERVAL 11 MINUTE), DATE_SUB(NOW(), INTERVAL 11 MINUTE)),
       (1, 3, DATE_SUB(NOW(), INTERVAL 21 MINUTE), DATE_SUB(NOW(), INTERVAL 21 MINUTE)),
       (1, 4, DATE_SUB(NOW(), INTERVAL 15 MINUTE), DATE_SUB(NOW(), INTERVAL 15 MINUTE)),
       (1, 5, DATE_SUB(NOW(), INTERVAL 61 MINUTE), DATE_SUB(NOW(), INTERVAL 61 MINUTE)),
       (1, 6, DATE_SUB(NOW(), INTERVAL 16 MINUTE), DATE_SUB(NOW(), INTERVAL 16 MINUTE)),
       (1, 7, DATE_SUB(NOW(), INTERVAL 18 MINUTE), DATE_SUB(NOW(), INTERVAL 18 MINUTE)),
       (2, 3, DATE_SUB(NOW(), INTERVAL 51 MINUTE), DATE_SUB(NOW(), INTERVAL 51 MINUTE)),
       (3, 4, DATE_SUB(NOW(), INTERVAL 42 MINUTE), DATE_SUB(NOW(), INTERVAL 42 MINUTE)),
       (4, 5, DATE_SUB(NOW(), INTERVAL 45 MINUTE), DATE_SUB(NOW(), INTERVAL 45 MINUTE));


SET foreign_key_checks = 1;

