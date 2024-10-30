package com.undefined.laundry.model.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteEntryRequest {
	@NotNull
	private Long telegramId;

	@NotNull
	private UUID uuid;
}
