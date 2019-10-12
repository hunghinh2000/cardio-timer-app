package com.hungjoker.timer.presenter;

import android.content.Context;
import android.os.Handler;

import com.hungjoker.timer.model.WorkoutTimer;
import com.hungjoker.timer.view.CountdownActivity;
import com.hungjoker.timer.view.View_CountdownTimer;

public class PresenterImp_CountdownTimer implements PresenterLogic_CountdownTimer {
    private View_CountdownTimer viewHomeTimer;

    private WorkoutTimer workoutTimer;
    private CountdownActivity.Mode mode = CountdownActivity.Mode.Practice;
    private long currPracticeDuration, currWorkDuration, currRestDuration, timeRemaining;
    private int currSet, totalSets;
    private Context context;
    private Handler practiceHandler = new Handler(), workHandler = new Handler(), restHandler = new Handler();
    private Runnable practiceRunnable, workRunnable, restRunnable;

    public PresenterImp_CountdownTimer(View_CountdownTimer viewHomeTimer,long practiceTime,
                                       long workTime, long restTime,
                                       CountdownActivity.TimerState timerState, int totalRound,
                                       Context context){
        this.viewHomeTimer = viewHomeTimer;
        workoutTimer = new WorkoutTimer(practiceTime, workTime, restTime, timerState, totalRound, context);

        currPracticeDuration = practiceTime;
        currWorkDuration = workTime;
        currRestDuration = restTime;
        timeRemaining = workTime;
        this.totalSets = totalRound;
        this.context = context;

        initRunnable();

    }

    @Override
    public void startCountdownTimer(int round) {
        workoutTimer.setTimerState(CountdownActivity.TimerState.Running, context);

        currSet = round;

        if(mode == CountdownActivity.Mode.Practice){
            timeRemaining = currPracticeDuration;
            practiceRunnable.run();
        }
        else if(mode == CountdownActivity.Mode.Work) {
            timeRemaining = currWorkDuration;
            workRunnable.run();
        }
        else if(mode == CountdownActivity.Mode.Rest) {
            timeRemaining = currRestDuration;
            restRunnable.run();
        }

    }

    @Override
    public void pauseCountdownTimer() {
        if(mode == CountdownActivity.Mode.Practice){
            currPracticeDuration = timeRemaining;
            practiceHandler.removeCallbacks(practiceRunnable);
        }
        else if(mode == CountdownActivity.Mode.Work) {
            currWorkDuration = timeRemaining;
            workHandler.removeCallbacks(workRunnable);
        }
        else {
            currRestDuration = timeRemaining;
            restHandler.removeCallbacks(restRunnable);
        }

        workoutTimer.setTimerState(CountdownActivity.TimerState.Pause, context);

        viewHomeTimer.onTimerPause();
    }
    @Override
    public void stopCountdownTimer() {
        workHandler.removeCallbacks(workRunnable);
        restHandler.removeCallbacks(restRunnable);
        practiceHandler.removeCallbacks(practiceRunnable);

        currPracticeDuration = workoutTimer.getPracticeTime(context);
        workoutTimer.setTimerState(CountdownActivity.TimerState.Stopped, context);

        mode = CountdownActivity.Mode.Practice;
        viewHomeTimer.onSwitchMode(mode, currSet);
        viewHomeTimer.onTimerStop();
    }

    @Override
    public void pauseTimerActivity() {
        if(workoutTimer.getTimerState(context) == CountdownActivity.TimerState.Pause ) {
            workHandler.removeCallbacks(workRunnable);
            restHandler.removeCallbacks(restRunnable);
            practiceHandler.removeCallbacks(practiceRunnable);

            if(mode == CountdownActivity.Mode.Practice){
                currPracticeDuration = timeRemaining;
            }
            else if(mode == CountdownActivity.Mode.Work){
                currWorkDuration = timeRemaining;
            }
            else {
                currRestDuration = timeRemaining;
            }

            viewHomeTimer.onTimerPause();
        } else if(workoutTimer.getTimerState(context) == CountdownActivity.TimerState.Stopped){
            workHandler.removeCallbacks(workRunnable);
            restHandler.removeCallbacks(restRunnable);
            practiceHandler.removeCallbacks(practiceRunnable);

            currPracticeDuration = workoutTimer.getPracticeTime(context);
            viewHomeTimer.onTimerStop();
        }

    }

    public void initRunnable(){
        practiceRunnable = new Runnable() {
            @Override
            public void run() {
                if(timeRemaining>0){
                    timeRemaining-=1000;
                    viewHomeTimer.onTimerPlay(timeRemaining, workoutTimer.getPracticeTime(context), mode);
                    practiceHandler.postDelayed(practiceRunnable, 1000);
                }else{
                    currWorkDuration = workoutTimer.getWorkTime(context);
                    mode = CountdownActivity.Mode.Work;
                    timeRemaining = currWorkDuration + 1000;
                    viewHomeTimer.onSwitchMode(mode, currSet);

                    workRunnable.run();
                }
            }
        };
        workRunnable = new Runnable() {
            @Override
            public void run() {
                if(timeRemaining>0){
                    timeRemaining-=1000;
                    viewHomeTimer.onTimerPlay(timeRemaining, workoutTimer.getWorkTime(context), mode);
                    workHandler.postDelayed(workRunnable, 1000);
                }else{
                    if(currSet == totalSets){
                        workHandler.removeCallbacks(workRunnable);
                        restHandler.removeCallbacks(restRunnable);
                        currWorkDuration = workoutTimer.getWorkTime(context);

                        mode = CountdownActivity.Mode.Over;

                        viewHomeTimer.onTimerFinish();
                    } else {
                        currRestDuration = workoutTimer.getRestTime(context);
                        mode = CountdownActivity.Mode.Rest;
                        timeRemaining = workoutTimer.getRestTime(context) + 1000;
                        viewHomeTimer.onSwitchMode(mode, currSet);

                        restRunnable.run();
                    }
                }
            }
        };
        restRunnable = new Runnable() {
            @Override
            public void run() {
                if(timeRemaining>0){
                    timeRemaining-=1000;
                    viewHomeTimer.onTimerPlay(timeRemaining, workoutTimer.getRestTime(context), mode);
                    restHandler.postDelayed(restRunnable, 1000);
                }else{
                    currSet++;
                    currWorkDuration = workoutTimer.getWorkTime(context);
                    mode = CountdownActivity.Mode.Work;
                    timeRemaining = workoutTimer.getWorkTime(context) + 1000;
                    viewHomeTimer.onSwitchMode(mode, currSet);
                    workRunnable.run();
                }
            }
        };
    }
}
