package com.undefined.laundry.model.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WriteEntryResponse {
	private UUID uuid;
	private long telegramId;
	private String username;
	private String fullName;
	@Schema(type = "String", pattern = "HH:mm:SS")
	private LocalTime time;
	private LocalDate date;
	private String room;
	private Integer floor;
}
