package com.tapque.tools;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

import java.util.List;


/**
* @author Secret
* @since 2019/4/9
*/
public class SingleMediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {

    private MediaScannerConnection mMediaScannerConnection;
    private List<String> mFileName;
    private List<String> mFileMimeTypes;
    private int scanFileCount;
    private int totalFileCount;

    public SingleMediaScanner(Context context, List<String> fileName, List<String> fileMimeTypes){
        mFileName = fileName;
        mFileMimeTypes = fileMimeTypes;
        mMediaScannerConnection = new MediaScannerConnection(context,this);
        scanFileCount = 0;
        totalFileCount = fileName.size();
    }

    public void scanFile(){
        if(null != mMediaScannerConnection && !mMediaScannerConnection.isConnected()){
            mMediaScannerConnection.connect();
        }
    }

    @Override
    public void onMediaScannerConnected() {
        for (int i = 0; i < mFileName.size(); i++) {
            if(null != mFileMimeTypes){
                mMediaScannerConnection.scanFile(mFileName.get(i),mFileMimeTypes.get(i));
            }
        }
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
         scanFileCount++;
         if(scanFileCount == totalFileCount){
             if(null != mMediaScannerConnection && mMediaScannerConnection.isConnected()){
                 mMediaScannerConnection.disconnect();
             }
         }
    }

}
