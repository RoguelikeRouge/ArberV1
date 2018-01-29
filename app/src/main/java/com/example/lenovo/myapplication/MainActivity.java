package com.example.lenovo.myapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    float[] arbData;
    TextView textView1;
    TextView textView2;
    TextView textView3;
    TextView textView4;
    TextView textView5;
    TextView textView6;
    TextView textView7;
    TextView textView8;
    TextView textView9;
    TextView textView10;
    TextView textView11;
    TextView textView12;
    TextView textView13;

    Spinner coin1Spinner;

    String selectedCurrency = "ODN";
    /*
    |"coin pair"-------Choose_coin_spinner--------"Choose_coin2_spinner(future)"
    |"buy" ------------***volume***---------------------Coin1_currency-from spinner
    |"at"      --------***price_in_coin2***-------------coin2_currency
    |total ----------***total_price_in_coin2***-------coin2_currency
    |on -------------***Exchange1_name*** ------------
    |
    |REPEAT FOR SELLS
    |
    |Percent profit
    |BTC profit
    |USD profit

     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        try {
            //Toast.makeText(getApplicationContext(), "hello", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //these are the only textviews that need to be updated - there are more static ones in content_main.xml
        textView1 = (TextView) findViewById(R.id.buy_vol_textview);
        textView2 = (TextView) findViewById(R.id.buy_price_textview);
        textView3 = (TextView) findViewById(R.id.buy_total_amount_textview);
        textView4 = (TextView) findViewById(R.id.buy_exchange_textview);
        textView5 = (TextView) findViewById(R.id.sell_vol_textview);
        textView6 = (TextView) findViewById(R.id.sell_price_textview);
        textView7 = (TextView) findViewById(R.id.sell_total_amount_textview);
        textView8 = (TextView) findViewById(R.id.sell_exchange_textview);
        textView9 = (TextView) findViewById(R.id.percent_profit_amount_textview);
        textView10 = (TextView) findViewById(R.id.btc_profit_amount_textview);
        textView11 = (TextView) findViewById(R.id.usd_profit_amount);

        textView12 = (TextView) findViewById(R.id.sell_vol_currency_textview);
        textView13 = (TextView) findViewById(R.id.buy_vol_currency_textview);


        prepareAsycMethods();

        coin1Spinner = (Spinner)findViewById(R.id.coin1_spinner);

        coin1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCurrency = coin1Spinner.getSelectedItem().toString();
                textView12.setText(selectedCurrency);
                textView13.setText(selectedCurrency);


            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void prepareAsycMethods() {

            final Handler handler = new Handler();
            Timer timer = new Timer();

            TimerTask doAsynchronousTask = new TimerTask() {
                @Override
                public void run() {
                    final AsyncTaskGetArbData asyncTask = new AsyncTaskGetArbData(); //move back if doesn't work
                    handler.post(new Runnable() {
                        public void run() {
                            try {

                                // PerformBackgroundTask this class is the class that extends AsynchTask
                                asyncTask.execute();

                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                            }
                        }
                    });
                }
            };
            timer.schedule(doAsynchronousTask, 0, 500); //execute in every 5000 ms




    }

    class AsyncTaskGetArbData extends AsyncTask<Void, String, Float> {


        ArbCode arbCode = new ArbCode();

        private final String TAG = AsyncTaskGetArbData.class.getName();

        TextView test;
        Context myContext;




        protected void onPreExecute(){
            Log.d(TAG, "On preExceute...");

        }

        protected Float doInBackground(Void...arg0) {



            Log.d(TAG, "On doInBackground...");

            try {
                arbCode.main(null);
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                arbCode.setUrlCurrency(selectedCurrency);
                arbCode.goArb();

            } catch (Exception e) {
                e.printStackTrace();
            }



            //return arbCode.getArbData();
            return arbCode.getArbData()[1];
        }



        protected void onProgressUpdate(Integer...a){
            Log.d(TAG,"You are in progress update ... " + a[0]);
        }

        protected void onPostExecute(Float result) {
            arbData = arbCode.getArbData();
            //TextView textView = (TextView) ((Context) mycontext).findViewById(R.id.mainText);
            //test.setText(Arrays.toString(arbCode.getArbData()));
            //{0,1,2,3,4,5,6,7,8,9,10}; //bestBuyPrice, BestBuyVol, BestbuyTotalPrice, Bestbuyex... profit%, profit btc, prof dol,
            textView1.setText(Float.toString(arbData[0]));
            textView2.setText(Float.toString(arbData[1]));
            textView3.setText(Float.toString(arbData[2]));
            textView4.setText(Float.toString(arbData[3]));
            textView5.setText(Float.toString(arbData[4]));
            textView6.setText(Float.toString(arbData[5]));
            textView7.setText(Float.toString(arbData[6]));
            textView8.setText(Float.toString(arbData[7]));
            textView9.setText(Float.toString(arbData[8]));
            textView10.setText(Float.toString(arbData[9]));
            textView11.setText(Float.toString(arbData[10]));

            /*

        textView1 = (TextView) findViewById(R.id.buy_vol_textview);
        textView2 = (TextView) findViewById(R.id.buy_price_textview);
        textView3 = (TextView) findViewById(R.id.buy_total_amount_textview);
        textView4 = (TextView) findViewById(R.id.buy_exchange_textview);
        textView5 = (TextView) findViewById(R.id.sell_vol_textview);
        textView6 = (TextView) findViewById(R.id.sell_price_textview);
        textView7 = (TextView) findViewById(R.id.sell_total_amount_textview);
        textView8 = (TextView) findViewById(R.id.sell_exchange_textview);
        textView9 = (TextView) findViewById(R.id.buy_vol_textview);
        textView10 = (TextView) findViewById(R.id.buy_price_textview);

*/

            Log.d(TAG,Arrays.toString(arbCode.getArbData()));
            //MainActivity mAct = new MainActivity();
            //mAct.textView.setText();

        }
    }


}




