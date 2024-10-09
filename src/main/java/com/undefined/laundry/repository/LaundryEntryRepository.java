package com.undefined.laundry.repository;

import com.undefined.laundry.model.LaundryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface LaundryEntryRepository extends JpaRepository<LaundryEntry, UUID> {
    List<LaundryEntry> findByDate(Date date);

    @Query("select le.time from LaundryEntry as le where le.date = :sDate")
    List<LocalTime> findTimeByDate(@Param("sDate") Date date);
}
