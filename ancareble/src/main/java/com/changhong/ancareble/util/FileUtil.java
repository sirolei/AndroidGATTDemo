package com.changhong.ancareble.util;

import android.os.Environment;

import java.io.File;

/**
 * Created by siro on 2016/1/19.
 */
public class FileUtil {
    private static final  String EXTERNAL_CACHE_DIRECTORY = "Ancare";
    private static final String TEMP_FILE = "tmp.ble";

    public static File getExternalCacheDirectory(){
        if (isExternalStorageWritable()){
            File rootDir = Environment.getExternalStorageDirectory();
            File cacheFile = new File(rootDir, EXTERNAL_CACHE_DIRECTORY);
            if (!cacheFile.isDirectory()){
                cacheFile.mkdir();
            }
            return cacheFile;
        }
        return null;
    }

    public static File getDataTempFile(){
        File file = new File(getExternalCacheDirectory(), TEMP_FILE);
        if (file.exists()){
            file.delete();
            file = new File(file.getAbsolutePath());
        }
        return file;
    }

    public static boolean isExternalStorageWritable(){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)){
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageReadable(){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)){
            return true;
        }
        return false;
    }
}
