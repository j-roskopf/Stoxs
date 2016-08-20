package joe.stoxs.Chart;

/**
 * Created by Joe on 7/26/2016.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmResults;
import joe.stoxs.Object.Profile;
import joe.stoxs.R;

import static joe.stoxs.Constant.Constants.DEFAULT_STARTING_MONEY;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;

import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import joe.stoxs.Constant.Constants;
import joe.stoxs.Object.Profile;
import joe.stoxs.Object.UserOwnedStock;
import joe.stoxs.R;

import static joe.stoxs.Constant.Constants.DEFAULT_STARTING_MONEY;
import static joe.stoxs.R.id.money;

/**
 * Created by Joe on 6/15/2016.
 */

public class ChartFragment extends Fragment {

    /**
     * non ui
     */
    private static String ARG_SECTION_NUMBER = "arg_section_number";

    private Realm realm;

    SingleChartInformation chartToDisplay;


    /**
     * ui
     */


    public static ChartFragment newInstance(SingleChartInformation chartToDisplay) {
        ChartFragment fragment = new ChartFragment();
        Bundle args = new Bundle();
        args.putParcelable("chart",chartToDisplay);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chart_fragment, container, false);
        initVars(view);

        chartToDisplay = (SingleChartInformation)getArguments().getParcelable("chart");

        if(chartToDisplay == null){
            Log.d("D","chartFragmentDebug yeah its null");
        }else{
            Log.d("D","chartFragmentDebug no its not null " + chartToDisplay.dataSets.size() + " " + chartToDisplay.xVals.size());

            //display chart info
            LineChart chart = (LineChart) view.findViewById(R.id.chart);
            LineData data = new LineData(chartToDisplay.xVals, chartToDisplay.dataSets);
            chart.setData(data);
            chart.setDescription("");
            chart.invalidate(); // refresh
        }






        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        realm.close();
    }


    public void initVars(View view){
        realm = Realm.getDefaultInstance();

    }


    @Override
    public void onStart() {
        super.onStart();
    }
}