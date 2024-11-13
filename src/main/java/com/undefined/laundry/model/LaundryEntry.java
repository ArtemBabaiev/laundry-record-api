package com.undefined.laundry.model;

import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.TenantId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "laundry_entry")
public class LaundryEntry {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@JdbcTypeCode(Types.VARCHAR)
	private UUID uuid;
	private long telegramId;
	private String username;
	@Column(columnDefinition = "TEXT")
	private String fullName;
	private LocalTime time;
	private LocalDate date;
	private String room;
	private Integer floor;
}
