package com.kite.automation.controllers;

import com.kite.automation.KiteSessions;
import com.kite.automation.google.sheets.SheetsService;
import com.kite.automation.persistence.RedisClient;
import com.kite.automation.schedules.KiteAutomationTaskScheduler;
import com.kite.automation.schedules.TradeLogger;
import com.kite.automation.strategies.AbstractStrategy;
import com.kite.automation.strategies.Strategies;
import com.kite.automation.strategies.v1.GenericStrategyConfig;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/housekeeping")
@Slf4j
public class HouseKeeping {

    @Autowired
    private Strategies strategies;

    @Autowired
    private TradeLogger tradeLogger;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private KiteAutomationTaskScheduler taskScheduler;

    @Autowired
    private KiteSessions kiteSessions;

    @Autowired
    private SheetsService sheetsService;

    @GetMapping("/strategy/configs/{userID}")
    public List<GenericStrategyConfig> strategyConfigs(@PathVariable(name = "userID") String userId) throws KiteException, IOException {
        return sheetsService.getStrategyConfigs(userId);
    }

    @GetMapping("/schedules")
    public int schedules() {
       return taskScheduler.getScheduledTasks();
    }

    @GetMapping("/redis/data/{namespace}/keys")
    public Set<String> getRedisData(@PathVariable String namespace) throws KiteException, IOException {
        return redisClient.getAllKeys(namespace);
    }

    @GetMapping("/redis/data/remove/{namespace}/{key}")
    public String removeData(@PathVariable String namespace, @PathVariable String key) throws KiteException, IOException {
        redisClient.removeData(namespace, key);
        return "SUCCESS";
    }

    @GetMapping("/{userID}/strategies")
    public List<AbstractStrategy> strategies(@PathVariable(name = "userID") String userId) {
        return strategies.getStrategyList(userId.toUpperCase());
    }

    @GetMapping("/{userID}/orders")
    public List<Order> getOrders(@PathVariable(name = "userID") String userId) throws KiteException, IOException {
        return kiteSessions.getSession(userId).getOrders();
    }

    @GetMapping("/{userID}/log")
    public void logTrades(@PathVariable(name = "userID") String userId) throws KiteException, IOException {
        tradeLogger.logTrades(userId);
    }


}
