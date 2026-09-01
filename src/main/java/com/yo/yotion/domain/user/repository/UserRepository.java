package com.yo.yotion.domain.user.repository;

import com.yo.yotion.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {





    // nickname으로 사용자 찾기
    Optional<User> findByNickname(String nickname);

    // email로 사용자 찾기
    Optional<User> findByEmail(String email);

    // googleid로 사용자 찾기
    Optional<User> findByGoogleId(String googleId);



    // nickname 중복체크
    boolean existsByNickname(String nickname);

    // email 중복체크
    boolean existsByEmail(String email);


}
