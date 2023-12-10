//package com.kite.automation.strategies;
//
//import com.kite.automation.KiteUtils;
//import com.kite.automation.Utils;
//import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
//import com.zerodhatech.kiteconnect.utils.Constants;
//import com.zerodhatech.models.Order;
//import lombok.NoArgsConstructor;
//import lombok.SneakyThrows;
//import lombok.experimental.SuperBuilder;
//import lombok.extern.slf4j.Slf4j;
//
//import java.io.IOException;
//import java.util.Calendar;
//
//@SuperBuilder
//@Slf4j
//@NoArgsConstructor
//public class BankNifty extends AbstractStrategy{
//
//    protected void execute() throws KiteException, IOException {
//        final double lastPrice = kiteUtils.getLtp(userId, "NSE:NIFTY BANK");;
//        final long roundPrice = Math.round(lastPrice / 100)*100;
//
//        final long callStrikePrice = getCallStrikePrice(roundPrice);
//        final long putStrikePrice = getPutStrikePrice(roundPrice);
//
//        callSymbol = strategyConfig.getSymbol()+callStrikePrice+"CE";
//        putSymbol = strategyConfig.getSymbol()+putStrikePrice+"PE";
//
//        log.info("Placing Main sell orders {}, {}", callSymbol, putSymbol);
//
//        String callOrderId = kiteUtils.placeMarketOrder(userId, callSymbol, Constants.PRODUCT_MIS, Constants.TRANSACTION_TYPE_SELL, strategyConfig.getQuantity(), strategyTag.name());
//        String putOrderId = kiteUtils.placeMarketOrder(userId, putSymbol, Constants.PRODUCT_MIS, Constants.TRANSACTION_TYPE_SELL, strategyConfig.getQuantity(), strategyTag.name());
//
//        try {
//            log.info("Waiting for a second before checking order status");
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            // Do nothing
//        }
//        callOrder = kiteUtils.getOrder(userId, callOrderId);
//        putOrder = kiteUtils.getOrder(userId, putOrderId);
//
//        if(callOrder != null)
//        {
//            log.info("{} sold executed at {}", callSymbol,callOrder.averagePrice );
//            double callSLPrice = Utils.getTickPrice(Float.parseFloat(callOrder.averagePrice)+50);
//            stopLossCallOrder = kiteUtils.placeStopLossOrder(userId, callSymbol, Constants.PRODUCT_MIS,  Constants.TRANSACTION_TYPE_BUY, strategyConfig.getQuantity(), callSLPrice, callSLPrice+2, strategyTag.name());
//            log.info("Stoploss orders placed for {} - {}" , callSymbol, stopLossCallOrder.orderId );
//        }
//
//        if(putOrder != null)
//        {
//            log.info("{} sold executed at {}", putSymbol,putOrder.averagePrice );
//            double putSLPrice = Utils.getTickPrice(Float.parseFloat(putOrder.averagePrice)+50);
//            stopLossPutOrder = kiteUtils.placeStopLossOrder(userId, putSymbol, Constants.PRODUCT_MIS,  Constants.TRANSACTION_TYPE_BUY, strategyConfig.getQuantity(), putSLPrice, putSLPrice+2, strategyTag.name());
//            log.info("Stoploss orders placed for {} - {}" , putSymbol, stopLossPutOrder.orderId );
//        }
//    }
//
//    @SneakyThrows
//    private long getCallStrikePrice(long roundPrice) {
//        for(int i=-4; i<= 30; i++ )
//        {
//            long callStrikePrice = roundPrice + i*100;
//            String symbol = "NFO:"+strategyConfig.getSymbol()+callStrikePrice+"CE";
//            double ltp = kiteUtils.getLtp(userId, symbol);
//            if(ltp < getCutoffPrice())
//            {
//                return callStrikePrice;
//            }
//        }
//        return -1;
//    }
//
//    @SneakyThrows
//    private long getPutStrikePrice(long roundPrice) {
//        for(int i=-4; i<= 30; i++ )
//        {
//            long putStrikePrice= roundPrice - i*100;
//            String symbol = "NFO:"+strategyConfig.getSymbol()+putStrikePrice+"PE";
//            double ltp = kiteUtils.getLtp(userId, symbol);
//            if(ltp < getCutoffPrice())
//            {
//                return putStrikePrice;
//            }
//        }
//        return -1;
//    }
//
//    private int getCutoffPrice()
//    {
//        final Calendar instance = Calendar.getInstance();
//        if(instance.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY)
//        {
//            return 125;
//        } else {
//            return 150;
//        }
//    }
//
//}
