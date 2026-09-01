package com.yo.yotion.domain.user.service;

import com.yo.yotion.domain.user.dto.SignupRequestDto;
import com.yo.yotion.domain.user.dto.UserResponseDto;
import com.yo.yotion.domain.user.entity.AuthProvider;
import com.yo.yotion.domain.user.entity.User;
import com.yo.yotion.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    //회원가입
    @Transactional
    public UserResponseDto signup(SignupRequestDto signupRequestDto) {
        // 1. 중복체크
        if (userRepository.existsByNickname(signupRequestDto.getNickname())) {
            throw new IllegalArgumentException("이미 존재하는 사용자명입니다.");
        }

        if (userRepository.existsByEmail(signupRequestDto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        // 2. User 생성
        User user = User.builder()
                .email(signupRequestDto.getEmail())
                .nickname(signupRequestDto.getNickname())
                .password(passwordEncoder.encode(signupRequestDto.getPassword()))
                .phoneNumber((signupRequestDto.getPhoneNumber()))
                .authProvider(AuthProvider.LOCAL)
                .build();

        // 3. 저장
        User savedUser = userRepository.save(user);


        // 4. DTO 변환 후 저장
        return UserResponseDto.from(savedUser);
    }

    // Google Auth 로그인 / 회원가입
    @Transactional
    public UserResponseDto googleLogin(String googleId, String email, String nickname) {
        return userRepository.findByGoogleId(googleId)
                // 1. 구글 로그인
                .map(user -> UserResponseDto.from(user))
                // 2. 신규 사용자 생성
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(email)
                            .googleId(googleId)
                            .nickname(nickname)
                            .authProvider(AuthProvider.GOOGLE)
                            .password(null)
                            .build();

                    // 저장
                    User savedUser = userRepository.save(newUser);

                    // DB에 입력
                    return UserResponseDto.from(savedUser);

                });
    }

    // 이메일 조회(로그인용)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    // ID로 사용자 조회
    public UserResponseDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return UserResponseDto.from(user);

    }

    // 비밀번호 검증 (로그인용)
    public boolean verifyPassword(User user, String rawPassword) {
        //0Auth 사용자는 비밀번호 검증 불가
        if (user.getAuthProvider() == AuthProvider.GOOGLE) {
            throw new IllegalArgumentException("0Auth 사용자는 비밀번호 로그인을 사용할 수 없습니다.");
        }

        return passwordEncoder.matches(rawPassword, user.getPassword());

    }

    // 비밀번호 변경
    @Transactional
    public void changePassword(Long id, String currentPassword, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        // 현재 비밃번호 확인
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호 암호화 후 저장. 더티체킹으로 저장 직접 안해도 됨.
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        user.changePassword(encodedNewPassword);

    }


    // 프로필 수정
    // 바꾸려는 닉네임, 폰번호 존재하는지 확인 -> 존재하지 않으면 바꿔주기.
    @Transactional
    public UserResponseDto updateProfile(Long id, String nickname, String phoneNumber) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        if (!nickname.equals(user.getNickname())) {
            if (userRepository.existsByNickname(nickname)) {
                throw new IllegalArgumentException("이미 존재하는 닉네임입니다");
            }
        }

        user.updateProfile(nickname, phoneNumber);

        return UserResponseDto.from(user);
    }




}


