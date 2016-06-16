package joe.stoxs.Object;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Joe on 5/31/2016.
 */

public class Profile extends RealmObject {

    private double money;

    private long lastUpdated;

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
