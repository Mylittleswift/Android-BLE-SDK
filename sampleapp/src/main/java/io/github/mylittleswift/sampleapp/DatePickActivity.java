package io.github.mylittleswift.sampleapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.youhong.oldhealthcare.blesdk.Util;

import java.util.Calendar;

public class DatePickActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    EditText et1;
    EditText et2;
    Button btn_confirm;
    Button btn_cancel;

    Calendar start_c = Calendar.getInstance();
    Calendar end_c = Calendar.getInstance();

    private static final int REQUEST_PICKER = 0x55;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_pick);
    }

    boolean isInitialized = false;

    @Override
    protected void onStart() {
        super.onStart();

        if (!isInitialized) {
            isInitialized = !isInitialized;
            getViews();
            initializeVariables();
        }

    }

    private void initializeVariables() {
        this.et1.setText(Util.FormDateTimeString(start_c));
        this.et2.setText(Util.FormDateTimeString(end_c));
    }

    private void getViews() {
        this.et1 = (EditText) findViewById(R.id.datePick_et1);
        et1.setOnClickListener(this);
        et1.setOnFocusChangeListener(this);
        this.et2 = (EditText) findViewById(R.id.datePick_et2);
        et2.setOnClickListener(this);
        et2.setOnFocusChangeListener(this);

        this.btn_cancel = (Button) findViewById(R.id.datePicker_btn_cancel);
        btn_cancel.setOnClickListener(this);
        this.btn_confirm = (Button) findViewById(R.id.datePicker_btn_confirm);
        btn_confirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v == et1) {
            isStart = true;
            Intent intent = new Intent(this, DatePickerActivity.class);
            intent.putExtra("DateTime", start_c);
            startActivityForResult(intent, REQUEST_PICKER);
        } else if (v == et2) {
            isStart = false;
            Intent intent = new Intent(this, DatePickerActivity.class);
            intent.putExtra("DateTime", end_c);
            startActivityForResult(intent, REQUEST_PICKER);
        } else if (v == btn_confirm) {

            Intent return_intent = new Intent();
            setResult(RESULT_OK, return_intent);
            return_intent.putExtra("Start_Date", start_c);
            return_intent.putExtra("End_Date", end_c);
            this.finish();

        } else if (v == btn_cancel) {

            Intent return_intent = new Intent();
            setResult(Activity.RESULT_CANCELED);
            this.finish();
        }
    }

    boolean isStart = true;

    @Override
    public void onFocusChange(View v, boolean hasFocus) {


        if (v == et1) {

            isStart = true;
        } else {
            isStart = false;


        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_PICKER) {

            if (isStart) {
                start_c = Util.analyzeDateString(data.getStringExtra("DateTime"));
                this.et1.setText(Util.FormDateTimeString(start_c));

            } else {
                end_c = Util.analyzeDateString(data.getStringExtra("DateTime"));
                this.et2.setText(Util.FormDateTimeString(end_c));
            }
        }
    }
}
