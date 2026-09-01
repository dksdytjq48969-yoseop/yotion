package com.yo.yotion.domain.user.controller;


import com.yo.yotion.domain.user.dto.ChangePasswordRequestDto;
import com.yo.yotion.domain.user.dto.SignupRequestDto;
import com.yo.yotion.domain.user.dto.UpdateProfileRequestDto;
import com.yo.yotion.domain.user.dto.UserResponseDto;
import com.yo.yotion.domain.user.service.UserService;
import com.yo.yotion.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signup(@Valid @RequestBody SignupRequestDto requestDto) {
        UserResponseDto user = userService.signup(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    // 내 정보 조회...
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long id = userDetails.getId();
        UserResponseDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }


    // 특정 사용자 조회
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserProfile(@PathVariable Long id) {
        UserResponseDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    // 프로필 수정
    @PutMapping("/me")
    public ResponseEntity<UserResponseDto> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequestDto requestDto) {

        Long id = userDetails.getId();
        UserResponseDto updatedUser = userService.updateProfile(
                id,
                requestDto.getNickname(),
                requestDto.getPhoneNumber()
        );
        return ResponseEntity.ok(updatedUser);
    }


    // 비밀번호 변경
    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Valid @RequestBody ChangePasswordRequestDto changePasswordRequestDto) {

        // OAuth 사용자 체크
        if(!customUserDetails.isLocalUser()) {
            throw new IllegalStateException("OAuth 사용자는 비밀번호를 변경할 수 없습니다");
        }

        userService.changePassword(
                customUserDetails.getId(),
                changePasswordRequestDto.getCurrentPassword(),
                changePasswordRequestDto.getNewPassword()
        );

        return ResponseEntity.ok().build();
    }

}
