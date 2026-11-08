package com.yo.yotion.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // httpsecurity 클래스 메소드들 이용해서 해당 요청들 허가하도록 함.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                HttpMethod.GET, "/api/auth/csrf"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.POST, "/api/users/signup"
                        ).permitAll()

                        .anyRequest().authenticated()
                )
                // 로그인 성공 실패 처리
                .formLogin(form -> form
                        .loginProcessingUrl("/api/users/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler((request, response, authentication)
                                -> response.setStatus(
                                        HttpServletResponse.SC_NO_CONTENT
                                )

                        )
                        .failureHandler((request, response, exception)
                                -> response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)
                        )
                        .permitAll()
                )
                // 로그아웃 처리
                .logout(logout -> logout
                        .logoutUrl("/api/users/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessHandler((request, response, authentication)
                                -> response.setStatus(HttpServletResponse.SC_NO_CONTENT)
                        )
                )
                // 인증받지 않은 사용자 처리
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, cause)
                                -> response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)
                        )
                        .accessDeniedHandler((request, response, accessDeniedException)
                                -> response.setStatus(HttpServletResponse.SC_FORBIDDEN)
                        )
                );

        return http.build();
    }
}