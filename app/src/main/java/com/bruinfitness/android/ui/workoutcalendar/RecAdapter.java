package com.bruinfitness.android.ui.workoutcalendar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bruinfitness.android.R;

import java.util.List;

public class RecAdapter extends RecyclerView.Adapter<RecAdapter.RecViewHolder> {

    private List<Workout> mWorkoutList;

    public RecAdapter(List<Workout> workoutList) {
        this.mWorkoutList = workoutList;
    }

    @Override
    public RecViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_workout, parent, false);
        return new RecViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecViewHolder holder, int position) {
        final Workout workout = mWorkoutList.get(position);

        holder.bind(workout);

        holder.itemView.setOnClickListener(v -> {
            boolean expanded = workout.isExpanded();
            workout.setExpanded(!expanded);
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return mWorkoutList == null ? 0 : mWorkoutList.size();
    }

    public class RecViewHolder extends RecyclerView.ViewHolder {

        private TextView workout_type;
        private TextView name;
        private TextView goal;
        private TextView description;
        private ImageView chevron;
        private View subItem;

        public RecViewHolder(View itemView) {
            super(itemView);

            workout_type = itemView.findViewById(R.id.item_workout_type);
            chevron = itemView.findViewById(R.id.workout_card_chevron);
            name = itemView.findViewById(R.id.sub_item_name);
            goal = itemView.findViewById(R.id.sub_item_goal);
            description = itemView.findViewById(R.id.sub_item_description);
            subItem = itemView.findViewById(R.id.sub_item);
        }

        private void bind(Workout workout) {
            boolean expanded = workout.isExpanded();

            subItem.setVisibility(expanded ? View.VISIBLE : View.GONE);
            //chevron.setVisibility(!expanded ? View.VISIBLE : View.GONE);
            chevron.setImageResource(expanded ? R.drawable.baseline_expand_less_white_36: R.drawable.baseline_chevron_down_white_36);

            workout_type.setText(workout.getWorkoutType());
            name.setText("Workout Name: " + workout.getName());
            goal.setText(workout.getGoal());
            description.setText("Description: " + workout.getDescription());
        }
    }


}
