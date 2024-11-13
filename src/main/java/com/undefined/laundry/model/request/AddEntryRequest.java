package com.undefined.laundry.model.request;

import java.time.LocalDate;
import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddEntryRequest implements HasDateTime {
	@NotNull
	private Long telegramId;

	@NotBlank
	private String username;

	@NotBlank
	private String fullName;

	@NotNull
	@Schema(type = "String", pattern = "HH:mm:SS")
	private LocalTime time;

	@NotNull
	private LocalDate date;

	@NotBlank
	private String room;
	
	@NotNull
	private Integer floor;
}
