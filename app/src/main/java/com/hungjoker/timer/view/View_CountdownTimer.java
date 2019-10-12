package com.hungjoker.timer.view;

public interface View_CountdownTimer {
    public void onSwitchMode(CountdownActivity.Mode mode, int round);
    public void onTimerPlay(long timeRemaining, long duration, CountdownActivity.Mode mode);
    public void onTimerPause();
    public void onTimerStop();
    public void onTimerFinish();
}
