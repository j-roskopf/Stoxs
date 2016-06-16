package joe.stoxs.Object;

import io.realm.RealmObject;

/**
 * Created by Joe on 6/7/2016.
 */

public class UserOwnedStock extends RealmObject {
    String name;
    String symbol;
    String price;
    String amountOwned;

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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAmountOwned() {
        return amountOwned;
    }

    public void setAmountOwned(String amountOwned) {
        this.amountOwned = amountOwned;
    }
}
