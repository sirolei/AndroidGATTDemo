package com.siro.blesounddemo.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by siro on 2016/1/26.
 */
public class SystemInfoUtil {

    public static final String TAG = SystemInfoUtil.class.getSimpleName();
    public static HashMap<String, String> cpuInfo = new HashMap<String, String>();
    private static final String KEY_CPU_NAME = "Processor";
    private static final String KEY_CPU_FACTORY = "Hardware";
    static {
        cpuInfo = getCpuInfo();
    }

    /**
     * 获取cpu的名字，例如： ARMv7 Processor rev 1 (v7l)
     * @return
     */
    public static String getCpuName()
    {
        return cpuInfo.get(KEY_CPU_NAME);
    }

    public static String getCpuFactoryName(){
        return cpuInfo.get(KEY_CPU_FACTORY);
    }

    public static HashMap<String, String> getCpuInfo(){
        HashMap<String, String> cpuInfo = new HashMap<>();

        FileReader fr = null;
        BufferedReader br = null;
        try
        {
            fr = new FileReader("/proc/cpuinfo");
            br = new BufferedReader(fr);
            String text;
            while ( (text= br.readLine()) != null){
                String[] array = text.split(":\\s+", 2);
                if (array != null && array.length >= 2){
                    cpuInfo.put(array[0].trim(), array[1].trim());
                    Log.d(TAG, "key " + array[0] + " --- value " + array[1]);
                }
            }
            return cpuInfo;
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {
            if (fr != null)
                try
                {
                    fr.close();
                } catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            if (br != null)
                try
                {
                    br.close();
                } catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
        return null;

    }

}


