package com.bruinfitness.android.ui.schedule;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bruinfitness.android.R;
import com.bruinfitness.android.ui.workoutcalendar.RecAdapter;
import com.bruinfitness.android.ui.workoutcalendar.Workout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleFragment extends Fragment {
    private static final String TAG = "ScheduleFragment";
    private DocumentReference firestoreDb = FirebaseFirestore.getInstance().collection("schedules").document("Boston");

    public ScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_schedule, container, false);

        Schedule schedule0 = new Schedule("CrossFit");
        Schedule schedule1 = new Schedule("10:00pm - 11:00pm", "Mon-Thurs");
        Schedule schedule2 = new Schedule("7:00pm - 7:30pm", "Mon-Fri");
        Schedule schedule3 = new Schedule("6:00pm - 7:00pm", "Mon-Fri");
        Schedule schedule4 = new Schedule("10:00pm - 11:00pm", "Mon-Thurs");
        Schedule schedule5 = new Schedule("7:00pm - 7:30pm", "Mon-Fri");
        Schedule schedule6 = new Schedule("Weightlifting");
        Schedule schedule7 = new Schedule("10:00pm - 11:00pm", "Mon-Thurs");
        Schedule schedule8 = new Schedule("7:00pm - 7:30pm", "Mon-Fri");
        Schedule schedule9 = new Schedule("6:00pm - 7:00pm", "Mon-Fri");
        Schedule schedule10 = new Schedule("10:00pm - 11:00pm", "Mon-Thurs");
        Schedule schedule11 = new Schedule("7:00pm - 7:30pm", "Mon-Fri");
        Schedule schedule12 = new Schedule("6:00pm - 7:00pm", "Mon-Fri");
        Schedule schedule13 = new Schedule("6:00pm - 7:00pm", "Mon-Fri");

        List<Schedule> scheduleList = new ArrayList<>();
        scheduleList.add(schedule0);
        scheduleList.add(schedule1);
        scheduleList.add(schedule2);
        scheduleList.add(schedule3);
        scheduleList.add(schedule4);
        scheduleList.add(schedule5);
        scheduleList.add(schedule6);
        scheduleList.add(schedule7);
        scheduleList.add(schedule8);
        scheduleList.add(schedule9);
        scheduleList.add(schedule10);
        scheduleList.add(schedule11);
        scheduleList.add(schedule12);
        scheduleList.add(schedule13);
        ScheduleRecAdapter adapter = new ScheduleRecAdapter(scheduleList);



        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);

        /*
        RecyclerView recyclerView2 = rootView.findViewById(R.id.recyclerView2);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView2.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView2.setAdapter(adapter);
        recyclerView2.setNestedScrollingEnabled(false);
         */

        return rootView;
    }


}
