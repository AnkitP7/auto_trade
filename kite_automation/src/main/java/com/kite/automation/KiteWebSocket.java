package com.kite.automation;

import com.kite.automation.strategies.AbstractStrategy;
import com.kite.automation.strategies.Strategies;
import com.kite.automation.strategies.StrategyTag;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.LTPQuote;
import com.zerodhatech.models.Order;
import com.zerodhatech.models.Tick;
import com.zerodhatech.ticker.KiteTicker;
import com.zerodhatech.ticker.OnConnect;
import com.zerodhatech.ticker.OnDisconnect;
import com.zerodhatech.ticker.OnOrderUpdate;
import com.zerodhatech.ticker.OnTicks;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class KiteWebSocket {

    private static KiteTicker tickerProvider;
    private static Map<Long, String> tokenSymbolMap = new HashMap<>();
    private static Map<String, Double> symbolValueMap = new ConcurrentHashMap<>();

    @SneakyThrows
    public void initWebSocket(KiteConnect kiteConnect) {
        if (tickerProvider == null || !tickerProvider.isConnectionOpen()) {
            ArrayList<Long> instrumentTokens = new ArrayList<>();
            final Map<String, LTPQuote> ltp = kiteConnect.getLTP(new String[]{"NSE:NIFTY 50", "NSE:NIFTY BANK"});
            for (Map.Entry<String, LTPQuote> entry :
                    ltp.entrySet()) {
                tokenSymbolMap.put(entry.getValue().instrumentToken, entry.getKey());
                instrumentTokens.add(entry.getValue().instrumentToken);
            }
            tickerProvider = new KiteTicker(kiteConnect.getAccessToken(), kiteConnect.getApiKey());
            tickerProvider.setOnConnectedListener(new OnConnect() {
                @Override
                public void onConnected() {
                    /** Subscribe ticks for token.
                     * By default, all tokens are subscribed for modeQuote.
                     * */
                    tickerProvider.subscribe(instrumentTokens);
                    tickerProvider.setMode(instrumentTokens, KiteTicker.modeLTP);
                }
            });

            tickerProvider.setOnDisconnectedListener(new OnDisconnect() {
                @Override
                public void onDisconnected() {
                    // your code goes here
                }
            });

//            /** Set listener to get order updates.*/
            tickerProvider.setOnOrderUpdateListener(new OnOrderUpdate() {
                @Override
                public void onOrderUpdate(Order order) {
//                    System.out.println("order update "+order.orderId + " " + order.accountId + " " + StrategyTag.valueOf(order.tag));
//                    if(strategies == null)
//                    {
//                        System.out.println("Strategies is null");
//                    }
//                    final AbstractStrategy strategy = strategies.getStrategy(order.accountId, StrategyTag.valueOf(order.tag));
//                    System.out.println("Got the strategy " + strategy);
//                    if(strategy != null) {
//                        strategy.handleCallback(order);
//                    }
                }

            });

            tickerProvider.setOnTickerArrivalListener(new OnTicks() {
                @Override
                public void onTicks(ArrayList<Tick> ticks) {
                    NumberFormat formatter = new DecimalFormat();
//                    System.out.println("ticks size "+ticks.size());
                    if (ticks.size() > 0) {
                        for (int i = 0; i < ticks.size(); i++) {
                            symbolValueMap.put(tokenSymbolMap.get(ticks.get(i).getInstrumentToken()), ticks.get(i).getLastTradedPrice());
//                            System.out.println(tokenSymbolMap.get(ticks.get(i).getInstrumentToken()) + " last price " + ticks.get(i).getLastTradedPrice());
                        }

                    }
                }
            });

            tickerProvider.setTryReconnection(true);
            //maximum retries and should be greater than 0
            tickerProvider.setMaximumRetries(10);
            //set maximum retry interval in seconds
            tickerProvider.setMaximumRetryInterval(30);

            /** connects to com.zerodhatech.com.zerodhatech.ticker server for getting live quotes*/
            tickerProvider.connect();

            /** You can check, if websocket connection is open or not using the following method.*/
            boolean isConnected = tickerProvider.isConnectionOpen();
            System.out.println(isConnected);

            /** set mode is used to set mode in which you need tick for list of tokens.
             * Ticker allows three modes, modeFull, modeQuote, modeLTP.
             * For getting only last traded price, use modeLTP
             * For getting last traded price, last traded quantity, average price, volume traded today, total sell quantity and total buy quantity, open, high, low, close, change, use modeQuote
             * For getting all data with depth, use modeFull*/
//            tickerProvider.setMode(instrumentTokens, KiteTicker.modeLTP);

//            // Unsubscribe for a token.
//            tickerProvider.unsubscribe(instrumentTokens);

//            // After using com.zerodhatech.com.zerodhatech.ticker, close websocket connection.
//            tickerProvider.disconnect();
        }
    }

    private static void subscribe(Map<String, LTPQuote> ltp) {
        ArrayList<Long> tokens = new ArrayList<>();
        for (Map.Entry<String, LTPQuote> entry :
                ltp.entrySet()) {
            final long instrumentToken = entry.getValue().instrumentToken;
            tokenSymbolMap.put(entry.getValue().instrumentToken, entry.getKey());
            tokens.add(instrumentToken);
        }
        tickerProvider.subscribe(tokens);
    }

    public static Double getLtp(String symbol) {
        return symbolValueMap.get(symbol);
    }

    public static Map<String, Double> getSymbolValueMap() {
        return symbolValueMap;
    }

    public void subscribeToWebSocket(KiteConnect kiteConnect, String[] symbols) throws KiteException, IOException {
        try {
            log.info("Subscribing symbols {}", Arrays.toString(symbols));
            final Map<String, LTPQuote> ltp = kiteConnect.getLTP(symbols);
            this.initWebSocket(kiteConnect);
            this.subscribe(ltp);
        } catch (Throwable e) {
            log.info("Error while registering to websocket {}", e.getMessage());

        }
    }

}
