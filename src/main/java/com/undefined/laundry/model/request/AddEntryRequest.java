package com.undefined.laundry.model.request;

import java.time.LocalDate;
import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
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
	private LocalDate date;

	@NotBlank
	private String room;

	@AssertTrue(message = "Provided date time should be future")
	private boolean isDateTimeValid() {
		if (this.date.isBefore(LocalDate.now())) {
			return false;
		}
		LocalTime nowTime = LocalTime.now();
		return this.time.getHour() > nowTime.getHour();
	}
}
