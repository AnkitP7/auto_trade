package com.kite.automation.schedules;

import com.kite.automation.KiteSessions;
import com.kite.automation.KiteUtils;
import com.kite.automation.google.sheets.SheetsService;
import com.kite.automation.strategies.Strategies;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.utils.Constants;
import com.zerodhatech.models.Order;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

@Component
@Slf4j
public class TradeLogger {

    @Autowired
    KiteSessions kiteSessions;

    @Autowired
    SheetsService sheetsService;

    @Autowired
    KiteUtils kiteUtils;

    @Autowired
    Strategies strategies;

    @SneakyThrows
    @Scheduled(cron = "59 29 15 ? * MON,TUE,WED,THU,FRI",zone = "Asia/Kolkata")
    public void logTrades()
    {
        System.out.println("Logging todays trades");
        Collection<KiteConnect> sessions = kiteSessions.getKiteSessions();
        for (KiteConnect kiteConnect:
             sessions) {
            System.out.println("Logging Trade for " + kiteConnect.getUserId());
            if(kiteConnect.getUserId().equalsIgnoreCase("AUTH_TOKEN"))
            {
                return;
            }
            logTrades(kiteConnect);
        }
    }

    public void logTrades(String userId)
    {
        System.out.println("Logging trades for userId");
        Collection<KiteConnect> sessions = kiteSessions.getKiteSessions();
        for (KiteConnect kiteConnect:
                sessions) {
            System.out.println("Logging Trade for " + kiteConnect.getUserId());
            if(kiteConnect.getUserId().equalsIgnoreCase(userId))
            {
                logTrades(kiteConnect);
            }
        }
    }

    @SneakyThrows
    private void logTrades(KiteConnect kiteConnect) {

        final String userId = kiteConnect.getUserId();
        final String apiKey = System.getenv(userId + "_API_KEY");
        if(apiKey == null)
        {
            return;
        }
        final List<Order> orders = kiteConnect.getOrders();
        log.info("Logging orders. Total orders {}", orders.size());
        // date, month,  datetime, tag, symbol, buyOrSell, quantity, price, Credit, Charges, net Credit
        List<List<Object>> logData = new ArrayList<>();
        for (Order order:
             orders) {

            if(!order.status.equalsIgnoreCase(Constants.ORDER_COMPLETE) )
            {
                continue;
            }
            List<Object> orderData = new ArrayList<>();


            final Calendar instance = Calendar.getInstance();
            int month = instance.get(Calendar.MONTH)+1;
            final int year = instance.get(Calendar.YEAR);
            String yearMonth = String.valueOf(year);
            String monthString = String.valueOf(month);
            if(month<10)
            {
                monthString = 0+monthString;
            }
            yearMonth = yearMonth+"_"+ monthString;
            orderData.add(instance.get(Calendar.YEAR)+"/"+ (instance.get(Calendar.MONTH)+1)+"/"+instance.get(Calendar.DAY_OF_MONTH));
            orderData.add(yearMonth);
            orderData.add(String.valueOf(order.orderTimestamp));
            orderData.add(order.tag == null ? "" : order.tag);
            orderData.add(String.valueOf(order.tradingSymbol));
            orderData.add(String.valueOf(order.transactionType));
            orderData.add(String.valueOf(order.quantity));
            orderData.add(String.valueOf(order.averagePrice));

            double credit = Double.parseDouble(order.quantity)*Double.parseDouble(order.averagePrice);
            if(order.transactionType.equalsIgnoreCase("BUY"))
            {
                credit = credit*-1;
            }
            double charges = kiteUtils.getCharges(userId, order);
            double netCredit = credit-charges;
            orderData.add(String.valueOf(credit));
            orderData.add(String.valueOf(charges));
            orderData.add(String.valueOf(netCredit));

            logData.add(orderData);

        }

        sheetsService.logTrades(userId, logData);

    }
}
