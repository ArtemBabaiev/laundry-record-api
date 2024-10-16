package com.undefined.laundry.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.undefined.laundry.model.request.AccountEntryRequest;
import com.undefined.laundry.model.response.AccountEntryResponse;
import com.undefined.laundry.model.response.LaundryEntryResponse;
import com.undefined.laundry.service.LaundryService;
import com.undefined.laundry.utils.annotation.TodayOrFuture;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/laundry-queue")
@Validated
public class LaundryQueueController {
	private final LaundryService laundryService;

	@Autowired
	public LaundryQueueController(LaundryService laundryService) {
		this.laundryService = laundryService;
	}

	@Operation(summary = "Get map of time and associated queue entry")
	@GetMapping
	public Map<LocalTime, LaundryEntryResponse> fetchQueue(
			@Parameter(description = "Date in `yyyy-MM-dd` format. must be today or the future") @RequestParam @TodayOrFuture LocalDate date) {
		return this.laundryService.getLaundryQueue(date);
	}

	@Operation(summary = "Get list of available hours for specified day")
	@GetMapping("/available")
	public List<String> fetchAvailableTime(
			@Parameter(description = "Date in `yyyy-MM-dd` format. must be today or the future") @RequestParam @TodayOrFuture LocalDate date) {
		return this.laundryService.getAvailableTime(date);
	}

	@Operation(summary = "Get every laudnry entry from today and forward for specified account")
	@GetMapping("/account")
	public List<AccountEntryResponse> fetchEntriesForAccount(
			@Parameter(description = "Id of telegram account") @RequestParam Long telegramId) {
		AccountEntryRequest request = AccountEntryRequest.builder()
				.telegramId(telegramId)
				.date(LocalDate.now())
				.build();

		return this.laundryService.getAccountEntries(request);
	}
}
