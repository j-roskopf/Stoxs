package joe.stoxs.Fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import joe.stoxs.R;

/**
 * Created by Joe on 6/15/2016.
 */

public class Watchlist extends Fragment {

    private static String ARG_SECTION_NUMBER = "arg_section_number";


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

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onStart() {
        super.onStart();
    }

}
