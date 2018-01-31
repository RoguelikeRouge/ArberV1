package com.example.lenovo.myapplication;

/**
 * Created by Lenovo on 22/01/2018.
 */



//CODE WORKS! It just got nuked during a shift in the indices from the json request. Once the indices are balanced you have to find
// where i put something in comments, if anything

// goals
// search all coins, only show arbable
// show volume top bids and asks
// bait bots into to paying too high
// calculate projected profit
// make price alerts
// DETECT WHEN BOT HAS BEEN TURNED OFF - no orders for 350 in a while
// Alert for keeping eye on a buy wall
// orders too low/too high to catch overbuys/sells
// automatically perform trade
// Goals - make it work for more cryptos
// propose chancer trades - buying low/selling high and THEN cashing out immediately
// show volume of top bids/ask and prospective profit
/**
 *
 * @author Lenovo
 */
//import java.awt.Toolkit;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.Collator;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
//import java.util.Iterator;
//import javax.sound.sampled.AudioFormat;
//import javax.sound.sampled.AudioSystem;
//import javax.sound.sampled.LineUnavailableException;
//import javax.sound.sampled.SourceDataLine;
import org.json.JSONArray;

import org.json.JSONException;
import org.json.JSONObject;

//import org.knowm.xchange.cryptopia.*;

public class ArbCode {

    //static String startupTime = getDateTime();
    String startupTime = "999";
    String cryptopiaPairs = "https://www.cryptopia.co.nz/api/GetCurrencies";
    String hitbtcPairs = "https://api.hitbtc.com/api/1/public/symbols";
    String url1 = "https://www.cryptopia.co.nz/api/GetMarketOrders/LTC_BTC/1"; // ENTER BASE URLS TO COMPARE: 1 MUST BE CRYPTOPIA, 2 MUST BE HITBTC
    String url2 = "https://api.hitbtc.com/api/1/public/LTCbtc/orderbook";
    String exchangeName1 = "cryptopia";
    String exchangeName2 = "hitbtc";
    String overviewExchange1;
    String overviewExchange2;
    float ex1Data[] = new float[]{1,2,3,4};
    float ex2Data[] = new float[]{1,2,3,4};

    float arbData[] = new float[]{0,1,2,3,4,5,6,7,8,9,10}; //bestBuyPrice, BestBuyVol, BestbuyTotalPrice, Bestbuyex... profit%, profit btc, prof dol,

    int dateAndTime = 0000;



    public static void main(String[] args) throws IOException, JSONException, InterruptedException, Exception {


        //this section creates two arrays holding the all the data on pairs trading on each exchange



        //String test[] = getTradingPairs(exchangeName1); // actually returns urls for now
        //String test2[] = getTradingPairs(exchangeName2);
        //System.out.println("---------");




        //System.out.println("LOOK HERE: cryptopia "+test[1]); //prints out stored urls
        //System.out.println("LOOK HERE: hitbtc "+test2[2]);


            //update getTradeDataFromJson() with this code //try use orderbook api instead



    }

    public String[] getTradingPairs(String exchangeName) throws JSONException, IOException{
        //WILL ACCIDENTALLY CREATE BTC_BTC trading pairs. Need to make code ignore errors here.

        // retrieves json of all coins. Grabs "data" json array. Runs lop to grab each index of the json array. prints each out
        String sourceUrl =""; // used to access all trading pairs
        String uniqueParameter1 = ""; //the jsons from different exchanges use different parameters to access individual json objects - they will be set as required
        String uniqueParameter2 = ""; // as above
        String outputUrl; //parameter that is returned by this method - unique url for each coin
        if(exchangeName.equals("hitbtc")){
            sourceUrl = "https://api.hitbtc.com/api/1/public/symbols";
            uniqueParameter1 = "symbols";
            uniqueParameter2 = "commodity";
        }
        else if(exchangeName.equals("cryptopia")){
            sourceUrl = "https://www.cryptopia.co.nz/api/GetCurrencies";
            uniqueParameter1 = "Data";
            uniqueParameter2 = "Symbol";
        }

        try{
            JSONObject jsonObject1 = this.readJsonFromUrl(sourceUrl); //THIS NEEDS TO BE IN A TRY-CATCH IN CASE IT DOESN'T CONNECT, WHICH HAS HAPPENED BEFORE
            JSONArray jsonArray1 = jsonObject1.getJSONArray(uniqueParameter1);

            String[] newURL1 = new String[jsonArray1.length()]; //initialising array of cryptopia urls before filling in for loop

            for (int i = 0; i < jsonArray1.length(); i++) {
                String currency = jsonArray1.getJSONObject(i).get(uniqueParameter2).toString();
                newURL1[i] = constructUrl(currency, exchangeName);
                System.out.println(newURL1[i]);
            }
            return newURL1;
        }
        catch(Exception e){

            System.out.println(e);

        }

        String error[] = new String[1];
        error[0] = "error";
        return error;


    }

