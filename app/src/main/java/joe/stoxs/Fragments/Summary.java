package joe.stoxs.Fragments;

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

/**
 * Created by Joe on 6/15/2016.
 */

public class Summary extends Fragment {

    /**
     * non ui
     */
    private static String ARG_SECTION_NUMBER = "arg_section_number";

    private NumberFormat formatter;

    private Realm realm;

    private final double DEFAULT_STARTING_MONEY = 50000;


    /**
     * ui
     */

    TextView money;


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

        realm.commitTransaction();

    }

    public void initVars(View view){
        formatter = NumberFormat.getCurrencyInstance();
        realm = Realm.getDefaultInstance();

        money = (TextView)view.findViewById(R.id.money);
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}