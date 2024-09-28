package com.kite.backtesting.db.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.sql.Date;
import java.time.LocalDate;

@Converter(autoApply = true)
public class DateConverter implements AttributeConverter<LocalDate, Date> {

    @Override
    public Date convertToDatabaseColumn(final LocalDate localDate) {
        return null != localDate ? Date.valueOf(localDate) : null;
    }

    @Override
    public LocalDate convertToEntityAttribute(final Date date) {
        return null != date ? date.toLocalDate() : null;
    }
}
