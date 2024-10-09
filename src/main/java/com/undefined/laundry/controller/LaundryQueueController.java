package com.undefined.laundry.controller;

import com.undefined.laundry.model.response.LaundryEntryResponse;
import com.undefined.laundry.service.LaundryService;
import com.undefined.laundry.utils.annotation.TodayOrFuture;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.util.Map;

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
    public Map<String, LaundryEntryResponse> fetchQueue(
            @Parameter(description = "Date in `yyyy-MM-dd` format. must be today or the future")
            @RequestParam @TodayOrFuture Date date) {
        return this.laundryService.getLaundryQueue(date);
    }

}
