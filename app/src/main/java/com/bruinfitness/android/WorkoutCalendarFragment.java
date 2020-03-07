package com.bruinfitness.android;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class WorkoutCalendarFragment extends Fragment {

    private static final String TAG = "MainActivity";
    public SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy_MM_dd");
    // Access a Cloud Firestore instance from your Activity
    // Enable Firestore logging
    private CollectionReference firestoreDb = FirebaseFirestore.getInstance().collection("workouts");

    public WorkoutCalendarFragment() {
        // Required empty public constructor
    }

    /*@Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.recview);
    }

     */


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.workout_calendar_fragment, container, false);

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

        //DELETE ME
        //writeDummyWorkoutsToFirestore("2020_03_06");


        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.WEEK_OF_MONTH, -1);
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.WEEK_OF_MONTH, 1);
        Calendar tmpDate = Calendar.getInstance();
        tmpDate.add(Calendar.WEEK_OF_MONTH, -1);
        Calendar currDate = Calendar.getInstance();


        List<Workout> workoutList2 = new ArrayList<>();
        RecAdapter adapter = new RecAdapter(workoutList2);
        RecyclerView recyclerView = rootView.findViewById(R.id.recview);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        HashMap<String, RecAdapter> dateWorkouts = new HashMap<String, RecAdapter>();

        // Iterate through the two weeks of dates
        while (!getDateString(tmpDate).equals(getDateString(endDate))){
            String tmpDateString = getDateString(tmpDate);
            Log.i(TAG, "querying Firestore for: " + tmpDateString + " workouts");
            /** Query specific Firestore collection **/
            firestoreDb.document(tmpDateString).collection("types")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            // initialize a new Workout List
                            List<Workout> workoutList = new ArrayList<>();
                            if(task.getResult().isEmpty()){
                                // No documents under this path
                                Log.w(TAG, "No documents under" + tmpDateString +" path!");
                                dateWorkouts.put(tmpDateString, new RecAdapter(workoutList));
                                Log.d(TAG, "Adding " + tmpDateString + " Recycler View adapter to dateWorkouts Hashmap");
                            }
                            else if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    Workout tmpWorkout = document.toObject(Workout.class);
                                    tmpWorkout.setWorkoutType(document.getId());
                                    workoutList.add(tmpWorkout);
                                }
                                dateWorkouts.put(tmpDateString, new RecAdapter(workoutList));
                                Log.d(TAG, "Adding " + tmpDateString + " Recycler View adapter to dateWorkouts Hashmap");
                                // If today's date then set recycler view to display that dates workouts
                                if (tmpDateString.equals(getDateString(currDate))){
                                    recyclerView.swapAdapter(dateWorkouts.get(getDateString(currDate)),false);
                                }
                                //adapter.notifyDataSetChanged();
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });

            tmpDate.add(Calendar.DAY_OF_YEAR, 1);
        }
        /**
         * Set up Horizontal Calendar view
         */
        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(rootView, R.id.calendarView)
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
                //Toast.makeText(MainActivity.this, getDateString(date), Toast.LENGTH_SHORT).show();
                recyclerView.swapAdapter(dateWorkouts.get(getDateString(date)),false);
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



        return rootView;
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