    public void updateRecord(String input) {
        PrintWriter pw = null;

        try {
            File file = new File("Arbitrage_Data_"+startupTime+".txt");
            FileWriter fw = new FileWriter(file, true);
            pw = new PrintWriter(fw);
            pw.println(input);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }

    public String constructUrl(String currency, String exchangeName) {
        String newAPIURL = "default Text";
        if(exchangeName.equals("hitbtc")){
            newAPIURL = "https://www.cryptopia.co.nz/api/GetMarketOrders/" + currency + "_BTC/1"; //makes url from string
        }
        else if(exchangeName.equals("cryptopia")){
            newAPIURL = "https://api.hitbtc.com/api/1/public/" + currency + "btc/orderbook";
        }
        return newAPIURL;
    }

    //returns top bid/ask and respective volumes in float array
    // exchange code is integer corresponding to an exchange - represents exchange as number so that it can squeeze into float array
    public float[] getTradeDataFromJson(String url, String exchangeName) throws IOException, JSONException{
        float[] tradeData = new float[5];
        JSONObject json = this.readJsonFromUrl(url);
        //Log.d("myTag", json.toString());
        String askTemp = null;
        String bidTemp = null;
        String askVolumeTemp = null;
        String bidVolumeTemp = null;
        float askVolume = -1; // need catches for negative numbers
        float bidVolume = -1;
        float askPrice =-1;
        float bidPrice =-1;
        float exchangeCode = 0; //default zero - debugger value
        if(exchangeName.equals("cryptopia")){
            JSONObject data = json.getJSONObject("Data");
            JSONArray jsonArray = data.getJSONArray("Buy");
            bidTemp = jsonArray.getJSONObject(0).get("Price").toString();
            bidVolumeTemp = jsonArray.getJSONObject(0).get("Volume").toString();
            jsonArray = data.getJSONArray("Sell");
            askTemp = jsonArray.getJSONObject(0).get("Price").toString();
            askVolumeTemp = jsonArray.getJSONObject(0).get("Volume").toString();
            exchangeCode = 1;
        }
        else if(exchangeName.equals("hitbtc")){
            JSONArray asks = json.getJSONArray("asks");
            JSONArray topAsk = (JSONArray) asks.get(0);
            JSONArray bids = json.getJSONArray("bids");
            JSONArray topBid = (JSONArray) bids.get(0);
            askTemp = (String) topAsk.get(0);
            askVolumeTemp = (String) topAsk.get(1);
            bidTemp = (String) topBid.get(0);
            bidVolumeTemp = (String) topBid.get(1);
            exchangeCode = 2;
        }
        askVolume = Float.parseFloat(askVolumeTemp);
        bidVolume = Float.parseFloat(bidVolumeTemp);
        askPrice = Float.parseFloat(askTemp);
        bidPrice = Float.parseFloat(bidTemp);
        tradeData[0] = askPrice;
        tradeData[1] = askVolume;
        tradeData[2] = bidPrice;
        tradeData[3] = bidVolume;
        tradeData[4] = exchangeCode;

        //RETURN EITHER OBJECT OR ARRAY
        return tradeData;
    }

    //Methods to retreive JSON from URL --------------------

    private String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        //InputStream is = new URL(url).openStream(); // this used to be here
        InputStream is = null;
        JSONObject json = null;
        try{
            is = new URL(url).openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = this.readAll(rd);
            json = new JSONObject(jsonText);


        }
        catch(Exception e){
            System.out.println(e);
            Log.d("camb", "ERROR", e);
        }

        finally {
            is.close();//is.close() //this used to be here
        }
        return json;
    }

    //MISC METHODS ----------------------------------

