package com.yo.yotion.security.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.csrf.CsrfToken;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class CsrfResponseDto {

    private final String headerName;
    private final String token;

    public static CsrfResponseDto from(CsrfToken csrfToken) {
        return CsrfResponseDto.builder()
                .headerName(csrfToken.getHeaderName())
                .token(csrfToken.getToken())
                .build();
    }
}
