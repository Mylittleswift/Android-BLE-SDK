package com.example.activity;

import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import android.widget.TextView;
import com.example.blesdk.R;


public class MainActivity extends Activity implements OnClickListener {

	private Button bt_connect, bt_basic_info, bt_history, bt_heartRate,
			bt_heartRate_set, bt_alarm, bt_pillAlarm, bt_activityAlarm,
			bt_deviceInfo, bt_goal, bt_realTimeActivity,bt_motor;
	private TextView tv_steps, tv_cal, tv_distance, tv_time;

	private String deviceAddress;

	private boolean connected, isStartRealTime;
	private TimerTask task;
	private TextView textView_realSteps;
//	private Timer timer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		deviceAddress = getIntent().getStringExtra("address");
		initView();
	}

	


	protected void refreshRealTimeUI(int steps, float cal, float km,
			int activityTime) {
		// TODO Auto-generated method stub
		tv_cal.setText(cal + "");
		tv_distance.setText(km + "");
		tv_time.setText(activityTime + "");
		tv_steps.setText(steps + "");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private void initView() {
	textView_realSteps=(TextView) findViewById(R.id.tv_steps);

		// TODO Auto-generated method stub
		bt_activityAlarm = (Button) findViewById(R.id.button_activity_alarm);
		bt_alarm = (Button) findViewById(R.id.button_alarm);
		bt_basic_info = (Button) findViewById(R.id.button_info);
		bt_connect = (Button) findViewById(R.id.button_connect);
		bt_deviceInfo = (Button) findViewById(R.id.button_device_info);
		bt_goal = (Button) findViewById(R.id.button_goal);
		bt_heartRate = (Button) findViewById(R.id.button_heart);
		bt_heartRate_set = (Button) findViewById(R.id.button_heart_set);
		bt_history = (Button) findViewById(R.id.button_history);
		bt_pillAlarm = (Button) findViewById(R.id.button_pill);
		bt_realTimeActivity = (Button) findViewById(R.id.button_startreal);

		bt_activityAlarm.setOnClickListener(this);
		bt_alarm.setOnClickListener(this);
		bt_basic_info.setOnClickListener(this);
		bt_connect.setOnClickListener(this);
		bt_deviceInfo.setOnClickListener(this);
		bt_goal.setOnClickListener(this);
		bt_heartRate.setOnClickListener(this);
		bt_heartRate_set.setOnClickListener(this);
		bt_history.setOnClickListener(this);
		bt_pillAlarm.setOnClickListener(this);
		bt_realTimeActivity.setOnClickListener(this);

		bt_motor=(Button)findViewById(R.id.button_motor);
		bt_motor.setOnClickListener(this);
		tv_cal = (TextView) findViewById(R.id.textView_cal);
		tv_distance = (TextView) findViewById(R.id.textView_distance);
		// tv_goal=(TextView)findViewById(R.id.textView_goal);
		tv_steps = (TextView) findViewById(R.id.textView_step);
		tv_time = (TextView) findViewById(R.id.textView_time);
	}

	private void enableButton(boolean enable) {
		bt_activityAlarm.setEnabled(enable);
		bt_alarm.setEnabled(enable);
		bt_basic_info.setEnabled(enable);
		bt_deviceInfo.setEnabled(enable);
		bt_goal.setEnabled(enable);
		bt_heartRate.setEnabled(enable);
		bt_heartRate_set.setEnabled(enable);
		bt_history.setEnabled(enable);
		bt_pillAlarm.setEnabled(enable);
		bt_realTimeActivity.setEnabled(enable);
		bt_motor.setEnabled(enable);
		if (!enable) {
			isStartRealTime = false;
			bt_realTimeActivity.setText("Start");
			refreshRealTimeUI(0, 0, 0, 0);
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {

		case R.id.button_connect:
			connectDevice();
			break;

		default:
			break;
		}
	}



	private void connectDevice() {
		// TODO Auto-generated method stub
		bt_connect.setEnabled(false);
	}
}
