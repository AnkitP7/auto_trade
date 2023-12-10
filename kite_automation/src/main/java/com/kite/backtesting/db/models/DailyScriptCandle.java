package com.kite.backtesting.db.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Value;

import java.time.LocalDate;

@Entity
@Table(name = "daily_script_candles")
@Value
public class DailyScriptCandle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String script;
    LocalDate date;
    Integer open;
    Integer high;
    Integer low;
    Integer close;
    Long volume;
}
