package com.kite.automation.strategies.v1;

import com.google.common.base.Strings;
import com.kite.automation.Utils;
import com.kite.automation.strategies.AbstractStrategy;
import com.kite.automation.ta.TAUtils;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.kiteconnect.utils.Constants;
import com.zerodhatech.models.HistoricalData;
import com.zerodhatech.models.Order;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@SuperBuilder
@Slf4j
@NoArgsConstructor
public class DirectionalSelling extends AbstractStrategy {

    @Override
    protected void execute() throws KiteException, IOException {
        final Index index = genericStrategyConfig.getIndex();
        final double lastPrice = kiteUtils.getLtp(userId, index.getExchangeAndSymbol());
        final long atmStrikePrice = Math.round(lastPrice / index.getStrikePriceMultiple())*index.getStrikePriceMultiple();

        initCallSymbol(atmStrikePrice);
        initPutSymbol(atmStrikePrice);
        processPutOption();
        processCallOption();

    }

    @SneakyThrows
    public void processCandle()
    {
        try {
            log.info("{} - Processing Directional Candle. Selected symbols {}, {}", userId, callSymbol, putSymbol);
            processCallOption();
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            log.info("{} - Processing Directional Candle. Selected symbols {}, {}", userId, callSymbol, putSymbol);
            processPutOption();
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }


//        kiteUtils.getLtp()

    }

    @SneakyThrows
    private void processPutOption() {
        if (Strings.isNullOrEmpty(putSymbol)) {
            return;
        }
        final List<HistoricalData> historicalDataList = kiteUtils.getHist(getExchangeSymbol(putSymbol), "5minute");
        // Remove Last Candle
        final HistoricalData removedCandle = historicalDataList.remove(historicalDataList.size() - 1);
        if (removedCandle != null) {
            log.info("Removed last candle for {} - {} {} {} {} {}", putSymbol, removedCandle.open, removedCandle.high, removedCandle.low, removedCandle.close, removedCandle.timeStamp);
        }
        final double channelHigh = TAUtils.getChannelHigh(historicalDataList, 12);
        final double channelLow = TAUtils.getChannelLow(historicalDataList, 12);
        log.info("Channel Low is {}", channelLow);
        log.info("Channel High is {}", channelHigh);

        if (putOrder == null) {
            final double ltp = kiteUtils.getLtp(userId, getExchangeSymbol(putSymbol));
            if (ltp < channelLow) {
                String putOrderId = kiteUtils.placeMarketOrder(genericStrategyConfig.getIndex().getFnoExchange(), userId, putSymbol, Constants.PRODUCT_MIS, Constants.TRANSACTION_TYPE_SELL, genericStrategyConfig.getQuantity(), getOrderTag());
                putOrder = kiteUtils.getOrder(userId, putOrderId);
            } else {
                putOrder = kiteUtils.placeStopLossOrder(genericStrategyConfig.getIndex().getFnoExchange(), userId, putSymbol, Constants.PRODUCT_MIS, Constants.TRANSACTION_TYPE_SELL, genericStrategyConfig.getQuantity(), channelLow - 0.5, channelLow - 2, getOrderTag());
            }
        } else {
            putOrder = kiteUtils.getOrder(userId, putOrder.orderId);
            if (!putOrder.status.equalsIgnoreCase(Constants.ORDER_COMPLETE)) {
                putOrder = kiteUtils.modifyOrReplace(userId, putOrder.orderId, channelLow - 0.5, channelLow - 2);
//                kiteUtils.cancelOrder(userId, putOrder.orderId);
//                putOrder = kiteUtils.placeStopLossOrder(genericStrategyConfig.getIndex().getFnoExchange(), userId, putSymbol, Constants.PRODUCT_MIS, Constants.TRANSACTION_TYPE_SELL, genericStrategyConfig.getQuantity(), channelLow - 0.5, channelLow - 2, getOrderTag());
            }
        }
        if (putOrder != null && putOrder.status.equalsIgnoreCase(Constants.ORDER_COMPLETE)) {
            if (stopLossPutOrder != null) {
                stopLossPutOrder = kiteUtils.getOrder(userId, stopLossPutOrder.orderId);
                if (stopLossPutOrder != null && !stopLossPutOrder.status.equalsIgnoreCase(Constants.ORDER_COMPLETE)) {
                    stopLossPutOrder = kiteUtils.modifyOrReplace(userId, stopLossPutOrder.orderId, channelHigh + 0.5, channelHigh + 2);
//                    kiteUtils.cancelOrder(userId, stopLossPutOrder.orderId);
//                    stopLossPutOrder = kiteUtils.placeStopLossOrder(genericStrategyConfig.getIndex().getFnoExchange(), userId, putSymbol, Constants.PRODUCT_MIS,
//                            Constants.TRANSACTION_TYPE_BUY, genericStrategyConfig.getQuantity(), channelHigh + 0.5, channelHigh + 2, getOrderTag());
                }
            } else {
                stopLossPutOrder = kiteUtils.placeStopLossOrder(genericStrategyConfig.getIndex().getFnoExchange(), userId, putSymbol, Constants.PRODUCT_MIS,
                        Constants.TRANSACTION_TYPE_BUY, genericStrategyConfig.getQuantity(), channelHigh + 0.5, channelHigh + 2, getOrderTag());
            }
        }

        strategies.add(this);

    }

