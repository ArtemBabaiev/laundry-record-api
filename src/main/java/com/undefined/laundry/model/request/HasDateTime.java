package com.undefined.laundry.model.request;

import java.time.LocalDate;
import java.time.LocalTime;

import com.undefined.laundry.utils.annotation.FutureDateTime;

@FutureDateTime
public interface HasDateTime {
	LocalDate getDate();
	LocalTime getTime();
}
