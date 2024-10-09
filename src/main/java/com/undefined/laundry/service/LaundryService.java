package com.undefined.laundry.service;

import com.undefined.laundry.config.PropertiesStore;
import com.undefined.laundry.model.LaundryEntry;
import com.undefined.laundry.model.response.LaundryEntryResponse;
import com.undefined.laundry.repository.LaundryEntryRepository;
import com.undefined.laundry.utils.DateTimeFormatters;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class LaundryService {
    private final LaundryEntryRepository laundryEntryRepository;
    private final ModelMapper modelMapper;
    private final PropertiesStore propertiesStore;

    @Autowired
    public LaundryService(LaundryEntryRepository laundryEntryRepository, ModelMapper modelMapper, PropertiesStore propertiesStore) {
        this.laundryEntryRepository = laundryEntryRepository;
        this.modelMapper = modelMapper;
        this.propertiesStore = propertiesStore;
    }

    public Map<LocalTime, LaundryEntryResponse> getLaundryQueue(Date date){
        List<LaundryEntry> laundryEntries = this.laundryEntryRepository.findByDate(date);
        TreeMap<LocalTime, LaundryEntryResponse> map = new TreeMap<>();
        for (LaundryEntry laundryEntry : laundryEntries){
            LaundryEntryResponse entryResponse = modelMapper.map(laundryEntry, LaundryEntryResponse.class);
            map.put(laundryEntry.getTime(), entryResponse);
        }
        propertiesStore.getAvailableTime().forEach(time -> {
            if (!map.containsKey(time)){
                map.put(time, null);
            }
        });

        return map;
    }

    public List<String> getAvailableTime(Date date) {
        List<String> availableTime = new ArrayList<>();
        List<LocalTime> usedTime = this.laundryEntryRepository.findTimeByDate(date);
        propertiesStore.getAvailableTime().forEach(time -> {
            if (!usedTime.contains(time)){
                availableTime.add(time.format(DateTimeFormatters.HH_colon_mm));
            }
        });

        return availableTime;
    }
}
