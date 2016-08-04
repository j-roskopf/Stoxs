package joe.stoxs.Chart;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Joe on 7/24/2016.
 */

public class SingleChartInformation implements Parcelable {

    ArrayList<ILineDataSet> dataSets;
    ArrayList<String> xVals;
    String description;

    public SingleChartInformation(){
        xVals = new ArrayList<>();
        dataSets = new ArrayList<ILineDataSet>();
    }

    public ArrayList<ILineDataSet> getDataSets() {
        return dataSets;
    }

    public void setDataSets(ArrayList<ILineDataSet> dataSets) {
        this.dataSets = dataSets;
    }

    public ArrayList<String> getxVals() {
        return xVals;
    }

    public void setxVals(ArrayList<String> xVals) {
        this.xVals = xVals;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    protected SingleChartInformation(Parcel in) {
        if (in.readByte() == 0x01) {
            dataSets = new ArrayList<ILineDataSet>();
            in.readList(dataSets, ILineDataSet.class.getClassLoader());
        } else {
            dataSets = null;
        }
        if (in.readByte() == 0x01) {
            xVals = new ArrayList<String>();
            in.readList(xVals, String.class.getClassLoader());
        } else {
            xVals = null;
        }
        description = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (dataSets == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(dataSets);
        }
        if (xVals == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(xVals);
        }
        dest.writeString(description);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<SingleChartInformation> CREATOR = new Parcelable.Creator<SingleChartInformation>() {
        @Override
        public SingleChartInformation createFromParcel(Parcel in) {
            return new SingleChartInformation(in);
        }

        @Override
        public SingleChartInformation[] newArray(int size) {
            return new SingleChartInformation[size];
        }
    };
}