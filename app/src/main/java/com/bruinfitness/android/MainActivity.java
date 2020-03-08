package com.bruinfitness.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Movie;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy_MM_dd");
    // Access a Cloud Firestore instance from your Activity
    // Enable Firestore logging
    private CollectionReference firestoreDb = FirebaseFirestore.getInstance().collection("workouts");

    // DELETE FOR FRAGMENTS
    private List<Fragment> fragments = new ArrayList<>(3);
    private static final String TAG_FRAGMENT_CALLS = "tag_frag_calls";
    private static final String TAG_FRAGMENT_RECENTS = "tag_frag_recents";
    private static final String TAG_FRAGMENT_TRIPS = "tag_frag_trips";
    private int currTab = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //BottomNavigationView
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_search:
                        //Toast.makeText(MainActivity.this, "Search", Toast.LENGTH_SHORT).show();
                        switchFragment(0, TAG_FRAGMENT_CALLS);
                        break;
                    case R.id.action_settings:
                        //Toast.makeText(MainActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                        /*Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        startActivity(intent);
                         */
                        switchFragment(1, TAG_FRAGMENT_RECENTS);
                        break;
                    case R.id.action_navigation:
                        //Toast.makeText(MainActivity.this, "Navigation", Toast.LENGTH_SHORT).show();
                        switchFragment(2, TAG_FRAGMENT_TRIPS);
                        break;
                }
                return true;
            }
        });

        buildFragmentsList();

        // Set the 0th Fragment to be displayed by default.
        switchFragment(0, TAG_FRAGMENT_CALLS);

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
        test recentsFragment = new test();
        WorkoutCalendarFragment tripsFragment = new WorkoutCalendarFragment();

        fragments.add(callsFragment);
        fragments.add(recentsFragment);
        fragments.add(tripsFragment);
    }


    public String getDateString (Calendar date){
        return dateFormatter.format(date.getTime());
    }

    public void writeDummyWorkoutsToFirestore(String dateString){
        Map<String, Object> nestedDocFields = new HashMap<>();
        nestedDocFields.put("description", "Hello world!");
        nestedDocFields.put("name", "Good Luck");
        nestedDocFields.put("goal", "win gold medals");

        ArrayList<String> types = new ArrayList<String>();
        types.add("CrossFit");
        types.add("Gymnastics");
        types.add("WeightLifting");

        for (String type : types) {

        firestoreDb.document(dateString).collection("types").document(type)
                .set(nestedDocFields)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
        }
    }

}
