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


    /**
     * ui
     */

    TextView money;

    com.github.clans.fab.FloatingActionButton refreshFabItem;



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
        refreshFabItem = (com.github.clans.fab.FloatingActionButton)view.findViewById(R.id.refresh_fab);
        money = (TextView)view.findViewById(R.id.money);

        setupRefreshButton();
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

    @Override
    public void onStart() {
        super.onStart();
    }
}