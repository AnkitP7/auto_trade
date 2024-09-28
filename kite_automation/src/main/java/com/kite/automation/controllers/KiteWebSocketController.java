package com.kite.automation.controllers;

import com.kite.automation.KiteSessions;
import com.kite.automation.KiteUtils;
import com.kite.automation.KiteWebSocket;
import com.kite.automation.persistence.RedisClient;
import com.kite.automation.schedules.TradeLogger;
import com.kite.automation.strategies.Strategies;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/ws")
@Slf4j
public class KiteWebSocketController {

    @Autowired
    private Strategies strategies;

    @Autowired
    private KiteWebSocket kiteWebSocket;

    @Autowired
    private TradeLogger tradeLogger;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private KiteSessions kiteSessions;

    @Autowired
    private KiteUtils kiteUtils;

    @GetMapping("/init")
    public String initWebSockt() throws KiteException, IOException {
        final Collection<KiteConnect> kiteSessions = this.kiteSessions.getKiteSessions();
        for (KiteConnect kiteConnect:
                kiteSessions) {
            try {
                kiteWebSocket.initWebSocket(kiteConnect);
            } catch (Exception ex)
            {
                log.error("Error while initializing web socket {}", ex.getMessage());
                return "FAILURE - " +ex.getMessage();
            }
            break;
        }
        return "SUCCESS";
    }

    @SneakyThrows
    @GetMapping("/subscribe/{symbol}")
    public String subscribe(@PathVariable String symbol) {
        final Collection<KiteConnect> kiteSessions = this.kiteSessions.getKiteSessions();
        final Object[] objects = kiteSessions.toArray();
        if(objects.length > 0)
        {
            KiteConnect kiteConnect = (KiteConnect)objects[0];
            kiteWebSocket.subscribeToWebSocket(kiteConnect, new String[]{symbol});
        }
        return "SUCCESS";
    }

    @SneakyThrows
    @GetMapping("/getTickData")
    public Map<String, Double> getTickData() {
        return  com.kite.automation.KiteWebSocket.getSymbolValueMap();
    }

}
