package joe.stoxs.Chart;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;
import joe.stoxs.Constant.Constants;
import joe.stoxs.Fragments.BaseStockSearch;
import joe.stoxs.Fragments.Summary;
import joe.stoxs.Fragments.Watchlist;
import joe.stoxs.MainActivity;
import joe.stoxs.Object.ChartObject;
import joe.stoxs.R;

public class ChartMainActivity extends AppCompatActivity {


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private Toolbar toolbar;

    AQuery aq;

    String symbol;

    ArrayList<SingleChartInformation> toPassToFragments;

    String jsonResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_main);

        initVars();

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        jsonResults = (getIntent().getExtras().getString("jsonResults"));

        Log.d("D","finalChartDebug 4 with jsonResults = " + jsonResults.toString());

        JSONArray results = null;

        try{
            results = new JSONArray(jsonResults);

            toPassToFragments = new ArrayList<>();

            SingleChartInformation lastWeek = getLastWeekChartInfo(results);
            SingleChartInformation lastMonth = getLastMonthChartInfo(results);
            SingleChartInformation lastSemiYear = getLastSemiYearChartInfo(results);
            SingleChartInformation lastYear = getLastYearChartInfo(results);

            toPassToFragments.add(lastWeek);
            toPassToFragments.add(lastMonth);
            toPassToFragments.add(lastSemiYear);
            toPassToFragments.add(lastYear);

            Log.d("D","finalChartDebug 5 with toPassToFragments = " + toPassToFragments.size());

        }catch (Exception e){
            Log.d("D","finalChartDebug 5.5 with e = " + e.getMessage());
        }

        Log.d("D","finalChartDebug 6 " + results == null ? "Yes" : "no");

        if(results!= null){
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            // Create the adapter that will return a fragment for each of the three
            // primary sections of the activity.
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);



            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);

        }

    }

    /**
     * initializes variables
     */
    public void initVars(){
        symbol = getIntent().getExtras().getString("symbol");
        aq = new AQuery(this);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return ChartFragment.newInstance(toPassToFragments.get(position));

        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "1 Week";
                case 1:
                    return "1 Month";
                case 2:
                    return "6 Months";
                case 3:
                    return "1 Year";
            }
            return null;
        }

    }

    private SingleChartInformation getLastWeekChartInfo(JSONArray results) {
        SingleChartInformation toReturn = new SingleChartInformation();

        ArrayList<Entry> valsComp1 = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();
        String oneWeekAgo = "";

        int position = 0;

        oneWeekAgo = getDateOneWeekAGo();

        for(int i = results.length()-1; i >= 0; i--){
            ChartObject chartObject = new ChartObject();
            try{
                JSONObject current = results.getJSONObject(i);
                chartObject.setTradingDay(current.getString("tradingDay"));

                if(isDateOnOrAfter(oneWeekAgo,chartObject.getTradingDay())){
                    chartObject.setSymbol(current.getString("symbol"));
                    chartObject.setTimestamp(current.getString("timestamp"));
                    chartObject.setTradingDay(current.getString("tradingDay"));
                    chartObject.setOpen(current.getString("open"));
                    chartObject.setHigh(current.getString("high"));
                    chartObject.setLow(current.getString("low"));
                    chartObject.setClose(current.getString("close"));
                    chartObject.setVolume(current.getString("volume"));
                    chartObject.setOpenInterest(current.getString("openInterest"));

                    xVals.add(chartObject.getTradingDay());
                    Entry c1e1 = new Entry(Float.parseFloat(chartObject.getClose()), position);
                    valsComp1.add(c1e1);

                    position++;
                }else{
                    //since we pass one year of results, we only want the first X amount of days, so break when we get here
                    break;
                }



            }catch (Exception e){

            }


        }


        Collections.reverse(xVals);
        Collections.reverse(valsComp1);

        LineDataSet setComp1 = new LineDataSet(valsComp1, symbol);
        setComp1.setLineWidth(12);
        setComp1.setValueTextSize(40);
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);

        // use the interface ILineDataSet
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(setComp1);

        toReturn.setxVals(xVals);
        toReturn.setDataSets(dataSets);
        toReturn.setDescription("Last Week");

        Log.d("D","chartDebugDateDebug one month ago = " + oneWeekAgo + " " + xVals.size());

        return toReturn;
    }

    private SingleChartInformation getLastMonthChartInfo(JSONArray results) {
        SingleChartInformation toReturn = new SingleChartInformation();

        ArrayList<Entry> valsComp1 = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();
        String oneMonthAgo = "";

        int position = 0;

        oneMonthAgo = getDateOneMonthAgo();

        for(int i = results.length()-1; i >= 0; i--){
            ChartObject chartObject = new ChartObject();
            try{
                JSONObject current = results.getJSONObject(i);
                chartObject.setTradingDay(current.getString("tradingDay"));

                if(isDateOnOrAfter(oneMonthAgo,chartObject.getTradingDay())){
                    chartObject.setSymbol(current.getString("symbol"));
                    chartObject.setTimestamp(current.getString("timestamp"));
                    chartObject.setTradingDay(current.getString("tradingDay"));
                    chartObject.setOpen(current.getString("open"));
                    chartObject.setHigh(current.getString("high"));
                    chartObject.setLow(current.getString("low"));
                    chartObject.setClose(current.getString("close"));
                    chartObject.setVolume(current.getString("volume"));
                    chartObject.setOpenInterest(current.getString("openInterest"));

                    xVals.add(chartObject.getTradingDay());
                    Entry c1e1 = new Entry(Float.parseFloat(chartObject.getClose()), position);
                    valsComp1.add(c1e1);

                    position++;
                }else{
                    //since we pass one year of results, we only want the first X amount of days, so break when we get here
                    break;
                }



            }catch (Exception e){

            }


        }


        Collections.reverse(xVals);
        Collections.reverse(valsComp1);

        LineDataSet setComp1 = new LineDataSet(valsComp1, symbol);
        setComp1.setLineWidth(12);
        setComp1.setValueTextSize(40);
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);

        // use the interface ILineDataSet
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(setComp1);

        toReturn.setxVals(xVals);
        toReturn.setDataSets(dataSets);
        toReturn.setDescription("Last Month");

        Log.d("D","chartDebugDateDebug one month ago = " + oneMonthAgo + " " + xVals.size());

        return toReturn;
    }

    private SingleChartInformation getLastSemiYearChartInfo(JSONArray results) {
        SingleChartInformation toReturn = new SingleChartInformation();

        ArrayList<Entry> valsComp1 = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();

        String sixMonthsAgo = "";

        int position = 0;

        sixMonthsAgo = getDateOneSemiYearAgo();

        for(int i = results.length()-1; i >= 0; i--){
            ChartObject chartObject = new ChartObject();
            try{
                JSONObject current = results.getJSONObject(i);
                chartObject.setTradingDay(current.getString("tradingDay"));

                if(isDateOnOrAfter(sixMonthsAgo,chartObject.getTradingDay())){
                    chartObject.setSymbol(current.getString("symbol"));
                    chartObject.setTimestamp(current.getString("timestamp"));
                    chartObject.setTradingDay(current.getString("tradingDay"));
                    chartObject.setOpen(current.getString("open"));
                    chartObject.setHigh(current.getString("high"));
                    chartObject.setLow(current.getString("low"));
                    chartObject.setClose(current.getString("close"));
                    chartObject.setVolume(current.getString("volume"));
                    chartObject.setOpenInterest(current.getString("openInterest"));

                    xVals.add(chartObject.getTradingDay());
                    Entry c1e1 = new Entry(Float.parseFloat(chartObject.getClose()), position);
                    valsComp1.add(c1e1);

                    position++;
                }else{
                    //since we pass one year of results, we only want the first X amount of days, so break when we get here
                    break;
                }



            }catch (Exception e){

            }


        }


        Collections.reverse(xVals);
        Collections.reverse(valsComp1);

        LineDataSet setComp1 = new LineDataSet(valsComp1, symbol);
        setComp1.setLineWidth(12);
        setComp1.setValueTextSize(40);
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);

        // use the interface ILineDataSet
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(setComp1);

        toReturn.setxVals(xVals);
        toReturn.setDataSets(dataSets);
        toReturn.setDescription("6 Months Ago");

        Log.d("D","chartDebugDateDebug 6 Months Ago = " + sixMonthsAgo + " " + xVals.size());


        return toReturn;
    }

    private SingleChartInformation getLastYearChartInfo(JSONArray results) {
        SingleChartInformation toReturn = new SingleChartInformation();

        ArrayList<Entry> valsComp1 = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();
        String dateOneYearAgo = "";

        int position = 0;

        dateOneYearAgo = getDateOneYearAgo();


        for(int i = results.length()-1; i >= 0; i--){
            ChartObject chartObject = new ChartObject();
            try{
                JSONObject current = results.getJSONObject(i);
                chartObject.setTradingDay(current.getString("tradingDay"));

                if(isDateOnOrAfter(dateOneYearAgo,chartObject.getTradingDay())){
                    chartObject.setSymbol(current.getString("symbol"));
                    chartObject.setTimestamp(current.getString("timestamp"));
                    chartObject.setTradingDay(current.getString("tradingDay"));
                    chartObject.setOpen(current.getString("open"));
                    chartObject.setHigh(current.getString("high"));
                    chartObject.setLow(current.getString("low"));
                    chartObject.setClose(current.getString("close"));
                    chartObject.setVolume(current.getString("volume"));
                    chartObject.setOpenInterest(current.getString("openInterest"));

                    xVals.add(chartObject.getTradingDay());
                    Entry c1e1 = new Entry(Float.parseFloat(chartObject.getClose()), position);
                    valsComp1.add(c1e1);

                    position++;
                }else{
                    //since we pass one year of results, we only want the first X amount of days, so break when we get here
                    break;
                }



            }catch (Exception e){

            }


        }

        Collections.reverse(xVals);
        Collections.reverse(valsComp1);

        LineDataSet setComp1 = new LineDataSet(valsComp1, symbol);
        setComp1.setLineWidth(12);
        setComp1.setValueTextSize(40);
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);

        // use the interface ILineDataSet
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(setComp1);

        toReturn.setxVals(xVals);
        toReturn.setDataSets(dataSets);
        toReturn.setDescription("Last Year");

        Log.d("D","chartDebugDateDebug one year ago = " + dateOneYearAgo + " " + xVals.size());

        return toReturn;
    }


    /**
     * returns date one year ago from today in format yyyy-mm-dd
     * @return
     */
    private String getDateOneYearAgo() {
        String toReturn = "";

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1); // to get previous year add -1
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        toReturn = formatter.format(cal.getTime());

        Log.d("D","chartDebug with oneYearAgo = " + toReturn);

        return toReturn;
    }

    /**
     * returns date one year ago from today in format yyyy-mm-dd
     * @return
     */
    private String getDateOneWeekAGo() {
        String toReturn = "";

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -7);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        toReturn = formatter.format(cal.getTime());

        Log.d("D","chartDebug with oneWeek = " + toReturn);

        return toReturn;
    }

    /**
     * returns date one year ago from today in format yyyy-mm-dd
     * @return
     */
    private String getDateOneMonthAgo() {
        String toReturn = "";

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1); // to get previous year add -1
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        toReturn = formatter.format(cal.getTime());

        Log.d("D","chartDebug with oneMonthAgo = " + toReturn);

        return toReturn;
    }

    /**
     * returns date one year ago from today in format yyyy-mm-dd
     * @return
     */
    private String getDateOneSemiYearAgo() {
        String toReturn = "";

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -6); // to get previous year add -1
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        toReturn = formatter.format(cal.getTime());

        Log.d("D","chartDebug with oneSemiYearAGo = " + toReturn);

        return toReturn;
    }

    /**
     * checks if dateToCheck is at least on or after the time frame.
     * @param timeFrame
     * @param dateToCheck
     * @return
     */
    public static boolean isDateOnOrAfter(String timeFrame, String dateToCheck){
        boolean toReturn = false;

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date timeFrameDate;
        Date dateToCheckDate;
        try {
            timeFrameDate = dateFormatter.parse(timeFrame);
            dateToCheckDate = dateFormatter.parse(dateToCheck);

            if(timeFrameDate.compareTo(dateToCheckDate) == 0 || timeFrameDate.compareTo(dateToCheckDate) == -1){
                toReturn = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return toReturn;
    }


}
