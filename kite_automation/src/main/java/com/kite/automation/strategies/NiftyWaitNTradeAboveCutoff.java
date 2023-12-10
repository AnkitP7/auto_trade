//package com.kite.automation.strategies;
//
//import lombok.NoArgsConstructor;
//import lombok.SneakyThrows;
//import lombok.experimental.SuperBuilder;
//import lombok.extern.slf4j.Slf4j;
//
//@SuperBuilder
//@Slf4j
//@NoArgsConstructor
//public class NiftyWaitNTradeAboveCutoff extends NiftyWaitNTrade{
//
//    @SneakyThrows
//    @Override
//    protected long getCallStrikePrice(long roundPrice) {
//        long prevCallStrikePrice = -1;
//        for(int i=-4; i<= 20; i++ )
//        {
//            long callStrikePrice = roundPrice + i*50;
//            String symbol = "NFO:"+strategyConfig.getSymbol()+callStrikePrice+"CE";
//            double ltp = kiteUtils.getLtp(userId, symbol);
//            if(ltp < getCutoffPrice())
//            {
//                return prevCallStrikePrice;
//            }
//            prevCallStrikePrice = callStrikePrice;
//        }
//        return -1;
//    }
//
//    @SneakyThrows
//    @Override
//    protected long getPutStrikePrice(long roundPrice) {
//        long prevPutStrikePrice = -1;
//        for(int i=-4; i<= 20; i++ )
//        {
//            long putStrikePrice= roundPrice - i*50;
//            String symbol = "NFO:"+strategyConfig.getSymbol()+putStrikePrice+"PE";
//            double ltp = kiteUtils.getLtp(userId, symbol);
//            if(ltp < getCutoffPrice())
//            {
//                return prevPutStrikePrice;
//            }
//            prevPutStrikePrice = putStrikePrice;
//        }
//        return -1;
//    }
//
//}
