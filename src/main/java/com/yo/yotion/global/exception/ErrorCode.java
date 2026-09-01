package com.yo.yotion.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // NOT FOUND
    PAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "페이지를 찾을 수 없습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "멤버를 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),

    // FORBIDDEN
    NOT_PAGE_OWNER(HttpStatus.FORBIDDEN, "페이지 소유자만 수행할 수 있습니다."),
    NOT_PAGE_MEMBER(HttpStatus.FORBIDDEN, "페이지 멤버가 아닙니다."),
    NO_INVITING_AUTHORIZATION(HttpStatus.FORBIDDEN, "초대 권한이 없습니다."),

    // CONFlICT
    ALREADY_MEMBER(HttpStatus.CONFLICT, "이미 페이지 멤버입니다."),
    ALREADY_INVITED(HttpStatus.CONFLICT, "이미 초대된 멤버입니다.");




    private final HttpStatus status;
    private final String message;
}
