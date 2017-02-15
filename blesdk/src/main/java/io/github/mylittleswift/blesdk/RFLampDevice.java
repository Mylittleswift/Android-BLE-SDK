package io.github.mylittleswift.blesdk;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import java.util.Calendar;
import java.util.List;

public class RFLampDevice extends Bledevice {

    private BluetoothGattCharacteristic shishiCharateristic;
    private BluetoothGattCharacteristic shujuCharateristic;

    public RFLampDevice(Context context, BluetoothDevice device) {
        // TODO Auto-generated constructor stub
        super(context, device);
        this.device = device;
    }

    @Override
    protected void discoverCharacteristicsFromService() {
        // ���޸�
        if (bleService == null || device == null) {

            return;
        }
        List<BluetoothGattService> services = bleService
                .getSupportedGattServices(this.device);
        if (services == null) {
            return;
        }
        for (BluetoothGattService service : services) {
            for (BluetoothGattCharacteristic characteristic : service
                    .getCharacteristics()) {

                if (service.getUuid().toString().contains("fff0")) {

                    if (characteristic.getUuid().toString().contains("fff6")) {
                        shujuCharateristic = characteristic;
                    } else if (characteristic.getUuid().toString().contains("fff7")) {
/*                    if (deviceName.toLowerCase().contains("dfu")) {

                    } else {*/

                        shishiCharateristic = characteristic;
                        this.setCharacteristicNotification(characteristic, true);
                        // }
                    }

                }
            
            }
        }

    }

    public void sendUpdate() {

        byte[] value = new byte[20];
        value[0] = 0x47;
        value[19] = 0x47;

        System.out.println("Master:" + Tools.byte2Hex(value));
        shujuCharateristic.setValue(value);
        this.writeValue(shujuCharateristic);

    }

    public void setST(boolean state) {

        byte b_state = 0x0;

        if (state) {
            b_state = 0x1;
        }

        byte[] value = new byte[20];
        value[0] = 0x3;
        value[1] = b_state;
        value[19] = this.Crc(value);
        shujuCharateristic.setValue(value);
        this.writeValue(shujuCharateristic);
    }

    public void enableRT() {
        byte[] value = new byte[20];
        value[0] = 0x9;
        value[19] = this.Crc(value);
        shujuCharateristic.setValue(value);
        this.writeValue(shujuCharateristic);
    }

    public void disableRT() {

        byte[] value = new byte[20];
        value[0] = 0x49;
        value[19] = this.Crc(value);
        shujuCharateristic.setValue(value);
        this.writeValue(shujuCharateristic);
    }

    public void fmReset() {
        byte[] value = new byte[20];
        value[0] = 0x2E;
        value[19] = this.Crc(value);
        shujuCharateristic.setValue(value);
        this.writeValue(shujuCharateristic);
    }

    public void readSTState() {
        byte[] value = new byte[20];
        value[0] = 0x43;
        value[19] = this.Crc(value);
        shujuCharateristic.setValue(value);
        this.writeValue(shujuCharateristic);
    }

    public void readData(Calendar startTime, Calendar endTime) {

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
        shujuCharateristic.setValue(value);
        this.writeValue(shujuCharateristic);
    }

    public void readData() {
        byte[] value = new byte[20];
        value[0] = 0x7;
        value[19] = Crc(value);
        shujuCharateristic.setValue(value);
        this.writeValue(shujuCharateristic);
    }

    public void checkUpdate() {
        byte[] value = new byte[16];
        value[0] = 0x27;
        for (int i = 0; i < 15; i++) {
            value[15] += value[i];
        }
        shujuCharateristic.setValue(value);
        this.writeValue(shujuCharateristic);
    }

    public void sendUpdate_SLAVE() {
        byte[] value = new byte[16];
        value[0] = (byte) 0x93;
        value[15] = (byte) 0x93;
        System.out.println("Slave:" + Tools.byte2Hex(value));
        shujuCharateristic.setValue(value);
        this.writeValue(shujuCharateristic);
    }

    public void sendUpdate_MASTER() {
        byte[] value = new byte[16];
        value[0] = 0x47;
        value[15] = 0x47;

        System.out.println("Master:" + Tools.byte2Hex(value));
        shujuCharateristic.setValue(value);
        this.writeValue(shujuCharateristic);
    }

    public void total(String day) {
        if (shujuCharateristic == null)
            return;
        byte[] value = new byte[16];
        value[0] = 0x07;
        value[1] = (byte) bcd(day);
        for (int i = 2; i < 15; i++) {
            value[i] = 0;
        }
        int s = 0;
        for (int i = 0; i < 15; i++) {
            s += value[i];
        }
        value[15] = (byte) s;

        shujuCharateristic.setValue(value);
        this.writeValue(shujuCharateristic);
    }

    public void goal(String day) {
        if (shujuCharateristic == null)
            return;
        byte[] value = new byte[16];
        value[0] = 0x08;
        value[1] = (byte) bcd(day);
        for (int i = 2; i < 15; i++) {
            value[i] = 0;
        }
        int s = 0;
        for (int i = 0; i < 15; i++) {
            s += value[i];
        }
        value[15] = (byte) s;
        shujuCharateristic.setValue(value);
        this.writeValue(shujuCharateristic);
    }

    public void SportDetali(String day) {
        if (shujuCharateristic == null) {
            System.out.println("shujuCharateristic is null");
            return;
        }

        byte[] value = new byte[16];
        value[0] = 0x43;
        value[1] = (byte) bcd(day);
        for (int i = 2; i < 15; i++) {
            value[i] = 0;
        }
        int s = 0;
        for (int i = 0; i < 15; i++) {
            s += value[i];
        }
        value[15] = (byte) s;
        shujuCharateristic.setValue(value);
        this.writeValue(shujuCharateristic);
    }

    public byte Crc(byte[] value) {
        byte c = 0;
        for (int i = 0; i < 19; i++) {
            c += value[i];
        }
        return c;
    }

    public int bcd(String s) {

        Integer i = Integer.parseInt(s, 16);
        return i;
    }

    public void jibu() {
        if (shujuCharateristic == null) {
            System.out.println("shujuCharateristic is null");
            return;
        }

        byte[] value = new byte[16];
        value[0] = 0x09;

        for (int i = 1; i < 15; i++) {
            value[i] = 0;
        }

        value[15] = 0x09;
        System.out.println("ʵʱ�ǲ�");
        shujuCharateristic.setValue(value);
        this.writeValue(shujuCharateristic);

    }

    public void writeTx(byte[] values) {

        shujuCharateristic.setValue(values);
        this.writeValue(shujuCharateristic);

    }
}
