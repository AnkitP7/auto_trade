package com.kite.automation.strategies.v1;

import com.kite.automation.strategies.StrategyTag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenericStrategyConfig {
    private StrategyTag strategyTag;
    private Index index;
    private String symbol;
    private String startTimeString;
    private Date startTime;
    private int quantity;
    private int moneyNess;
    private int stoplossPoints;
    private int stopLossPercent;
    private String premiumOperator;
    private int premiumCutoff;
    private int premiumStraddlePercent;
    private int premiumUnderLyingPercent;
    private int premiumDecayPoints;
    private int premiumDecayPercent;
}
