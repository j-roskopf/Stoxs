package joe.stoxs.Fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidquery.AQuery;

import butterknife.ButterKnife;
import io.realm.Realm;
import joe.stoxs.R;

/**
 * Created by Joe on 6/15/2016.
 */

public class Summary extends Fragment {

    private static String ARG_SECTION_NUMBER = "arg_section_number";


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