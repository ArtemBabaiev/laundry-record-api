package com.undefined.laundry.model.response;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class LaundryEntryResponse {
    private UUID uuid;
    private long telegramId;
    private String username;
    private String fullName;
    private String room;
    private Integer floor;
}
