package com.bruinfitness.android.ui.schedule;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bruinfitness.android.R;
import com.bruinfitness.android.ui.workoutcalendar.RecAdapter;
import com.bruinfitness.android.ui.workoutcalendar.Workout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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

    public ScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_schedule, container, false);

        // Setup Recycler View
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        ScheduleRecAdapter scheduleRecAdapter = scheduleTypes.get("allSchedules");
        // Check if we have already retrieved today's schedule, if so no need to hit the DB
        if (scheduleRecAdapter == null) {
            getFirestoreSchedules(recyclerView);
        } else {
            recyclerView.setAdapter(scheduleRecAdapter);
        }

        return rootView;
    }

    public void getFirestoreSchedules(RecyclerView recyclerView){

        /** Query specific Firestore collection **/
        firestoreDb
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        // initialize a Schedule list for all types of workouts List
                        List<Schedule> allScheduleList = new ArrayList<>();
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
                                scheduleTypes.put("allSchedules", new ScheduleRecAdapter(allScheduleList));
                                recyclerView.setAdapter(scheduleTypes.get("allSchedules"));
                            } else {
                                Log.d(TAG, "No such document: " + "allSchedules");
                                scheduleTypes.put("allSchedules", new ScheduleRecAdapter(allScheduleList));
                                Log.d(TAG, "Adding " + "allSchedules" + " Recycler View adapter to dateWorkouts Hashmap");
                            }
                        } else {
                            Log.d(TAG, "get document failed with ", task.getException());
                        }
                    }
                });
    }

}