    public String getDateTime (){
        DateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
        Date date = new Date();
        String output;
        output = dateFormat.format(date);
        return output;
    }




    public void getArbDataNew3(float[] exchange1Data, float[] exchange2Data){

        Log.d("z", Arrays.toString(exchange1Data));
        Log.d("z", Arrays.toString(exchange2Data));
        //ask, askVol, bid, bidVol
        //new vars
        float ex1Ask = exchange1Data[0];
        float ex2Ask = exchange2Data[0];
        float ex1Bid = exchange1Data[2];
        float ex2Bid = exchange2Data[2];
        float ex1AskVol = exchange1Data[1];
        float ex2AskVol = exchange2Data[1];
        float ex1BidVol = exchange1Data[3];
        float ex2BidVol = exchange2Data[3];


        float bestBidPrice = 0; // best means highest bid price
        float bestBidTotalPrice = 0;
        float bestBidVol = 0;
        float bestAskPrice = 0; // best means lowest
        float bestAskTotalPrice = 0;
        float bestAskVol =0;
        float bestBidExchange = 0;
        float bestAskExchange = 0;

        //DEBUGGER VALUES
        String test = "";
        float percentProfit = 0;
        float profitInBtc = 0;
        float profitInDollars = 0;

        //logic
        //if ask1<buy2 OR if ask2<buy1
        //bestBid means highest, bestAsk means lowest - you buy the cheapest ask, and sell to the priciest bid

        if (ex1Bid > ex2Ask) { //if bid on ex1 is more than ask on ex2 then buy ex2ask, sell ex1bid

            if(ex1BidVol>ex2AskVol){ //if ex1Ask volume is greater than
                bestBidVol = ex2AskVol; // maybe say "idealBidVol"
            }
            else {
                bestBidVol = ex1BidVol;
            }
            bestAskVol = bestBidVol;
            bestBidPrice = ex1Bid; //
            bestBidTotalPrice = bestBidPrice*bestBidVol;
            bestBidExchange = exchange1Data[4]; // exchange reference stored here
            bestAskPrice = ex2Ask;
            bestAskTotalPrice = bestAskPrice*bestAskVol;
            bestAskExchange = exchange2Data[4];
        }

        else if(ex2Bid > ex1Ask){

            if(ex2BidVol>ex1AskVol){ //if ex1Ask volume is greater than
                bestBidVol = ex1AskVol; // maybe say "idealBidVol"
            }
            else {
                bestBidVol = ex2BidVol;
            }
            bestAskVol = bestBidVol;
            bestBidPrice = ex2Bid; //
            bestBidTotalPrice = bestBidPrice*bestBidVol;
            bestBidExchange = exchange2Data[4]; // exchange reference stored here
            bestAskPrice = ex1Ask;
            bestAskTotalPrice = bestAskPrice*bestAskVol;
            bestAskExchange = exchange1Data[4];
        }

        profitInBtc = (bestBidPrice*bestBidVol)-(bestAskPrice*bestAskVol); //(askPrice*askVolume)-(bidPrice*askVolume); // "you sell the same number of units you bought!!"
        percentProfit = (bestBidTotalPrice-bestAskTotalPrice)/bestAskTotalPrice*100; //(bidPrice - askPrice) / askPrice * 100;
        profitInDollars = 11500*profitInBtc; // need live BTC price

        arbData[0] = bestBidVol;
        arbData[1] = bestBidPrice;
        arbData[2] = bestBidTotalPrice;
        arbData[3] = bestBidExchange;
        arbData[4] = bestAskVol;
        arbData[5] = bestAskPrice;
        arbData[6] = bestAskTotalPrice;
        arbData[7] = bestAskExchange;
        arbData[8] = percentProfit;
        arbData[9] = profitInBtc;
        arbData[10] = profitInDollars;
    }

