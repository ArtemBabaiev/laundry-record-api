package com.undefined.laundry.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.undefined.laundry.model.request.AccountEntryRequest;
import com.undefined.laundry.model.request.AddEntryRequest;
import com.undefined.laundry.model.request.DeleteEntryRequest;
import com.undefined.laundry.model.request.UpdateEntryRequest;
import com.undefined.laundry.model.response.AccountEntryResponse;
import com.undefined.laundry.model.response.ErrorResponse;
import com.undefined.laundry.model.response.LaundryEntryResponse;
import com.undefined.laundry.model.response.WriteEntryResponse;
import com.undefined.laundry.service.LaundryService;
import com.undefined.laundry.utils.annotation.TodayOrFuture;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/laundry-queue")
public class LaundryQueueController {
	private final LaundryService laundryService;

	@Autowired
	public LaundryQueueController(LaundryService laundryService) {
		this.laundryService = laundryService;
	}

	@Operation(summary = "Get map of time and associated queue entry")
	@GetMapping
	public Map<LocalTime, LaundryEntryResponse> fetchQueue(
			@Parameter(description = "Floor number") @RequestParam @NotNull Integer floor,
			@Parameter(description = "Date in `yyyy-MM-dd` format. must be today or the future") @RequestParam @TodayOrFuture LocalDate date) {
		return this.laundryService.getLaundryQueue(floor, date);
	}

	@Operation(summary = "Get list of available hours for specified day")
	@GetMapping("/available")
	public List<String> fetchAvailableTime(
			@Parameter(description = "Floor number") @RequestParam @NotNull Integer floor,
			@Parameter(description = "Date in `yyyy-MM-dd` format. must be today or the future") @RequestParam @TodayOrFuture LocalDate date) {
		return this.laundryService.getAvailableTime(floor, date);
	}

	@Operation(summary = "Get every laudnry entry from today and forward for specified account")
	@GetMapping("/account")
	public List<AccountEntryResponse> fetchEntriesForAccount(
			@Parameter(description = "Floor number") @RequestParam @NotNull Integer floor,
			@Parameter(description = "Id of telegram account") @RequestParam Long telegramId) {
		AccountEntryRequest request = AccountEntryRequest.builder()
				.telegramId(telegramId)
				.date(LocalDate.now())
				.floor(floor)
				.build();

		return this.laundryService.getAccountEntries(request);
	}

	@Operation(summary = "Add new entry to laundry queue", responses = { @ApiResponse(responseCode = "200"),
			@ApiResponse(responseCode = "400", description = "malformed request or bad datetime", content = @Content(schema = @Schema(implementation = ErrorResponse.class))) })
	@PostMapping
	public WriteEntryResponse postLaundryEntry(@RequestBody @Valid AddEntryRequest request) {
		return this.laundryService.addEntry(request);
	}

	@Operation(summary = "Modify existing entry of laundry queue", responses = { @ApiResponse(responseCode = "200"),
			@ApiResponse(responseCode = "404", description = "entry with provided uuid wasnt found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "mismatch of entry ownership", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "400", description = "malformed request or bad datetime", content = @Content(schema = @Schema(implementation = ErrorResponse.class))) })
	@PutMapping
	public WriteEntryResponse putLaundryEntry(@RequestBody @Valid UpdateEntryRequest request) {
		return this.laundryService.updateEntry(request);
	}

	@Operation(summary = "Modify existing entry of laundry queue", responses = { @ApiResponse(responseCode = "204"),
			@ApiResponse(responseCode = "404", description = "entry with provided uuid wasnt found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "401", description = "mismatch of entry ownership", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
			@ApiResponse(responseCode = "400", description = "malformed request", content = @Content(schema = @Schema(implementation = ErrorResponse.class))) })
	@DeleteMapping
	public ResponseEntity<Object> deleteLaundryEntry(@RequestBody @Valid DeleteEntryRequest request) {
		this.laundryService.deleteEntry(request);
		return ResponseEntity.noContent().build();
	}
}
