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
	List<LaundryEntry> findByDate(LocalDate date);

	@Query("SELECT le.time FROM LaundryEntry AS le WHERE le.date = :sDate")
	List<LocalTime> findTimeByDate(@Param("sDate") LocalDate date);

	@Query("SELECT le FROM LaundryEntry AS le WHERE le.telegramId = :sTelegramId AND le.date >= :sDate ORDER BY le.date ASC, le.time ASC")
	List<LaundryEntry> findByTelegramIdAndNotBeforeDate(@Param("sTelegramId") Long telegramId,
			@Param("sDate") LocalDate date);

	boolean existsByTimeAndDate(LocalTime time, LocalDate date);
}
