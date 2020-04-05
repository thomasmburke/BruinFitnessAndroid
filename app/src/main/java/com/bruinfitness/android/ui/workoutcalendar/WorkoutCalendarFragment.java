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
import android.widget.TextView;

import com.bruinfitness.android.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver;
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager;
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendar;
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter;
import com.michalsvec.singlerowcalendar.selection.CalendarSelectionManager;
import com.michalsvec.singlerowcalendar.utils.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private long DAY_IN_MS = 1000 * 60 * 60 * 24;
    private ListenerRegistration registration;
    private List<Workout> emptyList = new ArrayList<>();
    private DateUtils dateUtils = DateUtils.INSTANCE;

    public WorkoutCalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.i(TAG,"in onAttach");
    }

    @Override
    public void onStart() {
        super.onStart();
        addFirestoreWorkoutListener();
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
        //writeDummyWorkoutsToFirestore("2020_04_04");

        Calendar currDate = Calendar.getInstance();
        String currDateString = getDateString(currDate);


        RecyclerView recyclerView = rootView.findViewById(R.id.recview);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        RecAdapter recAdapter = dateWorkouts.get(currDateString);
        // Check if we have already retrieved today's workout, if so no need to hit the DB
        if (recAdapter == null) {
            getFirestoreWorkouts(currDateString, recyclerView);
        }else {
            recyclerView.setAdapter(recAdapter);
        }

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
    public void onStop() {
        super.onStop();
        // Stop listening to changes
        if (registration != null) {
            Log.i(TAG,"Detaching Firestore Workout snapshot listener");
            registration.remove();
        }
    }

    private void setupCalendar() {
        mv_calendar.setCalendarViewManager(new MyCalendarViewManager());
        mv_calendar.setCalendarChangesObserver(new CalendarChangesObserver() {
            @Override
            public void whenWeekMonthYearChanged(String s, String s1, String s2, String s3, Date date) {

            }

            @Override
            public void whenSelectionChanged(boolean b, int i, Date date) {
                RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.recview);
                RecAdapter recAdapter = dateWorkouts.get(dateFormatter.format(date));
                // check if adapter exists, if not provide an empty recycler view
                if (recAdapter != null) {
                    recyclerView.swapAdapter(dateWorkouts.get(dateFormatter.format(date)), false);
                } else {
                    recyclerView.swapAdapter(new RecAdapter(emptyList), false);
                }
                // Change date text
                TextView dateTextView = (TextView) getView().findViewById(R.id.date_text);
                TextView dayOfWeekTextView = (TextView) getView().findViewById(R.id.day_of_week_text);
                dateTextView.setText(dateUtils.getMonthName(date) + ", " + dateUtils.getDayNumber(date));
                dayOfWeekTextView.setText(dateUtils.getDayName(date));


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
        HashMap<String, Integer> calendarConfig = gatherCalendarConfigurations();

        mv_calendar.setFutureDaysCount(calendarConfig.get("futureDaysCount"));
        mv_calendar.setPastDaysCount(calendarConfig.get("pastDaysCount"));
        mv_calendar.setIncludeCurrentDate(true);
        mv_calendar.setInitialPositionIndex(calendarConfig.get("initialIndexPosition"));
        mv_calendar.init();
        mv_calendar.select(calendarConfig.get("select"));

    }

    /**
     * Date methods
     */

    public Date atEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public Date atStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public HashMap<String, Integer> gatherCalendarConfigurations(){
        HashMap<String, Integer> calendarConfig = new HashMap<String, Integer>();
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        switch (day) {
            case Calendar.SUNDAY:
                calendarConfig.put("futureDaysCount", 7);
                calendarConfig.put("pastDaysCount", 7);
                calendarConfig.put("initialIndexPosition", 5);
                calendarConfig.put("select", 7);
                break;
            case Calendar.MONDAY:
                calendarConfig.put("futureDaysCount",6);
                calendarConfig.put("pastDaysCount", 8);
                calendarConfig.put("initialIndexPosition", 6);
                calendarConfig.put("select", 8);
                break;
            case Calendar.TUESDAY:
                calendarConfig.put("futureDaysCount",5);
                calendarConfig.put("pastDaysCount", 9);
                calendarConfig.put("initialIndexPosition", 7);
                calendarConfig.put("select", 9);
                break;
            case Calendar.WEDNESDAY:
                calendarConfig.put("futureDaysCount", 4);
                calendarConfig.put("pastDaysCount", 10);
                calendarConfig.put("initialIndexPosition", 8);
                calendarConfig.put("select", 10);
                break;
            case Calendar.THURSDAY:
                calendarConfig.put("futureDaysCount", 3);
                calendarConfig.put("pastDaysCount", 11);
                calendarConfig.put("initialIndexPosition", 9);
                calendarConfig.put("select", 11);
                break;
            case Calendar.FRIDAY:
                calendarConfig.put("futureDaysCount", 2);
                calendarConfig.put("pastDaysCount", 12);
                calendarConfig.put("initialIndexPosition", 10);
                calendarConfig.put("select", 12);
                break;
            case Calendar.SATURDAY:
                calendarConfig.put("futureDaysCount", 1);
                calendarConfig.put("pastDaysCount", 13);
                calendarConfig.put("initialIndexPosition", 11);
                calendarConfig.put("select", 13);
                break;
            default :
                Log.e(TAG, "invalid day of the week");
        }
        return calendarConfig;
    }


    class MyCalendarViewManager implements CalendarViewManager

    {

        @Override
        public void bindDataToCalendarView(SingleRowCalendarAdapter.CalendarViewHolder calendarViewHolder, Date date, int i, boolean b) {
            TextView text_date = calendarViewHolder.itemView.findViewById(R.id.text_date);
            TextView text_day = calendarViewHolder.itemView.findViewById(R.id.text_day);

            text_date.setText(dateUtils.getDayNumber(date));
            text_day.setText(dateUtils.getDay3LettersName(date));
        }

        @Override
        public int setCalendarViewResourceId(int i, Date date, boolean b) {

            if (b) return R.layout.calendar_item_selected;

            return R.layout.calendar_item;
        }
    }

    /**
     * Utility methods
     */

    public String getDateString (Calendar date){
        return dateFormatter.format(date.getTime());
    }


    public List<Workout> orderWorkoutList(List<Workout> workoutList){
        if (workoutList.size() > 0) {
            Collections.sort(workoutList, new Comparator<Workout>() {
                @Override
                public int compare(final Workout object1, final Workout object2) {
                    return object1.getWorkoutType().compareTo(object2.getWorkoutType());
                }
            });
        }
        return workoutList;
    }

    /**
     * Firestore methods
     */

    public void addFirestoreWorkoutListener(){
        Date date = new Date();
        // TODO: These dates assume the timezone the user is in, but it should be the timezone the gym is in or UTC
        Date today = atStartOfDay(new Date(date.getTime()));
        int futureDays = gatherCalendarConfigurations().get("futureDaysCount");
        Date endOfWeek = atEndOfDay(new Date(date.getTime() + (futureDays * DAY_IN_MS)));
        Log.i(TAG,"Attaching Firestore Workout snapshot listener");

        // Add a snapshot listener for today until the end of the week
        // No need to update workouts that haven't been released yet or have already happened
        registration = firestoreDb
                .whereGreaterThanOrEqualTo("date", today)
                .whereLessThanOrEqualTo("date", endOfWeek)
                .limit(7)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        for (QueryDocumentSnapshot document : value) {
                            List<Workout> workoutList = new ArrayList<>();
                            Log.d(TAG, "DocumentSnapshot data: " + document.getId() + " => " + document.getData());
                            // temporarily store document data
                            Map<String, Object> tmpDoc = document.getData();
                            // iterate through each map (WorkoutType) in the document
                            for (Map.Entry<String, Object> entry : tmpDoc.entrySet()) {
                                // Convert each map in the document to a POJO object
                                if (entry.getKey().equals("date")){continue;}
                                Gson gson = new Gson();
                                JsonElement jsonElement = gson.toJsonTree(entry.getValue());
                                Workout workout = gson.fromJson(jsonElement, Workout.class);
                                workout.setWorkoutType(entry.getKey());
                                // Add each workout type to the workout list
                                workoutList.add(workout);
                            }
                            // Note HashMap put acts as both add and overwrite
                            dateWorkouts.put(document.getId(), new RecAdapter(orderWorkoutList(workoutList)));
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        }
                        Log.d(TAG, "Workouts read by snapshot Listener: ");
                    }
                });
    }

    public void getFirestoreWorkouts(String date, RecyclerView recyclerView){

        /** Query specific Firestore collection **/
        firestoreDb.document(date)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        // initialize a new Workout List
                        List<Workout> workoutList = new ArrayList<>();
                        // Check if the query executed successfully
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            // If the document is not empty
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getId() + " => " + document.getData());
                                // temporarily store document data
                                Map<String, Object> tmpDoc = document.getData();
                                // iterate through each map (WorkoutType) in the document
                                for (Map.Entry<String, Object> entry : tmpDoc.entrySet()) {
                                    // Convert each map in the document to a POJO object
                                    if (entry.getKey().equals("date")){continue;}
                                    Gson gson = new Gson();
                                    JsonElement jsonElement = gson.toJsonTree(entry.getValue());
                                    Workout workout = gson.fromJson(jsonElement, Workout.class);
                                    workout.setWorkoutType(entry.getKey());
                                    // Add each workout type to the workout list
                                    workoutList.add(workout);
                                }
                                // Note HashMap put acts as both add and overwrite
                                dateWorkouts.put(date, new RecAdapter(orderWorkoutList(workoutList)));
                                recyclerView.setAdapter(dateWorkouts.get(date));
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                // TODO: add an offline message to the user and check if a document already exists?
                                Log.d(TAG, "No such document: " + date);
                                dateWorkouts.put(date, new RecAdapter(orderWorkoutList(workoutList)));
                                Log.d(TAG, "Adding " + date + " Recycler View adapter to dateWorkouts Hashmap");
                            }
                        } else {
                            Log.d(TAG, "get document failed with ", task.getException());
                        }
                    }
                });
    }



    public void writeDummyWorkoutsToFirestore(String dateString){
        Map<String, Object> docData = new HashMap<>();

        Map<String, Object> nestedDocFields = new HashMap<>();
        nestedDocFields.put("description", "Hello world!");
        nestedDocFields.put("name", "Good Luck");
        nestedDocFields.put("goal", "win gold medals");

        docData.put("Gymnastics", nestedDocFields);
        docData.put("CrossFit", nestedDocFields);
        docData.put("Weightlifting", nestedDocFields);
        Date firestoreDate = null;
        try {
            SimpleDateFormat testFormat = new SimpleDateFormat("yyyy_MM_dd");
            firestoreDate = testFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Date test = new Date();
        docData.put("date", firestoreDate);

        firestoreDb.document(dateString)
                .set(docData)
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
