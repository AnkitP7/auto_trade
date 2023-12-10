package com.kite.backtesting.db.models;

import com.kite.backtesting.db.converters.DateConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "expiries_v2")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Expiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "script", nullable = false)
    String script;

    @Column(name = "date", nullable = false)
    @Convert(converter = DateConverter.class)
    LocalDate date;

    @Column(name = "weekly_expiry")
    @Convert(converter = DateConverter.class)
    LocalDate weeklyExpiry;

    @Column(name = "monthly_expiry")
    @Convert(converter = DateConverter.class)
    LocalDate monthlyExpiry;
}
