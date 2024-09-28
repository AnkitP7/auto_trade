//package com.kite.automation.strategies;
//
//import com.kite.automation.Utils;
//import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
//import com.zerodhatech.kiteconnect.utils.Constants;
//import lombok.NoArgsConstructor;
//import lombok.experimental.SuperBuilder;
//import lombok.extern.slf4j.Slf4j;
//
//import java.io.IOException;
//import java.util.Calendar;
//
//@SuperBuilder
//@Slf4j
//@NoArgsConstructor
//public class NiftyMoneyNess extends AbstractStrategy{
//
//    protected void execute() throws KiteException, IOException {
//        final double lastPrice = kiteUtils.getLtp(userId, "NSE:NIFTY 50");
//        final long atmStrikePrice = Math.round(lastPrice / 50)*50;
//
//        log.info("Shorting put and call at strike price of {}" , atmStrikePrice );
//
//        callSymbol = strategyConfig.getSymbol()+(atmStrikePrice+getMoneyNess())+"CE";
//        putSymbol = strategyConfig.getSymbol()+(atmStrikePrice-getMoneyNess())+"PE";
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
//            double callSLPrice = Utils.getTickPrice(Float.parseFloat(callOrder.averagePrice)*1.3f);
//            stopLossCallOrder = kiteUtils.placeStopLossOrder(userId, callSymbol, Constants.PRODUCT_MIS,  Constants.TRANSACTION_TYPE_BUY, strategyConfig.getQuantity(), callSLPrice, callSLPrice+2, strategyTag.name());
//            log.info("Stoploss orders placed for {} - {}" , callSymbol, stopLossCallOrder.orderId );
//        }
//
//        if(putOrder != null)
//        {
//            log.info("{} sold executed at {}", putSymbol,putOrder.averagePrice );
//            double putSLPrice = Utils.getTickPrice(Float.parseFloat(putOrder.averagePrice)*1.3f);
//            stopLossPutOrder = kiteUtils.placeStopLossOrder(userId, putSymbol, Constants.PRODUCT_MIS,  Constants.TRANSACTION_TYPE_BUY, strategyConfig.getQuantity(), putSLPrice, putSLPrice+2, strategyTag.name());
//            log.info("Stoploss orders placed for {} - {}" , putSymbol, stopLossPutOrder.orderId );
//        }
//
//
//    }
//
//    private int getMoneyNess()
//    {
////        final Calendar instance = Calendar.getInstance();
////        if(instance.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY || instance.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY)
////        {
////            return -50;
////        }
////        if(instance.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY)
////        {
////            return 50;
////        }
//        return 0;
//    }
//
//}
