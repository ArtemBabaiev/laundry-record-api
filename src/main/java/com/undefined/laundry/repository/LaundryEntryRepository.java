package com.undefined.laundry.repository;

import com.undefined.laundry.model.LaundryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface LaundryEntryRepository extends JpaRepository<LaundryEntry, UUID> {
    List<LaundryEntry> findByDate(Date date);
}
