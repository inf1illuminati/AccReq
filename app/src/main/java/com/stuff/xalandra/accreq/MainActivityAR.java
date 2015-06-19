package com.stuff.xalandra.accreq;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;


public class MainActivityAR extends Activity implements SensorEventListener {

    //helps select sensor
    Sensor sensor;
    //Manage sensors
    SensorManager sm;

    //tekst labels
    TextView displayReading;
    TextView arrayStuff;

    //knoppen
    Button stuurButton;
    Button nuButton;
    Button startloop;
    Button stoploop;


    //variabelen
    float a;
    float b;
    float c;
    float buttonclick;

    //array
    float dataKopie[] = new float[3];


    //timer!!///////////////////////////////////////////////////////////////////////////////////////
    TextView timerTextView;
    long startTime = 0;

    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            timerTextView.setText(String.format("%d:%02d", minutes, seconds));

            timerHandler.postDelayed(this, 500);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
        Button b = (Button)findViewById(R.id.button);
        b.setText("start");
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_ar);

        //HTTPrequest!!/////////////////////////////////////////////////////////////////////////////
            //knop weg, dus oncreate naar database spammen
            //knop
            final Button GetServerData = (Button) findViewById(R.id.GetServerData);

            // On button click call this listener
            GetServerData.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    Toast.makeText(getBaseContext(),
                            "Please wait, connecting to server.",
                            Toast.LENGTH_SHORT).show();


                    // Create Inner Thread Class
                    Thread background = new Thread(new Runnable() {

                        private final HttpClient Client = new DefaultHttpClient();
                        private String URL = "http://robotarm.serverict.nl/index.php?x="+dataKopie[0]+"&y="+dataKopie[1]+"&z="+dataKopie[2];


                        // After call for background.start this run method call
                        public void run() {
                            try {

                                String SetServerString = "";
                                HttpGet httpget = new HttpGet(URL);
                                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                                SetServerString = Client.execute(httpget, responseHandler);
                                threadMsg(SetServerString);



                            } catch (Throwable t) {
                                // just end the background thread
                                Log.i("Animation", "Thread  exception " + t);
                            }
                        }

                        private void threadMsg(String msg) {

                            if (!msg.equals(null) && !msg.equals("")) {
                                Message msgObj = handler.obtainMessage();
                                Bundle b = new Bundle();
                                b.putString("message", msg);
                                msgObj.setData(b);
                                handler.sendMessage(msgObj);
                            }
                        }

                        // Define the Handler that receives messages from the thread and update the progress
                        private final Handler handler = new Handler() {

                            public void handleMessage(Message msg) {

                                String aResponse = msg.getData().getString("message");

                                if ((null != aResponse)) {

                                    // ALERT MESSAGE
                                    Toast.makeText(
                                            getBaseContext(),
                                            "Server Response: " + aResponse,
                                            Toast.LENGTH_SHORT).show();
                                } else {

                                    // ALERT MESSAGE
                                    Toast.makeText(
                                            getBaseContext(),
                                            "Got No Response From Server.",
                                            Toast.LENGTH_SHORT).show();
                                }

                            }
                        };

                    });
                    // Start Thread
                    background.start();  //After call start method thread called run Method
                }
            });

        //ACCELERATORsensor!!///////////////////////////////////////////////////////////////////////
            //setting up sensor service
            sm = (SensorManager) getSystemService(SENSOR_SERVICE);
            //select accelerometer
            sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            //tekstBox.setText("Begin");

            nuButton = (Button)findViewById(R.id.nuButton);
            arrayStuff = (TextView) findViewById(R.id.arrayStuff);
            nuButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    // aan viewtekst
                    arrayStuff.setText(String.valueOf(dataKopie[0]));
                }
            });


         startloop = (Button)findViewById(R.id.startloop);
         startloop.setOnClickListener(new View.OnClickListener() {


                                          @Override
                                          public void onClick(View view) {


                                              int i = 0;
                                              while (i < 10) {
                                                 GetServerData.performClick();
                                                  i++;
                                              }

                                          }
                                      });



            stuurButton = (Button)findViewById(R.id.stuurButton);
            stuurButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    // Launching create new product activity
                    //Intent i = new Intent(getApplicationContext(), Gyroscope.class);
                    //startActivity(i);
                    //finish();
                }
            });

            sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

            displayReading = (TextView) findViewById(R.id.display_reading);

        //timer!!///////////////////////////////////////////////////////////////////////////////////
        //het idee was door middel van de timer gegevens van de sensoren voor een bepaalde tijd door
        // te sturen. Hier zijn we niet verder meegegaan door tijd tekort
            timerTextView = (TextView) findViewById(R.id.timerTextView);

            Button b = (Button) findViewById(R.id.button);
            b.setText("start");
            b.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Button b = (Button) v;
                    if (b.getText().equals("stop")) {
                        timerHandler.removeCallbacks(timerRunnable);
                        b.setText("start");
                    } else {
                        startTime = System.currentTimeMillis();
                        timerHandler.postDelayed(timerRunnable, 0);
                        b.setText("stop");
                    }
                }
            });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity_ar, menu);
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

    //ACCELERATORsensor!!///////////////////////////////////////////////////////////////////////////
        public void onAccuracyChanged(Sensor sensor, int accuracy){

        }

        @Override
        public void onSensorChanged(SensorEvent event) {

            //variabelen van waardes maken
            a = event.values[0];
            b = event.values[1];
            c = event.values[2];

            buttonclick = b;
            //array vullen met waardes
            dataKopie[0] = a;
            dataKopie[1] = b;
            dataKopie[2] = c;

            displayReading.setText("X waarde " + a + "\nY waarde " + b + "\nZ waarde " + c);
            //displayReading.setText("X waarde "+event.values[0]+ "\nY waarde "+event.values[1]+ "\nZ waarde "+event.values[2]);
        }

}
