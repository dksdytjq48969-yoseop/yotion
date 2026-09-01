package com.yo.yotion.domain.member.repository;

import com.yo.yotion.domain.member.entity.Invitation;
import com.yo.yotion.domain.member.entity.InvitationStatus;
import com.yo.yotion.domain.member.entity.Member;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository <Invitation, Long> {
    boolean existsByPage_IdAndInviteeUser_IdAndStatus(Long pageId, Long inviteeUserId, InvitationStatus status);
    Optional<Invitation> findByToken(String token);


}
