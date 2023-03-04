package com.example.michadziedzic.letsrunfinal;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class MapActivity extends Activity implements OnMapReadyCallback {

    TextView provider, speed, distance, speed2, time;
    java.text.DecimalFormat df=new java.text.DecimalFormat("0.000");
    LocationManager locationManager;
    Location location;
    Location savedLocation;
    //Criteria criteria=new Criteria();
    String providerName;
    double distanceSum=0;
    double speedSr=0;
    int pomocnicza=0;
    boolean isRunning=false;
    boolean wasStopped=true;
    boolean wasPaused=false;
    Timer myTimer;
    int count;
    Handler handler;
    private GoogleMap map;
    private LatLng latLng;
    private boolean pomocnicza2=true;
    private final float ZOOM_LEVEL=15;
    ArrayList arrayList=new ArrayList<>();
    LatLng mapCenter;
    boolean pomocnicza21=true;
    boolean pomocnicza22=false;
    int points=0;

    LocationListener locationListener=new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            showLocation();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_);

        //Getting IDs
        provider=(TextView)findViewById(R.id.provider);
        speed2=(TextView)findViewById(R.id.textView4);
        speed=(TextView)findViewById(R.id.speed);
        distance=(TextView)findViewById(R.id.distance);
        time=(TextView)findViewById(R.id.time);



        locationManager= (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        providerName=locationManager.NETWORK_PROVIDER;
        location = locationManager.getLastKnownLocation(providerName);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            provider.setText("Provider: GPS");
        else {
            provider.setText("GPS Disabled. Please, turn it on");
        }
        handler=new Handler();
        Runnable run=new Runnable(){
            @Override
            public void run(){
                while (true){
                    try{
                        Thread.sleep(1000);

                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            time.setText("Time: "+count/60+" minutes "+count%60+" seconds");
                            map();

                            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                                provider.setText("Provider: GPS");
                            else {
                                provider.setText("GPS Disabled. Please, turn it on");
                                pauza();
                            }
                        }
                    });
                }
            }

        };
        new Thread(run).start();

        map=((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
        //8-6-15
        //MapFragment mapFragment = (MapFragment) getFragmentManager()
        //        .findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);
    }
    public void map(){
        if(pomocnicza22&&isRunning){
            map.addPolyline(new PolylineOptions().addAll(arrayList).width(8).color(Color.RED));
            map.moveCamera(CameraUpdateFactory.newLatLng(mapCenter));
        }
    }
    @Override
    public void onMapReady(GoogleMap map) {

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter, ZOOM_LEVEL));

        map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.s))
                .position(mapCenter)
                .flat(true)
                .rotation(245));

    }





    private void startClock(){
        myTimer=new Timer();
        myTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                count++;
            }
        }, 0, 1000);
    }


    private void showLocation() {

        providerName=locationManager.GPS_PROVIDER;
        location=locationManager.getLastKnownLocation(providerName);

        if (location != null) {
            pomocnicza22=true;
            location.getLatitude();
            location.getLongitude();
            mapCenter = new LatLng(location.getLatitude(), location.getLongitude());
            if(pomocnicza21) {
                MapFragment mapFragment = (MapFragment) getFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
                pomocnicza21=false;
            }


            if(isRunning)
                showResults(location);
            arrayList.add(new LatLng(location.getLatitude(), location.getLongitude()));
        }
    }

    private void showResults(Location location){


        speedSr+=location.getSpeed();
        if(location.getSpeed()>0) {
            pomocnicza++;
        }
        speed.setText("Speed: "+df.format(location.getSpeed())+" m/s");

        if(savedLocation!=null){
            distanceSum+=savedLocation.distanceTo(location);
            distance.setText("Distance: "+df.format(distanceSum/1000)+" km");
            if(pomocnicza!=0)
                speed2.setText("Średnia prędkość: "+df.format(speedSr/pomocnicza)+" m/s");
        }
        savedLocation=location;
    }

    public int ranking(){

        points=(int)((distanceSum/100)+speedSr*(distanceSum/1000));
        return points;
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

    public void stopRun(View view) {
        if(isRunning || wasPaused) {
            myTimer.cancel();
            String stats="End of run!\nTime: "+count/60+" minutes "+count%60+" seconds\nDistance: "+df.format(distanceSum/1000)+" km\nAvarage speed: "+df.format(speedSr)+" m/s";
            Toast t = Toast.makeText(this, stats, Toast.LENGTH_LONG);
            t.show();
            locationManager.removeUpdates(locationListener);
            wasStopped = true;
            isRunning = false;
            wasPaused=false;
            pomocnicza21=true;

            if(!arrayList.isEmpty()) {
                map.addPolyline(new PolylineOptions().addAll(arrayList).width(8).color(Color.RED));
                map.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.k))
                        .position(mapCenter)
                        .flat(true)
                        .rotation(245));
                arrayList.clear();
            }
            if(speedSr<12) {
                new ConnectionTask(ranking()).execute();
            }

        }
    }

    private class ConnectionTask extends AsyncTask<Void, Void, Void> {
        int points;
        public ConnectionTask(int points){
            this.points = points;
        }

        protected Void doInBackground(Void... urls) {
            System.out.println("doInBackground");
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet("http://www.sportanapp.cba.pl/addpoint.php?q="+points+"&u="+getIntent().getStringExtra("nick")+"&p="+getIntent().getStringExtra("password"));
            HttpGet request2 = new HttpGet("http://www.sportanapp.cba.pl/addstats.php?d="+distanceSum+"&p="+speedSr+"&c="+((double)count)/60+"&u="+getIntent().getStringExtra("nick"));
            //HttpGet request = new HttpGet("http://ariadnusia.blog.onet.pl/2007/09/09/jak-zalozyc-licznik-odwiedzin-dziennych-licznik-on-line-itp/");
            try {
                client.execute(request);
                client.execute(request2);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }




    public void pauseRun(View view) {
        pauza();
    }

    private void pauza(){
        if(isRunning) {
            //locationManager.removeUpdates(locationListener);
            Toast t = Toast.makeText(this, "Break!", Toast.LENGTH_SHORT);
            t.show();
            savedLocation=null;
            isRunning = false;
            wasPaused=true;
            myTimer.cancel();
        }
    }

    public void startRun(View view) {

        if(wasStopped){ //zerujemy zmienne w przypadku pierwszego/nowego biegu
            map.clear();
            pomocnicza=0;
            speedSr=0;
            distanceSum=0;
            count=0;
            wasStopped=false;
        }
        if(isRunning==false){
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                savedLocation=null;
                providerName=locationManager.GPS_PROVIDER;
                location = locationManager.getLastKnownLocation(providerName); //last know location
                showLocation();
                locationManager.requestLocationUpdates(providerName, 1000, 1, locationListener);
                isRunning=true;
                wasPaused=false;
                Toast t=Toast.makeText(this, "RUN!", Toast.LENGTH_SHORT);
                t.show();
                startClock();
            }
            else {
                Toast t=Toast.makeText(this, "Turn on GPS to start run", Toast.LENGTH_SHORT);
                t.show();
            }
        }
    }


}