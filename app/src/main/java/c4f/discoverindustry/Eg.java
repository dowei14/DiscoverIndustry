package c4f.discoverindustry;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
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

    private TextView mClockView;
    private ImageButton nextPhase;
    private ImageButton nextGroup;
    private TextView tvCurrentPhase;
    private TextView tvCurrentGroup;
    private TextView tvGroupName;
    private LinearLayout linearLayoutGroup;
    private TimePicker timePicker;
    private TextView tvTMinus;

    SimpleDateFormat endTime;
    Date end;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eg);
        setAmbientEnabled();

        //ToDo: Hide Format
        TextView tvFormatName = (TextView) findViewById(R.id.format_name);
        TextView tvCurrentFormat = (TextView) findViewById(R.id.current_format);
        TextView tvStageName = (TextView) findViewById(R.id.tv_stage_name);
        mClockView = (TextView) findViewById(R.id.clock);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        nextPhase = (ImageButton) findViewById(R.id.button_next_state);
        nextGroup = (ImageButton) findViewById(R.id.button_switch_groups);
        tvCurrentPhase = (TextView) findViewById(R.id.tv_current_stage);
        tvCurrentGroup = (TextView) findViewById(R.id.tv_current_group);
        tvGroupName = (TextView) findViewById(R.id.tv_group_name);
        linearLayoutGroup = (LinearLayout) findViewById(R.id.linear_layout_group);
        tvTMinus = (TextView) findViewById(R.id.tv_t_minus);

        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));

        mClockView.setVisibility(View.GONE);
        linearLayoutGroup.setVisibility(View.GONE);
        nextGroup.setVisibility(View.GONE);
        tvStageName.setVisibility(View.GONE);
        tvCurrentFormat.setVisibility(View.GONE);
        tvFormatName.setVisibility(View.GONE);
        tvTMinus.setVisibility(View.GONE);


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
                else currentPhase = 0;
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
        mClockView.setText(AMBIENT_DATE_FORMAT.format(new Date()));
        if (end != null) tvTMinus.setText(getTMinus());
        switch (currentPhase){
            case SETUP:
                currentGroup = 1;
                tvTMinus.setVisibility(View.GONE);
                tvCurrentPhase.setText(getString(R.string.stage_setup));
                timePicker.setVisibility(View.VISIBLE);
                nextPhase.setImageResource(R.drawable.ic_play_arrow_black_36dp);
                break;
            case EINFUEHRUNG:
                tvTMinus.setVisibility(View.VISIBLE);
                nextPhase.setImageResource(R.drawable.ic_skip_next_black_36dp);
                timePicker.setVisibility(View.GONE);
                tvCurrentPhase.setText(getString(R.string.stage_intro));
                break;
            case GRUPPENPHASE:
                linearLayoutGroup.setVisibility(View.VISIBLE);
                nextGroup.setVisibility(View.VISIBLE);
                tvCurrentPhase.setText(getString(R.string.stage_group));
                tvCurrentPhase.setVisibility(View.GONE);
                tvCurrentGroup.setText(String.valueOf(currentGroup));
                break;
            case ABSCHLUSS:
                nextGroup.setVisibility(View.GONE);
                tvCurrentPhase.setVisibility(View.VISIBLE);
                tvCurrentPhase.setText(getString(R.string.stage_closing));
                linearLayoutGroup.setVisibility(View.GONE);
                break;
            default:
                throw new RuntimeException("unknown phase");
        }
        if (isAmbient()) {
            //mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));
            //mTextView.setTextColor(getResources().getColor(android.R.color.white));
            //mClockView.setVisibility(View.VISIBLE);

            //mClockView.setText(AMBIENT_DATE_FORMAT.format(new Date()));
        } else {
            //mContainerView.setBackground(null);
            //mTextView.setTextColor(getResources().getColor(android.R.color.black));
            //mClockView.setVisibility(View.GONE);
        }
    }

    private String getTMinus(){
        Date current = Calendar.getInstance().getTime();
        Long tMinus = end.getTime() - current.getTime();
        tMinus = TimeUnit.MILLISECONDS.toMinutes(tMinus);
        String returnString = "T-" + String.valueOf(tMinus);
        return returnString;
    }
}
