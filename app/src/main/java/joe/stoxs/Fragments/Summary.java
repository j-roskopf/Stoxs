package joe.stoxs.Fragments;

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
import com.codetroopers.betterpickers.numberpicker.NumberPickerBuilder;
import com.codetroopers.betterpickers.numberpicker.NumberPickerDialogFragment;

import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.math.BigInteger;
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

/**
 * Created by Joe on 6/15/2016.
 */

public class Summary extends Fragment implements NumberPickerDialogFragment.NumberPickerDialogHandlerV2 {

    /**
     * non ui
     */

    final int REFERENCE_MORE_MONEY = 1;

    private static String ARG_SECTION_NUMBER = "arg_section_number";

    private NumberFormat formatter;

    private Realm realm;


    /**
     * ui
     */

    TextView money;
    TextView lifetimeMoney;

    com.github.clans.fab.FloatingActionButton refreshFabItem;
    com.github.clans.fab.FloatingActionButton moreMoneyItem;

    public static Summary newInstance(int sectionNumber) {
        Summary fragment = new Summary();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.summary_fragment, container, false);
        initVars(view);

        getMoney();

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        realm.close();
    }

    /**
     * makes a call to the DB to get the amount of money the user has
     */
    public void getMoney(){
        RealmResults<Profile> profiles = realm.where(Profile.class).findAll();

        if(profiles.size() > 0){
            Profile profile = realm.where(Profile.class).findFirst();
            money.setText(formatter.format(profile.getMoney()));
            lifetimeMoney.setText(formatter.format(profile.getLifetimeEarned()));
            if(profile.getLifetimeEarned() < 0){
                //boo
                lifetimeMoney.setTextColor(getResources().getColor(R.color.negativeColor));
            }else{
                //nice
                lifetimeMoney.setTextColor(getResources().getColor(R.color.moneyColor));
            }
        }else{
            createProfile();
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

        money.setText(formatter.format(DEFAULT_STARTING_MONEY));
        lifetimeMoney.setText(formatter.format(0.0));



        realm.commitTransaction();

    }

    public void initVars(View view){
        formatter = NumberFormat.getCurrencyInstance();
        realm = Realm.getDefaultInstance();

        refreshFabItem = (com.github.clans.fab.FloatingActionButton)view.findViewById(R.id.refresh_fab);
        moreMoneyItem = (com.github.clans.fab.FloatingActionButton)view.findViewById(R.id.moreMoney);

        money = (TextView)view.findViewById(R.id.money);
        lifetimeMoney = (TextView)view.findViewById(R.id.lifetimeMoney);

        setupRefreshButton();

        setupMoreMoneyButton();
    }

    private void setupMoreMoneyButton() {
        moreMoneyItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPicker();
            }
        });
    }

    /**
     * Adds the on click / refresh login for refreshing the available stocks
     */
    public void setupRefreshButton(){
        refreshFabItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMoney();
            }
        });
    }

    /**
     * prompts the user with a dialog to give them more money
     */
    public void displayPicker(){
        NumberPickerBuilder npb = new NumberPickerBuilder()
                .setReference(REFERENCE_MORE_MONEY)
                .setFragmentManager(getActivity().getSupportFragmentManager())
                .setTargetFragment(Summary.this)
                .setMaxNumber(BigDecimal.valueOf(Constants.DEFAULT_STARTING_MONEY))
                .setPlusMinusVisibility(View.INVISIBLE)
                .setDecimalVisibility(View.INVISIBLE)
                .setStyleResId(R.style.BetterPickersDialogFragment_Light)
                .setLabelText("Dollars");
        npb.show();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDialogNumberSet(int reference, BigInteger number, double decimal, boolean isNegative, BigDecimal fullNumber) {

        final int amount = number.intValue();

        Log.d("D","moreMoneyDebug");

        if(reference == REFERENCE_MORE_MONEY){

            Profile profile = realm.where(Profile.class).findFirst();
            realm.beginTransaction();

            Log.d("D","moreMoneyDebug current amount of money = " + profile.getMoney());
            profile.setMoney(profile.getMoney() + amount);
            Log.d("D","moreMoneyDebug total money = " + (profile.getMoney() + amount));

            realm.commitTransaction();

            getMoney();
        }

    }
}