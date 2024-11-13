package com.undefined.laundry.model;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FloorGroup {
	private Integer floor;
	private String sheetTitle;
	private List<DateGroup> dateGroups;

	@Getter
	@Setter
	@AllArgsConstructor
	public static class DateGroup {
		private LocalDate date;
		private List<LaundryEntry> entries;
	}
}
