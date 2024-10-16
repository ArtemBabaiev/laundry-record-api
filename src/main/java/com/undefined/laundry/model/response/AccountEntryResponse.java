package com.undefined.laundry.model.response;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountEntryResponse {
	private LocalTime time;
	private LocalDate date;
	private String room;
	private Integer floor;
}
