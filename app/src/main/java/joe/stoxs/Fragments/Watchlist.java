package joe.stoxs.Fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmResults;
import joe.stoxs.Object.Markit.CompanyDetail;
import joe.stoxs.R;
import joe.stoxs.adapter.FavoritesAdapter;
import joe.stoxs.adapter.OwnedStocksAdapter;

/**
 * Created by Joe on 6/15/2016.
 */

public class Watchlist extends Fragment {

    private static String ARG_SECTION_NUMBER = "arg_section_number";

    /**
     * non ui
     */

    Realm realm;

    /**
     * UI
     */

    TextView errorMessage;
    RecyclerView favoritesList;
    com.github.clans.fab.FloatingActionButton refreshFabItem;


    public static Watchlist newInstance(int sectionNumber) {
        Watchlist fragment = new Watchlist();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.watchlist_fragment, container, false);

        initVars(view);

        getAndDisplayFavorites();

        setupRefreshButton();

        return view;
    }

    public void initVars(View view){
        realm = Realm.getDefaultInstance();
        errorMessage = (TextView) view.findViewById(R.id.errorMessage);
        favoritesList = (RecyclerView)view.findViewById(R.id.favoritesList);
        refreshFabItem = (com.github.clans.fab.FloatingActionButton) view.findViewById(R.id.refresh_fab);
    }

    @Override
    public void onStop() {
        super.onStop();
        realm.close();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void setupRefreshButton(){
        refreshFabItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAndDisplayFavorites();
            }
        });
    }

    public void getAndDisplayFavorites(){

        RealmResults<CompanyDetail> results = realm.where(CompanyDetail.class).findAll();

        if(results.size() <= 0){
            errorMessage.setVisibility(View.VISIBLE);
            favoritesList.setVisibility(View.INVISIBLE);
        }else{
            errorMessage.setVisibility(View.INVISIBLE);
            favoritesList.setVisibility(View.VISIBLE);
        }

        FavoritesAdapter adapter = new FavoritesAdapter(results,getContext());
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        favoritesList.setLayoutManager(llm);
        favoritesList.setAdapter(adapter);

    }

}
