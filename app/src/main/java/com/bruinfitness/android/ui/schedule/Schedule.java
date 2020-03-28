package com.bruinfitness.android.ui.schedule;

public class Schedule {

    private String cityName;
    private String day;
    private String time;

    public Schedule(){
        //public no-arg constructor needed
    }

    /**
     * Create a new Schedule object.
     *
     * @param cityName name of gym location i.e. Boston, New York
     * @param day day workout is on
     * @param time time of the workout
     */
    public Schedule(String cityName, String time, String day) {
        this.cityName = cityName;
        this.day = day;
        this.time = time;
    }

    public Schedule(String time, String day) {
        this.day = day;
        this.time = time;
    }

    /**
     * Get the string cityName string.
     */
    public String getCityName() {
        return this.cityName;
    }

    /**
     * Get the specific day of the workout.
     */
    public String getDay() {
        return this.day;
    }

    /**
     * Return the time of the workout.
     */
    public String getTime() {
        return this.time;
    }

    public void setCityName(String cityName){
        this.cityName = cityName;
    }
    public void setDay(String day){
        this.day = day;
    }
    public void setTime(String time){
        this.time = time;
    }
}
