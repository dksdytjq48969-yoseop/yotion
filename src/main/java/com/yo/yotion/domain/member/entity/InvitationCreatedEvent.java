package com.yo.yotion.domain.member.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class InvitationCreatedEvent {

    private final Invitation invitation;
}
