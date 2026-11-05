package com.yo.yotion.security;

import com.yo.yotion.domain.user.entity.AuthProvider;
import com.yo.yotion.domain.user.entity.User;
import com.yo.yotion.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    void 이메일로_사용자_조회_성공() {
        String email = "test@example.com";
        User user = User.builder()
                .id(1L)
                .email(email)
                .nickname("테스터")
                .password("encoded-password")
                .authProvider(AuthProvider.LOCAL)
                .build();

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        // 실행
        UserDetails result = userDetailsService.loadUserByUsername(email);

        // 검증
        CustomUserDetails details =
                assertInstanceOf(CustomUserDetails.class, result);

        assertEquals(1L, details.getId());
        assertEquals(email, details.getUsername());
        assertTrue(details.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void 이메일로_사용자_조회_실패() {
        String email = "missing@example.com";

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(email)
        );
    }
}
