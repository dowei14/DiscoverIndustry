package c4f.discoverindustry;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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

    SimpleDateFormat startTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eg);
        setAmbientEnabled();

        mClockView = (TextView) findViewById(R.id.clock);

        nextPhase = (ImageButton) findViewById(R.id.button_next_state);
        nextGroup = (ImageButton) findViewById(R.id.button_switch_groups);
        tvCurrentPhase = (TextView) findViewById(R.id.tv_current_stage);
        tvCurrentGroup = (TextView) findViewById(R.id.tv_current_group);
        tvGroupName = (TextView) findViewById(R.id.tv_group_name);
        linearLayoutGroup = (LinearLayout) findViewById(R.id.linear_layout_group);


        nextPhase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPhase == SETUP){
                    Date c = Calendar.getInstance().getTime();
                    startTime = new SimpleDateFormat("HH:mm:ss");
                    Log.e("HERE",startTime.format(c));
                    //TODO: set starting time UI, show T-XX in Minutes
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
        linearLayoutGroup.setVisibility(View.GONE);
        nextGroup.setVisibility(View.GONE);

        switch (currentPhase){
            case SETUP:
                tvCurrentPhase.setText(getString(R.string.stage_setup));
                break;
            case EINFUEHRUNG:
                tvCurrentPhase.setText(getString(R.string.stage_intro));
                break;
            case GRUPPENPHASE:
                linearLayoutGroup.setVisibility(View.VISIBLE);
                nextGroup.setVisibility(View.VISIBLE);
                tvCurrentPhase.setText(getString(R.string.stage_group));
                tvCurrentGroup.setText(String.valueOf(currentGroup));
                break;
            case ABSCHLUSS:
                tvCurrentPhase.setText(getString(R.string.stage_closing));
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
}
