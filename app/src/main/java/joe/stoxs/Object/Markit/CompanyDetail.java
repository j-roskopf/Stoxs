package joe.stoxs.Object.Markit;

import io.realm.RealmObject;

/**
 * Created by Joe on 6/4/2016.
 */

public class CompanyDetail extends RealmObject {
    String name;
    String symbol;
    String lastPrice;
    String change;
    String changePercent;
    String timestamp;
    String marketCap;
    String volume;
    String changeYTD;
    String changePercentYTD;
    String high;
    String low;
    String open;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(String lastPrice) {
        this.lastPrice = lastPrice;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getChangePercent() {
        return changePercent;
    }

    public void setChangePercent(String changePercent) {
        this.changePercent = changePercent;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(String marketCap) {
        this.marketCap = marketCap;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getChangeYTD() {
        return changeYTD;
    }

    public void setChangeYTD(String changeYTD) {
        this.changeYTD = changeYTD;
    }

    public String getChangePercentYTD() {
        return changePercentYTD;
    }

    public void setChangePercentYTD(String changePercentYTD) {
        this.changePercentYTD = changePercentYTD;
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

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }
}
