package com.bruinfitness.android.ui.schedule;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bruinfitness.android.R;
import com.bruinfitness.android.ui.workoutcalendar.RecAdapter;
import com.bruinfitness.android.ui.workoutcalendar.Workout;

import java.util.List;

public class ScheduleRecAdapter extends RecyclerView.Adapter<ScheduleRecAdapter.ScheduleRecViewHolder> {

    private List<Schedule> mScheduleList;

    public ScheduleRecAdapter(List<Schedule> scheduleList) {
        this.mScheduleList = scheduleList;
    }

    @Override
    public ScheduleRecViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_schedule, parent, false);
        return new ScheduleRecViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ScheduleRecViewHolder holder, int position) {
        final Schedule schedule = mScheduleList.get(position);

        holder.bind(schedule);
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

}
