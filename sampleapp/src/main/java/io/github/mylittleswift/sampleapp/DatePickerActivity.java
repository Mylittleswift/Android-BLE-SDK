package io.github.mylittleswift.sampleapp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.youhong.oldhealthcare.blesdk.Util;

import java.util.Calendar;

public class DatePickerActivity extends Activity implements View.OnClickListener
        , DatePicker.OnDateChangedListener, TimePicker.OnTimeChangedListener {

    DatePicker dp;
    TimePicker tp;
    Button btn_confirm;
    Button btn_cancel;
    ActionBar actionBar;

    String str_dateTime = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_date_picker);
        //   getActionBar().hide();
    }

    boolean isInitialized = false;

    @Override
    protected void onStart() {

        if (!isInitialized) {
            getViews();
            initializeVariables();
            adjustLayoutParams();
            isInitialized = !isInitialized;
        }
        super.onStart();
    }

    private void getViews() {

        Calendar c = (Calendar) getIntent().getSerializableExtra("DateTime");
        dp = (DatePicker) this.findViewById(R.id.datePicker_dp);
        dp.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), this);
        tp = (TimePicker) this.findViewById(R.id.datePicker_tp);
        tp.setOnTimeChangedListener(this);
        tp.setIs24HourView(true);

        tp.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
        tp.setCurrentMinute(c.get(Calendar.MINUTE));

        btn_confirm = (Button) this.findViewById(R.id.datePicker_btn_confirm);
        btn_confirm.setOnClickListener(this);
        btn_cancel = (Button) this.findViewById(R.id.datePicker_btn_cancel);
        btn_cancel.setOnClickListener(this);

    }

    private void initializeVariables() {

    }

    private void adjustLayoutParams() {


    }

    @Override
    public void onClick(View v) {


        if (v == btn_cancel) {
            this.setResult(Activity.RESULT_CANCELED);
            this.finish();

        } else if (v == btn_confirm) {
            this.setResult(Activity.RESULT_OK);

            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, dp.getYear());
            c.set(Calendar.MONTH, dp.getMonth());
            c.set(Calendar.DAY_OF_MONTH, dp.getDayOfMonth());
            c.set(Calendar.HOUR_OF_DAY, tp.getCurrentHour());
            c.set(Calendar.MINUTE, tp.getCurrentMinute());
            c.set(Calendar.SECOND, 0);

            str_dateTime = Util.FormDateTimeString(c);
            Intent data_intent = new Intent();
            data_intent.putExtra("DateTime", str_dateTime);
            setResult(Activity.RESULT_OK, data_intent);
            this.finish();
        }
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {


    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

    }
}
