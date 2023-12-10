package com.kite.backtesting.db.repositories;

import com.kite.backtesting.db.models.Expiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpiryRepository extends JpaRepository<Expiry, Long> {

    @Query("select script, date, weeklyExpiry, monthlyExpiry from Expiry")
    List<Expiry> find();

    @Query("select date from Expiry where date between ?1 and ?2 order by date")
    List<LocalDate> findDatesInRange(LocalDate startDate, LocalDate endDate);
}
