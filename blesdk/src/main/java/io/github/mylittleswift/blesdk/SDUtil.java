package io.github.mylittleswift.blesdk;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

/**
 * SD卡管理操�?
 * @author Jerry Lee
 *
 */
public class SDUtil {
	public static final String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath()+"/BleTest";
	private static String TAG = SDUtil.class.getSimpleName();
	public static final String log = baseDir + "/Log";
	public static final String btCommandLog = log+"/btCommandLog";
	private static double MB = 1024;
	private static double FREE_SD_SPACE_NEEDED_TO_CACHE = 10;
	private static double IMAGE_EXPIRE_TIME = 10;

	

	
	/**
	 * sd卡是否存�?
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
	public static String getDateString(Date date) {
		return formatDate(date, "yyyy-MM-dd");
	}
	public static String formatDate(Date date, String format) {
		String result = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			result = sdf.format(date);
		} catch (Exception e) {

		}

		return result;
	}

	

	/**
	 * 保存蓝牙命令log（测试用�?
	 * 
	 * @param fileName
	 * @param content
	 */
	public static void saveBTLog(String fileName, String content) {
		if (content == null || fileName == null)
			return;
		if (!existSDCard()) {
			return;
		}
		// 不存在则创建目录
		File dir = new File(btCommandLog);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String rFileName = btCommandLog+"/"+fileName+".txt";
		File file = new File(rFileName);
		if (!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		OutputStreamWriter os = null;
		try {
			os = new OutputStreamWriter(new FileOutputStream(file,true)) ;
			os.write("\n"+content);
			os.flush();
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	/**
	 * 计算sdcard上的剩余空间
	 * 
	 * @return
	 */
	public static int getFreeSpace() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
				.getPath());
		double sdFreeMB = ((double) stat.getAvailableBlocks() * (double) stat
				.getBlockSize()) / MB;
		return (int) sdFreeMB;
	}

	public static void removeExpiredCache(String dirPath, String filename) {
		File file = new File(dirPath, filename);
		if (System.currentTimeMillis() - file.lastModified() > IMAGE_EXPIRE_TIME) {
			Log.i(TAG, "Clear some expiredcache files ");
			file.delete();
		}
	}

	public static void removeCache(String dirPath) {
		File dir = new File(dirPath);
		File[] files = dir.listFiles();
		if (files == null) {
			return;
		}
		if (FREE_SD_SPACE_NEEDED_TO_CACHE > getFreeSpace()) {
			int removeFactor = (int) ((0.4 * files.length) + 1);
			Arrays.sort(files, new FileLastModifSort());
			Log.i(TAG, "Clear some expiredcache files ");
			for (int i = 0; i < removeFactor; i++) {
				files[i].delete();
			}

		}

	}

	private static class FileLastModifSort implements Comparator<File> {
		@Override
		public int compare(File arg0, File arg1) {
			if (arg0.lastModified() > arg1.lastModified()) {
				return 1;
			} else if (arg0.lastModified() == arg1.lastModified()) {
				return 0;
			} else {
				return -1;
			}
		}
	}
}