package com.kite.automation.ta;

import com.zerodhatech.models.HistoricalData;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@UtilityClass
@Slf4j
public class TAUtils {

    public double getEma(List<HistoricalData> seriesData, int period)
    {
        double ema = 0;
        if(seriesData == null || seriesData.size() == 0 || period == 0)
        {
            return ema;
        }
        List<HistoricalData> subList = seriesData;
        if(seriesData.size() > period+5)
        {
            subList = seriesData.subList(seriesData.size() - period-5, seriesData.size());
        }
        double prevEma = subList.get(0).close;
        double multiplier = 2d/(period+1d);
        for (HistoricalData data:
             subList) {
            ema = data.close*multiplier + prevEma*(1-multiplier);
            prevEma = ema;
        }

        log.info("EMA value  {}", ema);

        return ema;
    }

    public double getChannelHigh(List<HistoricalData> seriesData, int period)
    {
        double channelHigh = Double.MIN_VALUE;
        if(seriesData == null || seriesData.size() == 0 || period == 0)
        {
            return channelHigh;
        }
        List<HistoricalData> subList = seriesData;
        if(seriesData.size() > period)
        {
            subList = seriesData.subList(seriesData.size() - period, seriesData.size());
        }
        for (HistoricalData data:
                subList) {
            if(data.high > channelHigh)
            {
                channelHigh = data.high;
            }
        }
        log.info("channelHigh value  {}", channelHigh);
        return channelHigh;
    }

    public double getChannelLow(List<HistoricalData> seriesData, int period)
    {
        double channelLow = Double.MAX_VALUE;
        if(seriesData == null || seriesData.size() == 0 || period == 0)
        {
            return channelLow;
        }
        List<HistoricalData> subList = seriesData;
        if(seriesData.size() > period)
        {
            subList = seriesData.subList(seriesData.size() - period, seriesData.size());
        }
        for (HistoricalData data:
                subList) {
            if(data.low < channelLow)
            {
                channelLow = data.low;
            }
        }
        log.info("channelLow value  {}", channelLow);
        return channelLow;
    }

}
