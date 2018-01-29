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
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
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
    float ex1Data[] = new float[]{1,2,3,4};
    float ex2Data[] = new float[]{1,2,3,4};

    float arbData[] = new float[]{0,1,2,3,4,5,6,7,8,9,10}; //bestBuyPrice, BestBuyVol, BestbuyTotalPrice, Bestbuyex... profit%, profit btc, prof dol,
    String CAMERON = "default";


    public static void main(String[] args) throws IOException, JSONException, InterruptedException, Exception {


        //this section creates two arrays holding the all the data on pairs trading on each exchange



        //String test[] = getTradingPairs(exchangeName1); // actually returns urls for now
        //String test2[] = getTradingPairs(exchangeName2);
        //System.out.println("---------");




        //System.out.println("LOOK HERE: cryptopia "+test[1]); //prints out stored urls
        //System.out.println("LOOK HERE: hitbtc "+test2[2]);


            //update getTradeDataFromJson() with this code //try use orderbook api instead



    }

    //OLD METHOD - TO BE DELETED SOON
    private void getArbitrageData(float[] exchange1Data, float[] exchange2Data) throws Exception{

        //ask, askVol, bid, bidVol

        //DEBUGGER VALUES
        String test = "";
        float percentProfit = 0;
        float profitInBtc = 0;
        float profitInDollars = 0;
        System.out.println("Exchange 1--- Ask/volume :"+exchange1Data[0]+"/"+exchange1Data[1]+" bid/volume: "+exchange1Data[2]+"/"+exchange1Data[3]);
        System.out.println("Exchange 2--- Ask/volume :"+exchange2Data[0]+"/"+exchange2Data[1]+" bid/volume: "+exchange2Data[2]+"/"+exchange2Data[3]);
        //Now to tell arbitrage
        //Can maybe replace with global variables that just get reset
        if (exchange1Data[2] > exchange2Data[0]) { //if buy on cryptopia is more than sell on hitbtc
            test = "Buy at "+exchange2Data[0]+" for "+exchange1Data[3]+" units, sell at "+exchange1Data[2]+" for "+exchange1Data[3]+" units";
            System.out.println(test);
            profitInBtc = (exchange1Data[2]-exchange2Data[0])*exchange2Data[1]; //(askPrice*askVolume)-(bidPrice*askVolume); // "you sell the same number of units you bought!!"
            percentProfit = ((exchange1Data[2]-exchange2Data[0])/exchange2Data[0])*100; //(bidPrice - askPrice) / askPrice * 100;
            System.out.println("Percent profit: "+percentProfit);


        }
        else if(exchange2Data[2] > exchange1Data[0]){
            test = "Buy at "+exchange1Data[0]+" for "+exchange2Data[3]+" units, sell at "+exchange2Data[2]+" for "+exchange2Data[3]+" units";
            System.out.println(test);
            profitInBtc = (exchange2Data[2]-exchange1Data[0])*exchange1Data[1];
            percentProfit = ((exchange2Data[2]-exchange1Data[0])/exchange1Data[0])*100;
            System.out.println("Percent profit: "+percentProfit);

        }
        else{
            System.out.println("No dice.");
            percentProfit = ((exchange1Data[2]-exchange2Data[0])/exchange2Data[0])*100;
            test = "bid: "+exchange1Data[2]+" LESS THAN sell: "+exchange2Data[0]+" Difference: "+percentProfit;
            System.out.println(test);
            percentProfit = ((exchange2Data[2]-exchange1Data[0])/exchange1Data[0])*100;
            System.out.println("bid: "+exchange2Data[2]+" LESS THAN sell: "+exchange1Data[0]+" Difference: "+percentProfit);
        }
        int percentProfInt = (int) Math.ceil(percentProfit*100);
        if(percentProfit>1.7){
            //beep(50, percentProfInt);
        }
        profitInDollars = 15000*profitInBtc;
        System.out.println("Profit in BTC: "+profitInBtc);
        System.out.println("Profit in dollars: "+profitInDollars);

        //create instructions for trading bot
        CAMERON = "Time: "+getDateTime()+""+test+"  /nPercent profit: "+percentProfInt+" Profit in dollars: "+profitInDollars+"";
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
        Log.d("myTag", json.toString());
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


    //THIS NEEDS TO BE TIDIED-------
    public void getArbDataNew(float[] exchange1Data, float[] exchange2Data){

        //ask, askVol, bid, bidVol
        //new vars
        float bestBuyPrice = 0;
        float bestBuyTotalPrice = 0;
        float bestBuyVol = 0;
        float bestSellPrice = 0;
        float bestSellTotalPrice = 0;
        float bestSellVol =0;
        float bestBuyExchange = 0;
        float bestSellExchange = 0;

        //DEBUGGER VALUES
        String test = "";
        float percentProfit = 0;
        float profitInBtc = 0;
        float profitInDollars = 0;
        System.out.println("Exchange 1--- Ask/volume :"+exchange1Data[0]+"/"+exchange1Data[1]+" bid/volume: "+exchange1Data[2]+"/"+exchange1Data[3]);
        System.out.println("Exchange 2--- Ask/volume :"+exchange2Data[0]+"/"+exchange2Data[1]+" bid/volume: "+exchange2Data[2]+"/"+exchange2Data[3]);
        //Now to tell arbitrage
        //Can maybe replace with global variables that just get reset
        if (exchange1Data[2] > exchange2Data[0]) { //if buy on cryptopia is more than sell on hitbtc
            test = "Buy at "+exchange2Data[0]+" for "+exchange1Data[3]+" units, sell at "+exchange1Data[2]+" for "+exchange1Data[3]+" units";
            System.out.println(test);

            bestBuyPrice = exchange2Data[0];
            bestBuyVol = exchange2Data[1];
            bestBuyTotalPrice = bestBuyPrice*bestBuyVol;
            bestBuyExchange = exchange2Data[4];
            bestSellPrice = exchange1Data[2];
            bestSellTotalPrice = bestSellPrice*bestSellVol;
            bestSellVol = exchange1Data[3];
            bestSellExchange = exchange1Data[4];

            profitInBtc = (exchange1Data[2]-exchange2Data[0])*exchange2Data[1]; //(askPrice*askVolume)-(bidPrice*askVolume); // "you sell the same number of units you bought!!"
            percentProfit = ((exchange1Data[2]-exchange2Data[0])/exchange2Data[0])*100; //(bidPrice - askPrice) / askPrice * 100;
            System.out.println("Percent profit: "+percentProfit);


        }
        else if(exchange2Data[2] > exchange1Data[0]){
            test = "Buy at "+exchange1Data[0]+" for "+exchange2Data[3]+" units, sell at "+exchange2Data[2]+" for "+exchange2Data[3]+" units";
            System.out.println(test);

            bestBuyPrice = exchange2Data[0];
            bestBuyVol = exchange2Data[1];
            bestBuyTotalPrice = bestBuyPrice*bestBuyVol;
            bestBuyExchange = exchange2Data[4];
            bestSellPrice = exchange1Data[2];
            bestSellTotalPrice = bestSellPrice*bestSellVol;
            bestSellVol = exchange1Data[3];
            bestSellExchange = exchange1Data[4];

            profitInBtc = (exchange2Data[2]-exchange1Data[0])*exchange1Data[1];
            percentProfit = ((exchange2Data[2]-exchange1Data[0])/exchange1Data[0])*100;
            System.out.println("Percent profit: "+percentProfit);

        }
        else{
            System.out.println("No dice.");
            percentProfit = ((exchange1Data[2]-exchange2Data[0])/exchange2Data[0])*100;
            test = "bid: "+exchange1Data[2]+" LESS THAN sell: "+exchange2Data[0]+" Difference: "+percentProfit;
            System.out.println(test);
            //percentProfit = ((exchange2Data[2]-exchange1Data[0])/exchange1Data[0])*100;
            percentProfit = 0;
            profitInBtc = 0;
            System.out.println("bid: "+exchange2Data[2]+" LESS THAN sell: "+exchange1Data[0]+" Difference: "+percentProfit);
        }
        int percentProfInt = (int) Math.ceil(percentProfit*100);
        if(percentProfit>1.7){
            //beep(50, percentProfInt);
        }
        profitInDollars = 15000*profitInBtc;
        System.out.println("Profit in BTC: "+profitInBtc);
        System.out.println("Profit in dollars: "+profitInDollars);

        //create instructions for trading bot
        //CAMERON = "Time: "+getDateTime()+""+test+"  /nPercent profit: "+percentProfInt+" Profit in dollars: "+profitInDollars+"";
        arbData[0] = bestBuyVol;
        arbData[1] = bestBuyPrice;
        arbData[2] = bestBuyTotalPrice;
        arbData[3] = bestBuyExchange;
        arbData[4] = bestSellVol;
        arbData[5] = bestSellPrice;
        arbData[6] = bestSellTotalPrice;
        arbData[7] = bestSellExchange;
        arbData[8] = percentProfit;
        arbData[9] = profitInBtc;
        arbData[10] = profitInDollars;


    }

    public void getArbDataNew2(float[] exchange1Data, float[] exchange2Data){

        //ask, askVol, bid, bidVol
        //new vars
        float bestBuyPrice = 0;
        float bestBuyTotalPrice = 0;
        float bestBuyVol = 0;
        float bestSellPrice = 0;
        float bestSellTotalPrice = 0;
        float bestSellVol =0;
        float bestBuyExchange = 0;
        float bestSellExchange = 0;

        //DEBUGGER VALUES
        String test = "";
        float percentProfit = 0;
        float profitInBtc = 0;
        float profitInDollars = 0;
        System.out.println("Exchange 1--- Ask/volume :"+exchange1Data[0]+"/"+exchange1Data[1]+" bid/volume: "+exchange1Data[2]+"/"+exchange1Data[3]);
        System.out.println("Exchange 2--- Ask/volume :"+exchange2Data[0]+"/"+exchange2Data[1]+" bid/volume: "+exchange2Data[2]+"/"+exchange2Data[3]);
        //Now to tell arbitrage
        //Can maybe replace with global variables that just get reset
        if (exchange1Data[2] > exchange2Data[0]) { //if buy on cryptopia is more than sell on hitbtc
            test = "Buy at "+exchange2Data[0]+" for "+exchange1Data[3]+" units, sell at "+exchange1Data[2]+" for "+exchange1Data[3]+" units";
            System.out.println(test);

            bestBuyPrice = exchange2Data[0];
            bestBuyVol = exchange2Data[1];
            bestBuyTotalPrice = bestBuyPrice*bestBuyVol;
            bestBuyExchange = exchange2Data[4];
            bestSellPrice = exchange1Data[2];
            bestSellVol = exchange1Data[3];
            bestSellTotalPrice = bestSellPrice*bestSellVol;
            bestSellExchange = exchange1Data[4];

            profitInBtc = (exchange1Data[2]-exchange2Data[0])*exchange2Data[1]; //(askPrice*askVolume)-(bidPrice*askVolume); // "you sell the same number of units you bought!!"
            percentProfit = ((exchange1Data[2]-exchange2Data[0])/exchange2Data[0])*100; //(bidPrice - askPrice) / askPrice * 100;
            System.out.println("Percent profit: "+percentProfit);


        }
        else if(exchange2Data[2] > exchange1Data[0]){
            test = "Buy at "+exchange1Data[0]+" for "+exchange2Data[3]+" units, sell at "+exchange2Data[2]+" for "+exchange2Data[3]+" units";
            System.out.println(test);

            bestBuyPrice = exchange1Data[0];
            bestBuyVol = exchange1Data[1];
            bestBuyTotalPrice = bestBuyPrice*bestBuyVol;
            bestBuyExchange = exchange1Data[4];
            bestSellPrice = exchange2Data[2];
            bestSellVol = exchange2Data[3];
            bestSellTotalPrice = bestSellPrice*bestSellVol;
            bestSellExchange = exchange2Data[4];

            profitInBtc = (exchange2Data[2]-exchange1Data[0])*exchange1Data[1];
            percentProfit = ((exchange2Data[2]-exchange1Data[0])/exchange1Data[0])*100;
            System.out.println("Percent profit: "+percentProfit);

        }
        else{
            System.out.println("No dice.");
            percentProfit = ((exchange1Data[2]-exchange2Data[0])/exchange2Data[0])*100;
            test = "bid: "+exchange1Data[2]+" LESS THAN sell: "+exchange2Data[0]+" Difference: "+percentProfit;
            System.out.println(test);
            //percentProfit = ((exchange2Data[2]-exchange1Data[0])/exchange1Data[0])*100;
            percentProfit = 0;
            profitInBtc = 0;
            System.out.println("bid: "+exchange2Data[2]+" LESS THAN sell: "+exchange1Data[0]+" Difference: "+percentProfit);

        }
        int percentProfInt = (int) Math.ceil(percentProfit*100);
        if(percentProfit>1.7){
            //beep(50, percentProfInt);
        }
        profitInDollars = 15000*profitInBtc;
        System.out.println("Profit in BTC: "+profitInBtc);
        System.out.println("Profit in dollars: "+profitInDollars);

        //create instructions for trading bot
        //CAMERON = "Time: "+getDateTime()+""+test+"  /nPercent profit: "+percentProfInt+" Profit in dollars: "+profitInDollars+"";
        arbData[0] = bestBuyVol;
        arbData[1] = bestBuyPrice;
        arbData[2] = bestBuyTotalPrice;
        arbData[3] = bestBuyExchange;
        arbData[4] = bestSellVol;
        arbData[5] = bestSellPrice;
        arbData[6] = bestSellTotalPrice;
        arbData[7] = bestSellExchange;
        arbData[8] = percentProfit;
        arbData[9] = profitInBtc;
        arbData[10] = profitInDollars;


    }


    public void goArb() throws Exception {
        String output = "something";
        try {
            System.out.println(getDateTime());
            ex1Data = this.getTradeDataFromJson(this.url1, this.exchangeName1); // format [askPrice, askVolume, bidPrice, bidVolume] (i.e. 4 elements: 0 to 3)
            ex2Data = this.getTradeDataFromJson(this.url2, this.exchangeName2);
            //this.getArbitrageData(ex1Data, ex2Data);
            this.getArbDataNew2(ex1Data,ex2Data);
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



    public float[] getArbData(){

        return arbData;
    }



}

