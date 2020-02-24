package com.bruinfitness.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Movie;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy_MM_dd");
    // Access a Cloud Firestore instance from your Activity
    private CollectionReference firestoreDb = FirebaseFirestore.getInstance().collection("workouts");
    //private CollectionReference firestoreDb = FirebaseFirestore.getInstance().collection("test");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.WEEK_OF_MONTH, 1);
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.WEEK_OF_MONTH, -1);
        Calendar tmpDate = Calendar.getInstance();
        tmpDate.add(Calendar.WEEK_OF_MONTH, 1);

        while (!getDateString(tmpDate).equals(getDateString(endDate))){
            Log.i(TAG, getDateString(tmpDate));

            tmpDate.add(Calendar.DAY_OF_YEAR, 1);
        }
        Log.i(TAG, "startDate after loop=" + getDateString(startDate));
        Task<QuerySnapshot> workoutsfortheday = firestoreDb.document("2020-02-17").collection("types")
        //Task<QuerySnapshot> workoutsfortheday = firestoreDb
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //Workout workoutObject= task.getResult().getDocuments().get(0).toObject(Workout.class);
                    Log.d(TAG, "test");
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        //deleteme foo = document.toObject(deleteme.class);
                        Workout w1 = document.toObject(Workout.class);
                        //String description = w1.getDescription();
                        Log.d(TAG, "test");
                    }
                    Log.d(TAG, "test");
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });


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
                //Toast.makeText(MainActivity.this, "calendar long click!", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        //Set up sample workout list
        List<Workout> workoutList = new ArrayList<>();
        workoutList.add(new Workout("CrossFit", "Diane", "High-Intensity Training", "21-15-9 reps of: 225-pound Deadlifts"));
        workoutList.add(new Workout("Gymnastics", "No Name", "Technique Building & Flexibility", "5x3 ring muscle ups & handstand walks"));
        workoutList.add(new Workout("Weightlifting", "No Name", "Baseline Strength & Metcon", "Bench, deadlift, squat"));

        RecAdapter adapter = new RecAdapter(workoutList);

        RecyclerView recyclerView = findViewById(R.id.recview);

        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        //BottomNavigationView

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_search:
                        Toast.makeText(MainActivity.this, "Search", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_settings:
                        Toast.makeText(MainActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_navigation:
                        Toast.makeText(MainActivity.this, "Navigation", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });

    }

    public String getDateString (Calendar date){
        return dateFormatter.format(date.getTime());
    }

}
