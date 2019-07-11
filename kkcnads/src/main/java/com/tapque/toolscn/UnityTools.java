package com.tapque.toolscn;

import android.app.Activity;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class UnityTools {
    public  static   byte[] LoadAsset(Activity activity, String fileName){
        InputStream inputStream = null ;
        try {
            inputStream = activity.getAssets().open(fileName);
        } catch (IOException e) {
            Log.v ("unity", e.getMessage());
        }
        return readtextbytes(inputStream);
    }
    private static byte[] readtextbytes(InputStream inputStream)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//长度这里暂时先写成1024
        byte buf[] = new byte [1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
        }
        return outputStream.toByteArray();
    }

}



