package com.chatappbackend.dto.user;

import lombok.Getter;

@Getter
public class UpdateUserReqDTO {
    private String bio;

    private String displayName;

    private boolean displayActiveStatus;
}
