package com.undefined.laundry.config;

import com.undefined.laundry.utils.DateTimeFormatters;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Component
public class PropertiesStore {
    private final List<LocalTime> availableTime = new ArrayList<>();

    @Value("${available_hours}")
    private void setAvailableTime(List<Integer> hours) {
        for (Integer hour : hours){
            this.availableTime.add(LocalTime.of(hour,0,0));
        }
    }
}
