package com.kite.backtesting.db.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "one_minute_script_candles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OneMinuteScriptCandle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String script;
    LocalDateTime dateTime;
    Integer open;
    Integer high;
    Integer low;
    Integer close;
    Long volume;
}
