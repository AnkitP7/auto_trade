package com.kite.automation.strategies.v1;

import com.kite.automation.Utils;
import com.kite.automation.strategies.AbstractStrategy;
import com.kite.automation.strategies.Strategies;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.kiteconnect.utils.Constants;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@SuperBuilder
@Slf4j
@NoArgsConstructor
public class Straddle extends AbstractStrategy {


    @Override
    protected void execute() throws KiteException, IOException {
        final Index index = genericStrategyConfig.getIndex();
        final double lastPrice = kiteUtils.getLtp(userId, index.getExchangeAndSymbol());
        final long atmStrikePrice = Math.round(lastPrice / index.getStrikePriceMultiple())*index.getStrikePriceMultiple();

        final int stopLossPercent = genericStrategyConfig.getStopLossPercent();
        final float stoplossFactor = (100f+stopLossPercent)/100;

        final int moneyNess = genericStrategyConfig.getMoneyNess();

        log.info("{} - Shorting put and call at strike price of {} with stoploss {}" , userId, atmStrikePrice, stoplossFactor );

        long callStrikePrice = atmStrikePrice + moneyNess*index.getStrikePriceMultiple();
        long putStrikePrice = atmStrikePrice - moneyNess*index.getStrikePriceMultiple();

        callSymbol = genericStrategyConfig.getSymbol()+callStrikePrice+"CE";
        putSymbol = genericStrategyConfig.getSymbol()+putStrikePrice+"PE";

        String callOrderId = kiteUtils.placeMarketOrder(index.getFnoExchange(), userId, callSymbol, Constants.PRODUCT_MIS, Constants.TRANSACTION_TYPE_SELL, genericStrategyConfig.getQuantity(), getOrderTag());
        String putOrderId = kiteUtils.placeMarketOrder(index.getFnoExchange(), userId, putSymbol, Constants.PRODUCT_MIS, Constants.TRANSACTION_TYPE_SELL, genericStrategyConfig.getQuantity(), getOrderTag());

        try {
            log.info("{} - Waiting for a second before checking order status", userId);
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // Do nothing
        }
        callOrder = kiteUtils.getOrder(userId, callOrderId);
        putOrder = kiteUtils.getOrder(userId, putOrderId);

        if(callOrder != null)
        {
            log.info("{} - {} sold executed at {}", userId, callSymbol,callOrder.averagePrice );
            double callSLPrice = Utils.getTickPrice(Float.parseFloat(callOrder.averagePrice)*stoplossFactor);
            stopLossCallOrder = kiteUtils.placeStopLossOrder(index.getFnoExchange(), userId, callSymbol, Constants.PRODUCT_MIS,  Constants.TRANSACTION_TYPE_BUY, genericStrategyConfig.getQuantity(), callSLPrice, callSLPrice+2, getOrderTag());
            if(stopLossCallOrder != null) {
                log.info("{} - Stoploss orders placed for {} - {}", userId, callSymbol, stopLossCallOrder.orderId);
            }
        }

        if(putOrder != null)
        {
            log.info("{} sold executed at {}", putSymbol,putOrder.averagePrice );
            double putSLPrice = Utils.getTickPrice(Float.parseFloat(putOrder.averagePrice)*stoplossFactor);
            stopLossPutOrder = kiteUtils.placeStopLossOrder(index.getFnoExchange(), userId, putSymbol, Constants.PRODUCT_MIS,  Constants.TRANSACTION_TYPE_BUY, genericStrategyConfig.getQuantity(), putSLPrice, putSLPrice+2, getOrderTag());
            if(stopLossPutOrder != null) {
                log.info("Stoploss orders placed for {} - {}", putSymbol, stopLossPutOrder.orderId);
            }
        }

    }


}
