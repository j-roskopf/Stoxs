package joe.stoxs;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import joe.stoxs.Fragments.BaseStockSearch;
import joe.stoxs.Fragments.Summary;
import joe.stoxs.Fragments.Watchlist;

import static joe.stoxs.R.id.sellButton;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private Toolbar toolbar;

    private int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);



        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        setupDrawer();



    }

    public void setupDrawer(){
        new DrawerBuilder().withActivity(this).build();

        //if you want to update the items at a later time it is recommended to keep it in a variable
        PrimaryDrawerItem feedback = new PrimaryDrawerItem().withName("Feedback").withIdentifier(1);
        feedback.withIcon(R.drawable.ic_help_black_24dp);

        ExpandableDrawerItem expandableDrawerItem = new ExpandableDrawerItem().withName("Finance News").withIcon(R.drawable.ic_monetization_on_black_24dp).withSelectable(false).withSubItems(
                new SecondaryDrawerItem().withName("CNN").withLevel(2).withIcon(R.drawable.cnn).withIdentifier(2),
                new SecondaryDrawerItem().withName("Yahoo").withLevel(2).withIcon(R.drawable.yahoo).withIdentifier(3)
        );

        SectionDrawerItem sectionDrawerItem = new SectionDrawerItem().withDivider(true).withName("Stock Prices");

        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                /*.addProfiles(
                        new ProfileDrawerItem().withName("Joe ").withEmail("test@gmail.com")
                )*/
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();


        //create the drawer and remember the `Drawer` result object
        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withToolbar(toolbar)
                .addDrawerItems(
                        feedback,
                        expandableDrawerItem,
                        sectionDrawerItem,
                        new SecondaryDrawerItem().withName("CNN").withIcon(R.drawable.cnn).withIdentifier(4).withSelectable(false),
                        new SecondaryDrawerItem().withName("Google").withIcon(R.drawable.google).withIdentifier(5).withSelectable(false),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName("Reuters").withIdentifier(6),
                        new SecondaryDrawerItem().withName("NASDAQ").withIdentifier(7)
                )
                .build();

        result.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                long id = drawerItem.getIdentifier();

                String url = "";

                if(id == 1){
                    //feedback
                    Intent i = new Intent(getApplicationContext(),FeedbackActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }else if(id == 2){
                    //cnn news
                    url = "http://money.cnn.com";
                }else if(id == 3){
                    //yahoo news
                    url = "http://finance.yahoo.com";
                }else if(id == 4){
                    //cnn stock prices
                    url = "http://money.cnn.com/data/markets";
                }else if(id == 5){
                    //google stock prices
                    url = "https://www.google.com/finance";
                }else if(id == 6){
                    //reuters link
                    url = "http://www.reuters.com/";
                }else if(id == 7){
                    //nasdax link
                    url = "http://www.nasdaq.com/";
                }

                if(id >= 2 && !url.equals("")){
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                }

                return false;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater mi=getMenuInflater();
        mi.inflate(R.menu.menu_main, menu);


        MenuItem mSpinnerItem1 = menu.findItem( R.id.search);
        View view1 = mSpinnerItem1.getActionView();


        if (view1 instanceof SearchView)
        {
            // Associate searchable configuration with the SearchView
            SearchManager searchManager =
                    (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView =
                    (SearchView) view1;
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getComponentName()));

            Log.d("D","menuCreateDebug view instanceOf SearchView");

        }

        Log.d("D","menuCreateDebug view not instanceOf SearchView");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            currentPosition = position;
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if(position == 0){
                return BaseStockSearch.newInstance(position + 1);
            }else if(position == 1){
                return Watchlist.newInstance(position + 1);
            }else if(position == 2){
                return Summary.newInstance(position + 1);
            }

            return BaseStockSearch.newInstance(position + 1);

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "My Stocks";
                case 1:
                    return "Watchlist";
                case 2:
                    return "Summary";
            }
            return null;
        }

    }
}
