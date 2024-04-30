package com.alphatica.alis.trading.strategy;

import com.alphatica.alis.data.market.MarketData;
import com.alphatica.alis.data.market.MarketName;
import com.alphatica.alis.data.time.Time;
import com.alphatica.alis.data.time.TimeMarketData;
import com.alphatica.alis.data.time.TimeMarketDataSet;
import com.alphatica.alis.trading.TradingException;
import com.alphatica.alis.trading.account.Account;
import com.alphatica.alis.trading.account.Position;
import com.alphatica.alis.trading.order.Order;

import java.util.ArrayList;
import java.util.List;

import static com.alphatica.alis.data.layer.Layer.OPEN;
import static com.alphatica.alis.trading.order.Direction.BUY;
import static com.alphatica.alis.trading.order.Direction.SELL;
import static java.lang.String.format;


public class StrategyExecutor {
    private static final double COMMISSION = 0.01;

    private StrategyExecutor() {
    }

    public static Account execute(double initialCash, MarketData marketData, Strategy strategy, Time timeFrom, Time timeTo) {
        List<Time> times = marketData.getTimes().stream().filter(time -> !time.isBefore(timeFrom) && !time.isAfter(timeTo)).toList();
        List<Order> pendingOrders = new ArrayList<>();
        Account account = new Account(initialCash);
        TimeMarketDataSet current = null;
        for (Time time : times) {
            current = marketData.buildTimeMarketData(time);
            executeSells(pendingOrders, current, account);
            executeBuys(pendingOrders, current, account);
            account.updateLastKnown(current);
            pendingOrders = strategy.afterClose(current, account);
        }
        if (current != null) {
            closeAccount(account, current);
        }
        strategy.finished(account);
        return account;
    }

    private static void closeAccount(Account account, TimeMarketDataSet last) {
        for (MarketName market : last.getMarkets()) {
            account.getPosition(market).ifPresent(p -> {
                double value = p.getSize() * p.getLastPrice() * (1 - COMMISSION);
                account.addTrade(market, -p.getSize(), value);
            });
        }
    }

    private static void executeBuys(List<Order> pendingOrders, TimeMarketDataSet current, Account account) {
        for (Order order : pendingOrders) {
            if (order.direction() == BUY) {
                TimeMarketData marketData = current.get(order.market());
                if (marketData != null) {
                    double price = marketData.getData(OPEN, 0) * (1 + COMMISSION);
                    double count = getCount(order, account, price);
                    double value = count * price;
                    if (value > account.getCash()) {
                        throw new TradingException(format("Not enough cash to buy (value %f cash %f)", value, account.getCash()));
                    }
                    account.addTrade(order.market(), count, -value);
                }
            }
        }
    }

    private static void executeSells(List<Order> pendingOrders, TimeMarketDataSet current, Account account) {
        for (Order order : pendingOrders) {
            if (order.direction() == SELL) {
                TimeMarketData marketData = current.get(order.market());
                if (marketData != null) {
                    double price = marketData.getData(OPEN, 0);
                    double count = getCount(order, account, price);
                    double value = count * price * (1 - COMMISSION);
                    account.addTrade(order.market(), -count, value);
                }
            }
        }
    }

    @SuppressWarnings("java:S1301")
    private static double getCount(Order order, Account account, double price) {
        switch (order.size()) {
            case PROPORTION -> {
                switch (order.direction()) {
                    case BUY -> {
                        // Multiply value by 0.9999 to avoid "Not enough cash to buy" due to double precision issues.
                        return order.sizeValue() * account.getCash() * 0.9999 / price;
                    }
                    case SELL -> {
                        return order.sizeValue() * account.getPosition(order.market()).map(Position::getSize).orElse(0.0);
                    }
                }
            }
            case COUNT -> {
                return order.sizeValue();
            }
        }
        throw new AssertionError("Not all OrderSize variants have been processed");
    }

}
