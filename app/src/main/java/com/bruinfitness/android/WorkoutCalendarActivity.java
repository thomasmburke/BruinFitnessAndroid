package com.bruinfitness.android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class WorkoutCalendarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new WorkoutCalendarFragment())
                .commit();
    }
}
