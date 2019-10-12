package com.hungjoker.timer.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.hungjoker.timer.view.CountdownActivity;

public class WorkoutTimer {
    public static final String TIMER_SHARED_PREFERENCES = "timer_shared_preferences";
    private static final String PRACTICE_TIME = "practice_time";
    private static final String TIMER_STATE = "timer_state";
    private static final String WORK_TIME = "work_time";
    private static final String REST_TIME = "rest_time";
    private static final String TOTAL_SETS = "total_sets";

    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;

    public WorkoutTimer(long practiceTime, long workTime, long restTime, CountdownActivity.TimerState timerState, int totalRound, Context context){
        sharedPreferences = context.getSharedPreferences(TIMER_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        editor.putLong(PRACTICE_TIME, practiceTime);
        editor.putLong(WORK_TIME, workTime);
        editor.putLong(REST_TIME, restTime);
        editor.putInt(TIMER_STATE, timerState.ordinal());
        editor.putInt(TOTAL_SETS, totalRound);
        editor.apply();
    }

    public static long getPracticeTime(Context context){
        sharedPreferences = context.getSharedPreferences(TIMER_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(PRACTICE_TIME, 60*1000);
    }
    public static void setPracticeTime(long practiceTime, Context context){
        sharedPreferences = context.getSharedPreferences(TIMER_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putLong(PRACTICE_TIME, practiceTime);
        editor.apply();
    }

    public static long getWorkTime(Context context) {
        sharedPreferences = context.getSharedPreferences(TIMER_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(WORK_TIME, 30*1000);
    }
    public static void setWorkTime(long workTime, Context context) {
        sharedPreferences = context.getSharedPreferences(TIMER_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putLong(WORK_TIME, workTime);
        editor.apply();
    }

    public static long getRestTime(Context context) {
        sharedPreferences = context.getSharedPreferences(TIMER_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(REST_TIME, 30*1000);
    }
    public static void setRestTime(long restTime, Context context) {
        sharedPreferences = context.getSharedPreferences(TIMER_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putLong(REST_TIME, restTime);
        editor.apply();
    }

    public static CountdownActivity.TimerState getTimerState(Context context) {
        sharedPreferences = context.getSharedPreferences(TIMER_SHARED_PREFERENCES, Context.MODE_PRIVATE);

        return CountdownActivity.TimerState.values()[sharedPreferences.getInt(TIMER_STATE, 0)];
    }
    public static void setTimerState(CountdownActivity.TimerState timerState, Context context) {
        sharedPreferences = context.getSharedPreferences(TIMER_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        int index = timerState.ordinal();
        editor.putInt(TIMER_STATE, index);
        editor.apply();
    }

    public static int getTotalSets(Context context){
        sharedPreferences = context.getSharedPreferences(TIMER_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(TOTAL_SETS, 4);
    }
    public static void setTotalSets(int totalSets, Context context){
        sharedPreferences = context.getSharedPreferences(TIMER_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt(TOTAL_SETS, totalSets);
        editor.apply();
    }
}
