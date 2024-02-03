drop table if exists activity;
create table activity
(
    type       tinyint not null,
    created_at datetime(6),
    deleted_at datetime(6),
    id         bigint  not null auto_increment,
    target_id  bigint  not null,
    user_id    bigint  not null,
    primary key (id)
) engine = InnoDB;

-- FOLLOW ACTIVITY
insert into activity (type, user_id, target_id, created_at)
values (0, 1, 1, DATE_SUB(NOW(), INTERVAL 11 MINUTE)),
       (0, 1, 2, DATE_SUB(NOW(), INTERVAL 21 MINUTE)),
       (0, 1, 3, DATE_SUB(NOW(), INTERVAL 15 MINUTE)),
       (0, 1, 4, DATE_SUB(NOW(), INTERVAL 61 MINUTE)),
       (0, 1, 5, DATE_SUB(NOW(), INTERVAL 16 MINUTE)),
       (0, 1, 6, DATE_SUB(NOW(), INTERVAL 18 MINUTE)),
       (0, 2, 7, DATE_SUB(NOW(), INTERVAL 51 MINUTE)),
       (0, 3, 8, DATE_SUB(NOW(), INTERVAL 42 MINUTE)),
       (0, 4, 9, DATE_SUB(NOW(), INTERVAL 45 MINUTE));

-- POST ACTIVITY
insert into activity (type, user_id, target_id, created_at)
values (1, 1, 1, DATE_SUB(NOW(), INTERVAL 113 MINUTE)),
       (1, 2, 2, DATE_SUB(NOW(), INTERVAL 124 MINUTE)),
       (1, 3, 3, DATE_SUB(NOW(), INTERVAL 131 MINUTE)),
       (1, 4, 4, DATE_SUB(NOW(), INTERVAL 115 MINUTE)),
       (1, 5, 5, DATE_SUB(NOW(), INTERVAL 178 MINUTE)),
       (1, 6, 6, DATE_SUB(NOW(), INTERVAL 156 MINUTE));

-- POST_LIKE ACTIVITY
insert into activity (type, user_id, target_id, created_at)
values (2, 1, 1, DATE_SUB(NOW(), INTERVAL 201 MINUTE)),
       (2, 2, 2, DATE_SUB(NOW(), INTERVAL 203 MINUTE)),
       (2, 2, 3, DATE_SUB(NOW(), INTERVAL 205 MINUTE)),
       (2, 3, 4, DATE_SUB(NOW(), INTERVAL 207 MINUTE)),
       (2, 3, 5, DATE_SUB(NOW(), INTERVAL 231 MINUTE)),
       (2, 4, 6, DATE_SUB(NOW(), INTERVAL 241 MINUTE));

-- COMMENT ACTIVITY
insert into activity (type, user_id, target_id, created_at)
values (4, 1, 1, DATE_SUB(NOW(), INTERVAL 131 MINUTE)),
       (4, 2, 2, DATE_SUB(NOW(), INTERVAL 121 MINUTE)),
       (4, 3, 3, DATE_SUB(NOW(), INTERVAL 138 MINUTE)),
       (4, 4, 4, DATE_SUB(NOW(), INTERVAL 171 MINUTE)),
       (4, 5, 5, DATE_SUB(NOW(), INTERVAL 101 MINUTE));

-- COMMENT_LIKE ACTIVITY
insert into activity (type, user_id, target_id, created_at)
values (5, 1, 1, DATE_SUB(NOW(), INTERVAL 258 MINUTE)),
       (5, 2, 2, DATE_SUB(NOW(), INTERVAL 214 MINUTE)),
       (5, 3, 3, DATE_SUB(NOW(), INTERVAL 250 MINUTE)),
       (5, 3, 4, DATE_SUB(NOW(), INTERVAL 295 MINUTE)),
       (5, 4, 5, DATE_SUB(NOW(), INTERVAL 201 MINUTE)),
       (5, 4, 6, DATE_SUB(NOW(), INTERVAL 210 MINUTE)),
       (5, 5, 7, DATE_SUB(NOW(), INTERVAL 235 MINUTE)),
       (5, 5, 8, DATE_SUB(NOW(), INTERVAL 223 MINUTE)),
       (5, 6, 9, DATE_SUB(NOW(), INTERVAL 242 MINUTE));



# 0 | FOLLOW
# 1 | POST
# 2 | POST_LIKE
# 3 | POST_UNLIKE
# 4 | COMMENT
# 5 | COMMENT_LIKE
# 6 | COMMENT_UNLIKE