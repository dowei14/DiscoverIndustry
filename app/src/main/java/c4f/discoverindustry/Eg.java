package c4f.discoverindustry;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Eg extends WearableActivity {

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT = new SimpleDateFormat("HH:mm", Locale.GERMAN);

    private static final int SETUP = 0;
    private static final int EINFUEHRUNG = 1;
    private static final int GRUPPENPHASE = 2;
    private static final int ABSCHLUSS = 3;

    private static final int MAX_PHASES = 3;
    private static final int MAX_GROUPS = 5;

    private int currentGroup = 1;
    private int currentPhase = 0;

    private BoxInsetLayout mContainer;
    private TextView mClockView;
    private ImageButton nextPhase;
    private ImageButton nextGroup;
    private TextView tvCurrentPhase;
    private TextView tvGroupName;
    private TextView tvCurrentGroup;
    private LinearLayout linearLayoutGroup;
    private TimePicker timePicker;
    private TextView tvTMinus;

    private SimpleDateFormat endTime;
    private Date end;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eg);
        setAmbientEnabled();

        mContainer = findViewById(R.id.container);
        mClockView = findViewById(R.id.clock);
        timePicker = findViewById(R.id.timePicker);
        nextPhase = findViewById(R.id.button_next_state);
        nextGroup = findViewById(R.id.button_switch_groups);
        tvCurrentPhase = findViewById(R.id.tv_current_stage);
        tvCurrentGroup = findViewById(R.id.tv_current_group);
        linearLayoutGroup = findViewById(R.id.linear_layout_group);
        tvTMinus = findViewById(R.id.tv_t_minus);
        tvGroupName = findViewById(R.id.tv_group_name);

        timePicker.setIs24HourView(true);
        timePicker.setHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));

        mClockView.setVisibility(View.GONE);
        linearLayoutGroup.setVisibility(View.GONE);
        nextGroup.setVisibility(View.GONE);
        tvTMinus.setVisibility(View.GONE);

        mContainer.setBackgroundColor(ContextCompat.getColor(this,android.R.color.black));
        tvCurrentPhase.setTextColor(ContextCompat.getColor(this,android.R.color.white));
        tvCurrentGroup.setTextColor(ContextCompat.getColor(this,android.R.color.white));
        tvGroupName.setTextColor(ContextCompat.getColor(this,android.R.color.white));
        mClockView.setTextColor(ContextCompat.getColor(this,android.R.color.white));
        tvTMinus.setTextColor(ContextCompat.getColor(this,android.R.color.white));

        endTime = new SimpleDateFormat("HH:mm:ss");

        nextPhase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPhase == SETUP){
                    end = new Date();
                    end.setHours(timePicker.getHour());
                    end.setMinutes(timePicker.getMinute());
                }
                if (currentPhase < MAX_PHASES) currentPhase += 1;
                else {
                    currentPhase = 0;
                    timePicker.setHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
                    timePicker.setMinute(Calendar.getInstance().get(Calendar.MINUTE));
                }
                updateDisplay();
            }
        });

        nextGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentGroup < MAX_GROUPS) currentGroup += 1;
                else currentGroup = 1;
                updateDisplay();
            }
        });
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        nextPhase.setVisibility(View.VISIBLE);
        tvCurrentPhase.setVisibility(View.VISIBLE);
        mClockView.setText(AMBIENT_DATE_FORMAT.format(new Date()));
        if (end != null) tvTMinus.setText(getTMinus());
        switch (currentPhase){
            case SETUP:
                currentGroup = 1;
                tvTMinus.setVisibility(View.GONE);
                mClockView.setVisibility(View.GONE);
                tvCurrentPhase.setText(getString(R.string.stage_setup));
                timePicker.setVisibility(View.VISIBLE);
                nextPhase.setImageResource(R.drawable.ic_play_arrow_black_36dp);
                break;
            case EINFUEHRUNG:
                tvTMinus.setVisibility(View.VISIBLE);
                mClockView.setVisibility(View.VISIBLE);
                nextPhase.setImageResource(R.drawable.ic_skip_next_black_36dp);
                timePicker.setVisibility(View.GONE);
                tvCurrentPhase.setText(getString(R.string.stage_intro));
                break;
            case GRUPPENPHASE:
                linearLayoutGroup.setVisibility(View.VISIBLE);
                mClockView.setVisibility(View.VISIBLE);
                nextGroup.setVisibility(View.VISIBLE);
                tvCurrentPhase.setText(getString(R.string.stage_group));
                tvCurrentPhase.setVisibility(View.GONE);
                tvCurrentGroup.setText(String.valueOf(currentGroup));
                break;
            case ABSCHLUSS:
                nextGroup.setVisibility(View.GONE);
                mClockView.setVisibility(View.VISIBLE);
                tvCurrentPhase.setVisibility(View.VISIBLE);
                tvCurrentPhase.setText(getString(R.string.stage_closing));
                linearLayoutGroup.setVisibility(View.GONE);
                break;
            default:
                throw new RuntimeException("unknown phase");
        }
        if (isAmbient()) {
            nextPhase.setVisibility(View.GONE);
            nextGroup.setVisibility(View.GONE);
            timePicker.setVisibility(View.GONE);
            mClockView.setVisibility(View.VISIBLE);
            if (currentPhase == SETUP) {
                //tvCurrentPhase.setVisibility(View.INVISIBLE);
                tvCurrentPhase.setText(getString(R.string.app_name));

            }
            mClockView.setTextSize(60);
            tvTMinus.setTextSize(40);
            //mContainer.setBackgroundColor(ContextCompat.getColor(this,android.R.color.black));
            //tvCurrentPhase.setTextColor(ContextCompat.getColor(this,android.R.color.white));
           // tvCurrentGroup.setTextColor(ContextCompat.getColor(this,android.R.color.white));
            //tvGroupName.setTextColor(ContextCompat.getColor(this,android.R.color.white));
            //mClockView.setTextColor(ContextCompat.getColor(this,android.R.color.white));
            //tvTMinus.setTextColor(ContextCompat.getColor(this,android.R.color.white));
        } else {
            mClockView.setTextSize(30);
            tvTMinus.setTextSize(30);
            //mContainer.setBackground(null);
            //tvCurrentPhase.setTextColor(ContextCompat.getColor(this,android.R.color.black));
            //tvCurrentGroup.setTextColor(ContextCompat.getColor(this,android.R.color.black));
            //tvGroupName.setTextColor(ContextCompat.getColor(this,android.R.color.black));
            //mClockView.setTextColor(ContextCompat.getColor(this,android.R.color.black));
            //tvTMinus.setTextColor(ContextCompat.getColor(this,android.R.color.black));
        }
    }

    private String getTMinus(){
        Date current = Calendar.getInstance().getTime();
        Long tMinus = end.getTime() - current.getTime();
        tMinus = TimeUnit.MILLISECONDS.toMinutes(tMinus);
        return "T" + '\u2014' + String.valueOf(tMinus);
    }
}
