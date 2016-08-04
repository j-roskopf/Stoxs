package joe.stoxs.Object;

/**
 * Created by Joe on 7/24/2016.
 */

public class ChartObject{

    /**
     *
     "symbol":"A",
     "timestamp":"2016-06-15T00:00:00-04:00",
     "tradingDay":"2016-06-15",
     "open":44.99145,
     "high":45.26074,
     "low":44.78201,
     "close":44.8219,
     "volume":2034446,
     "openInterest":null
     },
     */

    public String symbol;
    public String timestamp;
    public String tradingDay;
    public String open;
    public String high;
    public String low;
    public String close;
    public String volume;
    public String openInterest;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTradingDay() {
        return tradingDay;
    }

    public void setTradingDay(String tradingDay) {
        this.tradingDay = tradingDay;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getOpenInterest() {
        return openInterest;
    }

    public void setOpenInterest(String openInterest) {
        this.openInterest = openInterest;
    }
}
