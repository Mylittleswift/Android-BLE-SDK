package io.github.mylittleswift.blesdk;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Util {

    public static String Show20Hexes(byte[] bytes) {

        StringBuffer buffer = new StringBuffer();
        for (byte b : bytes)
            buffer.append(ConvertHex2Ascii(b) + " ");

        return buffer.toString();
    }

    public static String ShowDecimal(byte b) {

        byte divisor = 0xA;
        byte last = (byte) (b % divisor);
        last += 0x30;
        b /= divisor;
        if (b == 0)
            return new String(new byte[]{last});

        byte middle = (byte) (b % divisor);
        middle += 0x30;
        b /= divisor;

        if (b == 0)
            return new String(new byte[]{middle, last});

        byte first = b;

        return new String(new byte[]{first, middle, last});
    }

    ///
    /// Convert hex decimal to hex ASCII string.
    ///
    private static String ConvertHex2Ascii(byte b) {

        char l_b = (char) (b & 0xF);
        char h_b = (char) ((b & 0xF0) >> 4);

        if (h_b < 10 && l_b < 10) {
            h_b += 0x30;
            l_b += 0x30;

        } else if (h_b >= 10 && l_b >= 10) {
            h_b += 0x37;
            l_b += 0x37;

        } else if (h_b >= 10 && l_b < 10) {
            h_b += 0x37;
            l_b += 0x30;

        } else {
            h_b += 0x30;
            l_b += 0x37;
        }

        return (h_b + "") + (l_b + "");

    }

    public static byte[] CopyByteArray(byte[] source, int index, int length) {

        byte[] b = new byte[length];
        for (int i = 0; i < length; i++) {

            b[i] = source[index + i];

        }
        return b;
    }

    public static void CopyByteArray(byte[] source, byte[] destination, int s_index, int d_index, int length) {

        try {

            for (int i = 0; i < length; i++) {

                destination[i + d_index] = source[i + s_index];
            }

        } catch (ArrayIndexOutOfBoundsException e) {

            Log.e("", "Source.Length:" + source.length + ",Destination.Length:" + destination.length);

        }

    }

    public static void putOnArray(byte[] data, int index, int value) {

        try {

            data[index] = (byte) (value & 0xFF);
            data[index + 1] = (byte) (value >>> 0x8 & 0xFF);
            data[index + 2] = (byte) (value >>> 0x10 & 0xFF);
            data[index + 3] = (byte) (value >>> 0x18 & 0xFF);

        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    public static void putOnArray(byte[] data, int index, short value) {

        try {

            data[index] = (byte) (value & 0xFF);
            data[index + 1] = (byte) (value >>> 0x8 & 0xFF);

        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    public static String ShowBytesSeparatedWithChar(byte[] data, char symbol) {
        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < data.length; i++) {

            buffer.append(ConvertHex2Ascii(data[i]));
            if (i != data.length - 1) {

                buffer.append(symbol);

            }

        }

        return buffer.toString();
    }

    public static int ConvertBCD2Decimal(byte bcd_code) {

        int result = 0;
        result += (bcd_code & 0xF);
        result += (bcd_code >>> 0x4 & 0xF) * 10;
        return result;

    }

    // the decimal range only from 0x0 to 0x63
    public static byte ConvertDecimal2BCD(byte decimal) {

        byte result = 0;
        result += (decimal % 10);
        result += (decimal / 10 << 0x4);
        return result;
    }

    public static byte[] ConverInt2BigEndianByteArray(int value) {

        byte[] b = new byte[4];

        b[0] = (byte) ((value >>> 24) & 0xFF);
        b[1] = (byte) ((value >>> 16) & 0xFF);
        b[2] = (byte) ((value >>> 8) & 0xFF);
        b[3] = (byte) (value & 0xFF);

        return b;
    }

    public static int ConvertBigEndianInt2Int(byte[] data, int index) {

        int val = 0;

        val += ((data[index] << 24) & 0xFF000000);
        val += ((data[index + 1] << 16) & 0x00FF0000);
        val += ((data[index + 2] << 8) & 0x0000FF00);
        val += (data[index + 3] & 0x000000FF);

        return val;
    }

    public static String FormDateTimeString(Calendar c) {

        StringBuffer buffer = new StringBuffer();
        buffer.append(c.get(Calendar.YEAR));
        buffer.append('-');
        if (c.get(Calendar.MONTH) + 1 < 10)
            buffer.append('0');
        buffer.append(c.get(Calendar.MONTH) + 1);
        buffer.append('-');

        if (c.get(Calendar.DAY_OF_MONTH) < 10)
            buffer.append('0');

        buffer.append(c.get(Calendar.DAY_OF_MONTH));
        buffer.append(' ');
        // Times
        if (c.get(Calendar.HOUR_OF_DAY) < 10)
            buffer.append('0');

        buffer.append(c.get(Calendar.HOUR_OF_DAY));
        buffer.append(':');

        if (c.get(Calendar.MINUTE) < 10)
            buffer.append('0');

        buffer.append(c.get(Calendar.MINUTE));
        buffer.append(':');

        if (c.get(Calendar.SECOND) < 10)
            buffer.append('0');

        buffer.append(c.get(Calendar.SECOND));
        return buffer.toString();

    }

    public static String FormDateTimeString2(Calendar c) {

        StringBuffer buffer = new StringBuffer();
        buffer.append(c.get(Calendar.YEAR));
        buffer.append('-');
        if (c.get(Calendar.MONTH) + 1 < 10)
            buffer.append('0');
        buffer.append(c.get(Calendar.MONTH) + 1);
        buffer.append('-');

        if (c.get(Calendar.DAY_OF_MONTH) < 10)
            buffer.append('0');

        buffer.append(c.get(Calendar.DAY_OF_MONTH));
        buffer.append(' ');
        // Times

        return buffer.toString();
    }

    public static Calendar analyzeDateString(String dateStr) {

        // Date date = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (date == null) {

            return null;

        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c;

    }




}
