package com.undefined.laundry.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.undefined.laundry.model.LaundryEntry;

@Repository
public interface LaundryEntryRepository extends JpaRepository<LaundryEntry, UUID> {
	List<LaundryEntry> findByDateAndFloor(LocalDate date, Integer floor);

	@Query("SELECT le.time FROM LaundryEntry AS le WHERE le.date = :sDate AND le.floor = :sFloor")
	List<LocalTime> findTimeByDateAndFloor(@Param("sDate") LocalDate date, @Param("sFloor") Integer floor);

	@Query("SELECT le FROM LaundryEntry AS le WHERE le.telegramId = :sTelegramId AND le.floor = :sFloor AND le.date >= :sDate ORDER BY le.date ASC, le.time ASC")
	List<LaundryEntry> findByTelegramIdAndFloorAndNotBeforeDate(@Param("sTelegramId") Long telegramId,
			@Param("sFloor") Integer floor, @Param("sDate") LocalDate date);

	boolean existsByTimeAndDateAndFloor(LocalTime time, LocalDate date, Integer floor);

	List<LaundryEntry> findByFloorNotNullAndDateBetween(LocalDate begin, LocalDate end);
}
