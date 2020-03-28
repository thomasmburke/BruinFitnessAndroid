package com.bruinfitness.android.ui.workoutcalendar;

/**
 * {@link Workout} represents a type of workout that the user wants to learn.
 * It contains resource IDs for the default translation, Miwok translation, audio file, and
 * optional image file for that word.
 */

public class Workout {

    /** String type of workout i.e. crossfit, gymnastics, weightlifting */
    private String workoutType;

    /** Name of the workout i.e. Diane, Murph */
    private String name;

    /** Explains the goal of the workout */
    private String goal;

    /** Workout contents */
    private String description;

    /** State of the item */
    private boolean mExpanded;

    /**
     * Create a new Workout object.
     *
     * @param workoutType type of workout i.e. crossfit, gymnastics, weightlifting
     * @param name Name of the workout i.e. Diane, Murph
     * @param goal Explains the goal of the workout
     * @param description Workout contents
     */
    public Workout(String workoutType, String name, String goal, String description) {
        this.workoutType = workoutType;
        this.name = name;
        this.goal = goal;
        this.description = description;
    }

    public Workout(String name, String goal, String description) {
        this.name = name;
        this.goal = goal;
        this.description = description;
    }


    public Workout(){
        //public no-arg constructor needed
    }



    /**
     * Get the string workoutType string.
     */
    public String getWorkoutType() {
        return this.workoutType;
    }

    /**
     * Get the specific name of the workout.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Return the goal of the workout.
     */
    public String getGoal() {
        return this.goal;
    }

    /**
     * Return the contents of the workout
     */
    public String getDescription() {
        return this.description;
    }

    public void setExpanded(boolean expanded) {
        this.mExpanded = expanded;
    }

    public boolean isExpanded() {
        return mExpanded;
    }

    public void setWorkoutType(String workoutType){
        this.workoutType = workoutType;
    }
    public void setGoal(String goal){
        this.goal = goal;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setDescription(String description){
        this.description = description;
    }

}
