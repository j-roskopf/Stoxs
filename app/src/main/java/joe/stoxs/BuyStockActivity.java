 package joe.stoxs;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.TextView;

import com.codetroopers.betterpickers.numberpicker.NumberPickerBuilder;
import com.codetroopers.betterpickers.numberpicker.NumberPickerDialogFragment;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.realm.Realm;
import joe.stoxs.Object.UserOwnedStock;

 public class BuyStockActivity extends AppCompatActivity implements NumberPickerDialogFragment.NumberPickerDialogHandlerV2 {

     /**
      * UI
      */
     @BindView(R.id.companyNameBuy)
     TextView companyName;

     @BindView(R.id.companyPriceBuy)
     TextView companyPrice;

     @BindView(R.id.totalPrice)
     TextView totalPrice;

     @BindView(R.id.totalAmount)
     TextView totalAmount;

     @BindView(R.id.seekbar)
     org.adw.library.widgets.discreteseekbar.DiscreteSeekBar seekbar;

     @BindView(R.id.numberButton)
     Button specifyNumber;

     @BindView(R.id.buyButton)
     Button buyButton;

     /**
      * non ui
      */

     String priceOfStock;
     String totalPriceOfStock;
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


        String name = getIntent().getExtras().getString("name");
        companyNameValue = name;
        String symbol = getIntent().getExtras().getString("symbol");
        symbolValue = symbol;
        String price = getIntent().getExtras().getString("price");
        priceOfStock = price;
        String volume = getIntent().getExtras().getString("volume");

        displayInfo(name, symbol, price);

        setupSeekBar(volume,price);

        setupNumberButton(volume);

        setupBuyButton();

    }

     @Override
     protected void onDestroy() {
         super.onDestroy();
         realm.close();
     }

     public void initVars(){
         priceOfStock = "0.0";
         formatter = NumberFormat.getCurrencyInstance();
         context = this;
         totalPriceOfStock = "";
         totalAmountOfStock = "";
         realm = Realm.getDefaultInstance();
     }

     public void setupBuyButton(){
         buyButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if(!totalPriceOfStock.equals("") && !totalAmountOfStock.equals("")){
                     new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                             .setTitleText("Confirm purchase")
                             .setContentText("You are purchasing " + totalAmountOfStock + " stocks of " + companyName.getText() + " for " + totalPriceOfStock)
                             .setConfirmText("Buy!")
                             .setCancelText("Cancel!")
                             .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                 @Override
                                 public void onClick(SweetAlertDialog sDialog) {
                                     realm.beginTransaction();

                                     UserOwnedStock stock = realm.createObject(UserOwnedStock.class); // Create a new object
                                     stock.setName(companyNameValue);
                                     stock.setSymbol(symbolValue);
                                     stock.setPrice(priceOfStock);
                                     stock.setAmountOwned(totalAmountOfStock);

                                     realm.commitTransaction();

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
                 }else{
                     new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                             .setTitleText("Please select some stocks")
                             .setConfirmText("Okay!")
                             .show();
                 }

             }
         });
     }

     public void setupSeekBar(String volume, final String price){
         seekbar.setMax(Integer.parseInt(volume));
         seekbar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
             Double priceToUse = Double.parseDouble(price);
             NumberFormat formatter = NumberFormat.getCurrencyInstance();
             @Override
             public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {


                 String price = formatter.format((value * priceToUse));
                 totalAmount.setText(value+"");
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

     public void displayInfo(String name, String symbol, String price){

         companyName.setText(name + "(" + symbol +")");

         NumberFormat formatter = NumberFormat.getCurrencyInstance();

         price = formatter.format(Double.parseDouble(price));

         companyPrice.setText(price);
     }

     public void setupNumberButton(final String volume){
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
     public void onDialogNumberSet(int reference, BigInteger number, double decimal, boolean isNegative, BigDecimal fullNumber) {

         totalAmount.setText(number.toString());
         String price = formatter.format(number.doubleValue() * Double.parseDouble(priceOfStock));
         totalPrice.setText(price);
         seekbar.setProgress(number.intValue());

         totalAmountOfStock = number.toString();
         totalPriceOfStock = price;
     }
}
