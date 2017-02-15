package io.github.mylittleswift.blesdk;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * Created by Administrator on 2017/1/5.
 */

public class ZipUtils {

    public static List<String> viewZipFiles(File zip) {

        List<String> list = new ArrayList<>();
        try {
            ZipFile zipFile = new ZipFile(zip);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            ZipEntry entry = null;

            for (; entries.hasMoreElements(); ) {
                entry = entries.nextElement();
                list.add(entry.getName());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static boolean uncompressZipFile(File zip, String unzipPath) {


        boolean result = true;
        if (unzipPath.lastIndexOf('\\') == unzipPath.toCharArray().length - 1 || unzipPath.lastIndexOf('/') == unzipPath.toCharArray().length - 1) {

            unzipPath = unzipPath.substring(0, unzipPath.toCharArray().length - 1);

        }


        try {
            ZipFile zipFile = new ZipFile(zip);

            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            byte[] buffer = new byte[1024];
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory()) {

                    String dirstr = unzipPath + "/" + entry.getName();
                    dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
                    File file = new File(dirstr);
                    file.mkdir();
                    continue;
                }

                File current_file = new File(unzipPath + "/" + entry.getName());
                current_file.createNewFile();

                InputStream is = new BufferedInputStream(zipFile.getInputStream(entry));
                OutputStream os = new BufferedOutputStream(new FileOutputStream(current_file));

                int readLen = 0;
                while ((readLen = is.read(buffer, 0, buffer.length)) != -1) {
                    os.write(buffer, 0, readLen);
                }
                os.close();
                is.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    /**
     * 2     * 解压缩功能.
     * 3     * 将zipFile文件解压到folderPath目录下.
     * 4     * @throws Exception
     * 5
     */
    public int upZipFile(File zipFile, String folderPath) throws ZipException, IOException {
        //public static void upZipFile() throws Exception{
        ZipFile zfile = new ZipFile(zipFile);
        Enumeration zList = zfile.entries();
        ZipEntry ze = null;
        byte[] buf = new byte[1024];
        while (zList.hasMoreElements()) {
            ze = (ZipEntry) zList.nextElement();
            if (ze.isDirectory()) {
                Log.d("upZipFile", "ze.getName() = " + ze.getName());
                String dirstr = folderPath + ze.getName();
                //dirstr.trim();
                dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
                Log.d("upZipFile", "str = " + dirstr);
                File f = new File(dirstr);
                f.mkdir();
                continue;
            }
            Log.d("upZipFile", "ze.getName() = " + ze.getName());
            OutputStream os = new BufferedOutputStream(new FileOutputStream(getRealFileName(folderPath, ze.getName())));
            InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
            int readLen = 0;
            while ((readLen = is.read(buf, 0, 1024)) != -1) {
                os.write(buf, 0, readLen);
            }
            is.close();
            os.close();
        }
        zfile.close();
        Log.d("upZipFile", "finishssssssssssssssssssss");
        return 0;
    }

    /**
     * 给定根目录，返回一个相对路径所对应的实际文件名.
     *
     * @param baseDir     指定根目录
     * @param absFileName 相对路径名，来自于ZipEntry中的name
     * @return java.io.File 实际的文件
     */

    // fuck
    public static File getRealFileName(String baseDir, String absFileName) {
        String[] dirs = absFileName.split("/");
        File ret = new File(baseDir);
        String substr = null;
        if (dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                substr = dirs[i];
                try {
                    //substr.trim();
                    substr = new String(substr.getBytes("8859_1"), "GB2312");

                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                ret = new File(ret, substr);
            }
            Log.d("upZipFile", "1ret = " + ret);
            if (!ret.exists())
                ret.mkdirs();
            substr = dirs[dirs.length - 1];
            try {
                //substr.trim();
                substr = new String(substr.getBytes("8859_1"), "GB2312");
                Log.d("upZipFile", "substr = " + substr);
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            ret = new File(ret, substr);
            Log.d("upZipFile", "2ret = " + ret);
            return ret;
        }
        return ret;
    }
}
