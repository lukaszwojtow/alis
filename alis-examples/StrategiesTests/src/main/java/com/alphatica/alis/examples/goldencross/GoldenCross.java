package com.alphatica.alis.examples.goldencross;

import com.alphatica.alis.data.loader.stooq.StooqLoader;
import com.alphatica.alis.data.market.Market;
import com.alphatica.alis.data.market.MarketData;
import com.alphatica.alis.data.market.MarketType;
import com.alphatica.alis.data.time.Time;
import com.alphatica.alis.trading.MarketScore;
import com.alphatica.alis.trading.account.Account;
import com.alphatica.alis.trading.strategy.SmaCrossStrategy;
import com.alphatica.alis.trading.strategy.StrategyExecutor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.concurrent.ExecutionException;

public class GoldenCross {
    private static final String WORK_DIR = System.getProperty("user.home") + File.separator + "Alphatica" + File.separator + "stooq_gpw";

    @SuppressWarnings("java:S106") // Suppress warning about 'System.out.println'
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        MarketData stooqData = StooqLoader.load(WORK_DIR);
        List<Market> markets = stooqData.listMarkets();
        List<MarketScore> scores = new ArrayList<>();
        for (Market market : markets) {
            if (market.getType() == MarketType.STOCK) {
                MarketData m = stooqData.fromSingle(market);
                Account account = StrategyExecutor.execute(100_000, m, new SmaCrossStrategy(), new Time(2007_07_09), new Time(2025_01_01));
                scores.add(new MarketScore(market.getName(), account.getNAV()));
            }
        }
        OptionalDouble average = scores.stream().mapToDouble(MarketScore::value).average();
        if (average.isPresent()) {
            System.out.printf("Average value %f%n", average.getAsDouble());
        } else {
            System.out.println("No scores found");
        }
        List<MarketScore> sorted = scores.stream().sorted().toList();
        for (MarketScore marketScore : sorted) {
            System.out.println("market: " + marketScore.market() + " value: " + marketScore.value());
        }
    }
}

