package com.kite.backtesting.db.models;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "one_minute_option_candles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OneMinuteOptionCandle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String ticker;
    String script;
    @Enumerated(EnumType.STRING)
    OptionType type;
    Integer strikePrice;
    LocalDate expiry;
    LocalDateTime dateTime;
    Integer open;
    Integer high;
    Integer low;
    Integer close;
    Long volume;
    Long openInterest;
}
