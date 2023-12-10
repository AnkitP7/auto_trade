package com.kite.automation.schedules;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.kite.automation.KiteSessions;
import com.kite.automation.KiteUtils;
import com.kite.automation.KiteWebSocket;
import com.kite.automation.persistence.RedisClient;
import com.kite.automation.strategies.AbstractStrategy;
import com.kite.automation.strategies.InstrumentService;
import com.kite.automation.strategies.Strategies;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.kiteconnect.utils.Constants;
import com.zerodhatech.models.Order;
import com.zerodhatech.models.OrderParams;
import com.zerodhatech.models.Position;
import io.swagger.models.auth.In;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class HbtScheduler {

    @Autowired
    private Strategies strategies;

    @Autowired
    private KiteWebSocket kiteWebSocket;

    @Autowired
    private InstrumentService instrumentService;

    @Autowired
    RedisClient redisClient;

    @Autowired
    private KiteSessions kiteSessions;

    @Scheduled(cron = "0 * 9-14 * * *", zone = "Asia/Kolkata")
    @Scheduled(cron = "0 0-25 15 * * *", zone = "Asia/Kolkata")
    @SneakyThrows
    public void checkForGlobalStopLoss() {
        Collection<KiteConnect> sessions = kiteSessions.getKiteSessions();
        for (KiteConnect kiteConnect :
                sessions) {
            final String apiKey = System.getenv(kiteConnect.getUserId() + "_API_KEY");
            if (apiKey == null) {
                return;
            }
            double totalM2M = 0;
            final Map<String, List<Position>> positions = kiteConnect.getPositions();
            final List<Position> positionList = positions.get("day");
            for (Position position :
                    positionList) {
                Double ltp = KiteWebSocket.getLtp("NFO:"+ position.tradingSymbol);
                if(ltp!=null) {
                    totalM2M = totalM2M + (position.sellValue - position.buyValue) + (position.netQuantity * ltp * position.multiplier);
                } else {
                    kiteWebSocket.subscribeToWebSocket(kiteConnect, new String[]{"NFO:"+ position.tradingSymbol});
                }
            }

            String maxLossValue = System.getenv(kiteConnect.getUserId() + "_MAX_LOSS");
            String stopGainValue = System.getenv(kiteConnect.getUserId() + "_STOP_GAIN");

            int maxProfit = 1000000;
            if (!Strings.isNullOrEmpty(stopGainValue)) {
                maxProfit = Integer.parseInt(stopGainValue);
            }

            int maxLoss = 1000000 * -1;
            if (!Strings.isNullOrEmpty(maxLossValue)) {
                maxLoss = Integer.parseInt(maxLossValue) * -1;
            }
            log.info("Max Allowed Loss {}, Stop gain value {}, Current M2M for {} = {} ", maxLoss, maxProfit, kiteConnect.getUserId(), totalM2M);
            if (totalM2M < maxLoss  || totalM2M > maxProfit) {
//                log.info("Squaring off positions");
//                final List<AbstractStrategy> strategyList = strategies.getStrategyList(kiteConnect.getUserId());
//                for (AbstractStrategy strategy:
//                        strategyList) {
//                    try {
//                        strategy.close();
//                    } catch (Throwable e) {
//                        log.info("Not able to close stratgey");
//                    }
//                }
//                closePendingMISPosition(kiteConnect);
            }

        }
    }

    @Scheduled(cron = "45 24 15 ? * MON,TUE,WED,THU,FRI",zone = "Asia/Kolkata")
    @SneakyThrows
    public void closePendingMISPositions() {
        log.info("Closing pending MIS positions for all sessions");
        Collection<KiteConnect> sessions = kiteSessions.getKiteSessions();
        for (KiteConnect kiteConnect :
                sessions) {
            log.info("{} - Closing pending MIS positions ", kiteConnect.getUserId());
            closePendingMISPosition(kiteConnect);
        }
    }

    @Scheduled(cron = "00 35 15 ? * MON,TUE,WED,THU,FRI",zone = "Asia/Kolkata")
    @SneakyThrows
    public void refreshInstruments() {
        log.info("Refreshing Instruments");
        instrumentService.init();
        log.info("Refreshing Instruments Done");
    }

    @Scheduled(cron = "2 0/5 9-15 * * *",zone = "Asia/Kolkata")
    @SneakyThrows
    public void processDirectional() {
        log.info("Processing Scheduled job for directions");
        Collection<KiteConnect> sessions = kiteSessions.getKiteSessions();
        for (KiteConnect kiteConnect :
                sessions) {
            final List<AbstractStrategy> strategyList = strategies.getStrategyList(kiteConnect.getUserId());
            for (AbstractStrategy strategy:
                    strategyList) {
                strategy.processCandle();
            }
        }
    }

    @SneakyThrows
    private void closePendingMISPosition(KiteConnect kiteConnect)
    {
        final String apiKey = System.getenv(kiteConnect.getUserId() + "_API_KEY");
        if (apiKey == null) {
            return;
        }
        final Map<String, List<Position>> positions = kiteConnect.getPositions();
        final List<Position> positionList = positions.get("day");
        for (Position position:
                positionList) {
            if(position.product.equalsIgnoreCase("MIS") && position.netQuantity != 0)
            {
                log.info("This position needs to be closed {}" , position);
                final int quantity = Math.abs(position.netQuantity);
                String buyOrSell = "BUY";
                if(position.netQuantity > 0)
                {
                    buyOrSell = "SELL";
                }

                OrderParams orderParams  = new OrderParams();
                orderParams.exchange = "NFO";
                orderParams.tradingsymbol = position.tradingSymbol;
                orderParams.transactionType = buyOrSell;
                orderParams.orderType = Constants.ORDER_TYPE_MARKET;
                orderParams.quantity = quantity;
                orderParams.validity = Constants.VALIDITY_DAY;
                orderParams.product = "MIS";
                try {
                    Order order = kiteConnect.placeOrder(orderParams, Constants.VARIETY_REGULAR);
                } catch (KiteException e) {
                    log.error("Error while placing order {}", e.message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }
    }


}
