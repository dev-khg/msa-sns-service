SET foreign_key_checks = 0;

drop table if exists activity;
drop table if exists comment;
drop table if exists comment_like;
drop table if exists follow;
drop table if exists post;
drop table if exists post_like;
drop table if exists users;

-- CREATE TABLES
create table activity
(
    id            bigint  not null auto_increment,
    user_id       bigint  not null,
    target_id     bigint  not null,
    activity_type tinyint not null,
    status        tinyint not null,
    created_at    datetime(6),
    updated_at    datetime(6),
    primary key (id)
) engine = InnoDB;

create table comment
(
    id         bigint       not null auto_increment,
    user_id    bigint       not null,
    post_id    bigint       not null,
    content    varchar(200) not null,
    created_at datetime(6),
    deleted_at datetime(6),
    updated_at datetime(6),
    primary key (id)
) engine = InnoDB;

create table comment_like
(
    id         bigint  not null auto_increment,
    comment_id bigint,
    user_id    bigint,
    status     tinyint not null,
    created_at datetime(6),
    updated_at datetime(6),
    primary key (id)
) engine = InnoDB;

create table follow
(
    id          bigint  not null auto_increment,
    followee_id bigint  not null,
    follower_id bigint  not null,
    status      tinyint not null,
    created_at  datetime(6),
    updated_at  datetime(6),
    primary key (id)
) engine = InnoDB;

create table post
(
    id         bigint not null auto_increment,
    user_id    bigint not null,
    content    TEXT   not null,
    created_at datetime(6),
    deleted_at datetime(6),
    updated_at datetime(6),
    primary key (id)
) engine = InnoDB;

create table post_like
(
    id         bigint not null auto_increment,
    post_id    bigint not null,
    user_id    bigint not null,
    created_at datetime(6),
    deleted_at datetime(6),
    updated_at datetime(6),
    primary key (id)
) engine = InnoDB;

create table users
(
    id            bigint      not null auto_increment,
    email         varchar(50) not null,
    username      varchar(50) not null,
    password      varchar(80) not null,
    profile_image varchar(100),
    description   varchar(200),
    created_at    datetime(6),
    deleted_at    datetime(6),
    updated_at    datetime(6),
    primary key (id)
) engine = InnoDB;

alter table users
    add constraint unique_email unique (email);
alter table users
    add constraint unique_username unique (username);

alter table comment
    add constraint comment_post_foreign
        foreign key (post_id) references post (id);
alter table comment
    add constraint comment_user_foreign
        foreign key (user_id) references users (id);

alter table comment_like
    add constraint comment_like_comment_foreign
        foreign key (comment_id) references comment (id);
alter table comment_like
    add constraint comment_like_user_foreign
        foreign key (user_id) references users (id);

alter table follow
    add constraint follow_followee_foreign
        foreign key (followee_id) references users (id);
alter table follow
    add constraint follow_follower_foreign
        foreign key (follower_id) references users (id);

alter table post
    add constraint post_user_foreign
        foreign key (user_id) references users (id);

alter table post_like
    add constraint post_like_post_foreign
        foreign key (post_id) references post (id);
alter table post_like
    add constraint post_like_user_foreign
        foreign key (user_id) references users (id);

SET foreign_key_checks = 1;


insert into users(email, username, password, profile_image, description, created_at, updated_at)
values ('tester-1@test.com', 'tester-1', 'test1234', 'tester-1.link.com', 'i am test-1', CURRENT_TIME, CURRENT_TIME),
       ('tester-2@test.com', 'tester-2', 'test1234', 'tester-2.link.com', 'i am test-2', CURRENT_TIME, CURRENT_TIME),
       ('tester-3@test.com', 'tester-3', 'test1234', 'tester-3.link.com', 'i am test-3', CURRENT_TIME, CURRENT_TIME),
       ('tester-4@test.com', 'tester-4', 'test1234', 'tester-4.link.com', 'i am test-4', CURRENT_TIME, CURRENT_TIME),
       ('tester-5@test.com', 'tester-5', 'test1234', 'tester-5.link.com', 'i am test-5', CURRENT_TIME, CURRENT_TIME);

insert into follow(follower_id, followee_id, status, created_at)
values (1, 2, 0, CURRENT_TIME),
       (1, 3, 0, CURRENT_TIME),
       (1, 4, 0, CURRENT_TIME),
       (1, 5, 0, CURRENT_TIME);

insert into post(user_id, content, created_at)
values (2, 'i am test-2. this is my feed.', current_time),
       (3, 'i am test-3. this is my feed.', current_time);

insert into post_like (post_id, user_id, created_at)
values (1, 3, CURRENT_TIME),
       (1, 4, CURRENT_TIME);

insert into comment (user_id, post_id, content, created_at)
values (2, 2, 'I am test-2. this is comment.', CURRENT_TIME),
       (5, 1, 'I am test-5. this is comment.', CURRENT_TIME);

insert into comment_like (comment_id, user_id, status, created_at)
values (1, 3, 0, CURRENT_TIME),
       (1, 4, 0, CURRENT_TIME);

-- follow type : Post(0), post_like(1), comment(2), comment_like(3), follow(4)
insert into activity (user_id, target_id, activity_type, status, created_at)
values (1, 2, 4, 0, CURRENT_TIME),
       (1, 3, 4, 0, CURRENT_TIME),
       (1, 4, 4, 0, CURRENT_TIME),
       (1, 5, 4, 0, CURRENT_TIME),
       -- POST
       (3, 1, 0, 0, CURRENT_TIME),
       (4, 2, 0, 0, CURRENT_TIME),
       -- POST_LIKE
       (3, 1, 1, 0, CURRENT_TIME),
       (4, 1, 1, 0, CURRENT_TIME),
       -- COMMENT
       (2, 1, 2, 0, CURRENT_TIME),
       (5, 2, 2, 0, CURRENT_TIME),
       -- COMMENT_LIKE
       (3, 1, 3, 0, CURRENT_TIME),
       (4, 1, 3, 0, CURRENT_TIME);