    @SneakyThrows
    private void processCallOption() {
        if (Strings.isNullOrEmpty(callSymbol)) {
            return;
        }
        final List<HistoricalData> historicalDataList = kiteUtils.getHist(getExchangeSymbol(callSymbol), "5minute");
        // Remove Last Candle
        final HistoricalData removedCandle = historicalDataList.remove(historicalDataList.size() - 1);
        if (removedCandle != null) {
            log.info("Removed last candle for {} - {} {} {} {} {}", callSymbol, removedCandle.open, removedCandle.high, removedCandle.low, removedCandle.close, removedCandle.timeStamp);
        }
        final double channelHigh = TAUtils.getChannelHigh(historicalDataList, 12);
        final double channelLow = TAUtils.getChannelLow(historicalDataList, 12);
        log.info("Channel Low is {}", channelLow);
        log.info("Channel High is {}", channelHigh);

        if (callOrder == null) {
            final double ltp = kiteUtils.getLtp(userId, getExchangeSymbol(callSymbol));
            if (ltp < channelLow) {
                String callOrderId = kiteUtils.placeMarketOrder(genericStrategyConfig.getIndex().getFnoExchange(), userId, callSymbol, Constants.PRODUCT_MIS, Constants.TRANSACTION_TYPE_SELL, genericStrategyConfig.getQuantity(), getOrderTag());
                callOrder = kiteUtils.getOrder(userId, callOrderId);
            } else {
                callOrder = kiteUtils.placeStopLossOrder(genericStrategyConfig.getIndex().getFnoExchange(), userId, callSymbol, Constants.PRODUCT_MIS, Constants.TRANSACTION_TYPE_SELL, genericStrategyConfig.getQuantity(), channelLow - 0.5, channelLow - 2, getOrderTag());
            }
        } else {
            callOrder = kiteUtils.getOrder(userId, callOrder.orderId);
            if (!callOrder.status.equalsIgnoreCase(Constants.ORDER_COMPLETE)) {
                callOrder = kiteUtils.modifyOrReplace(userId, callOrder.orderId, channelLow - 0.5, channelLow - 2);
//                kiteUtils.cancelOrder(userId, callOrder.orderId);
//                callOrder = kiteUtils.placeStopLossOrder(genericStrategyConfig.getIndex().getFnoExchange(), userId, callSymbol, Constants.PRODUCT_MIS, Constants.TRANSACTION_TYPE_SELL, genericStrategyConfig.getQuantity(), channelLow - 0.5, channelLow - 2, getOrderTag());
            }
        }
        if (callOrder != null && callOrder.status.equalsIgnoreCase(Constants.ORDER_COMPLETE)) {
            if (stopLossCallOrder != null) {
                stopLossCallOrder = kiteUtils.getOrder(userId, stopLossCallOrder.orderId);
                if (stopLossCallOrder != null && !stopLossCallOrder.status.equalsIgnoreCase(Constants.ORDER_COMPLETE)) {
                    stopLossCallOrder = kiteUtils.modifyOrReplace(userId, stopLossCallOrder.orderId, channelHigh + 0.5, channelHigh + 2);
//                    kiteUtils.cancelOrder(userId, stopLossCallOrder.orderId);
//                    stopLossCallOrder = kiteUtils.placeStopLossOrder(genericStrategyConfig.getIndex().getFnoExchange(), userId, callSymbol, Constants.PRODUCT_MIS,
//                            Constants.TRANSACTION_TYPE_BUY, genericStrategyConfig.getQuantity(), channelHigh + 0.5, channelHigh + 2, getOrderTag());
                }
            } else {
                stopLossCallOrder = kiteUtils.placeStopLossOrder(genericStrategyConfig.getIndex().getFnoExchange(), userId, callSymbol, Constants.PRODUCT_MIS,
                        Constants.TRANSACTION_TYPE_BUY, genericStrategyConfig.getQuantity(), channelHigh + 0.5, channelHigh + 2, getOrderTag());
            }
        }

        strategies.add(this);

    }

