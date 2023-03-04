package com.example.michadziedzic.letsrunfinal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class ExerciseActivity extends Activity implements AccelerometerListener {
    static final float ALPHA = 0.25f;
    float[] gravity;

    float lastX, lastY, lastZ;
    int tickTimer;
    File file;
    Scanner sc;
    FileWriter fw;
    FileReader fr;
    float[][] measures;
    int measuresCount;
    int sequence;
    PowerManager.WakeLock mWakeLock;

    int overallPoints;
    int points;
    int spree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        mWakeLock.acquire();

        gravity = new float[3];

        tickTimer = 10;
        lastX = 0;
        lastY = 0;
        lastZ = 0;
        measures = new float[3][10];
        measuresCount = 0;
        sequence = 0;

        overallPoints = 0;
        points = 0;
        spree = 0;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.accelerometer_example_main);
        System.out.println("Internal path: "+getFilesDir()); //TEST
            try {
                //create file
                file = new File(getFilesDir(), "log.txt");
                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();
                System.out.println("Creating new file!"); //TEST

                fw = new FileWriter(file);
                fr = new FileReader(file);

            }catch(IOException e){
                e.printStackTrace();
            }

        showMeasures();
        // Check onResume Method to start accelerometer listener
    }

    public void onAccelerationChanged(float x, float y, float z) {
        float[] lowPass = lowPass(new float[]{x, y, z});
        x = lowPass[0];
        y = lowPass[1];
        z = lowPass[2];
        if(tickTimer == 0) {
            //Called when Acceleration Detected
            if (Math.abs(x)- Math.abs(lastX) > 0.2 || Math.abs(y)- Math.abs(lastY) > 0.2 || Math.abs(y)- Math.abs(lastY) > 0.2) {
                lastX = x;
                lastY = y;
                lastZ = z;
                    if(measuresCount < 10) {
                        measures[0][measuresCount] = x;
                        measures[1][measuresCount] = y;
                        measures[2][measuresCount] = z;
                        measuresCount++;
                    }else{
                        if(Math.abs(measures[0][sequence]) - Math.abs(x) < 0.4 && Math.abs(measures[1][sequence]) - Math.abs(y) < 0.4 && Math.abs(measures[2][sequence]) - Math.abs(z) < 0.4){
                            MediaPlayer mp = MediaPlayer.create(this, R.raw.drum1);
                            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    mp.release();
                                }
                            });
                            mp.start();
                            sequence++;
                            if(sequence >= 10){
                                mp = MediaPlayer.create(this, R.raw.drum2);
                                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        mp.release();
                                    }
                                });
                                mp.start();
                                sequence = 0;
                                overallPoints += points;
                                new ConnectionTask(points).execute();
                            }else{
                                spree ++;
                                points += spree;
                            }
                        }else{
                            spree = 0;
                        }
                    }
                    showMeasures();
                    //fw.write(x + " " + y + " " + z);
                    //System.out.println("Saving to file!"); //TEST
            } else {
                if(!(measuresCount < 10)) {
                    //((TextView) findViewById(R.id.accelerometer_query)).setText("Move your phone to start training");
                }
            }
            tickTimer = 10;
        }else{
            tickTimer--;
        }
    }


    public void showMeasures(){
        LinearLayout l = (LinearLayout) findViewById(R.id.dots);
        l.removeAllViews();
        for(int i = 0; i < 10; i++){
            ImageView imageView = new ImageView(this.getApplicationContext());
            //System.out.println(l.getChildCount());
            if(i < sequence) {
                imageView.setImageResource(R.drawable.dot_green);
            }else if(i < measuresCount){
                imageView.setImageResource(R.drawable.dot_yellow);
            }else{
                imageView.setImageResource(R.drawable.dot_red);
            }
            imageView.setAdjustViewBounds(true);
            imageView.setMaxWidth(20);
            imageView.setMaxHeight(20);

            View item = getLayoutInflater().inflate(R.layout.dot, null);
            l.addView(imageView);
        }

        ((TextView) findViewById(R.id.points)).setText(getString(R.string.points) + ": " + overallPoints);
        /*
        String result = "";
        for(int i = 0; i < measures[0].length; i++) {
            if(i < sequence)
                result += "+ ";
            result += measures[0][i] + " " + measures[1][i] + " " + measures[2][i] + "\n";
        }
        ((TextView) findViewById(R.id.accelerometer_query)).setText(result);
        */
    }

    public void restart(View view) {
        tickTimer = 10;
        lastX = 0;
        lastY = 0;
        lastZ = 0;
        measures = new float[3][10];
        measuresCount = 0;
        sequence = 0;
        showMeasures();
    }

    /**public void saveMoves(View view) {
        Intent intent = new Intent(this, TrainingCreatorActivity.class);
        startActivity(intent);
    }*/

    private class ConnectionTask extends AsyncTask<Void, Void, Void> {
        int points;
        public ConnectionTask(int points){
            this.points = points;
        }
        protected Void doInBackground(Void... urls) {
            System.out.println("doInBackground");
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet("http://www.sportanapp.cba.pl/addpoint.php?q="+points+"&u="+getIntent().getStringExtra("nick")+"&p="+getIntent().getStringExtra("password"));
            //HttpGet request = new HttpGet("http://ariadnusia.blog.onet.pl/2007/09/09/jak-zalozyc-licznik-odwiedzin-dziennych-licznik-on-line-itp/");
            try {
                client.execute(request);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void onShake(float force) {
        // Do your stuff here

        // Called when Motion Detected
        //Toast.makeText(getBaseContext(), "Motion detected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        mWakeLock.acquire();
        /*
        Toast.makeText(getBaseContext(), "onResume Accelerometer Started",
                Toast.LENGTH_SHORT).show();
        */
        //Check device supported Accelerometer senssor or not
        if (AccelerometerManager.isSupported(this)) {

            //Start Accelerometer Listening
            AccelerometerManager.startListening(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mWakeLock.release();
        //Check device supported Accelerometer sensor or not
        if (AccelerometerManager.isListening()) {

            //Start Accelerometer Listening
            AccelerometerManager.stopListening();
            /*
            Toast.makeText(getBaseContext(), "onStop Accelerometer Stoped",
                    Toast.LENGTH_SHORT).show();
                    */
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWakeLock.release();
        Log.i("Sensor", "Service destroy");

        //Check device supported Accelerometer sensor or not
        if (AccelerometerManager.isListening()) {

            //Start Accelerometer Listening
            AccelerometerManager.stopListening();
            /*
            Toast.makeText(getBaseContext(), "onDestroy Accelerometer Stopped",
                    Toast.LENGTH_SHORT).show();
                    */
        }
    }
    protected float[] lowPass( float[] input, float[] output ) {
        if ( output == null ) return input;
        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }
    public float[] lowPass(float[] input)
    {
        // alpha is calculated as t / (t + dT)
        // with t, the low-pass filter's time-constant
        // and dT, the event delivery rate
        float[] linear_acceleration = new float[3];

        gravity[0] = ALPHA * gravity[0] + (1 - ALPHA) * input[0];
        gravity[1] = ALPHA * gravity[1] + (1 - ALPHA) * input[1];
        gravity[2] = ALPHA * gravity[2] + (1 - ALPHA) * input[2];

        linear_acceleration[0] = input[0] - gravity[0];
        linear_acceleration[1] = input[1] - gravity[1];
        linear_acceleration[2] = input[2] - gravity[2];

        return linear_acceleration;
    }
}
