package com.kite.backtesting.db.cache;

import com.kite.backtesting.db.models.Expiry;
import com.kite.backtesting.db.repositories.ExpiryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ExpiryCache {

    private final ExpiryRepository expiryRepository;
    private Map<String, Map<LocalDate, Expiry>> scriptToDateToExpiryMap;

    @Autowired
    public ExpiryCache(final ExpiryRepository expiryRepository) {
        this.expiryRepository = expiryRepository;
        loadExpiries();
    }

    public LocalDate getWeeklyExpiry(final String script, final LocalDate localDate) {
        return scriptToDateToExpiryMap.get(script)
            .get(localDate)
            .getWeeklyExpiry();
    }

    public LocalDate getMonthlyExpiry(final String script, final LocalDate localDate) {
        return scriptToDateToExpiryMap.get(script)
            .get(localDate)
            .getMonthlyExpiry();
    }

    private void loadExpiries() {
        final List<Expiry> expiries = expiryRepository.findAll();
        scriptToDateToExpiryMap = new HashMap<>();
        expiries.forEach(expiry -> {
            if (!scriptToDateToExpiryMap.containsKey(expiry.getScript())) {
                scriptToDateToExpiryMap.put(expiry.getScript(), new HashMap<>());
            }
            scriptToDateToExpiryMap.get(expiry.getScript()).put(expiry.getDate(), expiry);
        });
    }
}