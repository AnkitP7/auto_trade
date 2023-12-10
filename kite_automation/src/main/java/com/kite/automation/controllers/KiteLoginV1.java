package com.kite.automation.controllers;

import com.google.gson.Gson;
import com.kite.automation.KiteSessions;
import com.kite.automation.KiteUtils;
import com.kite.automation.KiteWebSocket;
import com.kite.automation.google.sheets.SheetsService;
import com.kite.automation.persistence.RedisClient;
import com.kite.automation.schedules.KiteAutomationTaskScheduler;
import com.kite.automation.schedules.TradeLogger;
import com.kite.automation.strategies.AbstractStrategy;
import com.kite.automation.strategies.Strategies;
import com.kite.automation.strategies.StrategyTag;
import com.kite.automation.strategies.v1.DirectionalSelling;
import com.kite.automation.strategies.v1.GenericStrategyConfig;
import com.kite.automation.strategies.v1.Straddle;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.kiteconnect.utils.Constants;
import com.zerodhatech.models.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/kite/v1")
@Slf4j
public class KiteLoginV1 {

    @Autowired
    private Strategies strategies;

    @Autowired
    private KiteAutomationTaskScheduler kiteAutomationTaskScheduler;

    @Autowired
    private KiteWebSocket kiteWebSocket;

    @Autowired
    private TradeLogger tradeLogger;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private KiteSessions kiteSessions;

    @Autowired
    private SheetsService sheetsService;

    @Autowired
    private KiteUtils kiteUtils;

    @GetMapping("/{userID}/login")
    public String login(@RequestParam(name = "request_token") String requestToken,
                        @PathVariable(name = "userID") String userId) throws KiteException, IOException {
        userId = userId.toUpperCase();
        log.info("Clearing strategies for user {}", userId);
        strategies.removeStrategies(userId);
        kiteAutomationTaskScheduler.removeScheduledTasks(userId);

        final String apiKey = System.getenv(userId + "_API_KEY");
        final String apiSecret = System.getenv(userId + "_API_SECRET");
        if(apiKey == null || apiKey.isEmpty())
        {
            return "User Not Allowed";
        }
        KiteConnect kiteConnect = new KiteConnect(apiKey);
//         Get accessToken as follows,
        User user = kiteConnect.generateSession(requestToken, apiSecret);

        kiteConnect.setAccessToken(user.accessToken);
        kiteConnect.setPublicToken(user.publicToken);
        kiteConnect.setUserId(userId);

        kiteSessions.addSession(kiteConnect);

        log.info("Kite Session Generated: {}", userId);

        final List<GenericStrategyConfig> strategyConfigs = sheetsService.getStrategyConfigs(userId);
        for (GenericStrategyConfig strategyConfig:
                strategyConfigs) {
            switch (strategyConfig.getStrategyTag())
            {
                case WNT:
                    break;
                case STRADDLE:
                    final AbstractStrategy strategy  = Straddle.builder().genericStrategyConfig(strategyConfig)
                            .kiteUtils(kiteUtils)
                            .userId(userId)
                            .strategies(strategies)
                            .strategyTag(StrategyTag.STRADDLE)
                            .build();
                    kiteAutomationTaskScheduler.scheduleTask(strategy);
                    strategies.add(strategy);
                    break;
                case DIRECTIONAL:
                    final AbstractStrategy directionalSelling  = DirectionalSelling.builder().genericStrategyConfig(strategyConfig)
                            .kiteUtils(kiteUtils)
                            .userId(userId)
                            .strategies(strategies)
                            .strategyTag(StrategyTag.DIRECTIONAL)
                            .build();
                    kiteAutomationTaskScheduler.scheduleTask(directionalSelling);
                    strategies.add(directionalSelling);
                    break;
            }


        }

        return "New Login successful for userID " + userId;

    }

    @PostMapping("/{userID}/callback")
    public void callback(HttpServletRequest request) throws KiteException, IOException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader in = request.getReader()) {
            char[] buf = new char[4096];
            for (int len; (len = in.read(buf)) > 0; )
                builder.append(buf, 0, len);
        }
        String requestBody = builder.toString();
        log.info("Received callback String {}", requestBody);
        String decode = URLDecoder.decode(requestBody, StandardCharsets.UTF_8.name()).replace("&", "");
        Gson gson = new Gson();
        final Map<String, String> map = gson.fromJson(decode, Map.class);
        if (map.get("status").equals(Constants.ORDER_COMPLETE)) {
            log.info("Received completed order callback {}", map);
            if(map.get("tag") == null)
            {
                return;
            }
            final AbstractStrategy strategy = strategies.getStrategy(map.get("user_id"), map.get("tag"));
            if (strategy != null) {
                strategy.handleCallback(map);
                strategies.add(strategy);
            }
        }
        if (map.get("status").equals("UPDATE") && map.get("order_type").equalsIgnoreCase("LIMIT")) {
            String orderId = map.get("order_id");
            log.info("Stoploss order is triggered {}", map);
            if(redisClient.lockKey(orderId, 60))
            {
                log.info("Stoploss order is triggered. Inside lock Key {}", orderId);
                final AbstractStrategy strategy = strategies.getStrategy(map.get("user_id"), map.get("tag"));
                if (strategy != null) {
                    strategy.handleStoplossTriggered(map);
                }
            }

        }
    }


}
