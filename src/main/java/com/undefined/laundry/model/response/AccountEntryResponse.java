package com.undefined.laundry.model.response;

import java.time.LocalDate;
import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountEntryResponse {
	@Schema(type = "String", pattern = "HH:mm:SS")
	private LocalTime time;
	private LocalDate date;
	private String room;
	private Integer floor;
}
