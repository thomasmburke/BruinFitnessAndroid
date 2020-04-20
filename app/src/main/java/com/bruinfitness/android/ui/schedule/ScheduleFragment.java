package com.bruinfitness.android.ui.schedule;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bruinfitness.android.MainActivity;
import com.bruinfitness.android.R;
import com.bruinfitness.android.ui.workoutcalendar.RecAdapter;
import com.bruinfitness.android.ui.workoutcalendar.Workout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleFragment extends Fragment {
    private static final String TAG = "ScheduleFragment";
    private DocumentReference firestoreDb = FirebaseFirestore.getInstance().collection("schedules").document("Boston");
    private HashMap<String, ScheduleRecAdapter> scheduleTypes = new HashMap<String, ScheduleRecAdapter>();
    private ListenerRegistration scheduleRegistration;
    private List<String> scheduleFilters = new ArrayList<>();

    public ScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Add Firestore Schedule Listener
        addFirestoreScheduleListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_schedule, container, false);

        // Setup Recycler View for workout schedule filters
        RecyclerView filterWorkoutScheduleRecyclerView = rootView.findViewById(R.id.filterWorkoutScheduleRecyclerView);
        ((SimpleItemAnimator) filterWorkoutScheduleRecyclerView.getItemAnimator()).setSupportsChangeAnimations(true);
        filterWorkoutScheduleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        filterWorkoutScheduleRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.HORIZONTAL));
        filterWorkoutScheduleRecyclerView.setHasFixedSize(true);


        // Setup Recycler View for workout schedule list
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setHasFixedSize(true);

        ScheduleRecAdapter scheduleRecAdapter = scheduleTypes.get("All");
        // Check if we have already retrieved today's schedule, if so no need to hit the DB
        if (scheduleRecAdapter == null) {
            getFirestoreSchedules(recyclerView, filterWorkoutScheduleRecyclerView);
        } else {
            recyclerView.setAdapter(scheduleRecAdapter);
            // Set schedule filter adapter for its recycler view
            ScheduleHeaderRecAdapter scheduleHeaderRecAdapter = new ScheduleHeaderRecAdapter(scheduleFilters, getContext(), recyclerView, scheduleTypes);
            filterWorkoutScheduleRecyclerView.setAdapter(scheduleHeaderRecAdapter);
        }

        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (scheduleRegistration != null) {
            Log.i(TAG,"Detaching Schedule Firestore snapshot listener");
            scheduleRegistration.remove();
        }

    }

    public void addFirestoreScheduleListener(){
        Log.i(TAG,"Attaching Schedule Firestore snapshot listener");
        scheduleRegistration = firestoreDb.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot document,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                if (document != null && document.exists()) {
                    List<Schedule> allScheduleList = new ArrayList<>();
                    Log.d(TAG, "Current data: " + document.getData());
                    // temporarily store document data
                    Map<String, Object> tmpDoc = document.getData();
                    Log.d(TAG, "DocumentSnapshot data: " + document.getId() + " => " + document.getData());
                    // iterate through each map (WorkoutType) in the document
                    for (Map.Entry<String, Object> entry : tmpDoc.entrySet()) {
                        // Cast schedule list Object into a list
                        List workoutTypeScheduleList = (List) entry.getValue();
                        // Add the workout type header schedule for recyclerview
                        Schedule scheduleHeader = new Schedule(entry.getKey());

                        allScheduleList.add(scheduleHeader);

                        // Creating recycler adapters for each tab in the fragment
                        List<Schedule> workoutTypeTabScheduleList = new ArrayList<>();
                        workoutTypeTabScheduleList.add(scheduleHeader);

                        // Iterate through list of schedules
                        for (Object scheduleEntryObject : workoutTypeScheduleList) {
                            // Cast schedule hashmap object into hashmap
                            HashMap<String, String> scheduleEntry = (HashMap<String, String>) scheduleEntryObject;
                            Gson gson = new Gson();
                            JsonElement jsonElement = gson.toJsonTree(scheduleEntry);
                            Schedule schedule = gson.fromJson(jsonElement, Schedule.class);
                            allScheduleList.add(schedule);
                            workoutTypeTabScheduleList.add(schedule);
                        }
                        // Creating recycler adapters for each tab in the fragment
                        scheduleTypes.put(entry.getKey(), new ScheduleRecAdapter(workoutTypeTabScheduleList));
                    }
                    // Note HashMap put acts as both add and overwrite
                    scheduleTypes.put("All", new ScheduleRecAdapter(allScheduleList));
                    scheduleTypes.get("All").notifyDataSetChanged();

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }

    public void getFirestoreSchedules(RecyclerView recyclerView, RecyclerView filterWorkoutScheduleRecyclerView){

        /** Query specific Firestore collection **/
        firestoreDb
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        // initialize a Schedule list for all types of workouts List
                        List<Schedule> allScheduleList = new ArrayList<>();
                        // Add schedule header to list
                        scheduleFilters.add("All");
                        // Check if the query executed successfully
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            // If the document is not empty
                            if (document.exists()) {
                                // temporarily store document data
                                Map<String, Object> tmpDoc = document.getData();
                                Log.d(TAG, "DocumentSnapshot data: " + document.getId() + " => " + document.getData());
                                // iterate through each map (WorkoutType) in the document
                                for (Map.Entry<String, Object> entry : tmpDoc.entrySet()) {
                                    // Cast schedule list Object into a list
                                    List workoutTypeScheduleList = (List) entry.getValue();
                                    // Add the workout type header schedule for recyclerview
                                    Schedule scheduleHeader = new Schedule(entry.getKey());
                                    // Add schedule header to list
                                    scheduleFilters.add(entry.getKey());
                                    allScheduleList.add(scheduleHeader);

                                    // Creating recycler adapters for each tab in the fragment
                                    List<Schedule> workoutTypeTabScheduleList = new ArrayList<>();
                                    workoutTypeTabScheduleList.add(scheduleHeader);

                                    // Iterate through list of schedules
                                    for (Object scheduleEntryObject : workoutTypeScheduleList) {
                                        // Cast schedule hashmap object into hashmap
                                        HashMap<String, String> scheduleEntry = (HashMap<String, String>) scheduleEntryObject;
                                        Gson gson = new Gson();
                                        JsonElement jsonElement = gson.toJsonTree(scheduleEntry);
                                        Schedule schedule = gson.fromJson(jsonElement, Schedule.class);
                                        allScheduleList.add(schedule);
                                        workoutTypeTabScheduleList.add(schedule);
                                    }
                                    // Creating recycler adapters for each tab in the fragment
                                    scheduleTypes.put(entry.getKey(), new ScheduleRecAdapter(workoutTypeTabScheduleList));
                                }
                                // Note HashMap put acts as both add and overwrite
                                scheduleTypes.put("All", new ScheduleRecAdapter(allScheduleList));
                                recyclerView.setAdapter(scheduleTypes.get("All"));
                                // Set schedule filter adapter for its recycler view
                                ScheduleHeaderRecAdapter scheduleHeaderRecAdapter = new ScheduleHeaderRecAdapter(scheduleFilters, getContext(), recyclerView, scheduleTypes);
                                filterWorkoutScheduleRecyclerView.setAdapter(scheduleHeaderRecAdapter);
                            } else {
                                Log.d(TAG, "No such document: " + "All");
                                scheduleTypes.put("All", new ScheduleRecAdapter(allScheduleList));
                                Log.d(TAG, "Adding " + "All" + " Recycler View adapter to dateWorkouts Hashmap");
                            }
                        } else {
                            Log.d(TAG, "get document failed with ", task.getException());
                        }
                    }
                });
    }

}
