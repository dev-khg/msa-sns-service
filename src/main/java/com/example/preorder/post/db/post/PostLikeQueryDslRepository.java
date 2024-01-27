package com.example.preorder.post.db.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostLikeQueryDslRepository {
//    private final JPAQueryFactory jpaQueryFactory;
//    private QPostEntity qPostEntity = QPostEntity.postEntity;
//    private QUserEntity qUserEntity = QUserEntity.userEntity;
//
//    public void temp(List<PostLikeEntity> postLikeEntities) {
//
//
////        jpaQueryFactory.select(
////                new QPostLikeResponse(
////                        qUserEntity.id,
////                        qPostEntity.id,
////                        qUserEntity.username,
////                        qPostEntity.userId
////                )
////        )
//    }
//
//    public List<Object> fetchPostUsername() {
//        return null;
//    }
//
//    private List<PostDto> get(List<PostLikeEntity> postLikeEntities) {
//
//        List<Long> longs = postLikeEntities.stream().map(a -> a.getPostId()).toList();
////        postLikeEntities.
//        jpaQueryFactory.select(
//                new QPostDto(qPostEntity.id, qPostEntity.userId, qUserEntity.username)
//        ).from(qPostEntity, qUserEntity)
//                .fetchJoin()
//                .where(qPostEntity.id.in(longs)
//                        .and(q)
//                )
//
//
//        return null;
//    }
}