    @SneakyThrows
//    @Override
    public void handleCallback(Map<String, String> orderMap) {
        super.handleCallback(orderMap);
        if (orderMap.get("user_id").equalsIgnoreCase(userId)
                && orderMap.get("tag").equalsIgnoreCase(getOrderTag())
                && orderMap.get("status").equalsIgnoreCase(Constants.ORDER_COMPLETE)) {
            log.info("Checking for orderId in callback");

            if (callOrder != null && orderMap.get("order_id").equalsIgnoreCase(callOrder.orderId)) {
                callOrder = kiteUtils.getOrder(userId, callOrder.orderId);
                log.info("Main call order completed placing stoploss order");
                final List<HistoricalData> historicalData = kiteUtils.getHist(callOrder.exchange+":"+ callOrder.tradingSymbol, "5minute");
                final double channelHigh = TAUtils.getChannelHigh(historicalData, 12) +0.5;
                log.info("Placing stoploss order for {}, Trigger Price {}, Price {}", callSymbol, channelHigh, channelHigh + 2);
                stopLossCallOrder = kiteUtils.placeStopLossOrder(genericStrategyConfig.getIndex().getFnoExchange(), userId, callSymbol, Constants.PRODUCT_MIS, Constants.TRANSACTION_TYPE_BUY, genericStrategyConfig.getQuantity(), channelHigh, channelHigh + 2, getOrderTag());
                log.info("Stoploss orders placed for {} - {}", callSymbol, stopLossCallOrder.orderId);
            }
            if (putOrder != null && orderMap.get("order_id").equalsIgnoreCase(putOrder.orderId)) {
                log.info("Main put order completed placing stoploss order");
                putOrder = kiteUtils.getOrder(userId,putOrder.orderId );
                final List<HistoricalData> historicalData = kiteUtils.getHist(putOrder.exchange+":"+ putOrder.tradingSymbol, "5minute");
                final double channelHigh = TAUtils.getChannelHigh(historicalData, 12) +0.5;
                log.info("Placing stoploss order for {}, Trigger Price {}, Price {}", putSymbol, channelHigh, channelHigh+2);
                stopLossPutOrder = kiteUtils.placeStopLossOrder(genericStrategyConfig.getIndex().getFnoExchange(), userId, putSymbol, Constants.PRODUCT_MIS,  Constants.TRANSACTION_TYPE_BUY, genericStrategyConfig.getQuantity(), channelHigh, channelHigh+2, getOrderTag());
                log.info("Stoploss orders placed for {} - {}" , putSymbol, stopLossPutOrder.orderId );
            }
        }
    }

    @SneakyThrows
    protected void initCallSymbol(long roundPrice) {
        for(int i=-4; i<= 20; i++ )
        {
            long callStrikePrice = roundPrice + i*genericStrategyConfig.getIndex().getStrikePriceMultiple();
            String symbol = genericStrategyConfig.getIndex().getFnoExchange()+":"+genericStrategyConfig.getSymbol()+callStrikePrice+"CE";
            double ltp = kiteUtils.getLtp(userId, symbol);
            if(ltp < getCutoffPrice())
            {
                callSymbol = genericStrategyConfig.getSymbol()+callStrikePrice+"CE";
                log.info("CALL Symbol selected for Directional {}", callSymbol);
                break;
            }
        }
    }

    @SneakyThrows
    protected void initPutSymbol(long roundPrice) {
        for(int i=-4; i<= 20; i++ )
        {
            long putStrikePrice= roundPrice - i*genericStrategyConfig.getIndex().getStrikePriceMultiple();
            String symbol = genericStrategyConfig.getIndex().getFnoExchange()+":"+genericStrategyConfig.getSymbol()+putStrikePrice+"PE";
            double ltp = kiteUtils.getLtp(userId, symbol);
            if(ltp < getCutoffPrice())
            {
                putSymbol = genericStrategyConfig.getSymbol()+putStrikePrice+"PE";
                log.info("PUT Symbol selected for Directions {}", putSymbol);
                break;
            }
        }
    }

    protected int getCutoffPrice()
    {
        return genericStrategyConfig.getPremiumCutoff();
    }

    private String getExchangeSymbol(String symbol)
    {
        return genericStrategyConfig.getIndex().getFnoExchange()+":"+symbol;
    }


}
