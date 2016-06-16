package joe.stoxs;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import joe.stoxs.Constant.Constants;
import joe.stoxs.Object.Markit.CompanyDetail;

public class DetailSearchView extends AppCompatActivity {

    /**
     * non ui
     */
    AQuery aq;

    String nameToPass;
    String symbolToPass;
    String priceToPass;
    String volumeToPass;

    /**
     * UI
     */
    @BindView(R.id.container)
    ScrollView container;

    @BindView(R.id.companyName)
    TextView companyName;

    @BindView(R.id.companyPrice)
    TextView companyPrice;

    @BindView(R.id.companyTime)
    TextView companyTime;

    @BindView(R.id.companyOpen)
    TextView companyOpen;

    @BindView(R.id.companyLow)
    TextView companyLow;

    @BindView(R.id.companyHigh)
    TextView companyHigh;

    @BindView(R.id.companyOpenPercent)
    TextView companyOpenPercent;

    @BindView(R.id.companyLowPercent)
    TextView companyLowPercent;

    @BindView(R.id.companyHighPercent)
    TextView companyHighPercent;

    @BindView(R.id.yearToDateText)
    TextView yearToDateText;

    @BindView(R.id.yearToDateValue)
    TextView yearToDateValue;

    @BindView(R.id.companyMarketCap)
    TextView companyMarketCap;

    @BindView(R.id.companyMarketCapValue)
    TextView companyMarketCapValue;

    @BindView(R.id.companyVolume)
    TextView companyVolume;

    @BindView(R.id.companyVolumeValue)
    TextView companyVolumeValue;

