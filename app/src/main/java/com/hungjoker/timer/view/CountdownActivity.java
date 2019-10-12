package com.hungjoker.timer.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import me.itangqi.waveloadingview.WaveLoadingView;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;

import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hungjoker.timer.R;
import com.hungjoker.timer.presenter.PresenterImp_CountdownTimer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CountdownActivity extends AppCompatActivity implements View_CountdownTimer, View.OnClickListener {

    public enum TimerState{
        Stopped, Pause, Running
    }
    public enum Mode{
        Practice, Work, Rest, Over
    }

    private int[] countdownSoundList = new int[5];

    private FloatingActionButton fabPlay, fabPause, fabStop;
    private TextView txtCountdown, txtWorkRest, txtSets;
    private WaveLoadingView wvCountdown;
    private ImageView btnCloseDialog;

    private long workTime, restTime, practiceTime;
    private TimerState timerState = TimerState.Stopped;
    private int currSet = 1, totalSets;

    private PresenterImp_CountdownTimer presenterImpHomeTimer;
    private SoundPool soundPool;
    private AudioManager audioManager;
    private Vibrator vibrator;
    private float volume;
    private int streamId = 0, workSound, restSound;
    private static final int streamType = AudioManager.STREAM_MUSIC;

    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown);

        practiceTime = getIntent().getIntExtra(HomeTimerActivity.PRACTICE_TIME_KEY, 0);
        workTime = getIntent().getIntExtra(HomeTimerActivity.WORK_TIME_KEY, 0);
        restTime = getIntent().getIntExtra(HomeTimerActivity.REST_TIME_KEY, 0);
        totalSets = getIntent().getIntExtra(HomeTimerActivity.SETS_KEY, 0);

        presenterImpHomeTimer = new PresenterImp_CountdownTimer(this, practiceTime, workTime,
                                                            restTime, timerState,
                                                            totalSets,this);

        setupSound();
        initUI();

    }
    @Override
    protected void onPause() {
        presenterImpHomeTimer.pauseTimerActivity();
        super.onPause();
    }
    @Override
    public void onBackPressed() {
        presenterImpHomeTimer.stopCountdownTimer();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.timer_settings, menu);
        return true;
    }

    @Override
    public void onSwitchMode(Mode mode, int currRound) {
        wvCountdown.setProgressValue(100);
        this.currSet = currRound;
        txtSets.setText("Sets " + currRound + " / " + totalSets);
        if(mode == Mode.Practice){
            txtWorkRest.setText("Warmup");
            wvCountdown.setWaveColor(ContextCompat.getColor(this, R.color.practiceWaveColor));
            fabPause.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.practiceWaveColor)));
            fabStop.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.practiceWaveColor)));
            fabPlay.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.practiceWaveColor)));
        }
        else if(mode == Mode.Work) {
            txtWorkRest.setText("Work");
            wvCountdown.setWaveColor(ContextCompat.getColor(this, R.color.workWaveColor));
            fabPause.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this,R.color.workWaveColor)));
            fabStop.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.workWaveColor)));
            fabPlay.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.workWaveColor)));
        } else {
            txtWorkRest.setText("Rest");
            wvCountdown.setWaveColor(ContextCompat.getColor(this, R.color.restWaveColor));
            fabPause.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.restWaveColor)));
            fabStop.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.restWaveColor)));
            fabPlay.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.restWaveColor)));
        }
    }
    @Override
    public void onTimerPlay(long timeRemaining, long duration, Mode mode) {
        if(timeRemaining>0 && timeRemaining<=5000)
            streamId = this.soundPool.play(countdownSoundList[(int)(timeRemaining/1000-1)], volume, volume, 1, 0, 1f);
        else if(timeRemaining==0){
            if(mode == Mode.Practice || mode == Mode.Rest)
                streamId = this.soundPool.play(workSound, volume, volume, 1, 0, 1f);
            else
                streamId = this.soundPool.play(restSound, volume, volume, 1, 0, 1f);

            vibrator.vibrate(1500);
        }


        fabPlay.setEnabled(false);
        fabPause.setEnabled(true);
        fabStop.setEnabled(true);

        txtCountdown.setText(String.valueOf((timeRemaining/1000)));
        wvCountdown.setProgressValue((int) (((float)(timeRemaining)/ (float)(duration)) * 100));
    }
    @Override
    public void onTimerPause() {
        this.soundPool.pause(streamId);

        fabPlay.setEnabled(true);
        fabPause.setEnabled(false);
        fabStop.setEnabled(true);
    }
    @Override
    public void onTimerStop() {
        this.soundPool.stop(streamId);

        currSet = 1;
        wvCountdown.setProgressValue(100);
        txtCountdown.setText(String.valueOf((practiceTime/1000)));
        txtWorkRest.setText("Warmup");
        txtSets.setText("");

        fabPlay.setEnabled(true);
        fabPause.setEnabled(false);
        fabStop.setEnabled(false);
    }

    @Override
    public void onTimerFinish() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_finish);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView txtTotalWorkTime = (TextView) dialog.findViewById(R.id.txtTotalWorkTime);
        TextView txtTotalRestTime = (TextView) dialog.findViewById(R.id.txtTotalRestTime);
        txtTotalWorkTime.setText("Total working time: "
                                    + milisecToString(workTime* totalSets));
        txtTotalRestTime.setText("Total resting time: "
                                    + milisecToString(restTime* totalSets));
        btnCloseDialog = (ImageView) dialog.findViewById(R.id.btnCloseDialog);
        btnCloseDialog.setOnClickListener(this);

        dialog.show();

        this.soundPool.stop(streamType);

        fabPlay.setEnabled(true);
        fabPause.setEnabled(false);
        fabStop.setEnabled(false);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){
            case R.id.fabPlay:
                presenterImpHomeTimer.startCountdownTimer(currSet);
                break;
            case R.id.fabPause:
                presenterImpHomeTimer.pauseCountdownTimer();
                break;
            case R.id.fabStop:
                presenterImpHomeTimer.stopCountdownTimer();
                break;
            case R.id.btnCloseDialog:
                this.finish();
                break;
        }
    }

    public void initUI(){
        fabPause = (FloatingActionButton) findViewById(R.id.fabPause);
        fabPlay = (FloatingActionButton) findViewById(R.id.fabPlay);
        fabStop = (FloatingActionButton) findViewById(R.id.fabStop);
        txtCountdown = (TextView) findViewById(R.id.txtCountdown);
        txtWorkRest = (TextView) findViewById(R.id.txtWorkRest);
        txtSets = (TextView) findViewById(R.id.txtSets);
        wvCountdown = (WaveLoadingView) findViewById(R.id.waveView);

        fabPlay.setEnabled(true);
        fabPause.setEnabled(false);
        fabStop.setEnabled(false);

        wvCountdown.setProgressValue(100);
        wvCountdown.setWaveColor(ContextCompat.getColor(this, R.color.practiceWaveColor));

        txtCountdown.setText(String.valueOf((practiceTime/1000)));
        txtSets.setText("");
        txtCountdown.bringToFront();
        txtWorkRest.bringToFront();
        txtSets.bringToFront();

        fabPlay.setOnClickListener(this);
        fabPause.setOnClickListener(this);
        fabStop.setOnClickListener(this);
    }

    public void setupSound(){
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        float currVolumeIndex = (float) audioManager.getStreamVolume(streamType);
        float maxVolumeIndex = (float) audioManager.getStreamMaxVolume(streamType);

        this.volume = currVolumeIndex/maxVolumeIndex;

        this.setVolumeControlStream(streamType);

        this.soundPool = new SoundPool(10, streamType, 0);

        this.countdownSoundList[0] = this.soundPool.load(this, R.raw.one, 1);
        this.countdownSoundList[1] = this.soundPool.load(this, R.raw.two, 1);
        this.countdownSoundList[2] = this.soundPool.load(this, R.raw.three, 1);
        this.countdownSoundList[3] = this.soundPool.load(this, R.raw.four, 1);
        this.countdownSoundList[4] = this.soundPool.load(this, R.raw.five, 1);
        this.workSound = this.soundPool.load(this, R.raw.work, 1);
        this.restSound = this.soundPool.load(this, R.raw.rest, 1);
    }

    private String milisecToString(long milisec){
        int minutes = (int)(milisec/1000)/60;
        int seconds = (int)(milisec/1000)%60;
        String sMinutes = String.valueOf(minutes);
        String sSeconds = String.valueOf(seconds);
        if(seconds<10)
            sSeconds = "0" + sSeconds;

        return sMinutes + " : " + sSeconds;
    }
}
