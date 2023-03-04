package com.example.michadziedzic.letsrunfinal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class UserManualActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manual);

        TextView header = (TextView) findViewById(R.id.textView6);
        TextView content = (TextView) findViewById(R.id.textView7);
        Button previous = (Button) findViewById(R.id.button6);
        Button next = (Button) findViewById(R.id.button5);

        final int page = (int)(getIntent().getIntExtra("page", 1));

        previous.setText("Previous");
        previous.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                System.out.println("previous");
                Intent i = new Intent(getApplicationContext(), UserManualActivity.class);
                i.putExtra("page", (page-1));
                startActivity(i);
            }
        });

        next.setText("Next");
        next.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                System.out.println("next");
                Intent i = new Intent(getApplicationContext(), UserManualActivity.class);
                i.putExtra("page", (page+1));
                startActivity(i);
            }
        });

        if(page == 1){
            previous.setEnabled(false);
        }
        if(page == 5){
            next.setEnabled(false);
        }

        switch(page){
            case 1:
                header.setText("Part 1: User manual");
                content.setText("Welcome to Sportan application's user manual! Here you can learn how to use it and get the maximum motivation to your daily trainings.");
                break;
            case 2:
                header.setText("Part 2: Registration and logging in");
                content.setText("To get the access to application's features, please login on the main menu screen. If you haven't your account yet, please register on our website: www.sportanapp.cba.pl");
                break;
            case 3:
                header.setText("Part 3: Training modules");
                content.setText("It's your choice - you can build up your upper body parts with our movement recognition module, or measure your running performance.");
                break;
            case 4:
                header.setText("Part 4: Running");
                content.setText("Press start to start your run! If you press pause you can take a look on map to see your trace. Press stop to end run and send your results ");
                break;
            case 5:
                header.setText("Part 5: Exercising");
                content.setText("Press restart to start saving your movement. If you save 10 moves you can start training.");
                break;
        }


    }
   @Override
    public void onBackPressed() {
       //Intent i = new Intent(getApplicationContext(), MainActivity.class);
       // startActivity(i);
        finish();
    }
    @Override
    public void onPause() {
        super.onPause();
        //Intent i = new Intent(getApplicationContext(), MainActivity.class);
        // startActivity(i);
        finish();
    }
}
