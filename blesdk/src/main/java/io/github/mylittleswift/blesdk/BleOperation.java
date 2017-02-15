package io.github.mylittleswift.blesdk;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/1/20.
 */

public class BleOperation {
    private static final int COMMAND_ID_SETTING_TIME = 0x1;
    private static final int COMMAND_ID_GETTING_TIME = 0x41;
    private static final int COMMAND_ID_SETTING_BROADCAST_NAME = 0x2;
    private static final int COMMAND_ID_GETTING_BROADCAST_NAME = 0x42;
    private static final int COMMAND_ID_SETTING_STORAGE = 0x3;

    private static final int COMMAND_ID_GETTING_STORAGE = 0x43;
    private static final int COMMAND_ID_SETTING_ID = 0x5;
    private static final int COMMAND_ID_GETTING_ID = 0x45;
    private static final int COMMAND_ID_GETTING_DATA = 0x7;
    private static final int COMMAND_ID_ENABLE_RT_DATA = 0x9;
    private static final int COMMAND_ID_DISABLE_RT_DATA = 0x49;

    private static final int COMMAND_ID_OTA = 0x47;
    private static final int COMMAND_ID_GETTING_VERSION = 0x27;
    private static final int COMMAND_ID_RESET = 0x2E;

    private static final int SHORTENED_LOCAL_NAME = 0x08;
    private static final int COMPLETE_LOCAL_NAME = 0x09;

    private static final int DURATION_OF_SCANNING = 15000;

    private Context mContext;
    private RFLampDevice mDevice;
    private boolean isConnected = false;
    private boolean isStateBCRegistered = false;
    private BluetoothAdapter adapter;


    public BleOperation(Context context) {

        this.mContext = context;
        adapter = BluetoothAdapter.getDefaultAdapter();
        registerBC();
    }

    private void registerBC() {

        if (!isStateBCRegistered) {

            IntentFilter filter = new IntentFilter();
            filter.addAction(LightBLEService.ACTION_DATA_AVAILABLE);
            filter.addAction(LightBLEService.ACTION_GATT_SERVICES_DISCOVERED);
            filter.addAction(LightBLEService.ACTION_GATT_DISCONNECTED);
            mContext.registerReceiver(state_receiver, filter);
            isStateBCRegistered = true;
        }
    }

    private void unregisterBC() {

        mContext.unregisterReceiver(state_receiver);
    }


