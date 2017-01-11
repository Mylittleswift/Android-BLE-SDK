package com.example.ble;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
















import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

public class Tools {
	private static final int SHORTENED_LOCAL_NAME = 0x08;
	private static final int COMPLETE_LOCAL_NAME = 0x09;
	
	public static int AlarmType;
	public static int DeviceType=1;//0 b018,1 b018+;
	public static int getBcd(String value) {
		Integer m = Integer.parseInt(value, 16);
		return m.intValue();
	}
	/**
	 * �ֽ�����ת���ַ�
	 * 
	 * @param data
	 * @return
	 */
	public static String byte2Hex(byte[] data) {
		
		if (data != null && data.length > 0) {
			StringBuilder sb = new StringBuilder(data.length);
			for (byte tmp : data) {
				sb.append(String.format("%02X ", tmp));
			}
			return sb.toString();
		}
		return "no data";
	}

	public static int getScreenHeight(Context context)
	{
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.heightPixels;
	}
	public static int getLowValue(byte b) {
		
		return b & 0xff;
	}
	public static int getHighValue(byte b) {
		return (b & 0xff) * 256;
	}
	public static byte[] getByteArray(byte b) {  
	    byte[] array = new byte[8];  
	    for (int i = 0; i <=7; i++) {  
	        array[i] = (byte)(b & 1);  
	        b = (byte) (b >> 1);
	    }  
	    return array;  
	}  
	
	public static String timeBucket(int timeBucket){
		int before=timeBucket+1;
		return getCalendarTime(timeBucket)+"-"+getCalendarTime(before);
	}
	public static String getCalendarTime(int timeBucket){
		SimpleDateFormat format=new SimpleDateFormat("HH:mm");
		String times="00:00";
		Calendar calendar=Calendar.getInstance();
		try {
			Date date=format.parse(times);
			calendar.setTimeInMillis(date.getTime()+timeBucket*15*60*1000l);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return format.format(calendar.getTime());
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
	public static String getWeekText(String day, String[] weeks) {
		StringBuffer sb = new StringBuffer();
		if (day.contains("0")) {
			sb.append(weeks[0]).append(" ");
		}
		if (day.contains("1")) {
			sb.append(weeks[1]).append(" ");
		}
		if (day.contains("2")) {
			sb.append(weeks[2]).append(" ");
		}
		if (day.contains("3")) {
			sb.append(weeks[3]).append(" ");
		}
		if (day.contains("4")) {
			sb.append(weeks[4]).append(" ");
		}
		if (day.contains("5")) {
			sb.append(weeks[5]).append(" ");
		}
		if (day.contains("6")) {
			sb.append(weeks[6]).append(" ");
		}
		return sb.toString();
	}
	public static  String ByteToHexString(byte a) {
		String s = "";
		s = Integer.toHexString(new Byte(a).intValue());
		if (s.length() == 1) {
			s = "0" + s;
		}
		return s;
	}
	public static String getHeartTime(byte hour, byte min, byte secend) {
		String date = "";
		date = ByteToHexString(hour) + ":" + ByteToHexString(min) + ":"
				+ ByteToHexString(secend);
		return date;
	}
	public static String getDate(byte year, byte month, byte day) {
		String date = "";
		date = ByteToHexString(year) + "-" + ByteToHexString(month) + "-"
				+ ByteToHexString(day);
		return date;
	}
	/**
	 * Decodes the local name
	 */
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
}
