package com.alphatica.alis.data.market;

import com.alphatica.alis.data.time.Time;
import com.alphatica.alis.data.time.TimeMarketData;
import com.alphatica.alis.data.time.TimeMarketDataSet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MarketData {
    List<Time> getTimes();

    Optional<Market> getMarket(MarketName marketName);

    List<Market> listMarkets();

    default MarketData fromSingle(Market market) {
        return new MarketData() {

            @Override
            public List<Time> getTimes() {
                return market.getTimes();
            }

            @Override
            public Optional<Market> getMarket(MarketName marketName) {
                if (marketName.equals(market.getName())) {
                    return Optional.of(market);
                } else {
                    return Optional.empty();
                }
            }

            @Override
            public List<Market> listMarkets() {
                return List.of(market);
            }
        };
    }

    default TimeMarketDataSet buildTimeMarketData(Time time) {
        Map<MarketName, TimeMarketData> result = new HashMap<>();
        for (Market market : listMarkets()) {
            market.getAt(time).ifPresent(timeData -> result.put(market.getName(), timeData));
        }
        return new TimeMarketDataSet(result);
    }
}
