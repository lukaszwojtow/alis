package com.alphatica.alis.trading.strategy;

import com.alphatica.alis.data.market.MarketName;
import com.alphatica.alis.data.time.TimeMarketData;
import com.alphatica.alis.data.time.TimeMarketDataSet;
import com.alphatica.alis.indicators.trend.MinMax;
import com.alphatica.alis.trading.account.Account;
import com.alphatica.alis.trading.order.Order;

import java.util.ArrayList;
import java.util.List;

import static com.alphatica.alis.trading.order.Direction.BUY;
import static com.alphatica.alis.trading.order.Direction.SELL;
import static com.alphatica.alis.trading.order.OrderSize.PROPORTION;

public class MinMaxStrategy implements Strategy {
    private final MinMax minMax;

    public MinMaxStrategy(int length) {
        this.minMax = new MinMax(length);
    }

    @Override
    public List<Order> afterClose(TimeMarketDataSet data, Account account) {
        List<Order> orders = new ArrayList<>();
        for (MarketName marketName : data.getMarkets()) {
            TimeMarketData marketData = data.get(marketName);
            minMax.calculate(marketData).ifPresent(ind -> {
                boolean positionOpened = account.getPosition(marketName).isPresent();
                if (!positionOpened && ind > 0) {
                    orders.add(new Order(marketName, BUY, PROPORTION, 1.0));
                }
                if (positionOpened && ind < 0) {
                    orders.add(new Order(marketName, SELL, PROPORTION, 1.0));
                }
            });
        }
        return orders;
    }
}
