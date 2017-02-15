package io.github.mylittleswift.blesdk;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.WindowManager;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class Tools {
	public  static int OTAtype=0;
	public static BluetoothDevice device=null;
	public static boolean isPick;
	public static RFLampDevice deviceMGR = null;

	private static Bitmap after;
	
	public static int getRectWidth(Context context){
		int width;
		WindowManager manager=(WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		width=manager.getDefaultDisplay().getWidth();
		return width/12;
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

	// ********************��ݼ��������**********************************

	/**
	 * ��ݼ���
	 * 
	 * @param arrayLengh
	 * @param arrayEncode
	 * @param arrayDecode
	 */
	
	
	public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  

	/**
	 * ��ȡϵͳʱ��
	 * 
	 * @return
	 */
	public static byte[] getSystemTime() {
		byte[] cal = new byte[7];
		Calendar calendar = Calendar.getInstance();
		cal[0] = (byte) (calendar.get(Calendar.YEAR) & 0xff);
		cal[1] = (byte) (calendar.get(Calendar.YEAR) >> 8 & 0xff);
		cal[2] = (byte) ((calendar.get(Calendar.MONTH) + 1) & 0xff);
		cal[3] = (byte) (calendar.get(Calendar.DAY_OF_MONTH) & 0xff);
		cal[4] = (byte) (calendar.get(Calendar.HOUR_OF_DAY) & 0xff);
		cal[5] = (byte) (calendar.get(Calendar.MINUTE) & 0xff);
		cal[6] = (byte) (calendar.get(Calendar.SECOND) & 0xff);
		MyLog.d("getSystemTime   " + Tools.byte2Hex(cal));
		return cal;
	}
	/**
	 * sd���Ƿ����
	 * 
	 * @return
	 */
	public static boolean existSDCard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else
			return false;
	}
	public static class dir{
		@SuppressLint("SdCardPath")
		public static final String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath()+"/funlight";
		/**������ϢͼƬ��Ŀ¼*/
		public static final String pushImage = baseDir + "/images";
		/**speakerĿ¼*/
		public static final String speaker = baseDir + "/media/";
		/**speaker�ļ�*/
		public static final String speakerFile = speaker+"/"+"record.amr";
		
		/**Log��־**/
		public static final String log = baseDir + "/Log";
		/**6������Log��־**/
		public static final String btCommandLog = log+"/btCommandLog";
	}
	
	
	public static List<UUID> parseUuids(byte[] advertisedData) {
	     List<UUID> uuids = new ArrayList<UUID>();
	     ByteBuffer buffer = ByteBuffer.wrap(advertisedData).order(ByteOrder.LITTLE_ENDIAN);
	     while (buffer.remaining() > 2) {
	         byte length = buffer.get();
	         if (length == 0) break;

	         byte type = buffer.get();
	         switch (type) {
	             case 0x02: // Partial list of 16-bit UUIDs
	             case 0x03: // Complete list of 16-bit UUIDs
	                 while (length >= 2) {
	                     uuids.add(UUID.fromString(String.format(
	                             "%08x-0000-1000-8000-00805f9b34fb", buffer.getShort())));
	                     length -= 2;
	                 }
	                 break;

	             case 0x06: // Partial list of 128-bit UUIDs
	             case 0x07: // Complete list of 128-bit UUIDs
	                 while (length >= 16) {
	                     long lsb = buffer.getLong();
	                     long msb = buffer.getLong();
	                     uuids.add(new UUID(msb, lsb));
	                     length -= 16;
	                 }
	                 break;

	             default:
	                 buffer.position(buffer.position() + length - 1);
	                 break;
	         }
	     }

	     return uuids;}
}
