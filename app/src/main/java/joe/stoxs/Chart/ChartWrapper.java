package joe.stoxs.Chart;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

import static android.R.id.list;
import static joe.stoxs.R.id.chart;

/**
 * Created by Joe on 7/26/2016.
 */

public class ChartWrapper implements Parcelable {

    ArrayList<SingleChartInformation> list;

    public ChartWrapper(){
        list = new ArrayList<>();
    }

    public ArrayList<SingleChartInformation> getList() {
        return list;
    }

    public void setList(ArrayList<SingleChartInformation> list) {
        this.list = list;
    }

    protected ChartWrapper(Parcel in) {
        if (in.readByte() == 0x01) {
            list = new ArrayList<SingleChartInformation>();
            in.readList(list, SingleChartInformation.class.getClassLoader());
        } else {
            list = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (list == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(list);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ChartWrapper> CREATOR = new Parcelable.Creator<ChartWrapper>() {
        @Override
        public ChartWrapper createFromParcel(Parcel in) {
            return new ChartWrapper(in);
        }

        @Override
        public ChartWrapper[] newArray(int size) {
            return new ChartWrapper[size];
        }
    };
}
