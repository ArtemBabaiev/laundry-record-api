package com.undefined.laundry.model.request;

import java.time.LocalDate;
import java.time.LocalTime;

import com.undefined.laundry.utils.annotation.TodayOrFuture;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddEntryRequest {
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
	@TodayOrFuture
	private LocalDate date;

	@NotBlank
	private String room;
}
