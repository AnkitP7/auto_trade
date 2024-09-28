package com.kite.automation.strategies;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.kite.automation.KiteUtils;
import com.kite.automation.strategies.v1.DirectionalSelling;
import com.kite.automation.strategies.v1.GenericStrategyConfig;
import com.kite.automation.strategies.v1.Straddle;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.kiteconnect.utils.Constants;
import com.zerodhatech.models.HistoricalData;
import com.zerodhatech.models.Order;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;

@SuperBuilder
@Data
@Slf4j
@NoArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "strategyTag",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Straddle.class, name = "STRADDLE"),
        @JsonSubTypes.Type(value = DirectionalSelling.class, name = "DIRECTIONAL")
})
public abstract class AbstractStrategy implements Runnable{

    protected StrategyTag strategyTag;

    protected KiteUtils kiteUtils;

    protected String userId;

    protected Strategies strategies;

    protected GenericStrategyConfig genericStrategyConfig;

    protected Order retryStopLossCallOrder = null;
    protected Order retryStopLossPutOrder = null;
    protected Order stopLossCallOrder = null;
    protected Order stopLossPutOrder = null;
    protected Order callOrder = null;
    protected Order putOrder = null;
    protected Order reEntryCallOrder = null;
    protected Order reEntryPutOrder = null;
    protected String callSymbol = null;
    protected String putSymbol = null;
    protected String hedgeCallSymbol = null;
    protected String hedgePutSymbol = null;

    @SneakyThrows
    @Override
    public void run() {
        process();
    }

    public void process() throws KiteException, IOException {
        log.info("Started processing order for {} {}", userId, strategyTag);
        if (genericStrategyConfig.getQuantity() > 0) {
            execute();
        }
        strategies.add(this);
    }

    protected abstract void execute() throws KiteException, IOException;

    public void handleStoplossTriggered(Map<String, String> orderMap) {
        final String orderId = orderMap.get("order_id");
        final String orderId1 = stopLossCallOrder == null ? null: stopLossCallOrder.orderId;
        final String orderId2 = stopLossPutOrder == null ? null: stopLossPutOrder.orderId;
        final String orderId3 = retryStopLossCallOrder == null ? null : retryStopLossCallOrder.orderId;
        final String orderId4 = retryStopLossPutOrder == null ? null : retryStopLossPutOrder.orderId;
        if (orderId.equalsIgnoreCase(orderId1) || orderId.equalsIgnoreCase(orderId2)
                || orderId.equalsIgnoreCase(orderId3) || orderId.equalsIgnoreCase(orderId4)) {
            Thread thread = new Thread(new Runnable() {
                @SneakyThrows
                @Override
                public void run() {
                    log.info("{} : Stoploss order triggered User ID {}, Tag {}", orderId, userId, strategyTag);
                    log.info("{} : Waiting for 15 seconds and converting order to market if not closed", orderId);
                    Thread.sleep(1000 * 3);
                    final Order order = kiteUtils.getOrder(userId, orderId);
                    if (!order.status.equalsIgnoreCase(Constants.ORDER_COMPLETE)) {
                        log.info("{} : Order not complete after 5 seconds. Converting to market", orderId);
                        kiteUtils.convertToMarket(userId, orderId);
                    } else {
                        log.info("{} : Order status is complete", orderId);
                    }
                }
            });
            thread.start();
        }
    }

    @SneakyThrows
    public void handleCallback(Map<String, String> orderMap) {
        log.info("Inside handleCallback for strategy");
        if (orderMap.get("user_id").equalsIgnoreCase(userId)
                && orderMap.get("tag").equalsIgnoreCase(getOrderTag())
                && orderMap.get("status").equalsIgnoreCase(Constants.ORDER_COMPLETE)) {
            log.info("Checking for orderId in callback");
            if (stopLossCallOrder != null && orderMap.get("order_id").equalsIgnoreCase(stopLossCallOrder.orderId)) {
                handleSLCallOrderCompletion();
            }
            if (stopLossPutOrder != null && orderMap.get("order_id").equalsIgnoreCase(stopLossPutOrder.orderId)) {
                log.info("Stoploss Put order completed and hedgePutSymbol is available. Placing market Sell order for hedge");
                if (hedgePutSymbol != null) {
                    kiteUtils.placeMarketOrder(genericStrategyConfig.getIndex().getFnoExchange(), userId, hedgePutSymbol, Constants.PRODUCT_MIS, Constants.TRANSACTION_TYPE_SELL, genericStrategyConfig.getQuantity(), strategyTag.name());
                    hedgePutSymbol = null;
                }
                stopLossPutOrder = kiteUtils.getOrder(userId, stopLossPutOrder.orderId);
            }


        }
    }

    protected void handleSLCallOrderCompletion() throws KiteException, IOException {
        log.info("Stoploss call order completed and hedgeCallSymbol is available. Placing market Sell order for hedge");
        if (hedgeCallSymbol != null) {
            kiteUtils.placeMarketOrder(genericStrategyConfig.getIndex().getFnoExchange(), userId, hedgeCallSymbol, Constants.PRODUCT_MIS, Constants.TRANSACTION_TYPE_SELL, genericStrategyConfig.getQuantity(), strategyTag.name());
            hedgeCallSymbol = null;
        }
        stopLossCallOrder = kiteUtils.getOrder(userId,stopLossCallOrder.orderId );
    }

    public void close() throws KiteException, IOException {
        log.info("Converting orders to market for {}, {}", userId, strategyTag.name());
        if(stopLossCallOrder != null)
        {
            kiteUtils.convertToMarketOrNrml(userId, stopLossCallOrder);
        }
        if(stopLossPutOrder != null)
        {
            kiteUtils.convertToMarketOrNrml(userId, stopLossPutOrder);
        }
        if(retryStopLossPutOrder != null) {
            kiteUtils.convertToMarketOrNrml(userId, retryStopLossPutOrder);
        }
        if(retryStopLossCallOrder != null) {
            kiteUtils.convertToMarketOrNrml(userId, retryStopLossCallOrder);
        }
    }

    @JsonIgnore
    public String getKeyId() {
        return genericStrategyConfig == null ? userId + "_" + strategyTag : userId + "_"+ getGenericStrategyTag();
    }


    @JsonIgnore
    public String getGenericStrategyTag() {
        if(genericStrategyConfig != null)
        {
            String tag =  genericStrategyConfig.getStrategyTag()+"_"+genericStrategyConfig.getIndex()+"_"+genericStrategyConfig.getStartTimeString();
            tag = tag.replace("FINNIFTY", "FN");
            tag = tag.replace("BANKNIFTY", "BN");
            tag = tag.replace("DIRECTIONAL", "D");
            tag = tag.replace("MIDCPNIFTY", "MCP");
            return tag;
        }
        return null;
    }

    @JsonIgnore
    public String getOrderTag()
    {
        return getGenericStrategyTag() == null ? strategyTag.name() : getGenericStrategyTag();
    }

    public void processCandle()
    {

    }


}
