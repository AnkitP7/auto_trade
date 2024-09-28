package com.kite.automation.strategies.v1;

import lombok.Getter;

@Getter
public enum Index {

    NIFTY("NIFTY 50", "NSE", "NFO",50),
    BANKNIFTY("NIFTY BANK", "NSE", "NFO",100),
    FINNIFTY("NIFTY FIN SERVICE", "NSE", "NFO",50),
    SENSEX("SENSEX", "BSE","BFO",100),
    MIDCPNIFTY("NIFTY MID SELECT", "NSE", "NFO", 25);

    private String symbol;
    private String exchange;
    private String fnoExchange;
    private int strikePriceMultiple;

    private Index(String symbol, String exchange, String fnoExchange, int strikePriceMultiple)
    {
        this.symbol = symbol;
        this.exchange = exchange;
        this.fnoExchange = fnoExchange;
        this.strikePriceMultiple = strikePriceMultiple;
    }

    public String getExchangeAndSymbol()
    {
        return exchange+":"+symbol;
    }

}
