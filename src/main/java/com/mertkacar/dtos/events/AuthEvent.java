package com.mertkacar.dtos.events;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class AuthEvent {
    private String type;
    private String username;
    private String mail;
    private boolean success;
    private long timestamp;
    private String ip;
}