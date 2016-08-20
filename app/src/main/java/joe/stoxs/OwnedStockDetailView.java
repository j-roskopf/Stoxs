package joe.stoxs;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.codetroopers.betterpickers.numberpicker.NumberPickerBuilder;
import com.codetroopers.betterpickers.numberpicker.NumberPickerDialogFragment;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.realm.Realm;
import io.realm.RealmResults;
import joe.stoxs.Constant.Constants;
import joe.stoxs.Object.Markit.Company;
import joe.stoxs.Object.Markit.CompanyDetail;
import joe.stoxs.Object.Profile;
import joe.stoxs.Object.UserOwnedStock;

import static android.R.attr.format;
import static joe.stoxs.Constant.Constants.DEFAULT_STARTING_MONEY;
import static joe.stoxs.R.id.boughtAtPriceValue;
import static joe.stoxs.R.id.companyHigh;
import static joe.stoxs.R.id.container;
import static joe.stoxs.R.id.seekbar;
import static joe.stoxs.R.id.totalAmount;
import static joe.stoxs.R.id.totalPrice;

public class OwnedStockDetailView extends AppCompatActivity implements NumberPickerDialogFragment.NumberPickerDialogHandlerV2 {

    /**
     * final
     */

    final int REFERENCE_SELL_AMOUNT = 0;

    final int REFERENCE_SET_NUMBER = 1;

    /**
     * non ui
     */

    AQuery aq;

    Context context;

    String currentPrice = "0";

    String priceStockBoughtAt = "0";

    NumberFormat formatter;

    Realm realm;

    String companyNameValue;

    String amountCurrentlyOwned;

    int amountWillSell = 0;

    String companyName;

    double amountGained = 0.0;

    /**
     * ui
     */

    @BindView(R.id.seekbar)
    org.adw.library.widgets.discreteseekbar.DiscreteSeekBar seekbar;

    @BindView(R.id.boughtAtPriceValue)
    TextView boughtAtPriceValue;

    @BindView(R.id.amountEarnedValue)
    TextView amountEarnedValue;

    @BindView(R.id.companyPriceBuy)
    TextView companyPrice;

    @BindView(R.id.container)
    ScrollView container;

    @BindView(R.id.progress)
    com.eyalbira.loadingdots.LoadingDots progress;

    @BindView(R.id.totalPrice)
    TextView totalPriceValue;

    @BindView(R.id.totalPriceText)
    TextView totalPriceText;

    @BindView(R.id.numberButton)
    Button specifyNumber;

