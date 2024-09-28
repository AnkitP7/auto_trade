package com.kite.automation.controllers;

import com.kite.automation.KiteUtils;
import com.kite.automation.strategies.InstrumentService;
import com.kite.automation.strategies.v1.Index;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.HistoricalData;
import com.zerodhatech.models.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/data")
@Slf4j
public class KiteDataController {

    @Autowired
    private InstrumentService instrumentService;

    @Autowired
    private KiteUtils kiteUtils;

    @GetMapping("/symbol/{index}")
    public String getEnvKey(@PathVariable String index) {
        return instrumentService.getTradingSymbolStart(Index.valueOf(index));
    }

    @GetMapping("/convert/{userID}/{orderId}")
    public boolean convertOrder(@PathVariable String userID, @PathVariable String orderId) throws IOException, KiteException {
        final Order order = kiteUtils.getOrder(userID, orderId);
        return kiteUtils.convertMISToNRML(userID, order);
    }

    @GetMapping("{optionExchange}/{symbol}/{interval}")
    public List<HistoricalData> getCandles(@PathVariable String optionExchange, @PathVariable String symbol, @PathVariable String interval) throws KiteException, IOException {
        return kiteUtils.getHist(optionExchange+":"+symbol, interval);
    }

}
