package com.yo.yotion.domain.member.repository;

import com.yo.yotion.domain.member.dto.MemberResponseDto;
import com.yo.yotion.domain.member.entity.Member;
import com.yo.yotion.domain.member.entity.MemberRole;
import com.yo.yotion.domain.page.entity.Page;
import com.yo.yotion.domain.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository <Member, Long> {

    // 해당 페이지의 모든 멤버 검색
    @EntityGraph(attributePaths = {"user"})
    List<Member> findByPage_Id(Long pageId);

    // 특정 사용자의 참여중인 page조회
    @EntityGraph(attributePaths = {"page"})
    List<Member> findByUser_Id(Long userId);

    // 특정 사용자가 페이지 멤버인지 확인
    Optional<Member> findByPage_IdAndUser_Id(Long pageId, Long userId);

    // 권한 확인
    @Query("""
        SELECT m FROM Member m
        WHERE m.page.id = :pageId
        AND m.user.id = :userId
        AND m.role IN :roles
        """)
    Optional<Member> findByPageIdAndUserIdAndRoleIn(
            @Param("pageId")Long pageId,
            @Param("userId")Long userId,
            @Param("roles") List<MemberRole> roles
    );

    boolean existsByPage_IdAndUser_Id(Long pageId, Long userId);

}
