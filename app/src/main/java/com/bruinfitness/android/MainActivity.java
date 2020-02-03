package com.bruinfitness.android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy_MM_dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.WEEK_OF_MONTH, 1);
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.WEEK_OF_MONTH, -1);

        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(7)
                .configure()
                .showTopText(false)
                .textSize(12, 12, 12)
                .end()
                .build();


        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                Toast.makeText(MainActivity.this, getDateString(date), Toast.LENGTH_SHORT).show();
                Log.i(TAG, getDateString(date));
            }

            @Override
            public void onCalendarScroll(HorizontalCalendarView calendarView,
                                         int dx, int dy) {
                //Toast.makeText(MainActivity.this, "calendar scroll", Toast.LENGTH_SHORT).show();

            }

            @Override
            public boolean onDateLongClicked(Calendar date, int position) {
                Toast.makeText(MainActivity.this, "calendar long click!", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    public String getDateString (Calendar date){
        return dateFormatter.format(date.getTime());
    }
}
