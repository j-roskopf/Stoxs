package joe.stoxs.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.ArrayList;

import joe.stoxs.Object.Markit.Company;
import joe.stoxs.R;

import static com.amulyakhare.textdrawable.TextDrawable.builder;

/**
 * Created by Joe on 6/1/2016.
 */

public class SearchAdapter extends ArrayAdapter<Company> {
    private final Context context;
    private final ArrayList<Company> values;

    public SearchAdapter(Context context, ArrayList<Company> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View rowView = ((LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.search_result_item, parent, false);

        TextView name = (TextView) rowView.findViewById(R.id.name);
        TextView symbol = (TextView) rowView.findViewById(R.id.symbol);
        ImageView image = (ImageView) rowView.findViewById(R.id.photo);

        Log.d("D","valuesName = " + values.get(i).getName());

        name.setText(values.get(i).getName());
        symbol.setText(values.get(i).getSymbol());
        TextDrawable drawable;

        if(values.get(i).getName().equals("")){
            drawable =  builder()
                    .buildRound(values.get(i).getSymbol().substring(0,1), getMatColor("500"));
            image.setImageDrawable(drawable);
        }else{
           drawable =  TextDrawable.builder()
                    .buildRound(values.get(i).getName().substring(0,1), getMatColor("500"));
            image.setImageDrawable(drawable);
        }



        return rowView;
    }


    private int getMatColor(String typeColor)
    {
        int returnColor = Color.BLACK;
        int arrayId = context.getResources().getIdentifier("mdcolor_" + typeColor, "array", context.getApplicationContext().getPackageName());

        if (arrayId != 0)
        {
            TypedArray colors = context.getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.BLACK);
            colors.recycle();
        }
        return returnColor;
    }

}
