package com.bruinfitness.android.ui.schedule;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bruinfitness.android.R;
import com.bruinfitness.android.ui.workoutcalendar.RecAdapter;
import com.bruinfitness.android.ui.workoutcalendar.Workout;

import java.util.List;

public class ScheduleRecAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Schedule> mScheduleList;
    private static int TYPE_HEADER = 1;
    private static int TYPE_ENTRY = 2;

    public ScheduleRecAdapter(List<Schedule> scheduleList) {
        this.mScheduleList = scheduleList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == TYPE_ENTRY) { // for entry layout
            view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.item_schedule, parent, false);
            return new ScheduleRecViewHolder(view);

        } else { // for email layout
            view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.schedule_header, parent, false);
            return new ScheduleHeaderRecViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.isEmpty(mScheduleList.get(position).getWorkoutTypeHeader())) {
            return TYPE_ENTRY;
        } else {
            return TYPE_HEADER;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        final Schedule schedule = mScheduleList.get(position);
        if (getItemViewType(position) == TYPE_ENTRY) {
            ((ScheduleRecViewHolder) viewHolder).bind(schedule);
        } else {
            ((ScheduleHeaderRecViewHolder) viewHolder).bind(schedule);
        }
    }

    @Override
    public int getItemCount() {
        return mScheduleList == null ? 0 : mScheduleList.size();
    }



    public class ScheduleRecViewHolder extends RecyclerView.ViewHolder {

        private TextView txtTime, txtDay;

        public ScheduleRecViewHolder(View itemView) {
            super(itemView);

            txtTime = itemView.findViewById(R.id.item_workout_time);
            txtDay = itemView.findViewById(R.id.item_workout_day);
        }

        private void bind(Schedule schedule) {
            txtTime.setText(schedule.getTime());
            txtDay.setText(schedule.getDay());
        }
    }

    public class ScheduleHeaderRecViewHolder extends RecyclerView.ViewHolder {

        private TextView txtWorkoutTypeHeader;

        public ScheduleHeaderRecViewHolder(View itemView) {
            super(itemView);

            txtWorkoutTypeHeader = itemView.findViewById(R.id.item_workout_type_header);
        }

        private void bind(Schedule schedule) {
            txtWorkoutTypeHeader.setText(schedule.getWorkoutTypeHeader());
        }
    }

}
