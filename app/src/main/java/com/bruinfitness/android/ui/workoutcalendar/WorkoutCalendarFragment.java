package com.bruinfitness.android.ui.workoutcalendar;


import android.content.Context;
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

import com.bruinfitness.android.R;
import com.bruinfitness.android.RecAdapter;
import com.bruinfitness.android.Workout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver;
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager;
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendar;
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter;
import com.michalsvec.singlerowcalendar.selection.CalendarSelectionManager;
import com.michalsvec.singlerowcalendar.utils.DateUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    private static final String TAG = "WorkoutCalendarFragment";
    public SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy_MM_dd");
    private SingleRowCalendar mv_calendar;
    private HashMap<String, RecAdapter> dateWorkouts = new HashMap<String, RecAdapter>();
    // Access a Cloud Firestore instance from your Activity
    private CollectionReference firestoreDb = FirebaseFirestore.getInstance().collection("workouts");


    public WorkoutCalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.i(TAG,"in onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"in onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.workout_calendar_fragment, container, false);

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

        //DELETE ME
        //writeDummyWorkoutsToFirestore("2020_03_16");


        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.WEEK_OF_MONTH, -1);
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.WEEK_OF_MONTH, 1);
        Calendar tmpDate = Calendar.getInstance();
        tmpDate.add(Calendar.WEEK_OF_MONTH, -1);
        Calendar currDate = Calendar.getInstance();
        String currDateString = getDateString(currDate);

        /*
        List<Workout> workoutList2 = new ArrayList<>();
        RecAdapter adapter = new RecAdapter(workoutList2);
        RecyclerView recyclerView = rootView.findViewById(R.id.recview);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
         */


        firestoreDb.document(currDateString).collection("types")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        // initialize a new Workout List
                        List<Workout> workoutList = new ArrayList<>();
                        if(task.getResult().isEmpty()){
                            // No documents under this path
                            Log.w(TAG, "No documents under " + currDateString +" path!");
                            dateWorkouts.put(currDateString, new RecAdapter(workoutList));
                            Log.d(TAG, "Adding " + currDateString + " Recycler View adapter to dateWorkouts Hashmap");
                        }
                        else if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Workout tmpWorkout = document.toObject(Workout.class);
                                tmpWorkout.setWorkoutType(document.getId());
                                workoutList.add(tmpWorkout);
                            }
                            dateWorkouts.put(currDateString, new RecAdapter(workoutList));
                            Log.d(TAG, "Adding " + currDateString + " Recycler View adapter to dateWorkouts Hashmap");

                            RecyclerView recyclerView = rootView.findViewById(R.id.recview);
                            ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.setAdapter(dateWorkouts.get(currDateString));

                            //recyclerView.swapAdapter(dateWorkouts.get(currDateString),false);

                            //adapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        if (dateWorkouts.size() < 14){
                            Log.i(TAG,"Having to fetch the other docs: "+ dateWorkouts.size());
                            //***************TESTING*******************
                            // Iterate through the two weeks of dates
                            while (!getDateString(tmpDate).equals(getDateString(endDate))){
                                String tmpDateString = getDateString(tmpDate);
                                Log.i(TAG, "querying Firestore for: " + tmpDateString + " workouts");
                                if (getDateString(tmpDate).equals(getDateString(currDate))){
                                    tmpDate.add(Calendar.DAY_OF_YEAR, 1);
                                    continue;
                                }
                                getFirestoreWorkouts(tmpDateString);
                                tmpDate.add(Calendar.DAY_OF_YEAR, 1);
                            }
                            //***************TESTING*******************
                        }else{
                            Log.i(TAG,"didnt have to get other docs: "+ dateWorkouts.size());
                        }


                    }
                });


        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG,"in onViewCreated");
        // initialize views
        mv_calendar = view.findViewById(R.id.single_row_calendar);

        setupCalendar();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG,"in onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG,"in onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG,"in onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG,"in onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG,"in onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"in onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG,"in onDetach");
    }

    private void setupCalendar() {
        mv_calendar.setCalendarViewManager(new MyCalendarViewManager());
        mv_calendar.setCalendarChangesObserver(new CalendarChangesObserver() {
            @Override
            public void whenWeekMonthYearChanged(String s, String s1, String s2, String s3, Date date) {

            }

            @Override
            public void whenSelectionChanged(boolean b, int i, Date date) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");
                RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.recview);
                recyclerView.swapAdapter(dateWorkouts.get(dateFormat.format(date)),false);

            }

            @Override
            public void whenCalendarScrolled(int i, int i1) {

            }

            @Override
            public void whenSelectionRestored() {

            }

            @Override
            public void whenSelectionRefreshed() {

            }
        });
        mv_calendar.setCalendarSelectionManager(new CalendarSelectionManager() {
            @Override
            public boolean canBeItemSelected(int i, Date date) {
                return true;
            }
        });
        mv_calendar.setFutureDaysCount(7);
        mv_calendar.setPastDaysCount(7);
        mv_calendar.setIncludeCurrentDate(true);
        mv_calendar.setInitialPositionIndex(5);
        mv_calendar.init();
        mv_calendar.select(7);

    }


    class MyCalendarViewManager implements CalendarViewManager

    {

        @Override
        public void bindDataToCalendarView(SingleRowCalendarAdapter.CalendarViewHolder calendarViewHolder, Date date, int i, boolean b) {
            TextView text_date = calendarViewHolder.itemView.findViewById(R.id.text_date);
            TextView text_day = calendarViewHolder.itemView.findViewById(R.id.text_day);

            DateUtils utils = DateUtils.INSTANCE;

            text_date.setText(utils.getDayNumber(date));
            text_day.setText(utils.getDay3LettersName(date));
        }

        @Override
        public int setCalendarViewResourceId(int i, Date date, boolean b) {

            if (b) return R.layout.calendar_item_selected;

            return R.layout.calendar_item;
        }
    }

    public String getDateString (Calendar date){
        return dateFormatter.format(date.getTime());
    }

    public void getFirestoreWorkouts(String date){
        /** Query specific Firestore collection **/
        firestoreDb.document(date).collection("types")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        // initialize a new Workout List
                        List<Workout> workoutList = new ArrayList<>();
                        if(task.getResult().isEmpty()){
                            // No documents under this path
                            Log.w(TAG, "No documents under " + date +" path!");
                            dateWorkouts.put(date, new RecAdapter(workoutList));
                            Log.d(TAG, "Adding " + date + " Recycler View adapter to dateWorkouts Hashmap");
                        }
                        else if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Workout tmpWorkout = document.toObject(Workout.class);
                                tmpWorkout.setWorkoutType(document.getId());
                                workoutList.add(tmpWorkout);
                            }
                            dateWorkouts.put(date, new RecAdapter(workoutList));
                            Log.d(TAG, "Adding " + date + " Recycler View adapter to dateWorkouts Hashmap");
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
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