    BroadcastReceiver state_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction() == LightBLEService.ACTION_DATA_AVAILABLE) {

                byte[] values = intent.getByteArrayExtra(LightBLEService.EXTRA_DATA);

                switch (values[0]) {

                    case COMMAND_ID_SETTING_TIME:
                        if (setTime_callback != null) {
                            setTime_callback.onResultNotify(true);
                        }
                        break;
                    case COMMAND_ID_GETTING_TIME:
                        if (getDateTime_callback != null) {

                            Calendar c = Calendar.getInstance();
                            int year = Util.ConvertBCD2Decimal(values[1]) + 2000;
                            int month = Util.ConvertBCD2Decimal(values[2]) - 1;
                            int day = Util.ConvertBCD2Decimal(values[3]);
                            int hour = Util.ConvertBCD2Decimal(values[4]);
                            int minute = Util.ConvertBCD2Decimal(values[5]);
                            int second = Util.ConvertBCD2Decimal(values[6]);

                            c.set(Calendar.YEAR, year);
                            c.set(Calendar.MONTH, month);
                            c.set(Calendar.DAY_OF_MONTH, day);
                            c.set(Calendar.HOUR_OF_DAY, hour);
                            c.set(Calendar.MINUTE, minute);
                            c.set(Calendar.SECOND, second);

                            getDateTime_callback.ongetDateTime(c);
                        }

                        break;
                    case COMMAND_ID_SETTING_BROADCAST_NAME:
                        if (setBroadcastName_callback != null)
                            setBroadcastName_callback.onResultNotify(true);
                        break;
                    case COMMAND_ID_GETTING_BROADCAST_NAME:
                        if (getBraodcast_callback != null) {
                            String name = "";
                            try {
                                name = new String(values, 1, 14, "ascii");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            getBraodcast_callback.onGetBroadcastingName(name);
                        }

                        break;
                    case COMMAND_ID_SETTING_STORAGE:
                        if (setStorageStatus_callback != null) {
                            setStorageStatus_callback.onResultNotify(true);
                        }

                        break;
                    case COMMAND_ID_GETTING_STORAGE:

                        if (getStorageStatus_callback != null) {
                            if (values[1] == 0x1)

                                getStorageStatus_callback.onResultNotify(true);

                            else
                                getStorageStatus_callback.onResultNotify(false);
                        }
                        break;
                    case COMMAND_ID_SETTING_ID:
                        if (setDeviceId_callback != null) {
                            setDeviceId_callback.onResultNotify(true);
                        }
                        break;
                    case COMMAND_ID_GETTING_ID:
                        if (getDeviceId_callback != null) {

                            byte[] id = new byte[14];
                            for (int i = 0; i < id.length; i++) {
                                id[i] = values[1 + i];

                            }
                            getDeviceId_callback.onGetDeviceId(id);
                        }

                        break;
                    case COMMAND_ID_GETTING_DATA:

                        if (readData_callback != null) {


                            if (values[1] != (byte) 0xFF) {
                                Calendar c1 = Calendar.getInstance();

                                int year = Util.ConvertBCD2Decimal(values[1]) + 2000;
                                int month = Util.ConvertBCD2Decimal(values[2]) - 1;
                                int day = Util.ConvertBCD2Decimal(values[3]);
                                int hour = Util.ConvertBCD2Decimal(values[4]);
                                int minute = Util.ConvertBCD2Decimal(values[5]);
                                int temperature = ((values[6] << 8) & 0x0000FF00) + (values[7] & 0x000000FF);
                                float f_temp = (temperature * 1.0f) / 10;

                                c1.set(Calendar.YEAR, year);
                                c1.set(Calendar.MONTH, month);
                                c1.set(Calendar.DAY_OF_MONTH, day);
                                c1.set(Calendar.HOUR_OF_DAY, hour);
                                c1.set(Calendar.MINUTE, minute);

                                readData_callback.onGetStoredTemperature(c1, f_temp);
                            }
                            if (values[9] != (byte) 0xFF) {


                                Calendar c2 = Calendar.getInstance();
                                int year2 = Util.ConvertBCD2Decimal(values[9]) + 2000;
                                int month2 = Util.ConvertBCD2Decimal(values[10]) - 1;
                                int day2 = Util.ConvertBCD2Decimal(values[11]);
                                int hour2 = Util.ConvertBCD2Decimal(values[12]);
                                int minute2 = Util.ConvertBCD2Decimal(values[13]);
                                int temperature2 = ((values[14] << 8) & 0x0000FF00) + (values[15] & 0x000000FF);
                                float f_temp2 = (temperature2 * 1.0f) / 10;

                                c2.set(Calendar.YEAR, year2);
                                c2.set(Calendar.MONTH, month2);
                                c2.set(Calendar.DAY_OF_MONTH, day2);
                                c2.set(Calendar.HOUR_OF_DAY, hour2);
                                c2.set(Calendar.MINUTE, minute2);


                                readData_callback.onGetStoredTemperature(c2, f_temp2);
                            }
                        }

                        break;
                    case COMMAND_ID_ENABLE_RT_DATA:
                        if (enableRealTimeTemperature_callback1 != null) {
                            if (values[2] == 0) {
                                enableRealTimeTemperature_callback1.onResultNotify(true);

                            } else {

                                int temperature = ((values[1] << 8) & 0x0000FF00) + (values[2] & 0x000000FF);
                                float f_temp = (temperature * 1.0f) / 10;
                                int battery = values[0x5];
                                enableRealTimeTemperature_callback2.onRealTimeTemperatureReceived(f_temp, battery);
                            }
                        }
                        break;
                    case COMMAND_ID_DISABLE_RT_DATA:
                        if (disableRealTimeTemperature_callback != null) {

                            disableRealTimeTemperature_callback.onResultNotify(true);

                        }

                        break;
                    case COMMAND_ID_GETTING_VERSION:
                        if (getFirmwareVersion_callback != null) {

                            Calendar c = Calendar.getInstance();

                            int year = Util.ConvertBCD2Decimal(values[8]) + 2000;
                            int month = Util.ConvertBCD2Decimal(values[9]) - 1;
                            int day = Util.ConvertBCD2Decimal(values[10]);

                            c.set(Calendar.YEAR, year);
                            c.set(Calendar.MONTH, month);
                            c.set(Calendar.DAY_OF_MONTH, day);
                            byte[] ver = new byte[3];
                            ver[0] = values[5];
                            ver[1] = values[6];
                            ver[2] = values[7];
                            String version = Util.ShowBytesSeparatedWithChar(ver, '.');
                            getFirmwareVersion_callback.onGetFirmwareVersion(version, c);
                        }
                        break;
                    case COMMAND_ID_RESET:
                        if (reset_callback != null) {

                            reset_callback.onResultNotify(true);

                        }
                        break;
                }


            } else if (intent.getAction() == LightBLEService.ACTION_GATT_SERVICES_DISCOVERED) {

                isConnected = true;
                if (connect_callback != null) {
                    Log.e("abc", "abc");
                    connect_callback.onResultNotify(true);

                }

            } else if (intent.getAction() == LightBLEService.ACTION_GATT_DISCONNECTED) {

                isConnected = false;
                if (disconnect_callback != null) {
                    disconnect_callback.onResultNotify(true);
                }
            }
        }
    };


    //Normal setting callback
    public interface ResultNotifyCallback {

        public void onResultNotify(boolean result);

    }

    //For syncTime
    public interface GetDateTimeCallback {

        public void ongetDateTime(Calendar c);

    }

    //For GetBroacastingName
    public interface GetBroadcastingNameCallback {

        public void onGetBroadcastingName(String broadcastName);

    }

    //For getStoringStauts

    //For getDeviceId
    public interface GetDeviceIdCallback {

        public void onGetDeviceId(byte[] deviceId);
    }

    public interface GetStoredTemperatureCallback {

        public void onGetStoredTemperature(Calendar c, float temperature);

    }

    public interface RealTimeTemperatureReceivedCallback {

        /**
         * @param temperature     温度值
         * @param batteryReamains 0~3档位的电量
         */
        public void onRealTimeTemperatureReceived(float temperature, int batteryReamains);

    }

    public interface GetFirmwareVersionCallback {

        /**
         * @param versionId Firmware版本号
         * @param date      Firmware 编译日期 有效值(年,月,日)
         */
        public void onGetFirmwareVersion(String versionId, Calendar date);

    }

    public interface DeviceDisconveredCallback {
        public void onNewDeviceDisconvered(String deviceName, String mac, int rssi);
    }

    ResultNotifyCallback setTime_callback;

    private boolean printLog() {
        if (!isConnected) {
            Log.e("Notify", "Device not connected");
            return false;
        }
        return true;
    }

    public byte Crc(byte[] value) {
        byte c = 0;
        for (int i = 0; i < 19; i++) {
            c += value[i];
        }
        return c;
    }

    /**
     * 为设备设置时间
     *
     * @param dateTime 设置的时间数据.
     * @param callback 设置成功时的回调.
     */
    public void command_setTime(Calendar dateTime, ResultNotifyCallback callback) {

        if (!printLog()) {

            return;
        }
        byte[] values = new byte[20];
        values[0] = COMMAND_ID_SETTING_TIME;
        values[1] = Util.ConvertDecimal2BCD(((byte) (dateTime.get(Calendar.YEAR) - 2000)));
        values[2] = Util.ConvertDecimal2BCD(((byte) dateTime.get(Calendar.MONTH)));
        values[3] = (Util.ConvertDecimal2BCD((byte) dateTime.get(Calendar.DAY_OF_MONTH)));
        values[4] = (Util.ConvertDecimal2BCD((byte) dateTime.get(Calendar.HOUR_OF_DAY)));
        values[5] = (Util.ConvertDecimal2BCD((byte) dateTime.get(Calendar.MINUTE)));
        values[6] = (Util.ConvertDecimal2BCD((byte) dateTime.get(Calendar.SECOND)));
        this.setTime_callback = callback;
        values[19] = Crc(values);
        mDevice.writeTx(values);
    }

    GetDateTimeCallback getDateTime_callback;

    /**
     * 获取设备的当前时间
     *
     * @param callback 获取成功时的回调
     */
    public void command_getTime(GetDateTimeCallback callback) {
        if (!printLog()) {

            return;
        }
        this.getDateTime_callback = callback;
        byte[] values = new byte[20];
        values[0] = COMMAND_ID_GETTING_TIME;
        values[19] = Crc(values);
        mDevice.writeTx(values);
    }


    ResultNotifyCallback setBroadcastName_callback;

    /**
     * 为设备设置广播时的名称
     *
     * @param broadcastName 设备名称,字符集为ascii, 且最大长度不超过14个字符
     * @param callback      设置成功时的回调
     */
    public void command_setBroadcastName(String broadcastName, ResultNotifyCallback callback) {

        if (!printLog()) {

            return;
        }
        broadcastName = broadcastFilter(broadcastName);
        byte[] values = new byte[20];
        values[0] = COMMAND_ID_SETTING_BROADCAST_NAME;
        for (int i = 0; i < broadcastName.length(); i++) {

            values[1 + i] = broadcastName.getBytes()[i];

        }
        values[19] = Crc(values);
        this.setBroadcastName_callback = callback;
        mDevice.writeTx(values);
    }

    private String broadcastFilter(String name) {
        name = name.substring(0, name.length() < 14 ? name.length() : 14);
        byte[] datas = name.getBytes();
        for (int i = 0; i < datas.length; i++) {
            if (datas[i] < 32 || datas[1] > 127) {
                datas[i] = ' ';
            }
        }
        return new String(datas);
    }

    GetBroadcastingNameCallback getBraodcast_callback;

    /**
     * 获取设备当前的广播名称
     *
     * @param callback 获取成功时返回的回调
     */
    public void command_getBroadacastName(GetBroadcastingNameCallback callback) {
        if (!printLog()) {
            return;
        }

        byte[] values = new byte[20];
        values[0] = COMMAND_ID_GETTING_BROADCAST_NAME;
        values[19] = Crc(values);

        this.getBraodcast_callback = callback;
        mDevice.writeTx(values);
    }

    ResultNotifyCallback setStorageStatus_callback;

    /**
     * 为设备设置存储状态,status状态标识是否将每次测量的温度记录在体温计内
     *
     * @param status   true时开启,false时关闭
     * @param callback 设置成功时调用的回调
     */
    public void command_setStorageStatus(boolean status, ResultNotifyCallback callback) {
        if (!printLog()) {
            return;
        }
        byte[] values = new byte[20];
        values[0] = COMMAND_ID_SETTING_STORAGE;
        if (status)
            values[1] = 0x1;

        values[19] = Crc(values);
        setStorageStatus_callback = callback;
        mDevice.writeTx(values);
    }

    ResultNotifyCallback getStorageStatus_callback;

    /**
     * 获取设备当前的存储状态
     *
     * @param callback 获取成功时调用的回调
     */
    public void command_getStorageStatus(ResultNotifyCallback callback) {
        if (!printLog()) {

            return;
        }

        byte[] values = new byte[20];
        values[0] = COMMAND_ID_GETTING_STORAGE;
        values[19] = Crc(values);
        getStorageStatus_callback = callback;
        mDevice.writeTx(values);
    }

    ResultNotifyCallback setDeviceId_callback;

    /**
     * 为设备设置DeviceId
     *
     * @param deviceId 14个字节长度且值从0x0~0xF的设备ID
     * @param callback 设置成功时调用的回调
     */
    public void command_setDeviceId(byte[] deviceId, ResultNotifyCallback callback) {
        if (!printLog()) {
            return;
        }
        byte[] values = new byte[20];
        values[0] = COMMAND_ID_SETTING_ID;

        for (int i = 0; i < deviceId.length && i < 14; i++) {
            values[1 + i] = deviceId[i];
        }
        values[19] = Crc(values);
        setDeviceId_callback = callback;
        mDevice.writeTx(values);
    }

    GetDeviceIdCallback getDeviceId_callback;

    /**
     * 获取设备当前的DeviceId
     *
     * @param callback 获取成功时调用的回调
     */
    public void command_getDeviceId(GetDeviceIdCallback callback) {
        if (!printLog()) {

            return;
        }
        byte[] values = new byte[20];
        values[0] = COMMAND_ID_GETTING_ID;
        values[19] = Crc(values);

        this.getDeviceId_callback = callback;
        mDevice.writeTx(values);
    }

    ResultNotifyCallback enableRealTimeTemperature_callback1;
    RealTimeTemperatureReceivedCallback enableRealTimeTemperature_callback2;

    /**
     * 开启实时温度反馈,间隔为10s
     *
     * @param callback1 开启成功时将会调用的回调
     * @param callback2 温度和电量反馈时调用的回调
     */
    public void command_enableRealTimeTemperature(ResultNotifyCallback callback1, RealTimeTemperatureReceivedCallback callback2) {
        if (!printLog()) {

            return;
        }
        byte[] values = new byte[20];
        values[0] = COMMAND_ID_ENABLE_RT_DATA;
        values[19] = Crc(values);
        enableRealTimeTemperature_callback1 = callback1;
        enableRealTimeTemperature_callback2 = callback2;
        mDevice.writeTx(values);
    }

    ResultNotifyCallback disableRealTimeTemperature_callback;

    /**
     * 关闭实时温度反馈,间隔为10s
     *
     * @param callback 关闭成功时调用的回调
     */
    public void command_disableRealTimeTemperature(ResultNotifyCallback callback) {
        if (!printLog()) {

            return;
        }
        byte[] values = new byte[20];
        values[0] = COMMAND_ID_DISABLE_RT_DATA;
        values[19] = Crc(values);

        disableRealTimeTemperature_callback = callback;
        mDevice.writeTx(values);
    }

    GetFirmwareVersionCallback getFirmwareVersion_callback;

    /**
     * 获取设备的当前版本号
     *
     * @param callback 获取成功时调用的回调
     */
    public void command_getFirmwareVersion(GetFirmwareVersionCallback callback) {
        if (!printLog()) {

            return;
        }
        byte[] values = new byte[20];
        values[0] = COMMAND_ID_GETTING_VERSION;
        values[19] = Crc(values);

        getFirmwareVersion_callback = callback;
        mDevice.writeTx(values);
    }

    ResultNotifyCallback reset_callback;

    /**
     * 重置设备
     *
     * @param callback 设置成功时调用的回调
     */
    public void command_reset(ResultNotifyCallback callback) {

        if (!printLog()) {

            return;
        }

        byte[] values = new byte[20];
        values[0] = COMMAND_ID_RESET;
        values[19] = Crc(values);

        reset_callback = callback;
        mDevice.writeTx(values);
    }

    GetStoredTemperatureCallback readData_callback;

    public void command_readData(Calendar startTime, Calendar endTime, GetStoredTemperatureCallback callback) {

        readData_callback = callback;
        byte start_year = (byte) (startTime.get(Calendar.YEAR) - 2000);
        byte start_month = (byte) (startTime.get(Calendar.MONTH) + 1);
        byte start_day = (byte) (startTime.get(Calendar.DAY_OF_MONTH));
        byte start_hour = (byte) (startTime.get(Calendar.HOUR_OF_DAY));
        byte start_minute = (byte) (startTime.get(Calendar.MINUTE) + 1);

        byte end_year = (byte) (endTime.get(Calendar.YEAR) - 2000);
        byte end_month = (byte) (endTime.get(Calendar.MONTH) + 1);
        byte end_day = (byte) (endTime.get(Calendar.DAY_OF_MONTH));
        byte end_hour = (byte) (endTime.get(Calendar.HOUR_OF_DAY));

        byte end_minute = (byte) (endTime.get(Calendar.MINUTE) + 1);
        byte[] value = new byte[20];

        value[0] = 0x7;
        value[1] = Util.ConvertDecimal2BCD(start_year);
        value[2] = Util.ConvertDecimal2BCD(start_month);
        value[3] = Util.ConvertDecimal2BCD(start_day);
        value[4] = Util.ConvertDecimal2BCD(start_hour);
        value[5] = Util.ConvertDecimal2BCD(start_minute);

        value[7] = Util.ConvertDecimal2BCD(end_year);
        value[8] = Util.ConvertDecimal2BCD(end_month);
        value[9] = Util.ConvertDecimal2BCD(end_day);
        value[0xA] = Util.ConvertDecimal2BCD(end_hour);
        value[0xB] = Util.ConvertDecimal2BCD(end_minute);
        value[19] = Crc(value);
        mDevice.writeTx(value);
    }


    DeviceDisconveredCallback scan_callback;

    private class DeviceInfo {

        int rssi;
        String name;
        String mac;
        BluetoothDevice device;

        DeviceInfo(int rssi, String name, String mac, BluetoothDevice device) {
            this.rssi = rssi;
            this.name = name;
            this.mac = mac;
            this.device = device;
        }

    }

    public static String decodeDeviceName(byte[] data) {
        String name = null;
        int fieldLength, fieldName;
        int packetLength = data.length;
        for (int index = 0; index < packetLength; index++) {
            fieldLength = data[index];
            if (fieldLength == 0)
                break;
            fieldName = data[++index];

            if (fieldName == COMPLETE_LOCAL_NAME || fieldName == SHORTENED_LOCAL_NAME) {
                name = decodeLocalName(data, index + 1, fieldLength - 1);
                break;
            }
            index += fieldLength - 1;
        }
        return name;
    }

    @Nullable
    public static String decodeLocalName(final byte[] data, final int start, final int length) {
        try {
            return new String(data, start, length, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            Log.e("scan", "Unable to convert the complete local name to UTF-8", e);
            return null;
        } catch (final IndexOutOfBoundsException e) {
            Log.e("scan", "Error when reading complete local name", e);
            return null;
        }
    }

    BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

            DeviceInfo deviceInfo = new DeviceInfo(rssi, decodeDeviceName(scanRecord), device.getAddress(), device);
            Message msg = new Message();
            msg.obj = deviceInfo;
            handler.sendMessage(msg);
        }
    };
    Map<String, BluetoothDevice> map = new HashMap<>();
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            DeviceInfo deviceInfo = (DeviceInfo) msg.obj;

            if (!map.containsKey(deviceInfo.mac)) {

                scan_callback.onNewDeviceDisconvered(deviceInfo.name, deviceInfo.mac, deviceInfo.rssi);
                map.put(deviceInfo.mac, deviceInfo.device);

            }
        }
    };

    boolean isScanning = false;

    private void stopLeScan(boolean immediately) {

        if (immediately) {

            if (isScanning) {

                this.adapter.stopLeScan(leScanCallback);
                isScanning = false;
            }

        } else {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (isScanning) {
                        adapter.stopLeScan(leScanCallback);
                    }
                }
            }, DURATION_OF_SCANNING);
        }

    }

    public void scan(DeviceDisconveredCallback callback) {
        scan_callback = callback;
        if (!isScanning) {
            adapter.startLeScan(leScanCallback);
            isScanning = true;
            stopLeScan(false);
            map.clear();
        }
    }

    public void stopScan() {

        stopLeScan(true);

    }

    ResultNotifyCallback connect_callback;

    public void connect(String mac, ResultNotifyCallback callback1) {

        connect_callback = callback1;
        if (isConnected) {
            return;
        }
        BluetoothDevice device = (BluetoothDevice) map.get(mac);
        this.mDevice = new RFLampDevice(mContext, device);
    }

    ResultNotifyCallback disconnect_callback;

    public void disconnect(@Nullable ResultNotifyCallback callback) {

        disconnect_callback = callback;

        if (!isConnected) {
            return;
        }
        unregisterBC();
        mDevice.disconnectedDevice();
        mDevice = null;

    }
}
