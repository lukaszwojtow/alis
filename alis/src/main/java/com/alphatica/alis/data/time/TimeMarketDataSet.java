package com.alphatica.alis.data.time;

import com.alphatica.alis.data.market.MarketName;

import java.util.List;
import java.util.Map;

public class TimeMarketDataSet {
    private final Map<MarketName, TimeMarketData> set;

    public TimeMarketDataSet(Map<MarketName, TimeMarketData> set) {
        this.set = set;
    }

    public TimeMarketData get(MarketName name) {
        return set.get(name);
    }

    public List<MarketName> getMarkets() {
        return set.keySet().stream().toList();
    }
}
