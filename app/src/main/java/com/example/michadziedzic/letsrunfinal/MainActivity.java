package com.example.michadziedzic.letsrunfinal;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class MainActivity extends Activity {
    EditText nick;
    EditText password;
    int x=0;
    public int i=1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nick = (EditText) findViewById(R.id.nick);
        password = (EditText) findViewById(R.id.password);
        Button button1 = (Button) findViewById(R.id.button3);
        button1.setText("Exercise");
        nick.setOnKeyListener(new EditText.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //if(keyCode == KEYCODE_ENTER)
                return false;
            }
        });
        password.setOnKeyListener(new EditText.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                return false;
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //new ConnectionTask().execute();
                System.out.println(i);
                if (!nick.getText().toString().equals("") && !password.getText().toString().equals("")&&i==1) {
                    Intent i = new Intent(getApplicationContext(), ExerciseActivity.class);
                    i.putExtra("nick", nick.getText().toString());
                    i.putExtra("password", password.getText().toString());
                    startActivity(i);
                }
                System.out.println(i);
            }
        });
        Button button2 = (Button) findViewById(R.id.button4);
        button2.setText("Run");
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //new ConnectionTask().execute();
                if (!nick.getText().toString().equals("") && !password.getText().toString().equals("")&&i==1) {
                    Intent i = new Intent(getApplicationContext(), MapActivity.class);
                    i.putExtra("nick", nick.getText().toString());
                    i.putExtra("password", password.getText().toString());
                    startActivity(i);
                }
            }
        });
        Button button3 = (Button) findViewById(R.id.button7);
        button3.setText("User manual");
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), UserManualActivity.class);
                startActivity(i);
            }
        });
    }
/**
    private class ConnectionTask extends AsyncTask<Void, Void, Void> {
        String nick, password;
        public ConnectionTask(){}
        public ConnectionTask(String nick, String password){
            this.nick=nick;
            this.password=password;
        }

        protected Void doInBackground(Void... urls) {
            System.out.println("doInBackground");

            i=logowanie();
            return null;
        }
    }



    public int logowanie(){

        HttpGet request = new HttpGet("http://www.sportanapp.cba.pl/checkLogin.php?p="+password.getText().toString()+"&u="+nick.getText().toString());
        HttpClient client = new DefaultHttpClient();
        HttpResponse response=null;

        try{
            response = client.execute(request);

        }
        catch (IOException e){
            e.printStackTrace();
        }
        HttpEntity entity = response.getEntity();
        if(response==null){
            return 0;
        }
        try {
            int y = Integer.parseInt(EntityUtils.toString(entity));
            return y;
        }
        catch(Exception e){

        }
        return 0;
    }
 */


    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        // Get the Camera instance as the activity achieves full user focus
        x=0;
    }
    @Override
    public void onBackPressed(){
            if(x==0){
                x++;
                Toast.makeText(this, "Press back one more time to exit", Toast.LENGTH_SHORT).show();
            }
            else {
                System.exit(0);
            }
    }

    public void goCalendar(View view) {
        Intent intent = new Intent(this, TrainingCreatorActivity.class);
        startActivity(intent);
    }
}
