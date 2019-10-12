package com.hungjoker.timer.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import me.itangqi.waveloadingview.WaveLoadingView;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hungjoker.timer.R;
import com.hungjoker.timer.model.WorkoutTimer;
import com.shawnlin.numberpicker.NumberPicker;

public class HomeTimerActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener, View.OnClickListener {

    public static final String PRACTICE_TIME_KEY = "practice_time";
    public static final String WORK_TIME_KEY = "work_time";
    public static final String REST_TIME_KEY = "rest_time";
    public static final String SETS_KEY = "set";
    public static final int REQUEST_VIBRATE_CODE = 1111;

    private long practiceTime, workTime, restTime;
    private int totalSets;

    private NumberPicker npPractice, npWorkTime, npRestTime, npSets;
    private RelativeLayout rlOptions;
    private TextView txtTotal, txtTitle, txtPractice;
    private Button btnConfirm;
    private WaveLoadingView wvHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_timer);

        practiceTime = WorkoutTimer.getPracticeTime(this);
        workTime = WorkoutTimer.getWorkTime(this);
        restTime = WorkoutTimer.getRestTime(this);
        totalSets = WorkoutTimer.getTotalSets(this);

        initUi();

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.VIBRATE}, REQUEST_VIBRATE_CODE);
            return;
        }
    }

    void initUi(){
        npWorkTime = (NumberPicker) findViewById(R.id.npWorkTime);
        npRestTime = (NumberPicker) findViewById(R.id.npRestTime);
        npSets = (NumberPicker) findViewById(R.id.npSets);
        npPractice = (NumberPicker) findViewById(R.id.npPractice);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtTotal = (TextView) findViewById(R.id.txtTotal);
        txtPractice = (TextView) findViewById(R.id.txtPractice);
        btnConfirm = (Button) findViewById(R.id.btnConfirm);
        rlOptions = (RelativeLayout) findViewById(R.id.rlOptions);
        wvHome = (WaveLoadingView) findViewById(R.id.wvHome);

        rlOptions.bringToFront();
        txtTitle.bringToFront();

        npWorkTime.setTypeface(ResourcesCompat.getFont(this, R.font.main_font));
        npRestTime.setTypeface(ResourcesCompat.getFont(this, R.font.main_font));
        npSets.setTypeface(ResourcesCompat.getFont(this, R.font.main_font));
        npPractice.setTypeface(ResourcesCompat.getFont(this, R.font.main_font));

        npPractice.setMinValue(0);
        npPractice.setMaxValue(120);
        npPractice.setValue((int)(practiceTime/5000));
        npPractice.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.format("%02d", value*5);
            }
        });

        npWorkTime.setMinValue(1);
        npWorkTime.setMaxValue(120);
        npWorkTime.setValue((int)(workTime/5000));
        npWorkTime.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.format("%02d", value*5);
            }
        });

        npRestTime.setMinValue(0);
        npRestTime.setMaxValue(120);
        npRestTime.setValue((int)(restTime/5000));
        npRestTime.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.format("%02d", value*5);
            }
        });

        npSets.setValue(totalSets);

        npPractice.setOnValueChangedListener(this);
        npWorkTime.setOnValueChangedListener(this);
        npRestTime.setOnValueChangedListener(this);
        npSets.setOnValueChangedListener(this);
        btnConfirm.setOnClickListener(this);

        int total = (int) (practiceTime + (workTime + restTime)* totalSets)/1000;
        txtTotal.setText(String.format("%d : %02d", total/60, total%60));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_VIBRATE_CODE){

        }
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        int practiceTime = npPractice.getValue()*5;
        int workTime = npWorkTime.getValue()*5;
        int restTime = npRestTime.getValue()*5;
        int round = npSets.getValue();
        int total = practiceTime + (workTime + restTime)*round;

        String res = String.format("%d : %02d", total/60, total%60);
        txtTotal.setText(res);
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){
            case R.id.btnConfirm:
                Intent intent = new Intent(this, CountdownActivity.class);

                intent.putExtra(PRACTICE_TIME_KEY, npPractice.getValue()*5000);
                intent.putExtra(WORK_TIME_KEY, npWorkTime.getValue()*5000);
                intent.putExtra(REST_TIME_KEY, npRestTime.getValue()*5000);
                intent.putExtra(SETS_KEY, npSets.getValue());

                ActivityOptions transition  = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    transition = ActivityOptions.makeSceneTransitionAnimation(this,
                                                    Pair.create(txtPractice, "txtPracticeTransition"),
                                                    Pair.create(txtTotal, "txtTotalTransition"));
                }

                startActivity(intent, transition.toBundle());
                break;
        }
    }
}
