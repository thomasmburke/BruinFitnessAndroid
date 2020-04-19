package com.bruinfitness.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.bruinfitness.android.ui.schedule.ScheduleFragment;
import com.bruinfitness.android.ui.workoutcalendar.WorkoutCalendarFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String TAG_WORKOUT_CALENDAR = "WorkoutCalendarFragment";
    private static final String TAG_SCHEDULE = "ScheduleFragment";
    private static final String TAG_FRAGMENT_TRIPS = "tag_frag_trips";
    private List<Fragment> fragments = new ArrayList<>(3);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.bottom_navigation);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        /*
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_workoutcalendar, R.id.navigation_schedule, R.id.navigation_workoutcalendar2)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
         */

        //BottomNavigationView
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_workoutcalendar:
                        switchFragment(0, TAG_WORKOUT_CALENDAR);
                        break;
                    case R.id.navigation_schedule:
                        switchFragment(1, TAG_SCHEDULE);
                        break;
                    case R.id.navigation_workoutcalendar2:
                        switchFragment(2, TAG_FRAGMENT_TRIPS);
                        break;
                }
                return true;
            }
        });

        buildFragmentsList();

        // Set the 0th Fragment to be displayed by default.
        switchFragment(0, TAG_WORKOUT_CALENDAR);



    }

    private void switchFragment(int pos, String tag) {


        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .replace(R.id.frame_fragmentholder, fragments.get(pos), tag)
                // Ensure that onDestroyView is the last lifecycle method called not onDestroy, this ensures onCreate is only called once
                .addToBackStack(TAG)
                .commit();
    }


    private void buildFragmentsList() {
        WorkoutCalendarFragment callsFragment = new WorkoutCalendarFragment();
        ScheduleFragment scheduleFragment = new ScheduleFragment();
        ScheduleFragment tripsFragment = new ScheduleFragment();

        fragments.add(callsFragment);
        fragments.add(scheduleFragment);
        fragments.add(tripsFragment);
    }
}