    public void goArb() throws Exception {

        try {
            System.out.println(getDateTime());
            ex1Data = this.getTradeDataFromJson(this.url1, this.exchangeName1); // format [askPrice, askVolume, bidPrice, bidVolume] (i.e. 4 elements: 0 to 3)
            ex2Data = this.getTradeDataFromJson(this.url2, this.exchangeName2);

            //this.getArbitrageData(ex1Data, ex2Data);
            this.getArbDataNew3(ex1Data,ex2Data);
            System.out.println("---------");
        } catch (Exception e) {
            System.out.println(e);
            Log.e("cam", this.url1, e);
        } finally {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.e("cam2", "I got an error", e);
            }
        }
        //return this.getArbitrageData(ex1Data, ex2Data);
    }

    public void setUrlCurrency(String currency){
        url1 = "https://www.cryptopia.co.nz/api/GetMarketOrders/"+currency+"_BTC/1"; // ENTER BASE URLS TO COMPARE: 1 MUST BE CRYPTOPIA, 2 MUST BE HITBTC
        url2 = "https://api.hitbtc.com/api/1/public/"+currency+"btc/orderbook";
    }



    //GETS ALL THE COINS LIVE ON AN EXCHANGE - DOESN'T CHECK IF THEY HAVE SOME UNIQUE PROBLEM STATED IN THE JSON
    public List<String> getAllCoins(String exchangeName){
            //WILL ACCIDENTALLY CREATE BTC_BTC trading pairs. Need to make code ignore errors here.
        Log.d("method", "called getallcoins");

            // retrieves json of all coins. Grabs "data" json array. Runs lop to grab each index of the json array. prints each out
            String sourceUrl =""; // used to access all trading pairs
            String uniqueParameter1 = ""; //the jsons from different exchanges use different parameters to access individual json objects - they will be set as required
            String uniqueParameter2 = ""; // as above
            String outputUrl; //parameter that is returned by this method - unique url for each coin
            if(exchangeName.equals("hitbtc")){
                sourceUrl = "https://api.hitbtc.com/api/1/public/symbols";
                uniqueParameter1 = "symbols";
                uniqueParameter2 = "commodity";
            }
            else if(exchangeName.equals("cryptopia")){
                sourceUrl = "https://www.cryptopia.co.nz/api/GetCurrencies";
                uniqueParameter1 = "Data";
                uniqueParameter2 = "Symbol";
                Log.d("method", exchangeName);
            }

            try{
                JSONObject jsonObject1 = readJsonFromUrl(sourceUrl); //THIS NEEDS TO BE IN A TRY-CATCH IN CASE IT DOESN'T CONNECT, WHICH HAS HAPPENED BEFORE
                JSONArray jsonArray1 = jsonObject1.getJSONArray(uniqueParameter1);

                //String[] allCoinsArray = new String[jsonArray1.length()]; //initialising array of all base trade pairs before filling in for loop
                String[] allCoinsArray;
                //
                Set<String> hashSet = new HashSet<>(); // like a list, but ignores duplicates

                for (int i = 0; i < jsonArray1.length()-1; i++) { // i set to 1 for some reason - not sure why! gets null value if set to 0 ??
                    hashSet.add(jsonArray1.getJSONObject(i).get(uniqueParameter2).toString());
                }

                List<String> allCoinsList = new ArrayList<String>();
                allCoinsList.addAll(hashSet);
                java.util.Collections.sort(allCoinsList, Collator.getInstance()); // sorts alphabetically, magically

                //Log.d("all coins array",Arrays.toString(allCoinsArray)); // MAKE NEW LOG PLZ

                return allCoinsList;
            }
            catch(Exception e){

                System.out.println("Problem with allCoinGetter"+e);
                Log.d("method", "error"+e);


            }

            String error[] = new String[1];
            error[0] = "error";
            return null; //not sure what else to return here
    }
    //currently just compares two array lists which should contain all trading pairs - the matching ones are put in a new array and returned
    public String[] getValidSpinnerCoinOptions(List<String> allCoinsList1, List<String> allCoinsList2){
        allCoinsList1.retainAll(allCoinsList2);
        String allCoinsArray[] = new String[allCoinsList1.size()];
        allCoinsList1.toArray(allCoinsArray);


        return allCoinsArray;

    }

    public String getOverviewExchange1(){

        return "Ask/vol: "+ex1Data[0]+"/"+ex1Data[1]+" Bid/vol:"+ex1Data[2]+"/"+ex1Data[3];
    }

    public String getOverviewExchange2(){

        return "Ask/vol: "+ex2Data[0]+"/"+ex2Data[1]+" Bid/vol:"+ex2Data[2]+"/"+ex2Data[3];

    }

    public String getLastUpdatedTime(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return "Last Updated: "+dateFormat.format(date); //2016/11/16 12:08:43

    }


    public float[] getArbData(){

        return arbData;
    }



}

