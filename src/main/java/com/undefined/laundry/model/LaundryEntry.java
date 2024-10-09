package com.undefined.laundry.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.TenantId;
import org.hibernate.annotations.Type;

import java.sql.Date;
import java.sql.Time;
import java.sql.Types;
import java.time.LocalTime;
import java.util.UUID;

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
    private Date date;
    private String room;
    @TenantId
    private Integer floor;
}
