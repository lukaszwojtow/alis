package com.alphatica.alis.examples.minmax;

import com.alphatica.alis.data.loader.stooq.StooqLoader;
import com.alphatica.alis.data.market.Market;
import com.alphatica.alis.data.market.MarketData;
import com.alphatica.alis.data.market.MarketType;
import com.alphatica.alis.data.time.Time;
import com.alphatica.alis.indicators.trend.MinMax;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class MinMaxNow {
    private static final String WORK_DIR = System.getProperty("user.home") + File.separator + "Alphatica" + File.separator + "stooq_gpw";

    @SuppressWarnings("java:S106") // Suppress warning about 'System.out.println'
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        MarketData stooqData = StooqLoader.load(WORK_DIR);
        Time last = stooqData.getTimes().getLast();
        System.out.println("Last time: " + last);
        MinMax minMax = new MinMax(170);
        final int[] stats = {0, 0};
        for (Market market : stooqData.listMarkets()) {
            if (market.getType() == MarketType.STOCK) {
                market.getAt(last).flatMap(minMax::calculate).ifPresent(minMaxNow -> {
                    if (minMaxNow > 0.0) {
                        stats[0]++;
                    } else {
                        stats[1]++;
                    }
                    System.out.printf("Market %s MinMax: %.2f%n", market.getName(), minMaxNow);
                });
            }
        }
        System.out.println("Up: " + stats[0] + " Down: " + stats[1] + " Proportion: " + (double) stats[0] / (stats[0] + stats[1]));
    }
}
