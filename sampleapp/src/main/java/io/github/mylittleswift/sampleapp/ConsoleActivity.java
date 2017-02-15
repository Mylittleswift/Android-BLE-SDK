package io.github.mylittleswift.sampleapp;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import io.github.mylittleswift.blesdk.BleOperation;
import com.youhong.oldhealthcare.blesdk.Util;

import java.util.Calendar;

public class ConsoleActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tv_enableRT;
    TextView tv_disableRT;
    TextView tv_enableST;
    TextView tv_disableST;
    TextView tv_readST;
    TextView tv_reset;
    TextView tv_ota;
    TextView tv_readData;
    TextView tv_prompt;
    TextView tv_clear;

    TextView tv_setBCName;
    TextView tv_getBCName;
    TextView tv_setId;
    TextView tv_getId;
    TextView tv_getVersion;
    TextView tv_setTime;
    TextView tv_getTime;

    /**
     * Constants
     **/
    public static final int REQUEST_DATE_PICK = 0x44;


    BleOperation.ResultNotifyCallback connect_callback = new BleOperation.ResultNotifyCallback() {
        @Override
        public void onResultNotify(boolean result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isConnected = true;
                    tv_prompt.setText("connected" + " \n");

                }
            });

        }
    };

    BleOperation.ResultNotifyCallback enable_RT_callback = new BleOperation.ResultNotifyCallback() {
        @Override
        public void onResultNotify(boolean result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    tv_prompt.setText(tv_prompt.getText() + "开启实时温度反馈成功\n");

                }
            });

        }
    };

    BleOperation.RealTimeTemperatureReceivedCallback received_RT_callback = new BleOperation.RealTimeTemperatureReceivedCallback() {
        @Override
        public void onRealTimeTemperatureReceived(final float temperature, final int batteryReamains) {

            tv_prompt.setText(tv_prompt.getText() + "Temperature is :"
                    + temperature + "battery level is:" + batteryReamains + "\n");

        }
    };

    BleOperation.ResultNotifyCallback disable_RT_callback = new BleOperation.ResultNotifyCallback() {
        @Override
        public void onResultNotify(boolean result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    tv_prompt.setText(tv_prompt.getText() + "关闭实时温度反馈成功\n");

                }
            });

        }
    };

    BleOperation.ResultNotifyCallback reset_callback = new BleOperation.ResultNotifyCallback() {
        @Override
        public void onResultNotify(boolean result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    tv_prompt.setText(tv_prompt.getText() + "重置成功\n");

                }
            });
        }
    };

    BleOperation.ResultNotifyCallback enable_ST_callback = new BleOperation.ResultNotifyCallback() {
        @Override
        public void onResultNotify(boolean result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    tv_prompt.setText(tv_prompt.getText() + "开启温度存储成功\n");

                }
            });
        }
    };


    BleOperation.ResultNotifyCallback disable_ST_callback = new BleOperation.ResultNotifyCallback() {
        @Override
        public void onResultNotify(boolean result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    tv_prompt.setText(tv_prompt.getText() + "关闭温度存储成功\n");
                }
            });
        }
    };

    BleOperation.ResultNotifyCallback read_ST_callback = new BleOperation.ResultNotifyCallback() {
        @Override
        public void onResultNotify(final boolean result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    if (result) {

                        tv_prompt.setText(tv_prompt.getText() + "当前存储状态为开启\n");

                    } else {

                        tv_prompt.setText(tv_prompt.getText() + "当前存储状态为关闭\n");

                    }
                }
            });
        }
    };


    BleOperation.ResultNotifyCallback setBCName_callback = new BleOperation.ResultNotifyCallback() {
        @Override
        public void onResultNotify(boolean result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    tv_prompt.setText(tv_prompt.getText() + "设置广播名称成功\n");
                }
            });
        }
    };


    BleOperation.GetBroadcastingNameCallback getBCName_callback = new BleOperation.GetBroadcastingNameCallback() {
        @Override

        public void onGetBroadcastingName(final String broadcastName) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    tv_prompt.setText(tv_prompt.getText() + "当前设备的广播名称为" + broadcastName + "\n");
                }
            });
        }
    };


    BleOperation.ResultNotifyCallback setId_callback = new BleOperation.ResultNotifyCallback() {
        @Override
        public void onResultNotify(boolean result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    tv_prompt.setText(tv_prompt.getText() + "设置设备Id成功\n");
                }
            });
        }
    };

    BleOperation.GetDeviceIdCallback getId_callback = new BleOperation.GetDeviceIdCallback() {
        @Override
        public void onGetDeviceId(byte[] deviceId) {

            tv_prompt.setText(tv_prompt.getText() + "获取设备Id为" + Util.Show20Hexes(deviceId) + "\n");

        }
    };

    BleOperation.GetFirmwareVersionCallback getFirmwareVersion_callback = new BleOperation.GetFirmwareVersionCallback() {
        @Override
        public void onGetFirmwareVersion(String versionId, Calendar date) {
            tv_prompt.setText(tv_prompt.getText() + "固件版本为" + versionId + "\n");
        }
    };

    BleOperation.GetDateTimeCallback getTime_callback = new BleOperation.GetDateTimeCallback() {
        @Override
        public void ongetDateTime(final Calendar c) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_prompt.setText(tv_prompt.getText() + "获取的设备当前时间为" + Util.FormDateTimeString(c) + "\n");

                }
            });


        }
    };
    BleOperation.ResultNotifyCallback setTime_callback = new BleOperation.ResultNotifyCallback() {
        @Override
        public void onResultNotify(boolean result) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    tv_prompt.setText(tv_prompt.getText() + "时间同步成功" + "\n");

                }
            });

        }
    };


    BleOperation.GetStoredTemperatureCallback readData_callback = new BleOperation.GetStoredTemperatureCallback() {
        @Override
        public void onGetStoredTemperature(final Calendar c, final float temperature) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    tv_prompt.setText(tv_prompt.getText() + "获取温度的时间为" + Util.FormDateTimeString(c) +
                            "   温度:" + temperature +
                            "\n");

                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_console);
        String mac = getIntent().getStringExtra("MAC");
        Common.mBleOperation.connect(mac, connect_callback);

    }


    boolean isInitalized = false;

    IntentFilter filter = new IntentFilter();

    @Override
    protected void onStart() {
        super.onStart();

        if (!isInitalized) {

            getViews();
            isInitalized = !isInitalized;

        }

    }

    private void getViews() {
        tv_enableRT = (TextView) findViewById(R.id.console_tv_enableRT);
        tv_enableRT.setOnClickListener(this);
        tv_disableRT = (TextView) findViewById(R.id.console_tv_disableRT);
        tv_disableRT.setOnClickListener(this);
        tv_enableST = (TextView) findViewById(R.id.console_tv_enableST);
        tv_enableST.setOnClickListener(this);
        tv_disableST = (TextView) findViewById(R.id.console_tv_disableST);
        tv_disableST.setOnClickListener(this);
        tv_readST = (TextView) findViewById(R.id.console_tv_readST);
        tv_readST.setOnClickListener(this);
        tv_reset = (TextView) findViewById(R.id.console_tv_reset);
        tv_reset.setOnClickListener(this);
        tv_ota = (TextView) findViewById(R.id.console_tv_ota);
        tv_ota.setOnClickListener(this);
        tv_readData = (TextView) findViewById(R.id.console_tv_readData);
        tv_readData.setOnClickListener(this);
        tv_clear = (TextView) findViewById(R.id.console_tv_clear);
        tv_clear.setOnClickListener(this);

        tv_setBCName = (TextView) findViewById(R.id.console_tv_setBCName);
        tv_setBCName.setOnClickListener(this);

        tv_getBCName = (TextView) findViewById(R.id.console_tv_getBCName);
        tv_getBCName.setOnClickListener(this);

        tv_setId = (TextView) findViewById(R.id.console_tv_setId);
        tv_setId.setOnClickListener(this);

        tv_getId = (TextView) findViewById(R.id.console_tv_getId);
        tv_getId.setOnClickListener(this);
        tv_getVersion = (TextView) findViewById(R.id.console_tv_getVersion);
        tv_getVersion.setOnClickListener(this);

        tv_setTime = (TextView) findViewById(R.id.console_tv_setTime);
        tv_setTime.setOnClickListener(this);

        tv_getTime = (TextView) findViewById(R.id.console_tv_getTime);
        tv_getTime.setOnClickListener(this);


        tv_prompt = (TextView) findViewById(R.id.console_tv_prompt);
    }

    @Override
    protected void onStop() {

        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Common.mBleOperation.disconnect(null);
    }

    boolean isConnected = false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK && requestCode == this.REQUEST_DATE_PICK) {

            Calendar start_c = (Calendar) data.getSerializableExtra("Start_Date");
            Calendar end_c = (Calendar) data.getSerializableExtra("End_Date");
            Common.mBleOperation.command_readData(start_c, end_c, readData_callback);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onClick(View v) {

        if (!isConnected) {

            return;

        }

        if (v == tv_enableRT) {

            Common.mBleOperation.command_enableRealTimeTemperature(enable_RT_callback, received_RT_callback);

        } else if (v == tv_disableRT) {

            Common.mBleOperation.command_disableRealTimeTemperature(disable_RT_callback);


        } else if (v == tv_enableST) {

            Common.mBleOperation.command_setStorageStatus(true, enable_ST_callback);


        } else if (v == tv_disableST) {

            Common.mBleOperation.command_setStorageStatus(false, disable_ST_callback);

        } else if (v == tv_readST) {

            Common.mBleOperation.command_getStorageStatus(read_ST_callback);

        } else if (v == tv_reset) {

            Common.mBleOperation.command_reset(reset_callback);


        } else if (v == tv_ota) {


        } else if (v == tv_readData) {

            Intent intent = new Intent(this, DatePickActivity.class);
            startActivityForResult(intent, REQUEST_DATE_PICK);

        } else if (v == tv_clear) {

            this.tv_prompt.setText("");

        } else if (v == tv_setBCName) {

            Common.mBleOperation.command_setBroadcastName("HTS", setBCName_callback);

        } else if (v == tv_getBCName) {

            Common.mBleOperation.command_getBroadacastName(getBCName_callback);

        } else if (v == tv_setId) {

            Common.mBleOperation.command_setDeviceId(new byte[]{1, 2, 3, 4,}, setId_callback);

        } else if (v == tv_getId) {

            Common.mBleOperation.command_getDeviceId(getId_callback);

        } else if (v == tv_getVersion) {

            Common.mBleOperation.command_getFirmwareVersion(getFirmwareVersion_callback);

        } else if (v == tv_setTime) {

            Common.mBleOperation.command_setTime(Calendar.getInstance(), setTime_callback);

        } else if (v == tv_getTime) {

            Common.mBleOperation.command_getTime(getTime_callback);

        }
    }
}