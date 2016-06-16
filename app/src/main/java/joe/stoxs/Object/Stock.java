package joe.stoxs.Object;

import io.realm.RealmObject;

/**
 * Created by Joe on 5/31/2016.
 */

public class Stock extends RealmObject {

    String name;

    String symbol;

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
}
