package com.bruinfitness.android;

/**
 * {@link Workout} represents a type of workout that the user wants to learn.
 * It contains resource IDs for the default translation, Miwok translation, audio file, and
 * optional image file for that word.
 */

public class Workout {

    /** String type of workout i.e. crossfit, gymnastics, weightlifting */
    private String mWorkoutType;

    /** Name of the workout i.e. Diane, Murph */
    private String mName;

    /** Explains the goal of the workout */
    private String mGoal;

    /** Workout contents */
    private String mDescription;

    /**
     * Create a new Word object.
     *
     * @param workoutType type of workout i.e. crossfit, gymnastics, weightlifting
     * @param name Name of the workout i.e. Diane, Murph
     * @param goal Explains the goal of the workout
     * @param description Workout contents
     */
    public Workout(String workoutType, String name, String goal, String description) {
        mWorkoutType = workoutType;
        mName = name;
        mGoal = goal;
        mDescription = description;
    }

    /**
     * Get the string workoutType string.
     */
    public String getWorkoutType() {
        return mWorkoutType;
    }

    /**
     * Get the specific name of the workout.
     */
    public String getName() {
        return mName;
    }

    /**
     * Return the goal of the workout.
     */
    public String getGoal() {
        return mGoal;
    }

    /**
     * Return the contents of the workout
     */
    public String getDescription() {
        return mDescription;
    }
}
