package com.bruinfitness.android.ui.schedule;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bruinfitness.android.R;

import java.util.List;

public class ScheduleHeaderRecAdapter extends RecyclerView.Adapter<ScheduleHeaderRecAdapter.ScheduleHeaderRecViewHolder> {

    private List<String> mWorkoutTypeList;

    public ScheduleHeaderRecAdapter(List<String> workoutTypeList) {
        this.mWorkoutTypeList = workoutTypeList;
    }

    @Override
    public ScheduleHeaderRecViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_schedule_workout_filter, parent, false);
        return new ScheduleHeaderRecViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ScheduleHeaderRecViewHolder holder, int position) {
        final String workoutType = mWorkoutTypeList.get(position);

        holder.bind(workoutType);

        holder.itemView.setOnClickListener(v -> {
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return mWorkoutTypeList == null ? 0 : mWorkoutTypeList.size();
    }

    public class ScheduleHeaderRecViewHolder extends RecyclerView.ViewHolder {

        private TextView workout_type;

        public ScheduleHeaderRecViewHolder(View itemView) {
            super(itemView);
            workout_type = itemView.findViewById(R.id.item_workout_type_filter);
        }

        private void bind(String workoutType) {
            workout_type.setText(workoutType);

        }
    }


}
