package joe.stoxs.Chart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;
import joe.stoxs.Constant.Constants;
import joe.stoxs.Object.ChartObject;
import joe.stoxs.Object.Markit.Company;
import joe.stoxs.R;

public class ChartActivity extends AppCompatActivity {

    /**
     * non ui
     */
    AQuery aq;

    String symbol;

    /**
     * ui
     */

    RecyclerView chartList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        symbol = getIntent().getExtras().getString("symbol");

        initVars();

        Log.d("D","chartDebug with symbol = " + symbol);

        getInfo(symbol);

    }

    /**
     * initializes variables
     */
    public void initVars(){
        chartList = (RecyclerView) findViewById(R.id.chartList);

        aq = new AQuery(this);
    }

    /**
     * displays generic error message
     */
    public void displayGenericErrorMessage(){
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Error")
                .setContentText("It looks like something went wrong")
                .setConfirmText("Okay")
                .show();
    }

    /**
     * returns date one year ago from today in format yyyymmdd
     * @return
     */
    private String getDateOneYearAgoForApi() {
        String toReturn = "";

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1); // to get previous year add -1
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        toReturn = formatter.format(cal.getTime());

        Log.d("D","chartDebug with oneYearAgo = " + toReturn);

        return toReturn;
    }


    /**
     * Makes network call to get info on symbol
     * @param symbol
     */
    public void getInfo(String symbol){
        String urlToUse = Constants.BASE_CHART_URL;

        urlToUse = urlToUse.replace("insert_key_here",Constants.MARKET_DATA_API_KEY);
        urlToUse = urlToUse.replace("insert_symbol_here",symbol);
        final String dateOneYearAgo = getDateOneYearAgoForApi();
        urlToUse = urlToUse.replace("insert_date_here",dateOneYearAgo);


        aq.ajax(urlToUse, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d("D","chartDebug with url = " + url);

                if(json != null){
                    try{
                        Log.d("D","chartDebug with json = " + json);
                        if(json.getJSONObject("status").getString("code").equals("200")){
                            JSONArray results = json.getJSONArray("results");

                            displayResults(results,dateOneYearAgo);
                            Log.d("D","chartDebug with results.size = " + results.length());
                        }else{
                            displayGenericErrorMessage();
                        }
                    }catch (Exception e){
                        displayGenericErrorMessage();
                    }


                }else{
                    displayGenericErrorMessage();
                    Log.d("D","charDebug with error = " + status.getCode() + " " + status.getMessage() + " " + status.getError());
                }
            }
        });
    }


    /**
     * displays results in a chart
     * @param results
     */
    public void displayResults(JSONArray results,String oneYearAgo){

        Intent i = new Intent(ChartActivity.this,ChartMainActivity.class);
        Log.d("D","putting jsonResults = " + results.toString());
        Log.d("D","chartDebug 3 with jsonResults = " + results.toString());
        i.putExtra("jsonResults",results.toString());
        startActivity(i);
        finish();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