    @BindView(R.id.sellButton)
    Button sellButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owned_stock_detail_view);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        progress.setVisibility(View.VISIBLE);
        container.setVisibility(View.GONE);

        initVars();

        String symbol = getIntent().getExtras().getString("symbol");
        String amountOwned  = getIntent().getExtras().getString("amountOwned");
        String priceBoughtAt = getIntent().getExtras().getString("amountPurchaseAt");
        getSupportActionBar().setTitle(amountOwned + " stocks owned of " + symbol);


        priceStockBoughtAt = priceBoughtAt;
        amountCurrentlyOwned = amountOwned;

        displayInfo(amountOwned,priceBoughtAt);

        getCurrentInfo(symbol,amountOwned,priceBoughtAt);

        formatStrings(priceBoughtAt);

        setupNumberButton(amountOwned);

        setupSellButton(amountOwned);

    }

    public void setupNumberButton(final String amountOwned){
        specifyNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberPickerBuilder npb = new NumberPickerBuilder()
                        .setReference(REFERENCE_SET_NUMBER)
                        .setFragmentManager(getSupportFragmentManager())
                        .setMaxNumber(BigDecimal.valueOf(Double.parseDouble(amountOwned)))
                        .setPlusMinusVisibility(View.INVISIBLE)
                        .setDecimalVisibility(View.INVISIBLE)
                        .setStyleResId(R.style.BetterPickersDialogFragment_Light)
                        .setLabelText("Stocks");
                npb.show();
            }
        });
    }

    public void setupSellButton(final String amountOwned){
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(amountWillSell == 0){
                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Please select some stocks to sell")
                            .setConfirmText("Okay!")
                            .show();
                }else{
                    final double totalPriceOfStock = amountWillSell * Double.parseDouble(currentPrice);
                    NumberFormat formatter = NumberFormat.getCurrencyInstance();
                    String formattedPrice = formatter.format(totalPriceOfStock);
                    new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                            .setTitleText("Confirm purchase")
                            .setContentText("You are selling " + amountWillSell + " stocks of " + companyName + " for " + formattedPrice)
                            .setConfirmText("Sell!")
                            .setCancelText("Cancel!")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {

                                    final boolean soldAll = sell(amountWillSell);
                                    String message;
                                    if(soldAll){
                                        message = "You have sold all your stocks for " + companyName;
                                    }else{
                                        message = "You have sold " + amountWillSell + " of your stocks for " + companyName;
                                    }

                                    if(amountGained != 0.0){
                                        Log.d("D","sellDebug adding " + amountGained + " to lifetime total");
                                        addToLifeTimeTotal(amountGained);
                                    }

                                    sDialog
                                            .setTitleText("Success!")
                                            .setContentText(message)
                                            .setConfirmText("OK")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    if(soldAll){
                                                        //remove entry in DB
                                                        removeEntry();
                                                    }else{

                                                    }
                                                    addMoneyToUser(totalPriceOfStock);
                                                    finish();
                                                }
                                            })
                                            .showCancelButton(false)
                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                }
                            })
                            .show();
                }


            }
        });
    }

    /**
     *
     * Adds amount to the user's money.
     *
     *
     * @param amountToAdd - amount you're selling the stocks for. To be added to the profile class in the DB
     */
    public void addMoneyToUser(double amountToAdd){
            Profile profile = realm.where(Profile.class).findFirst();
            realm.beginTransaction();

            Log.d("D","sellMoneyDebug current amount of money = " + profile.getMoney());
            profile.setMoney(profile.getMoney() + amountToAdd);
            Log.d("D","sellMoneyDebug current amount of money after selling = " + profile.getMoney());

            realm.commitTransaction();

    }

    public boolean sell(int amount){
        boolean soldAll = false;
        UserOwnedStock toEdit = realm.where(UserOwnedStock.class)
                .equalTo("name", companyNameValue).findFirst();
        realm.beginTransaction();
        Log.d("D","sellDebug with amount in db = " + toEdit.getAmountOwned());
        int amountDifferent = Integer.parseInt(toEdit.getAmountOwned()) - (amount);

        if(amountDifferent == 0){
            soldAll = true;
        }

        Log.d("D","sellDebug putting this many in the DB = " + amountDifferent);
        toEdit.setAmountOwned(amountDifferent+"");
        realm.commitTransaction();

        return soldAll;
    }

    public void removeEntry(){
        UserOwnedStock toEdit = realm.where(UserOwnedStock.class)
                .equalTo("name", companyNameValue).findFirst();
        realm.beginTransaction();

        toEdit.deleteFromRealm();

        realm.commitTransaction();

    }

    public void formatStrings(String priceBoughtAt) {

        Double priceToUse = Double.parseDouble(priceBoughtAt);
        NumberFormat formatter = NumberFormat.getCurrencyInstance();

        //bought at price
        String price = formatter.format(priceToUse);
        boughtAtPriceValue.setText(price);
    }


    public void initVars(){
        aq = new AQuery(this);
        context = this;
        formatter = NumberFormat.getCurrencyInstance();
        realm = Realm.getDefaultInstance();

    }

    public void getCurrentInfo(String symbol, final String amountOwned, final String amountPurchasedAt){
        Log.d("D","ownedStockDetailView with symbol = " + symbol);
        aq.ajax(Constants.BASE_API_URL_SYMBOL+symbol, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                Log.d("D","ownedStockDetailView with url = " + url);

                if(json != null){

                    Log.d("D","searchDetailDebug with json = " + json.toString());

                    CompanyDetail company = null;

                    try{
                        company = new CompanyDetail();
                        String APIStatus = json.getString("Status");
                        if(APIStatus.equals("SUCCESS")){
                            String name = json.getString("Name");
                            String symbol = json.getString("Symbol");
                            companyName = name + "(" + symbol +")";
                            companyNameValue = name;
                            String lastPrice = json.getString("LastPrice");
                            companyPrice.setText("$" + lastPrice);
                            currentPrice = lastPrice;
                            setupSeekBar(amountOwned,lastPrice,amountPurchasedAt);

                            String volume = json.getString("Volume");
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

                            progress.setVisibility(View.GONE);
                            container.setVisibility(View.VISIBLE);

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
                            Log.d("D","ownedStockDetailView Company was null");
                        }
                    }catch (Exception e){
                        Log.d("D","ownedStockDetailView Exception in displaying with e = " + e.getMessage());
                    }

                }else{

                    Log.d("D","searchDetailDebug with error = " + status.getCode() + " " + status.getMessage() + " " + status.getError());

                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void displayData(CompanyDetail c){

    }

    public void displayInfo(String amountOwned, String priceBoughtAt){
        boughtAtPriceValue.setText(priceBoughtAt);
    }

    public void setupSeekBar(String volume, final String price, final String amountPurchasedAt){
        seekbar.setMax(Integer.parseInt(volume));
        seekbar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            Double priceToUse = Double.parseDouble(price);
            Double priceBoughtAt = Double.parseDouble(amountPurchasedAt);
            NumberFormat formatter = NumberFormat.getCurrencyInstance();
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {

                amountWillSell = value;

                double amountToSell = value * priceToUse;

                double amountSpentWhenPurchasing = value * priceBoughtAt;

                //calculate if we're gaining or losing money
                double total = amountToSell - amountSpentWhenPurchasing;

                //store the amount the user is gaining / losing so we can add it to their lifetime total
                //make sure to round to 2 places
                amountGained = ((double) Math.round(total * 100) / 100);

                String totalFormatted = formatter.format((total));

                if(total < 0){
                    //selling at a loss
                    totalPriceText.setText("Selling at a loss: ");
                    totalPriceValue.setTextColor(getResources().getColor(R.color.negativeColor));
                }else{
                    //selling at a gain
                    totalPriceText.setText("Selling at a profit: ");
                    totalPriceValue.setTextColor(getResources().getColor(R.color.moneyColor));
                }

                totalPriceValue.setText(totalFormatted);

                String price = formatter.format((value * priceToUse));
                amountEarnedValue.setText(price);

            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });
    }

    /**
     * Added the total to the lifetime amount the user has gained / loss
     * @param total
     */
    private void addToLifeTimeTotal(double total) {
        RealmResults<Profile> profiles = realm.where(Profile.class).findAll();
        if (profiles.size() > 0) {
            Profile profile = realm.where(Profile.class).findFirst();
            realm.beginTransaction();
            profile.setLifetimeEarned(profile.getLifetimeEarned() + total );
            realm.commitTransaction();
        }else{
            //first time loading I guess, create a profile with the default amount of money
            createProfile();

            Profile profile = realm.where(Profile.class).findFirst();
            realm.beginTransaction();
            profile.setLifetimeEarned(profile.getLifetimeEarned() + total );
            realm.commitTransaction();
        }
    }

    /**
     * creates a profile with the default amount of money if no profile is detected
     */
    public void createProfile(){
        realm.beginTransaction();

        Profile profile = realm.createObject(Profile.class); // Create a new object
        profile.setMoney(DEFAULT_STARTING_MONEY);
        profile.setLastUpdated(Calendar.getInstance().getTimeInMillis());

        realm.commitTransaction();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void onDialogNumberSet(int reference, BigInteger number, double decimal, boolean isNegative, BigDecimal fullNumber) {

        double boughtAt = Double.parseDouble(priceStockBoughtAt);
        double current = Double.parseDouble(currentPrice);
        final int amount = number.intValue();
        double amountToSell = amount * current;
        double amountSpentWhenPurchasing = amount * boughtAt;
        double total = amountToSell - amountSpentWhenPurchasing;

        if(reference == REFERENCE_SET_NUMBER){

            seekbar.setProgress(amount);

            amountWillSell = amount;

            String totalFormatted = formatter.format((total));

            if(total < 0){
                //selling at a loss
                totalPriceText.setText("Selling at a loss: ");
                totalPriceValue.setTextColor(getResources().getColor(R.color.negativeColor));
            }else{
                //selling at a gain
                totalPriceText.setText("Selling at a profit: ");
                totalPriceValue.setTextColor(getResources().getColor(R.color.moneyColor));
            }
            totalPriceValue.setText(totalFormatted);

            String price = formatter.format((amount * current));
            amountEarnedValue.setText(price);
        }else if (reference == REFERENCE_SELL_AMOUNT){
            //when the sell amount used to open the number picker dialog
        }

    }

}
