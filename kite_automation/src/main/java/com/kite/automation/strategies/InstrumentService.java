package com.kite.automation.strategies;

import com.kite.automation.KiteUtils;
import com.kite.automation.strategies.v1.Index;
import com.zerodhatech.models.Instrument;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class InstrumentService {

    @Autowired
    KiteUtils kiteUtils;

    @ToString.Exclude
    private HashMap<Index, TreeMap<Date, List<String>>> indexMapHashMap = new HashMap<>();

    @SneakyThrows
    @PostConstruct
    public void init()
    {
        final List<Instrument> instruments = kiteUtils.fetchInstruments();
        final Index[] values = Index.values();
        for (Index index:
             values) {
            TreeMap<Date, List<String>> expiryToSymbolsMap = new TreeMap<Date, List<String>>();
            indexMapHashMap.put(index, expiryToSymbolsMap);
            final List<Instrument> indexInstruments = instruments.stream()
                    .filter(instrument -> {
                        return index.name().equalsIgnoreCase(instrument.name)
                                &&
                                ("CE".equalsIgnoreCase(instrument.instrument_type)
                                || "PE".equalsIgnoreCase(instrument.instrument_type));
                    }).collect(Collectors.toList());

            for (Instrument instrument:
                 indexInstruments) {
                Date expiry = instrument.expiry;
                final Calendar instance = Calendar.getInstance();
                instance.setTime(expiry);
                instance.set(Calendar.HOUR_OF_DAY, 20);
                if(Calendar.getInstance().getTime().before(instance.getTime())) {
                    List<String> symbolList = expiryToSymbolsMap.get(expiry);
                    if (symbolList == null) {
                        symbolList = new ArrayList<>();
                        expiryToSymbolsMap.put(expiry, symbolList);
                    }
                    symbolList.add(instrument.tradingsymbol);
                }
            }

        }

    }

    public String getTradingSymbolStart(Index index)
    {
        final TreeMap<Date, List<String>> dateListMap = indexMapHashMap.get(index);
        final List<String> symbolList = dateListMap.firstEntry().getValue();
        return symbolList.get(0).substring(0, index.name().length()+5);
    }

}
