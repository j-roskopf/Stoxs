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

import java.util.ArrayList;

import io.realm.RealmResults;
import joe.stoxs.DetailSearchView;
import joe.stoxs.Object.Markit.Company;
import joe.stoxs.Object.UserOwnedStock;
import joe.stoxs.OwnedStockDetailView;
import joe.stoxs.R;

/**
 * Created by Joe on 6/7/2016.
 */

public class OwnedStocksAdapter extends RecyclerView.Adapter<OwnedStocksAdapter.StockViewHolder>{

    private RealmResults<UserOwnedStock> stocks;
    private Context context;


    public OwnedStocksAdapter(RealmResults<UserOwnedStock> stocks, Context context){
        this.context = context;
        this.stocks = stocks;
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
                    Intent intent = new Intent(context, OwnedStockDetailView.class);
                    intent.putExtra("symbol",stocks.get(position).getSymbol());
                    intent.putExtra("amountOwned",stocks.get(position).getAmountOwned());
                    intent.putExtra("amountPurchaseAt",stocks.get(position).getPrice());
                    Log.d("D","goingToOpenOwnedStocksDetailView with symbol and i = " + stocks.get(position).getSymbol() + " " + position);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return stocks.size();
    }

    @Override
    public StockViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        return new StockViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.owned_stock_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(StockViewHolder stockViewHolder, int i) {
        stockViewHolder.stockName.setText(stocks.get(i).getName());
        stockViewHolder.stockAmount.setText("Amount owned: " + stocks.get(i).getAmountOwned());
        TextDrawable drawable = TextDrawable.builder().buildRound(stocks.get(i).getName().substring(0,1), getMatColor("500"));
        stockViewHolder.position = i;
        stockViewHolder.stockImage.setImageDrawable(drawable);
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

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}