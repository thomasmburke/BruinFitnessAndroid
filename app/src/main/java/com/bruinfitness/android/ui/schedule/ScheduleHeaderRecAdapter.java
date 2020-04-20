package com.bruinfitness.android.ui.schedule;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bruinfitness.android.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.graphics.Typeface.BOLD;


public class ScheduleHeaderRecAdapter extends RecyclerView.Adapter<ScheduleHeaderRecAdapter.ScheduleHeaderRecViewHolder> {

    private List<String> mWorkoutTypeList;
    private final Context mContext;
    private int mPrevPosition = 0;
    private RecyclerView mRecyclerView;
    private HashMap<String, ScheduleRecAdapter> mScheduleTypes;

    public void setmPrevPosition(int prevPosition){
        this.mPrevPosition = prevPosition;
    }

    public int getmPrevPosition(){
        return this.mPrevPosition;
    }

    public ScheduleHeaderRecAdapter(List<String> workoutTypeList, Context context, RecyclerView recyclerView, HashMap<String, ScheduleRecAdapter> scheduleTypes) {
        this.mWorkoutTypeList = workoutTypeList;
        this.mContext = context;
        this.mRecyclerView = recyclerView;
        this.mScheduleTypes = scheduleTypes;
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
        if(getmPrevPosition() == position){
            holder.workout_type.setTextColor(ResourcesCompat.getColor(mContext.getResources(), R.color.bruinGreen, null));
            holder.workout_type.setTypeface(null, BOLD);
        }else{
            holder.workout_type.setTextColor(ResourcesCompat.getColor(mContext.getResources(), R.color.white, null));
            holder.workout_type.setTypeface(null, Typeface.NORMAL);
        }


        holder.itemView.setOnClickListener(v -> {
            notifyItemChanged(getmPrevPosition());
            setmPrevPosition(position);
            notifyItemChanged(position);
            ScheduleRecAdapter tmpAdapter = mScheduleTypes.get(holder.workout_type.getText().toString());
            mRecyclerView.swapAdapter(tmpAdapter, true);
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

           // workout_type.setTextColor(ResourcesCompat.getColor(mContext.getResources(), R.color.white, null));

        }
    }


}
