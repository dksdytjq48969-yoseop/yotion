package com.yo.yotion.domain.post.repository;

import com.yo.yotion.domain.post.entity.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository <Post, Long> {

    // EntityGraph는 페이지, 유저 정보 다 갖고오고 동시에 유저들의 정보도 조인해놓기.
    @EntityGraph(attributePaths = {"user", "page"})
    List<Post> findByPage_Id(Long pageId);

    // 게시글 조회
    @EntityGraph(attributePaths = {"user", "page"})
    Optional<Post> findById(Long postId);

    // 게시글 검색(제목)
    @Query("""
        SELECT DISTINCT p
        FROM Post p
        JOIN FETCH p.user
        JOIN FETCH p.page
        WHERE p.title 
                LIKE %:keyword%
        """)
    List<Post> searchPosts(@Param("keyword") String keyword);


}
