package joe.stoxs;

import android.content.Context;
import android.support.annotation.IntegerRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.codetroopers.betterpickers.numberpicker.NumberPickerBuilder;
import com.codetroopers.betterpickers.numberpicker.NumberPickerDialogFragment;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.realm.Realm;
import io.realm.RealmResults;
import joe.stoxs.Constant.Constants;
import joe.stoxs.Constant.SharedVariables;
import joe.stoxs.Object.Profile;
import joe.stoxs.Object.UserOwnedStock;

import static joe.stoxs.Constant.Constants.DEFAULT_STARTING_MONEY;
import static joe.stoxs.R.id.companyName;
import static joe.stoxs.R.id.money;
import static joe.stoxs.R.id.stockName;

public class BuyStockActivity extends AppCompatActivity implements NumberPickerDialogFragment
        .NumberPickerDialogHandlerV2 {

    /**
     * UI
     */
    @BindView(R.id.totalPrice)
    TextView totalPrice;

    @BindView(R.id.totalAmount)
    TextView totalAmount;

    @BindView(R.id.seekbar)
    org.adw.library.widgets.discreteseekbar.DiscreteSeekBar seekbar;

    @BindView(R.id.numberButton)
    Button specifyNumber;

    /**
     * non ui
     */

    String priceOfStock;
    String totalPriceOfStock;
    String valueOfTotalPriceOfStock;
    String totalAmountOfStock;
    NumberFormat formatter = NumberFormat.getCurrencyInstance();
    Realm realm;
    Context context;
    String companyNameValue;
    String symbolValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_stock);
        ButterKnife.bind(this);

        initVars();

        companyNameValue = getIntent().getExtras().getString("name");
        symbolValue = getIntent().getExtras().getString("symbol");
        priceOfStock = getIntent().getExtras().getString("price");
        String volume = getIntent().getExtras().getString("volume");

        getSupportActionBar().setTitle("Buy: " + symbolValue + " at " + "$" + priceOfStock + "/stock");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupSeekBar(volume, priceOfStock);

        setupNumberButton(volume);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    public void initVars() {
        priceOfStock = "0.0";
        formatter = NumberFormat.getCurrencyInstance();
        context = this;
        totalPriceOfStock = "";
        totalAmountOfStock = "";
        valueOfTotalPriceOfStock = "";
        realm = Realm.getDefaultInstance();
    }

    public boolean hasEnoughtMoney(String amountToPurchaseFor) {
        boolean toReturn;
        RealmResults<Profile> profiles = realm.where(Profile.class).findAll();

        if (profiles.size() > 0) {
            Profile profile = realm.where(Profile.class).findFirst();
            toReturn = checkIfUserHasEnough(amountToPurchaseFor, profile.getMoney());

        } else {
            toReturn = createProfile(amountToPurchaseFor);
        }

        return toReturn;
    }

    public boolean makePurchase(String amountToPurchaseFor) {
        boolean toReturn;
        RealmResults<Profile> profiles = realm.where(Profile.class).findAll();

        if (profiles.size() > 0) {
            Profile profile = realm.where(Profile.class).findFirst();
            toReturn = checkIfUserHasEnough(amountToPurchaseFor, profile.getMoney());
            if (toReturn) {
                double amountLeft = profile.getMoney() - Double.parseDouble(amountToPurchaseFor);
                updateUserMoney(amountLeft);
            }
        } else {
            toReturn = createProfile(amountToPurchaseFor);
        }

        return toReturn;
    }

    /**
     * creates a profile with the default amount of money if no profile is detected
     */
    public boolean createProfileAndMakePurchase(String amountToPurchaseFor) {
        realm.beginTransaction();

        Profile profile = realm.createObject(Profile.class); // Create a new object
        profile.setMoney(Constants.DEFAULT_STARTING_MONEY);
        profile.setLastUpdated(Calendar.getInstance().getTimeInMillis());


        realm.commitTransaction();

        boolean hasEnough = checkIfUserHasEnough(amountToPurchaseFor, DEFAULT_STARTING_MONEY);
        if (hasEnough) {
            double amountLeft = DEFAULT_STARTING_MONEY - Double.parseDouble(amountToPurchaseFor);
            updateUserMoney(amountLeft);
            return true;
        } else {
            return false;
        }

    }

    /**
     * creates a profile with the default amount of money if no profile is detected
     */
    public boolean createProfile(String amountToPurchaseFor) {
        realm.beginTransaction();

        Profile profile = realm.createObject(Profile.class); // Create a new object
        profile.setMoney(Constants.DEFAULT_STARTING_MONEY);
        profile.setLastUpdated(Calendar.getInstance().getTimeInMillis());


        realm.commitTransaction();

        boolean hasEnough = checkIfUserHasEnough(amountToPurchaseFor, DEFAULT_STARTING_MONEY);
        if (hasEnough) {
            return true;
        } else {
            return false;
        }

    }

    public void updateUserMoney(double amountLeft) {
        Profile profile = realm.where(Profile.class).findFirst();
        realm.beginTransaction();

        if (profile.getMoney() >= amountLeft) {
            profile.setMoney(amountLeft);
        } else {
            new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Something went wrong")
                    .setConfirmText("The developer sucks")
                    .show();
        }

        realm.commitTransaction();
    }

    public boolean checkIfUserHasEnough(String amountToPurchase, double amountUserHas) {
        if (Double.parseDouble(amountToPurchase) <= amountUserHas) {
            return true;
        } else {
            return false;
        }
    }

    private int calculateAmountUserCanBuy(double price, int volume){
        Log.d("D","calculateAmountUserCanBuyDebug");
        RealmResults<Profile> profiles = realm.where(Profile.class).findAll();
        Log.d("D","calculateAmountUserCanBuyDebug with profiles.size = " + profiles.size());
        int amount = 0;
        if (profiles.size() > 0) {
            Profile profile = realm.where(Profile.class).findFirst();
            amount = (int) (profile.getMoney()/price);
            if(amount > volume)
                amount = volume;
        }else{
            //first time loading I guess, create a profile with the default amount of money
            createProfile();
            amount = (int) (DEFAULT_STARTING_MONEY/price);
        }

        return amount;
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

    public void setupSeekBar(String volume, final String price) {
        seekbar.setMax(calculateAmountUserCanBuy(Double.parseDouble(price), Integer.parseInt(volume)));
        seekbar.setIndicatorPopupEnabled(false);
        seekbar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            Double priceToUse = Double.parseDouble(price);
            NumberFormat formatter = NumberFormat.getCurrencyInstance();

            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                String price = formatter.format((value * priceToUse));
                totalAmount.setText(value + "");
                valueOfTotalPriceOfStock = (value * priceToUse) + "";
                totalPrice.setText(price);


                totalAmountOfStock = value + "";
                totalPriceOfStock = price;
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });
    }


    public void setupNumberButton(final String volume) {
        specifyNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberPickerBuilder npb = new NumberPickerBuilder()
                        .setFragmentManager(getSupportFragmentManager())
                        .setMaxNumber(BigDecimal.valueOf(Double.parseDouble(volume)))
                        .setPlusMinusVisibility(View.INVISIBLE)
                        .setDecimalVisibility(View.INVISIBLE)
                        .setStyleResId(R.style.BetterPickersDialogFragment_Light)
                        .setLabelText("Stocks");
                npb.show();
            }
        });
    }

    @Override
    public void onDialogNumberSet(int reference, BigInteger number, double decimal, boolean
            isNegative, BigDecimal fullNumber) {

        totalAmount.setText(number.toString());
        String price = formatter.format(number.doubleValue() * Double.parseDouble(priceOfStock));
        valueOfTotalPriceOfStock = (number.doubleValue() * Double.parseDouble(priceOfStock)) + "";
        totalPrice.setText(price);
        seekbar.setProgress(number.intValue());

        totalAmountOfStock = number.toString();
        totalPriceOfStock = price;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.buy_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == android.R.id.home){
            finish();
        }else if (id == R.id.buy) {
            if (!totalPriceOfStock.equals("") && !totalAmountOfStock.equals("")) {
                if (hasEnoughtMoney(valueOfTotalPriceOfStock)) {
                    new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                            .setTitleText("Confirm purchase")
                            .setContentText("You are purchasing " + totalAmountOfStock + " stocks of " + companyNameValue + " for " + totalPriceOfStock)
                            .setConfirmText("Buy!")
                            .setCancelText("Cancel!")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    realm.beginTransaction();

                                    if(userAlreadyOwnsStock(companyNameValue)){

                                        UserOwnedStock stock = realm.where(UserOwnedStock.class).equalTo("name",companyNameValue).findFirst();
                                        stock.setAmountOwned(String.valueOf(Integer.parseInt(stock.getAmountOwned()) + Integer.parseInt(totalAmountOfStock)));
                                        realm.commitTransaction();

                                    }else{
                                        UserOwnedStock stock = realm.createObject(UserOwnedStock.class); // Create a new object
                                        stock.setName(companyNameValue);
                                        stock.setSymbol(symbolValue);
                                        stock.setPrice(priceOfStock);
                                        stock.setAmountOwned(totalAmountOfStock);

                                        realm.commitTransaction();
                                    }

                                    makePurchase(valueOfTotalPriceOfStock);

                                    SharedVariables.myStocksIsDirty = true;

                                    sDialog
                                            .setTitleText("Success!")
                                            .setContentText("Your stocks have been purchase!")
                                            .setConfirmText("OK")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    finish();
                                                }
                                            })
                                            .showCancelButton(false)
                                            .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                }
                            })
                            .show();
                } else {
                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Error")
                            .setContentText("It looks like you don't have enough money to make that purchase")
                            .setConfirmText("Okay!")
                            .show();
                }
            } else {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Error")
                        .setContentText("Please select some stocks")
                        .setConfirmText("Okay!")
                        .show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Takes a stock name. Checks if the user already owns that stock
     * @return
     */
    private boolean userAlreadyOwnsStock(String stockName) {
        boolean toReturn;

        RealmResults<UserOwnedStock> stocks = realm.where(UserOwnedStock.class).equalTo("name",stockName).findAll();

        if (stocks.size() > 0) {
            toReturn = true;
        } else {
            toReturn = false;
        }

        Log.d("D","userAlreadyOwnsStockDebug does user already own stock = " + toReturn);

        return toReturn;
    }
}
