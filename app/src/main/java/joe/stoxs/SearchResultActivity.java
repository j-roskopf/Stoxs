package joe.stoxs;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import joe.stoxs.Constant.Constants;
import joe.stoxs.Object.Markit.Company;
import joe.stoxs.adapter.SearchAdapter;

public class SearchResultActivity extends AppCompatActivity {

    /**
     * UI
     */

    ListView listView;

    /**
     * non UI
     */

    AQuery aq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        setTitle("Search Results");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initVars();

        handleIntent(getIntent());
    }

    public void initVars(){
        listView = (ListView)findViewById(R.id.searchResultsListView);
        aq = new AQuery(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {

        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            search(query);

            Toast.makeText(this,query,Toast.LENGTH_SHORT).show();
        }
    }

    public void search(String query){
        Log.d("D","searchDebug with query = " + query);
        aq.ajax(Constants.BASE_API_URL_SEARCH+query, JSONArray.class, new AjaxCallback<JSONArray>() {

            @Override
            public void callback(String url, JSONArray json, AjaxStatus status) {

                Log.d("D","searchDebug with url = " + url);

                if(json != null){

                    Log.d("D","searchDebug with json = " + json);

                    Company company;
                    ArrayList<Company> toReturn = new ArrayList<>();

                    for(int i = 0; i < json.length(); i++){
                        try{
                            JSONObject jsonObject = json.getJSONObject(i);
                            company = new Company();
                            company.setSymbol(jsonObject.getString("Symbol"));
                            company.setName(jsonObject.getString("Name"));
                            company.setExchange(jsonObject.getString("Exchange"));
                            boolean contains = false;
                            for(Company c : toReturn){
                                if(c.getName().equals(jsonObject.getString("Name"))){
                                    contains = true;
                                    break;
                                }
                            }

                            if(!contains){
                                toReturn.add(company);
                            }

                        }catch (Exception e){

                        }

                    }

                    Log.d("D","searchDebug with toReturn.size = " + toReturn.size());

                    displayData(toReturn);

                }else{

                    Log.d("D","searchDebug with error = " + status.getCode() + " " + status.getMessage() + " " + status.getError());

                }
            }
        });
    }

    public void displayData(final ArrayList<Company> toDisplay){
        listView.setAdapter(new SearchAdapter(this,toDisplay));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), DetailSearchView.class);
                intent.putExtra("symbol",toDisplay.get(position).getSymbol());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Log.d("D","goingToOpenDetailSearch with symbol and i = " + toDisplay.get(position).getSymbol() + " " + position);
                getApplicationContext().startActivity(intent);
            }
        });
    }
}
