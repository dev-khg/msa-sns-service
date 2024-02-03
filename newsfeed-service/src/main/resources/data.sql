SET foreign_key_checks = 0;
drop table if exists comment;
drop table if exists comment_like;
drop table if exists post;
drop table if exists post_like;
create table comment
(
    created_at datetime(6),
    deleted_at datetime(6),
    id         bigint       not null auto_increment,
    post_id    bigint       not null,
    updated_at datetime(6),
    user_id    bigint       not null,
    content    varchar(255) not null,
    primary key (id)
) engine = InnoDB;
create table comment_like
(
    comment_id bigint not null,
    created_at datetime(6),
    deleted_at datetime(6),
    id         bigint not null auto_increment,
    updated_at datetime(6),
    user_id    bigint not null,
    primary key (id)
) engine = InnoDB;
create table post
(
    created_at datetime(6),
    deleted_at datetime(6),
    id         bigint not null auto_increment,
    updated_at datetime(6),
    user_id    bigint not null,
    content    TEXT   not null,
    primary key (id)
) engine = InnoDB;
create table post_like
(
    created_at datetime(6),
    deleted_at datetime(6),
    id         bigint not null auto_increment,
    post_id    bigint not null,
    updated_at datetime(6),
    user_id    bigint not null,
    primary key (id)
) engine = InnoDB;
alter table comment
    add constraint COMMENT_POST_FK
        foreign key (post_id)
            references post (id);
alter table comment_like
    add constraint COMMENT_LIKE_COMMENT_FK
        foreign key (comment_id)
            references comment (id);
alter table post_like
    add constraint POST_LIKE_POST_FK
        foreign key (post_id)
            references post (id);

SET foreign_key_checks = 1;

insert into post(user_id, content, created_at, updated_at)
values (1, 'Hi this is test post-1.', DATE_SUB(NOW(), INTERVAL 113 MINUTE), DATE_SUB(NOW(), INTERVAL 113 MINUTE)),
       (2, 'Hi this is test post-2.', DATE_SUB(NOW(), INTERVAL 124 MINUTE), DATE_SUB(NOW(), INTERVAL 124 MINUTE)),
       (3, 'Hi this is test post-3.', DATE_SUB(NOW(), INTERVAL 131 MINUTE), DATE_SUB(NOW(), INTERVAL 131 MINUTE)),
       (4, 'Hi this is test post-4.', DATE_SUB(NOW(), INTERVAL 115 MINUTE), DATE_SUB(NOW(), INTERVAL 115 MINUTE)),
       (5, 'Hi this is test post-5.', DATE_SUB(NOW(), INTERVAL 178 MINUTE), DATE_SUB(NOW(), INTERVAL 178 MINUTE)),
       (6, 'Hi this is test post-6.', DATE_SUB(NOW(), INTERVAL 156 MINUTE), DATE_SUB(NOW(), INTERVAL 156 MINUTE));

insert into post_like (user_id, post_id, updated_at, created_at)
values (1, 1, DATE_SUB(NOW(), INTERVAL 201 MINUTE), DATE_SUB(NOW(), INTERVAL 201 MINUTE)),
       (2, 2, DATE_SUB(NOW(), INTERVAL 203 MINUTE), DATE_SUB(NOW(), INTERVAL 203 MINUTE)),
       (2, 3, DATE_SUB(NOW(), INTERVAL 205 MINUTE), DATE_SUB(NOW(), INTERVAL 205 MINUTE)),
       (3, 4, DATE_SUB(NOW(), INTERVAL 207 MINUTE), DATE_SUB(NOW(), INTERVAL 207 MINUTE)),
       (3, 5, DATE_SUB(NOW(), INTERVAL 231 MINUTE), DATE_SUB(NOW(), INTERVAL 231 MINUTE)),
       (4, 6, DATE_SUB(NOW(), INTERVAL 241 MINUTE), DATE_SUB(NOW(), INTERVAL 241 MINUTE));

insert into comment (post_id, user_id, content, created_at, updated_at)
values (1, 1, 'this-is-test-comment-1', DATE_SUB(NOW(), INTERVAL 131 MINUTE), DATE_SUB(NOW(), INTERVAL 131 MINUTE)),
       (2, 2, 'this-is-test-comment-2', DATE_SUB(NOW(), INTERVAL 121 MINUTE), DATE_SUB(NOW(), INTERVAL 121 MINUTE)),
       (2, 3, 'this-is-test-comment-3', DATE_SUB(NOW(), INTERVAL 138 MINUTE), DATE_SUB(NOW(), INTERVAL 138 MINUTE)),
       (3, 4, 'this-is-test-comment-4', DATE_SUB(NOW(), INTERVAL 171 MINUTE), DATE_SUB(NOW(), INTERVAL 171 MINUTE)),
       (3, 5, 'this-is-test-comment-5', DATE_SUB(NOW(), INTERVAL 101 MINUTE), DATE_SUB(NOW(), INTERVAL 101 MINUTE));

insert into comment_like (user_id, comment_id, created_at, updated_at)
values (1, 1, DATE_SUB(NOW(), INTERVAL 258 MINUTE), DATE_SUB(NOW(), INTERVAL 258 MINUTE)),
       (2, 1, DATE_SUB(NOW(), INTERVAL 214 MINUTE), DATE_SUB(NOW(), INTERVAL 214 MINUTE)),
       (3, 2, DATE_SUB(NOW(), INTERVAL 250 MINUTE), DATE_SUB(NOW(), INTERVAL 250 MINUTE)),
       (3, 2, DATE_SUB(NOW(), INTERVAL 295 MINUTE), DATE_SUB(NOW(), INTERVAL 295 MINUTE)),
       (4, 3, DATE_SUB(NOW(), INTERVAL 201 MINUTE), DATE_SUB(NOW(), INTERVAL 201 MINUTE)),
       (4, 3, DATE_SUB(NOW(), INTERVAL 210 MINUTE), DATE_SUB(NOW(), INTERVAL 210 MINUTE)),
       (5, 4, DATE_SUB(NOW(), INTERVAL 235 MINUTE), DATE_SUB(NOW(), INTERVAL 235 MINUTE)),
       (5, 4, DATE_SUB(NOW(), INTERVAL 223 MINUTE), DATE_SUB(NOW(), INTERVAL 223 MINUTE)),
       (6, 5, DATE_SUB(NOW(), INTERVAL 242 MINUTE), DATE_SUB(NOW(), INTERVAL 242 MINUTE));

