package com.alphatica.alis.data.loader.stooq;

import com.alphatica.alis.data.market.Market;
import com.alphatica.alis.data.market.MarketData;
import com.alphatica.alis.data.market.MarketName;
import com.alphatica.alis.data.time.Time;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;

public class StooqData implements MarketData {
    private final HashMap<MarketName, StooqMarket> markets;
    private List<Time> times;

    public StooqData() {
        markets = new HashMap<>();
        times = new ArrayList<>();
    }

    public void addMarkets(SortedMap<MarketName, StooqMarket> newMarkets) {
        markets.putAll(newMarkets);
        Set<Time> timeHashSet = new HashSet<>();
        for (StooqMarket stock : markets.values()) {
            timeHashSet.addAll(stock.getTimes());
        }
        this.times = timeHashSet.stream().sorted().toList();
    }

    @Override
    public List<Time> getTimes() {
        return times;
    }

    @Override
    public Optional<Market> getMarket(MarketName marketName) {
        return Optional.ofNullable(markets.get(marketName));
    }

    @Override
    public List<Market> listMarkets() {
        return markets.values().stream().map(Market.class::cast).sorted(Comparator.comparing(Market::getName)).toList();
    }


}
