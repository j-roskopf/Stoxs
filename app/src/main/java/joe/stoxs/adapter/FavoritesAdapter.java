package joe.stoxs.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.text.DecimalFormat;

import io.realm.RealmResults;
import joe.stoxs.DetailSearchView;
import joe.stoxs.Object.Markit.CompanyDetail;
import joe.stoxs.Object.UserOwnedStock;
import joe.stoxs.OwnedStockDetailView;
import joe.stoxs.R;

/**
 * Created by Joe on 6/7/2016.
 */

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.StockViewHolder>{

    private RealmResults<CompanyDetail> stocks;
    private Context context;
    DecimalFormat formater;


    public FavoritesAdapter(RealmResults<CompanyDetail> stocks, Context context){
        this.context = context;
        this.stocks = stocks;
        formater = new DecimalFormat("#.##");

    }

    public class StockViewHolder extends RecyclerView.ViewHolder {
        TextView stockName;
        TextView stockAmount;
        ImageView stockImage;
        int position;

        StockViewHolder(View itemView) {
            super(itemView);
            stockName = (TextView)itemView.findViewById(R.id.stockName);
            stockAmount = (TextView)itemView.findViewById(R.id.stockAmount);
            stockImage = (ImageView)itemView.findViewById(R.id.stockImage);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DetailSearchView.class);
                    intent.putExtra("symbol",stocks.get(position).getSymbol());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(stocks.isValid()){
            return stocks.size();
        }else{
            return 0;
        }

    }

    @Override
    public StockViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.owned_stock_item, viewGroup, false);
        StockViewHolder svh = new StockViewHolder(v);

        return svh;
    }

    @Override
    public void onBindViewHolder(StockViewHolder stockViewHolder, int i) {
        stockViewHolder.stockName.setText(stocks.get(i).getName());
        stockViewHolder.stockAmount.setText( "YTD Change %" + formater.format(Double.parseDouble(stocks.get(i).getChangePercentYTD())));
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(stocks.get(i).getName().substring(0,1), getMatColor("500"));
        stockViewHolder.position = i;
        stockViewHolder.stockImage.setImageDrawable(drawable);
    }

    /**
     * gets random color from colors.xml that conforms to material colors
     * @param typeColor
     * @return
     */
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

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}