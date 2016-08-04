package joe.stoxs.Constant;

/**
 * Created by Joe on 6/1/2016.
 */

public class Constants {

    //barchart
    public static String BASE_CHART_URL ="http://marketdata.websol.barchart.com/getHistory.json?key=insert_key_here&symbol=insert_symbol_here&type=daily&startDate=insert_date_here";
    public static String getHistoryQuerySample ="http://marketdata.websol.barchart.com/getHistory.json?key=insert_key_here&symbol=IBM&type=daily&startDate=20160615";
    public static String getQuoteQuerySample = "http://marketdata.websol.barchart.com/getQuote.json?key=insert_key_here&symbols=IBM,GOOGL";

    public static String MARKET_DATA_API_KEY = "5c4aa30b7667234f8626e60ed2494274";

    //markit
    public static String BASE_API_URL_SYMBOL = "http://dev.markitondemand.com/MODApis/Api/v2/Quote/json?symbol=";
    public static String BASE_API_URL_SEARCH = "http://dev.markitondemand.com/MODApis/Api/v2/Lookup/json?input=";

    public static final double DEFAULT_STARTING_MONEY = 50000;

}
