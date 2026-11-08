package com.yo.yotion.security;

import com.yo.yotion.security.dto.CsrfResponseDto;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CsrfController {

    @GetMapping("/api/auth/csrf")
    public CsrfResponseDto getCsrfToken(CsrfToken csrfToken) {
        return CsrfResponseDto.from(csrfToken);

    }
}
