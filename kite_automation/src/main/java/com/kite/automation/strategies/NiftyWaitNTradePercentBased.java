//package com.kite.automation.strategies;
//
//import com.kite.automation.Utils;
//import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
//import com.zerodhatech.kiteconnect.utils.Constants;
//import lombok.NoArgsConstructor;
//import lombok.SneakyThrows;
//import lombok.experimental.SuperBuilder;
//import lombok.extern.slf4j.Slf4j;
//
//import java.io.IOException;
//import java.util.Map;
//
//@SuperBuilder
//@Slf4j
//@NoArgsConstructor
//public class NiftyWaitNTradePercentBased extends AbstractStrategy{
//    protected void execute() throws KiteException, IOException {
//        final double lastPrice = kiteUtils.getLtp(userId, "NSE:NIFTY 50");;
//        final long roundPrice = Math.round(lastPrice / 50)*50;
//
//        String atmCallSymbol = strategyConfig.getSymbol()+roundPrice+"CE";
//        String atmPutSymbol = strategyConfig.getSymbol()+roundPrice+"PE";
//        double straddlePrice = kiteUtils.getLtp(userId, "NFO:"+ atmCallSymbol) + kiteUtils.getLtp(userId, "NFO:"+ atmPutSymbol);
//
//        final long callStrikePrice = getCallStrikePrice(roundPrice, straddlePrice);
//        final long putStrikePrice = getPutStrikePrice(roundPrice, straddlePrice);
//
//        callSymbol = strategyConfig.getSymbol()+callStrikePrice+"CE";
//        putSymbol = strategyConfig.getSymbol()+putStrikePrice+"PE";
//
//        double callLtp = kiteUtils.getLtp(userId, "NFO:"+callSymbol);
//        double putLtp = kiteUtils.getLtp(userId, "NFO:"+putSymbol);
//
//        double callTriggerPrice = Utils.getTickPrice((float)(0.9*callLtp));
//        double putTriggerPrice = Utils.getTickPrice((float)(0.9*putLtp));
//
//        log.info("Placing Main SL-sell orders {}, {}", callSymbol, putSymbol);
//
//        callOrder = kiteUtils.placeStopLossOrder(userId, callSymbol, Constants.PRODUCT_MIS, Constants.TRANSACTION_TYPE_SELL, strategyConfig.getQuantity(),callTriggerPrice, callTriggerPrice-4, strategyTag.name());
//        putOrder = kiteUtils.placeStopLossOrder(userId, putSymbol, Constants.PRODUCT_MIS, Constants.TRANSACTION_TYPE_SELL, strategyConfig.getQuantity(),putTriggerPrice, putTriggerPrice-4,  strategyTag.name());
//
//    }
//
//    @SneakyThrows
//    @Override
//    public void handleCallback(Map<String, String> orderMap) {
//        super.handleCallback(orderMap);
//        if (orderMap.get("user_id").equalsIgnoreCase(userId)
//                && orderMap.get("tag").equalsIgnoreCase(strategyTag.name())
//                && orderMap.get("status").equalsIgnoreCase(Constants.ORDER_COMPLETE)) {
//            log.info("Checking for orderId in callback");
//
//            if (callOrder != null && orderMap.get("order_id").equalsIgnoreCase(callOrder.orderId)) {
//                callOrder = kiteUtils.getOrder(userId,callOrder.orderId );
//                log.info("Main call order completed placing stoploss order");
//                float callSLPrice = Utils.getTickPrice(Float.parseFloat(callOrder.averagePrice)*1.3f);
//                log.info("Placing stoploss order for {}, Trigger Price {}, Price {}", callSymbol, callSLPrice, callSLPrice+2);
//                stopLossCallOrder = kiteUtils.placeStopLossOrder(userId, callSymbol, Constants.PRODUCT_MIS,  Constants.TRANSACTION_TYPE_BUY, strategyConfig.getQuantity(), callSLPrice, callSLPrice+2, strategyTag.name());
//                log.info("Stoploss orders placed for {} - {}" , callSymbol, stopLossCallOrder.orderId );
//            }
//            if (putOrder != null && orderMap.get("order_id").equalsIgnoreCase(putOrder.orderId)) {
//                log.info("Main put order completed placing stoploss order");
//                putOrder = kiteUtils.getOrder(userId,putOrder.orderId );
//                float putSLPrice = Utils.getTickPrice(Float.parseFloat(putOrder.averagePrice)*1.3f);
//                log.info("Placing stoploss order for {}, Trigger Price {}, Price {}", putSymbol, putSLPrice, putSLPrice+2);
//                stopLossPutOrder = kiteUtils.placeStopLossOrder(userId, putSymbol, Constants.PRODUCT_MIS,  Constants.TRANSACTION_TYPE_BUY, strategyConfig.getQuantity(), putSLPrice, putSLPrice+2, strategyTag.name());
//                log.info("Stoploss orders placed for {} - {}" , putSymbol, stopLossPutOrder.orderId );
//            }
//        }
//    }
//
//    @SneakyThrows
//    protected long getCallStrikePrice(long roundPrice, double lastPrice) {
//        long cutoff = Math.round(lastPrice*0.10);
//        for(int i=-4; i<= 20; i++ )
//        {
//            long callStrikePrice = roundPrice + i*50;
//            String symbol = "NFO:"+strategyConfig.getSymbol()+callStrikePrice+"CE";
//            double ltp = kiteUtils.getLtp(userId, symbol);
//            if(ltp < cutoff)
//            {
//                return callStrikePrice;
//            }
//        }
//        return -1;
//    }
//
//    @SneakyThrows
//    protected long getPutStrikePrice(long roundPrice, double lastPrice) {
//        long cutoff = Math.round(lastPrice*0.10);
//        for(int i=-4; i<= 20; i++ )
//        {
//            long putStrikePrice= roundPrice - i*50;
//            String symbol = "NFO:"+strategyConfig.getSymbol()+putStrikePrice+"PE";
//            double ltp = kiteUtils.getLtp(userId, symbol);
//            if(ltp < cutoff)
//            {
//                return putStrikePrice;
//            }
//        }
//        return -1;
//    }
//}
