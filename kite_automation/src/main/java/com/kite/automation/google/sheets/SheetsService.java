package com.kite.automation.google.sheets;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.kite.automation.Utils;
import com.kite.automation.strategies.InstrumentService;
import com.kite.automation.strategies.StrategyTag;
import com.kite.automation.strategies.v1.GenericStrategyConfig;
import com.kite.automation.strategies.v1.Index;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Service
@Slf4j
public class SheetsService {
    private final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
    private final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Autowired
    private InstrumentService instrumentService;

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private String CREDENTIALS = System.getenv("GCP_CREDENTIALS");
    private String SS_ID = System.getenv("GCP_SS_ID");

    @SneakyThrows
    private Sheets getService()
    {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, GoogleCredential.fromStream(new ByteArrayInputStream(CREDENTIALS.getBytes()))
                .createScoped(SCOPES))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }


    public void logTrades(String userID, List<List<Object>> values) throws Exception
    {
        ValueRange body = new ValueRange()
                .setValues(values);
        getService().spreadsheets().values().append(getSsId(userID), userID+"_Orders", body)
                .setValueInputOption("USER_ENTERED")
                .execute();
    }

    public List<GenericStrategyConfig> getStrategyConfigs(String userID)
    {
        List<GenericStrategyConfig> configList = new ArrayList<>();
        final String range = userID+"!A1:K40";
        ValueRange result = null;
        try {
            result = getService().spreadsheets().values().get(getSsId(userID), range).execute();
        } catch (IOException ex) {
            log.info("Exception while fetching strategty config {}", ex.getMessage());
        }
        if(result == null)
        {
            try {
                Thread.sleep(1000);
                result = getService().spreadsheets().values().get(getSsId(userID), range).execute();
            } catch (IOException | InterruptedException ex) {
                log.info("Exception while fetching strategty config {}", ex.getMessage());
            }
        }
        if(result == null)
        {
            return null;
        }
        final List<List<Object>> values = result.getValues();
        boolean firstRow = true;
        for (List<Object> row:
                values) {
            if(firstRow)
            {
                firstRow = false;
                continue;
            }
            if( Calendar.getInstance().get(Calendar.DAY_OF_WEEK) != Utils.getDayOfWeek(row.get(0).toString()) )
            {
                continue;
            }
            StrategyTag strategyTag = StrategyTag.valueOf(row.get(1).toString());
            Index index = Index.valueOf(row.get(2).toString());
            String time = row.get(3).toString();
            String[] timeSplit  = time.split(":");
            final Calendar instance = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"));
            instance.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeSplit[0]));
            instance.set(Calendar.MINUTE, Integer.parseInt(timeSplit[1]));
            instance.set(Calendar.SECOND, 0);
            Date startTime = instance.getTime();
            int quantity = Integer.parseInt(row.get(4).toString());
            String stoploss = row.get(5).toString();
            int stoplossPercent = -1;
            int stoplossPoints = - 1;
            if(stoploss.contains("%"))
            {
                stoplossPercent = Integer.parseInt(stoploss.replace("%", ""));
            } else {
                stoplossPoints = Integer.parseInt(stoploss);
            }
            int moneyNess = Integer.parseInt(row.get(6).toString());
            String premiumOperator = row.size() > 7 ? row.get(7).toString() : null;
            String premiumCutoff = row.size() > 8 ? row.get(8).toString() : null;
            String premiumDecay = row.size() > 9 ? row.get(9).toString() : null;

            time = time.replace(":","");
            time = time.replace(" ","");
            GenericStrategyConfig strategyConfig = GenericStrategyConfig.builder()
                    .index(index)
                    .symbol(instrumentService.getTradingSymbolStart(index))
                    .startTimeString(time)
                    .startTime(startTime)
                    .moneyNess(moneyNess)
                    .strategyTag(strategyTag)
                    .quantity(quantity)
                    .stopLossPercent(stoplossPercent)
                    .stoplossPoints(stoplossPoints)
                    .premiumOperator(premiumOperator)
                    .build();

            if(premiumCutoff !=null) {
                int premiumCutoffPonts = premiumCutoff.contains("%") ? -1 : Integer.parseInt(premiumCutoff);
                int premiumCutoffPercent = premiumCutoff.contains("%") ? Integer.parseInt(premiumCutoff.replace("%", "")) : -1;
                strategyConfig.setPremiumCutoff(premiumCutoffPonts);
                strategyConfig.setPremiumStraddlePercent(premiumCutoffPercent);
            }

            if(premiumDecay != null) {
                int premiumDecayPoints = premiumDecay.contains("%") ? -1 : Integer.parseInt(premiumDecay);
                int premiumDecayPercent = premiumDecay.contains("%") ? Integer.parseInt(premiumDecay.replace("%", "")) : -1;
                strategyConfig.setPremiumDecayPoints(premiumDecayPoints);
                strategyConfig.setPremiumDecayPercent(premiumDecayPercent);
            }


            configList.add(strategyConfig);
            System.out.println(row);

        }

        return configList;
    }

    public String getSsId(String userId) {
        String ssid = System.getenv(userId+"_GCP_SS_ID");
        return ssid == null ? SS_ID:ssid;
    }
}
