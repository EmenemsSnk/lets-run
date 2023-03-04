package com.example.michadziedzic.letsrunfinal;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class TrainingCreatorActivity  extends Activity {
    CalendarView calendarView;
    EditText editText;
    int selectedYear;
    int selectedMonth;
    int selectedDay;
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_creator);
        Calendar calendar = Calendar.getInstance();
        selectedYear = calendar.get(Calendar.YEAR);
        selectedMonth = calendar.get(Calendar.MONTH);
        selectedDay = calendar.get(Calendar.DAY_OF_MONTH);
        editText = (EditText)findViewById(R.id.editText);
        calendarView = (CalendarView)findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                selectedYear = year;
                selectedMonth = month;
                selectedDay = dayOfMonth;
            }
        });
    }
    public void save(View view) {
        if(!editText.getText().toString().equals("")) {
            Calendar begin = Calendar.getInstance();
            begin.set(selectedYear, selectedMonth, selectedDay, 0, 0, 0);
            Calendar end = Calendar.getInstance();
            end.set(selectedYear, selectedMonth, selectedDay, 23, 59, 59);

            Intent intent = new Intent(Intent.ACTION_EDIT);
            intent.setType("vnd.android.cursor.item/event");
            intent.putExtra("beginTime", begin.getTimeInMillis());
            intent.putExtra("allDay", true);
            intent.putExtra("rrule", "FREQ=WEEKLY");
            intent.putExtra("endTime", end.getTimeInMillis());
            intent.putExtra("title", editText.getText().toString());
            startActivity(intent);
        }else{
            Toast.makeText(getApplicationContext(), "Please enter the name", Toast.LENGTH_SHORT).show();
        }
    }
}
