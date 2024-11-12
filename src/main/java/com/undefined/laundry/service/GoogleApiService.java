package com.undefined.laundry.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AddSheetRequest;
import com.google.api.services.sheets.v4.model.BatchClearValuesRequest;
import com.google.api.services.sheets.v4.model.BatchClearValuesResponse;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import com.google.api.services.sheets.v4.model.DeleteSheetRequest;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.undefined.laundry.model.FloorGroup;
import com.undefined.laundry.model.FloorGroup.DateGroup;
import com.undefined.laundry.model.LaundryEntry;
import com.undefined.laundry.utils.DateTimeFormatters;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class GoogleApiService {

	@Value("${SPREADSHEET_ID}")
	private String spreadsheetId;

	@Autowired
	private LaundryService laundryService;

	@Autowired
	private Sheets service;

	public void syncSpreadSheet() {
		LocalDate now = LocalDate.now();
		List<FloorGroup> floorGroups = this.laundryService.getQueueForTable(now.getMonthValue(), now.getYear());
		try {
			// create or delete sheet
			prepareSheets(floorGroups);
			// clear data in batch
			clearExistingData(floorGroups);
			// update data in batch
			updateData(floorGroups);
		} catch (Exception e) {
			log.error("Error synching google sheet", e);
		}

	}

	@Scheduled(cron = "*/10 * * * *")
	private void prepareSheets(List<FloorGroup> floorGroups) throws IOException {
		List<String> requiredSheets = floorGroups.stream().map(FloorGroup::getSheetTitle).collect(Collectors.toList());
		Spreadsheet spreadsheet = service.spreadsheets().get(spreadsheetId).execute();
		List<DeleteSheetRequest> deleteRequests = new ArrayList<>();
		List<Request> requests = new ArrayList<>();
		for (Sheet sheet : spreadsheet.getSheets()) {
			var properties = sheet.getProperties();
			if (requiredSheets.contains(properties.getTitle())) {
				requiredSheets.remove(properties.getTitle());
			} else {
				deleteRequests.add(new DeleteSheetRequest().setSheetId(properties.getSheetId()));

			}
		}
		requiredSheets.forEach(rs -> requests.add(
				new Request().setAddSheet(new AddSheetRequest().setProperties(new SheetProperties().setTitle(rs)))));
		deleteRequests.forEach(dr -> requests.add(new Request().setDeleteSheet(dr)));
		if (!requests.isEmpty()) {
			BatchUpdateSpreadsheetRequest batchRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
			service.spreadsheets().batchUpdate(spreadsheetId, batchRequest).execute();
			log.info("Batch sheets update completed.");
		}
	}

	private void clearExistingData(List<FloorGroup> floorGroups) throws IOException {
		List<String> sheetsTitle = floorGroups.stream().map(FloorGroup::getSheetTitle).collect(Collectors.toList());
		BatchClearValuesRequest requestBody = new BatchClearValuesRequest().setRanges(sheetsTitle);

		// Execute the batch clear request
		BatchClearValuesResponse response = service.spreadsheets().values().batchClear(spreadsheetId, requestBody)
				.execute();
		log.info("Cleared ranges {}", response.getClearedRanges());
	}

	private void updateData(List<FloorGroup> floorGroups) throws IOException {
		List<ValueRange> data = new ArrayList<>();
		for (FloorGroup floorGroup : floorGroups) {
			ValueRange vr = new ValueRange().setRange(floorGroup.getSheetTitle() + "!A1")
					.setValues(getTable(floorGroup.getDateGroups()));
			data.add(vr);
		}

		BatchUpdateValuesRequest batchUpdateRequest = new BatchUpdateValuesRequest().setValueInputOption("RAW")
				.setData(data);

		BatchUpdateValuesResponse response = service.spreadsheets().values()
				.batchUpdate(spreadsheetId, batchUpdateRequest).execute();

		response.getResponses().forEach(update -> log.info("Updated range: {}", update.getUpdatedRange()));
	}

	private List<List<Object>> getTable(List<DateGroup> dateGroups) {

		List<List<Object>> table = new ArrayList<>();
		table.add(Arrays.asList("", "Година", "Кімната", "Ім'я"));
		for (DateGroup dateGroup : dateGroups) {
			List<LaundryEntry> entries = dateGroup.getEntries();
			for (int i = 0; i < entries.size(); i++) {
				LaundryEntry entry = entries.get(i);
				List<Object> row = new ArrayList<>();
				row.add(i != 0 ? "" : DateTimeFormatters.ddMMyyyy_dashes.format(entry.getDate()));
				row.add(DateTimeFormatters.HH_colon_mm.format(entry.getTime()));
				row.add(entry.getRoom());
				row.add(entry.getFullName());
				table.add(row);
			}
		}

		return table;
	}
}
