package com.alphatica.alis.trading.order;

import com.alphatica.alis.data.market.MarketName;

public record Order(MarketName market, Direction direction, OrderSize size, double sizeValue) {
}
