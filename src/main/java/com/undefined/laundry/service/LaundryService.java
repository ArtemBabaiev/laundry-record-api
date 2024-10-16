package com.undefined.laundry.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.undefined.laundry.config.PropertiesStore;
import com.undefined.laundry.model.LaundryEntry;
import com.undefined.laundry.model.request.AccountEntryRequest;
import com.undefined.laundry.model.request.AddEntryRequest;
import com.undefined.laundry.model.response.AccountEntryResponse;
import com.undefined.laundry.model.response.AddEntryResponse;
import com.undefined.laundry.model.response.LaundryEntryResponse;
import com.undefined.laundry.repository.LaundryEntryRepository;
import com.undefined.laundry.utils.DateTimeFormatters;
import com.undefined.laundry.utils.exception.BadRequestException;

@Service
public class LaundryService {
	private final LaundryEntryRepository laundryEntryRepository;
	private final ModelMapper modelMapper;
	private final PropertiesStore propertiesStore;

	@Autowired
	public LaundryService(LaundryEntryRepository laundryEntryRepository, ModelMapper modelMapper,
			PropertiesStore propertiesStore) {
		this.laundryEntryRepository = laundryEntryRepository;
		this.modelMapper = modelMapper;
		this.propertiesStore = propertiesStore;
	}

	public Map<LocalTime, LaundryEntryResponse> getLaundryQueue(LocalDate date) {
		List<LaundryEntry> laundryEntries = this.laundryEntryRepository.findByDate(date);
		TreeMap<LocalTime, LaundryEntryResponse> map = new TreeMap<>();
		for (LaundryEntry laundryEntry : laundryEntries) {
			LaundryEntryResponse entryResponse = modelMapper.map(laundryEntry, LaundryEntryResponse.class);
			map.put(laundryEntry.getTime(), entryResponse);
		}
		propertiesStore.getAvailableTime().forEach(time -> {
			if (!map.containsKey(time)) {
				map.put(time, null);
			}
		});

		return map;
	}

	public List<String> getAvailableTime(LocalDate date) {
		List<String> availableTime = new ArrayList<>();
		List<LocalTime> usedTime = this.laundryEntryRepository.findTimeByDate(date);
		propertiesStore.getAvailableTime().forEach(time -> {
			if (!usedTime.contains(time)) {
				availableTime.add(time.format(DateTimeFormatters.HH_colon_mm));
			}
		});

		return availableTime;
	}

	public List<AccountEntryResponse> getAccountEntries(AccountEntryRequest request) {
		List<LaundryEntry> entities = laundryEntryRepository.findByTelegramIdAndNotBeforeDate(request.getTelegramId(),
				request.getDate());
		return entities.stream().map(entity -> modelMapper.map(entity, AccountEntryResponse.class)).toList();
	}
	
	public AddEntryResponse addEntry(AddEntryRequest request) {
		LocalTime hourOnly = LocalTime.of(request.getTime().getHour(), 0, 0);
		if (!propertiesStore.getAvailableTime().contains(hourOnly)) {
			throw new BadRequestException("Provided time is not supported");
		}
		if (this.laundryEntryRepository.existsByTimeAndDate(hourOnly, request.getDate())) {
			throw new BadRequestException("Such time and date already occupied");
		}
		request.setTime(hourOnly);
		LaundryEntry entity = modelMapper.map(request, LaundryEntry.class);
		entity = this.laundryEntryRepository.save(entity);
		return modelMapper.map(entity, AddEntryResponse.class);
	}
}
