package com.alphatica.alis.data.time;

import com.alphatica.alis.data.layer.Layer;
import com.alphatica.alis.data.market.MarketName;
import com.alphatica.alis.data.market.MarketType;
import com.alphatica.alis.tools.data.DoubleArrayRange;

import java.util.List;

public class TimeMarketData {

    private final MarketName marketName;
    private final MarketType marketType;
    private final Time time;
    private final List<DoubleArrayRange> data;

    public TimeMarketData(MarketName marketName, MarketType marketType, Time time, List<DoubleArrayRange> data) {
        this.marketName = marketName;
        this.marketType = marketType;
        this.time = time;
        this.data = data;
    }

    public MarketName getMarketName() {
        return marketName;
    }

    public MarketType getMarketType() {
        return marketType;
    }

    public Time getTime() {
        return time;
    }

    public double getData(Layer layer, int index) {
        return data.get(layer.getIndex()).get(index);
    }

    public DoubleArrayRange getLayer(Layer layer) {
        return data.get(layer.getIndex());
    }
}
