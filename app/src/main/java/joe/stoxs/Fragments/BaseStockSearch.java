package joe.stoxs.Fragments;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.androidquery.AQuery;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import joe.stoxs.Object.UserOwnedStock;
import joe.stoxs.R;
import joe.stoxs.adapter.OwnedStocksAdapter;
import joe.stoxs.adapter.SearchAdapter;

/**
 * Created by Joe on 5/25/2016.
 */

public class BaseStockSearch extends Fragment {

    private static String ARG_SECTION_NUMBER = "arg_section_number";

    private final String API_KEY = "5c4aa30b7667234f8626e60ed2494274";

    String getHistoryQuerySample ="http://marketdata.websol.barchart.com/getHistory.json?key=5c4aa30b7667234f8626e60ed2494274&symbol=IBM&type=daily&startDate=20150531000000";
    String getQuoteQuerySample = "http://marketdata.websol.barchart.com/getQuote.json?key=5c4aa30b7667234f8626e60ed2494274&symbols=IBM,GOOGL";

    private String BASE_API_URL_SYMBOL = "http://dev.markitondemand.com/MODApis/Api/v2/Quote/json?symbol=";
    private String BASE_API_URL_SEARCH = "http://dev.markitondemand.com/MODApis/Api/v2/Lookup/json?input=";

    private AQuery mAquery;

    Realm realm;

    /**
     * UI
     */

    RecyclerView recyclerView;

    View v;

    com.github.clans.fab.FloatingActionButton refreshFabItem;

    TextView errorMessage;


    public static BaseStockSearch newInstance(int sectionNumber) {
        BaseStockSearch fragment = new BaseStockSearch();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.base_stock_search, container, false);
        v = view;
        ButterKnife.bind(this, view);

        initVars();

        displayStocks();


        return view;
    }

    public void refresh(){
        displayStocks();
    }

    @Override
    public void onStop() {
        super.onStop();
        realm.close();

    }

    @Override
    public void onStart() {
        super.onStart();
        realm = Realm.getDefaultInstance();
    }

    public void initVars(){
        mAquery = new AQuery(getContext());
        recyclerView = (RecyclerView)v.findViewById(R.id.ownedStocksRecyclerView);
        realm = Realm.getDefaultInstance();
        errorMessage = (TextView)v.findViewById(R.id.errorMessage);
        refreshFabItem = (com.github.clans.fab.FloatingActionButton) v.findViewById(R.id.refresh_fab);

        setupRefreshButton();

    }

    /**
     * Adds the on click / refresh login for refreshing the available stocks
     */
    public void setupRefreshButton(){
        refreshFabItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayStocks();
            }
        });
    }

    public void displayStocks(){

        RealmQuery<UserOwnedStock> query = realm.where(UserOwnedStock.class);

        RealmResults<UserOwnedStock> allStocks = query.findAll();

        if(allStocks.size() == 0){
            errorMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }else{
            errorMessage.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            OwnedStocksAdapter adapter = new OwnedStocksAdapter(allStocks,getContext());
            LinearLayoutManager llm = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(llm);
            recyclerView.setAdapter(adapter);
        }


    }



}