    @BindView(R.id.progress)
    com.eyalbira.loadingdots.LoadingDots progress;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_search_view);
        ButterKnife.bind(this);

        progress.startAnimation();

        String symbol = getIntent().getExtras().getString("symbol");
        if(symbol == null || symbol.equals("")){
            finish();
        }else{
            initVars();
            getDetails(symbol);
        }

    }

    public void initVars(){
        aq = new AQuery(this);
    }

    public void getDetails(String symbol){
        Log.d("D","searchDetailDebug with symbol = " + symbol);
        aq.ajax(Constants.BASE_API_URL_SYMBOL+symbol, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d("D","searchDetailDebug with url = " + url);

                if(json != null){

                    Log.d("D","searchDetailDebug with json = " + json.toString());

                    CompanyDetail company = null;

                    try{
                        company = new CompanyDetail();
                        String APIStatus = json.getString("Status");
                        if(APIStatus.equals("SUCCESS")){
                            String name = json.getString("Name");
                            String symbol = json.getString("Symbol");
                            String lastPrice = json.getString("LastPrice");
                            String volume = json.getString("Volume");

                            nameToPass = name;
                            symbolToPass = symbol;
                            priceToPass = lastPrice;
                            volumeToPass = volume;

                            String change = json.getString("Change");
                            String changePercent = json.getString("ChangePercent");
                            String timestamp = json.getString("Timestamp");
                            String marketCap = json.getString("MarketCap");

                            String changeYTD = json.getString("ChangeYTD");
                            String changePercentYTD = json.getString("ChangePercentYTD");
                            String high = json.getString("High");
                            String low = json.getString("Low");
                            String open = json.getString("Open");
                            company.setName(name);
                            company.setSymbol(symbol);
                            company.setLastPrice(lastPrice);
                            company.setChange(change);
                            company.setChangePercent(changePercent);
                            company.setTimestamp(timestamp);
                            company.setMarketCap(marketCap);
                            company.setVolume(volume);
                            company.setChangeYTD(changeYTD);
                            company.setChangePercentYTD(changePercentYTD);
                            company.setHigh(high);
                            company.setLow(low);
                            company.setOpen(open);
                        }else{
                            Toast.makeText(getApplicationContext(),"Something went wrong :(",Toast.LENGTH_SHORT).show();
                        }


                    }catch (Exception e){
                        Log.d("D","Exception in parsing with e = " + e.getMessage());
                    }

                    try{
                        if(company != null){
                            displayData(company);
                        }else{
                            Log.d("D","Company was null");
                        }
                    }catch (Exception e){
                        Log.d("D","Exception in displaying with e = " + e.getMessage());
                    }



                }else{

                    Log.d("D","searchDetailDebug with error = " + status.getCode() + " " + status.getMessage() + " " + status.getError());

                }
            }
        });
    }

    public void displayData(CompanyDetail c){
        NumberFormat formatter = NumberFormat.getCurrencyInstance();

        companyName.setText(c.getName() + "(" + c.getSymbol() +")");

        String price = formatter.format(Double.parseDouble(c.getLastPrice()));

        companyPrice.setText(price);

        companyTime.setText(c.getTimestamp());
        companyOpen.setText("Open $"+c.getOpen());
        companyHigh.setText("High $"+c.getHigh());
        companyLow.setText("Low $"+c.getLow());

        if(Double.parseDouble(c.getLow()) >= Double.parseDouble(c.getLastPrice())){
            companyLow.setTextColor(getResources().getColor(R.color.moneyColor));
        }else{
            companyLow.setTextColor(getResources().getColor(R.color.negativeColor));
        }

        if(Double.parseDouble(c.getHigh()) >= Double.parseDouble(c.getLastPrice())){
            companyHigh.setTextColor(getResources().getColor(R.color.moneyColor));
        }else{
            companyHigh.setTextColor(getResources().getColor(R.color.negativeColor));
        }

        if(Double.parseDouble(c.getOpen()) >= Double.parseDouble(c.getLastPrice())){
            companyOpen.setTextColor(getResources().getColor(R.color.moneyColor));
        }else{
            companyOpen.setTextColor(getResources().getColor(R.color.negativeColor));
        }

        Double lowPercent = calculatePercent(c.getLastPrice(),c.getLow());
        if(lowPercent >= 1){
            companyLowPercent.setTextColor(getResources().getColor(R.color.moneyColor));
        }else{
            companyLowPercent.setTextColor(getResources().getColor(R.color.negativeColor));
        }
        Double highPercent = calculatePercent(c.getLastPrice(),c.getHigh());
        if(highPercent >= 1){
            companyHighPercent.setTextColor(getResources().getColor(R.color.moneyColor));
        }else{
            companyHighPercent.setTextColor(getResources().getColor(R.color.negativeColor));
        }
        Double openPercent = calculatePercent(c.getLastPrice(),c.getOpen());
        if(openPercent >= 1){
            companyOpenPercent.setTextColor(getResources().getColor(R.color.moneyColor));
        }else{
            companyOpenPercent.setTextColor(getResources().getColor(R.color.negativeColor));
        }

        DecimalFormat precision = new DecimalFormat("0.000");
        companyLowPercent.setText(precision.format(lowPercent) + "%");
        companyHighPercent.setText(precision.format(highPercent) + "%");
        companyOpenPercent.setText(precision.format(openPercent) + "%");
        if(Double.parseDouble(c.getChangePercentYTD()) < 0.0){
            yearToDateValue.setTextColor(getResources().getColor(R.color.negativeColor));
        }else{
            yearToDateValue.setTextColor(getResources().getColor(R.color.moneyColor));
        }
        yearToDateValue.setText(precision.format(Double.parseDouble(c.getChangePercentYTD())) + "%");

        String moneyString = formatter.format(Double.parseDouble(c.getMarketCap()));

        companyMarketCapValue.setText(moneyString);
        companyVolumeValue.setText(c.getVolume() + " shares");

        progress.setVisibility(View.GONE);
        container.setVisibility(View.VISIBLE);

    }

    public Double calculatePercent(String one, String two){
        Double first = Double.parseDouble(one);
        Double second = Double.parseDouble(two);
        return second / first;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater mi=getMenuInflater();
        mi.inflate(R.menu.menu_search_detail, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add) {
            Intent i = new Intent(this,BuyStockActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra("name",nameToPass);
            i.putExtra("symbol",symbolToPass);
            i.putExtra("price",priceToPass);
            i.putExtra("volume",volumeToPass);
            startActivity(i);

            return true;
        }

        if (id == R.id.favorite) {
            Snackbar.make(findViewById(R.id.activity_detail_search_view), "Implement favorites", Snackbar.LENGTH_LONG)
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
