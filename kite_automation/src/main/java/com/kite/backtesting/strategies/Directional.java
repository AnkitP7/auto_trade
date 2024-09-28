package com.kite.backtesting.strategies;

import com.kite.backtesting.db.cache.ExpiryCache;
import com.kite.backtesting.db.models.OneMinuteOptionCandle;
import com.kite.backtesting.db.repositories.ExpiryRepository;
import com.kite.backtesting.db.repositories.OptionCandleRepository;
import jakarta.transaction.Transactional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Directional {
    @Autowired
    private OptionCandleRepository repository;

    @Autowired
    private ExpiryRepository expiryRepository;

    private ExpiryCache expiryCache;

    private static final String SCRIPT = "NIFTY";

    @Before
    public void setup()
    {
        expiryCache = new ExpiryCache(expiryRepository);
    }

    @Test
    @Transactional
    public void testFetchData(){
        LocalDate startDate = LocalDate.of(2022, 04,01);
        LocalDate endDate = LocalDate.of(2023, 03,31);

        for (LocalDate date = startDate; date.isBefore(endDate); date = date.plusDays(1))
        {
            System.out.println(date);
            processDay(date);
        }

        /*Test data retrieval*/
//        final List<OneMinuteOptionCandle> niftyData = repository.getByScriptAndDateTime("NIFTY", LocalDateTime.of(2022, Month.FEBRUARY, 14, 9, 55));
////        final OneMinuteOptionCandle referenceById = repository.getReferenceById(5304201L);
////        System.out.println(referenceById);
//        System.out.println(niftyData);
//        System.out.println(niftyData.size());
    }

    private void processDay(LocalDate date) {
        LocalTime startTime = LocalTime.of(9, 19);
        LocalTime endTime = LocalTime.of(15, 26);
        final List<OneMinuteOptionCandle> byScriptAndDateTime = repository.getByScriptAndDateTime(SCRIPT, LocalDateTime.of(date, startTime));
        if(byScriptAndDateTime.size() == 0)
        {
            System.out.println("Looks like trading holiday. No data available");
            return;
        }

        for (LocalTime time = startTime; time.isBefore(endTime); time = time.plusMinutes(1))
        {
//            System.out.println(time);
        }
    }
}
