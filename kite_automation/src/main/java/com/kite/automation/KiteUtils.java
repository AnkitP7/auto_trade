package com.kite.automation;

import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.InputException;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.PermissionException;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.TokenException;
import com.zerodhatech.kiteconnect.utils.Constants;
import com.zerodhatech.models.ContractNote;
import com.zerodhatech.models.ContractNoteParams;
import com.zerodhatech.models.HistoricalData;
import com.zerodhatech.models.Instrument;
import com.zerodhatech.models.LTPQuote;
import com.zerodhatech.models.Order;
import com.zerodhatech.models.OrderParams;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class KiteUtils {

    @Autowired
    private KiteWebSocket kiteWebSocket;
    @Autowired
    private KiteSessions kiteSessions;

    public String placeMarketOrder(String exchange, String userId, String symbol, String misOrNrml, String buyOrSell, int quantity, String tag) {
        OrderParams orderParams  = new OrderParams();
        orderParams.exchange = exchange;
        orderParams.tradingsymbol = symbol;
        orderParams.transactionType = buyOrSell;
        orderParams.orderType = Constants.ORDER_TYPE_MARKET;
        orderParams.quantity = quantity;
        orderParams.tag = tag;
        orderParams.validity = Constants.VALIDITY_DAY;
        orderParams.product = misOrNrml;

        Order order = null;
        try {
            order = getKiteSession(userId).placeOrder(orderParams, Constants.VARIETY_REGULAR);
        } catch (KiteException e) {
            log.error("Error while placing order {}", e.message);
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return order.orderId;
    }

    public Order placeStopLossOrder(String exchange, String userId, String symbol, String misOrNrml, String buyOrSell, int quantity, double triggerPrice, double price, String tag) {

        OrderParams orderParams  = new OrderParams();
        orderParams.exchange = exchange;
        orderParams.tradingsymbol = symbol;
        orderParams.transactionType = buyOrSell;
        orderParams.orderType = Constants.ORDER_TYPE_SL;
        orderParams.quantity = quantity;
        orderParams.price = price;
        orderParams.triggerPrice = triggerPrice;
        orderParams.tag = tag;
        orderParams.validity = Constants.VALIDITY_DAY;
        orderParams.product = misOrNrml;

        Order order = null;
        try {
            order = getKiteSession(userId).placeOrder(orderParams, Constants.VARIETY_REGULAR);
            order = getOrder(userId, order.orderId);
        } catch (KiteException e) {
            log.error("{} - Error while placing order {}", userId, e.message);
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return order;
    }

    public void convertToMarket(String userId, String orderId) throws KiteException, IOException {
        if(orderId == null)
        {
            return;
        }
        log.info("Converting order {} to market order", orderId);
        OrderParams orderParams  = new OrderParams();
        orderParams.orderType = Constants.ORDER_TYPE_MARKET;
        try {
            getKiteSession(userId).modifyOrder(orderId, orderParams, Constants.VARIETY_REGULAR);
        } catch (InputException ex)
        {
            log.info("Not able to convert order to market order - {}", ex.getMessage());
        }
    }

    public void convertToMarketOrNrml(String userId, Order order) throws KiteException, IOException {
        if(order == null)
        {
            return;
        }
        final double ltp = getLtp(userId, order.exchange+":"+order.tradingSymbol);
        if(ltp < 0.20)
        {
            log.info("{} - Since market price was trading below 0.20 converting order to NRML {}", userId, order.orderId);
            boolean result = convertMISToNRML(userId, order);
            if(result == true)
            {
                log.info("{} Position converted to NRML. Let pending order get automatically cancelled {}" ,userId, order.orderId);
                return;
            } else {
                log.error("{} - Failed to convert order to NRML {}", userId, order.orderId);
            }
        }
        log.info("Converting order {} to market order", order.orderId);
        OrderParams orderParams  = new OrderParams();
        orderParams.orderType = Constants.ORDER_TYPE_MARKET;
        try {
            getKiteSession(userId).modifyOrder(order.orderId, orderParams, Constants.VARIETY_REGULAR);
        } catch (InputException ex)
        {
            log.info("Not able to convert order to market order - {}", ex.getMessage());
        }
    }

    @SneakyThrows
    public Order getOrder(String userId, String orderId) {
        final List<Order> orders = getKiteSession(userId).getOrders();
        for (Order order : orders) {
            if(order.orderId.equalsIgnoreCase(orderId))
            {
                return order;
            }
        }
        return null;
    }

    public double getLtp(String userId, String symbol) throws KiteException, IOException {
        System.out.println("getting ltp for symbol " + symbol);
        System.out.println("kite session " + getKiteSession(userId));
        try {
            final Map<String, LTPQuote> ltp = getKiteSession(userId).getLTP(new String[]{symbol});
            return ltp.get(symbol).lastPrice;
        } catch (TokenException tokenException)
        {
            System.out.println(tokenException.getMessage());
            System.out.println(tokenException.code);
            System.out.println(tokenException.message);
            System.out.println(tokenException.toString());
            throw  tokenException;
        }

    }

    @SneakyThrows
    public List<HistoricalData> getHist(String symbol, String interval)
    {
        try {
            final Map<String, LTPQuote> ltp = getKiteSession().getLTP(new String[]{symbol});
            final long instrumentToken = ltp.get(symbol).instrumentToken;
            Calendar toCalender = Calendar.getInstance();
            Calendar fromCalender = Calendar.getInstance();
            fromCalender.set(Calendar.HOUR_OF_DAY, 9);
            fromCalender.set(Calendar.MINUTE, 15);
            fromCalender.set(Calendar.SECOND, 0);
            fromCalender.add(Calendar.DAY_OF_MONTH, -3);
            final HistoricalData historicalData = getKiteSession().getHistoricalData(fromCalender.getTime(), toCalender.getTime(), String.valueOf(instrumentToken), interval, false, false);
            final List<HistoricalData> dataArrayList = historicalData.dataArrayList;
            return dataArrayList;
        } catch (TokenException tokenException)
        {
            System.out.println(tokenException.message);
        } catch (PermissionException permissionException)
        {
            System.out.println(permissionException.message);
        }
        return null;

    }

    @SneakyThrows
    public Order cancelOrder(String userId, String orderId)
    {
        return getKiteSession(userId).cancelOrder(orderId, Constants.VARIETY_REGULAR);
    }

    @SneakyThrows
    public List<Order> getOrderHistory(String userId, String orderId)
    {
        return getKiteSession(userId).getOrderHistory(orderId);
    }

    public Order modifyOrder(String userId, String orderId, double triggerPrice, double price)
    {
        try {
            OrderParams orderParams = new OrderParams();
            orderParams.triggerPrice = triggerPrice;
            orderParams.price = price;
            return getKiteSession(userId).modifyOrder(orderId,orderParams,  Constants.VARIETY_REGULAR);
        } catch (Throwable e) {
            log.error("Error while modifying order {} - {}", orderId, e.getMessage());
        }
        return null;

    }

    public Order modifyOrReplace(String userId, String orderId, double triggerPrice, double price) {
        try {
            OrderParams orderParams = new OrderParams();
            orderParams.triggerPrice = triggerPrice;
            orderParams.price = price;
            Order order =  getKiteSession(userId).modifyOrder(orderId,orderParams,  Constants.VARIETY_REGULAR);
            order = getOrder(userId, order.orderId);
            return order;
        } catch (Throwable e) {
            log.error("Error while modifying order {} - {}", orderId, e.getMessage());
            Order order = getOrder(userId, orderId);
            cancelOrder(userId, orderId);
            Order replacedOrder = placeStopLossOrder(order.exchange, userId, order.tradingSymbol, Constants.PRODUCT_MIS,
                    Constants.TRANSACTION_TYPE_BUY, Integer.parseInt(order.quantity), triggerPrice, price, order.tag);
            return replacedOrder;
        }
    }

    public boolean convertMISToNRML(String userId, Order order)
    {
        try {
            getKiteSession(userId).convertPosition(order.tradingSymbol, order.exchange, Constants.TRANSACTION_TYPE_SELL, Constants.POSITION_DAY, Constants.PRODUCT_MIS, Constants.PRODUCT_NRML, Integer.parseInt(order.quantity));
            return true;
        }catch (InputException e) {
            e.printStackTrace();
            log.error("Input Exception converting order {} - {} {}", order.orderId, e.message, e.code);
        }
        catch (KiteException e) {
            e.printStackTrace();
            log.error("Kite Exception converting order {} - {} {}", order.orderId, e.message, e.code);
        } catch (IOException e)
        {
            e.printStackTrace();
            log.error("IOException while converting order {} - {}", order.orderId, e.getMessage());
        } catch (JSONException e)
        {
            e.printStackTrace();
            log.error("JSON Exception while converting order {} - {}", order.orderId, e.getMessage());
        }
        return false;
    }

    private KiteConnect getKiteSession(String userId)
    {
        return kiteSessions.getSession(userId);
    }

    private KiteConnect getKiteSession()
    {
        return getKiteSession("DA0522");
    }


    public List<Instrument> fetchInstruments() throws IOException, KiteException {
        final List<Instrument> instruments = getKiteSession().getInstruments();
        return instruments;
    }

    @SneakyThrows
    public double getCharges(String userId, Order order) {
        List<ContractNoteParams> contractNoteParamsList = new ArrayList<>();

        ContractNoteParams contractNoteParams = new ContractNoteParams();
        contractNoteParams.exchange = order.exchange;
        contractNoteParams.orderType = order.orderType;
        contractNoteParams.quantity = Integer.parseInt(order.quantity);
        contractNoteParams.product = order.product;
        contractNoteParams.tradingSymbol = order.tradingSymbol;
        contractNoteParams.averagePrice = Double.parseDouble(order.averagePrice);
        contractNoteParams.orderID = order.orderId;
        contractNoteParams.transactionType = order.transactionType;
        contractNoteParams.variety = order.orderVariety;
        contractNoteParamsList.add(contractNoteParams);

        final List<ContractNote> virtualContractNotes = getKiteSession(userId).getVirtualContractNote(contractNoteParamsList);
        if (virtualContractNotes.size() == 1) {
            ContractNote contractNote = virtualContractNotes.get(0);
            return contractNote.charges.total;
        }
        return 0;
    }

}
