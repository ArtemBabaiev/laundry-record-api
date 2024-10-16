package com.undefined.laundry.model.request;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AccountEntryRequest {
	private Long telegramId;
	private LocalDate date;
}
