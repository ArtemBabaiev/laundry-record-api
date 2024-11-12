package com.undefined.laundry.model.request;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateEntryRequest implements HasDateTime {
	@NotNull
	private UUID uuid;
	
	@NotNull
	private Long telegramId;
	
	@NotNull
	private LocalTime time;
	
	@NotNull
	private LocalDate date;

	@NotNull
	private Integer floor;
}
