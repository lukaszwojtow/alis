package com.alphatica.alis.examples.marketbreadth;

import com.alphatica.alis.data.loader.stooq.StooqLoader;
import com.alphatica.alis.data.market.MarketData;
import com.alphatica.alis.data.time.Time;
import com.alphatica.alis.trading.account.Account;
import com.alphatica.alis.trading.strategy.StrategyExecutor;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class MarketBreadth {
    private static final String WORK_DIR = System.getProperty("user.home") + File.separator + "Alphatica" + File.separator + "stooq_gpw";

    @SuppressWarnings("java:S106") // Suppress warning about 'System.out.println'
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        MarketData stooqData = StooqLoader.load(WORK_DIR);
        Account account = StrategyExecutor.execute(67772, stooqData, new MinMaxRatioStrategy(), new Time(2007_07_09), new Time(2025_01_01));
        System.out.println("Account: " + account.getNAV());
    }
}
