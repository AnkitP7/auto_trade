package com.kite.backtesting.db.repositories;

import com.kite.backtesting.db.models.OneMinuteOptionCandle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OptionCandleRepository extends JpaRepository<OneMinuteOptionCandle, Long> {
    List<OneMinuteOptionCandle> getByScriptAndDateTime(String script, LocalDateTime dateTime);

    List<OneMinuteOptionCandle> getByScript(String script);

    OneMinuteOptionCandle getByTickerAndDateTime(String ticker, LocalDateTime dateTime);

    List<OneMinuteOptionCandle> getByTickerAndDateTimeBetween(String ticker,
                                                              LocalDateTime startDateTime, LocalDateTime endDateTime);
}
