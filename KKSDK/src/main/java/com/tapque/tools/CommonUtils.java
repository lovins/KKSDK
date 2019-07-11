package com.tapque.tools;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.tapque.ads.AdsCallbackCenter;
import com.tapque.ads.MyApplication;
import com.tapque.mopubads.BuildConfig;
import com.tapque.mopubads.R;
//import com.unity3d.player.UnityPlayer;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Secret
 * @since 2019/4/8
 */
public class CommonUtils {
    public  static  String  TAG="CommonUtils";
    private static boolean DEBUG = false;
    private static ShareToBaseAction shareToAction;
    private static void debug(boolean debug) {
        DEBUG = debug;
    }

    private static void initShareField() {
        if (null == shareToAction) {
            shareToAction = new ShareToBaseAction();
        }
    }
    /**
     * for share to ins
     */
    public static void shareIns(@NonNull Context context, String path) {
        initShareField();

        IAppInfo appInfo = shareToAction.getCustomizeApp(context.getApplicationContext(), "instagram.android", "video/*");
        if (null == appInfo) {
            Toast.makeText(context, "this is no target app", Toast.LENGTH_SHORT).show();
        } else {
            File videoFile = new File(path);
            if (videoFile.exists()) {
                shareToAction.shareImageToCustomizeApp(context.getApplicationContext(), appInfo, videoFile.getAbsolutePath(), "video");
            }
        }
    }
    /**
     * for share more
     */
    public static void androidShareMore(@NonNull Context context, String path, String title) {
        initShareField();
        File videoFile = new File(path);
        if (videoFile.exists()) {
            String fileName = videoFile.getAbsolutePath();
            shareToAction.shareWithLocal(context.getApplicationContext(), TextUtils.isEmpty(title) ? "Tell Friend" : title, fileName);
        } else {
            if (DEBUG) {
                Toast.makeText(context, "this is no target file", Toast.LENGTH_SHORT).show();
            }

        }
    }
    /**
     * for copy explicit text
     */
    public static void androidCopyText(@NonNull Context context, String text) {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (null != cm) {
            cm.setText(text);
        }
    }

    /**
     * for the phone vibrator
     * @param millisecond vibrator time
     */
    public static void androidVibrator(@NonNull Context context, long millisecond) {
        Vibrator vibrator = (Vibrator) context.getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.vibrate(millisecond);
    }
    /**
     * for check explicit permission
     * @param permission the special permission default is Manifest.permission.WRITE_EXTERNAL_STORAGE#{@link Manifest.permission}
     */
    public static boolean androidCheckPermission(@NonNull Context context, String permission) {
        if (TextUtils.isEmpty(permission)) {
            permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
    public static void androidInternalFile2External(Context context ,String filePath,String mime) {
        if (androidCheckPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            /**
             * create by SecreteYi,Don't modify.thank you
             */
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Uri.parse(filePath).getLastPathSegment());
            File file2 = new File(filePath);
            List<String> fileNameStrings = new ArrayList<>();
            List<String> fileMimeString = new ArrayList<>();
            if(copyVoxFile(file2,file) == 0){
                if (TextUtils.isEmpty(mime)) {
                    mime = "video/mp4";
                }
                fileNameStrings.add(file.getAbsolutePath());
                fileMimeString.add(mime);
                SingleMediaScanner singleMediaScanner = new SingleMediaScanner(context, fileNameStrings,fileMimeString);
                singleMediaScanner.scanFile();
                Toast.makeText(context, "SaveSucceed", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(context, "this is no target file", Toast.LENGTH_SHORT).show();
            }
        } else {
            androidCheckPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }
    static int copyVoxFile(File fromFile, File toFile) {
        try {
            InputStream is = new FileInputStream(fromFile);
            OutputStream os = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = is.read(bt)) > 0) {
                os.write(bt, 0, c);
            }
            is.close();
            os.close();
            return 0;
        } catch (Exception ex) {
            Log.e("Android", "copyVoxFile: " + ex.getMessage());
            return -1;
        }
    }

    /**
     * copy file content
     */
    public static int androidCopyFile(Context context, String fromFile, String toFile) {
        try {
            InputStream is = context.getAssets().open(fromFile);
            OutputStream os = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = is.read(bt)) > 0) {
                os.write(bt, 0, c);
            }
            os.flush();
            os.close();
            is.close();
            return 0;
        } catch (Exception e) {
            if (DEBUG) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return -1;
        }
    }
    /**
     * might have some problem
     *
     * show android soft keyboard
     */
    static  InputMethodManager inputMethodManager;
    static EditText editText;
    static View editorView;
    static  Activity mActivity;
    static boolean hasInitEditorView;
    static  String unityGameobjectName;
    static  String unityCallbackMethodName;

    public  static  void showSoftKeyboard(Activity activity, final String gameObjectName, final String callbackMethodName){
        mActivity=activity;
        unityGameobjectName=gameObjectName;
        unityCallbackMethodName=callbackMethodName;
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!hasInitEditorView){
                    hasInitEditorView=true;
                    if(DEBUG){
                        Log.e(TAG, "初始化软键盘:" );
                    }
                    editorView=LayoutInflater.from(mActivity).inflate(R.layout.editor,null);
                    mActivity.addContentView(editorView,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                }else{
                    if(DEBUG){
                        Log.e(TAG, "显示软键盘: " );
                    }
                    editorView.setVisibility(View.VISIBLE);
                }
                editText=editorView.findViewById(R.id.editor);
                editText.setFocusable(true);
                editText.setFocusableInTouchMode(true);
                editText.requestFocus();
                inputMethodManager =
                        (InputMethodManager)editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

                inputMethodManager.showSoftInput(editText, 0);
                editText.addTextChangedListener(textWatcher);
            }
        });
    }

    public  static  void clearSoftkeyboard(){
        if(null!=editorView&&null!=editText){
            editText.setText(null);
        }
    }
    static TextWatcher textWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(DEBUG){
                Log.e(TAG, "onTextChanged: "+String.valueOf(s));
            }
            AdsCallbackCenter.sendKeyBoardText(unityGameobjectName,unityCallbackMethodName,s.toString());
        }
        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    public  static  void hideSoftKeyboard(){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(null!=inputMethodManager&&inputMethodManager.isActive()){
                    editText.removeTextChangedListener(textWatcher);
                    editText.setText(null);
                    inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(),0);
                    editorView.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    public  static  String readStringFromAssets(Activity activity,String fileName){
        String returnString="";
        InputStream inputStream=null;
        try {
            inputStream=activity.getAssets().open(fileName);
        } catch (IOException e) {
            if(BuildConfig.DEBUG){
                Log.e(TAG, "读取安卓资源文件输入流异常: "+e.getLocalizedMessage());
            }
        }
        InputStreamReader inputStreamReader=null;
        if(null!=inputStream){
            try {
                inputStreamReader=new InputStreamReader(inputStream,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                if(BuildConfig.DEBUG){
                    Log.e(TAG, "读取安卓资源文件异常: "+e.getLocalizedMessage());
                }
            }
        }

        if(null!=inputStreamReader){
            BufferedReader reader = new BufferedReader(inputStreamReader);
            StringBuffer sb = new StringBuffer("");
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            returnString=sb.toString();
        }
        return  returnString;
    }
    public  static  void goGoogleComment(Activity activity,String packageName){
        String url= "https://play.google.com/store/apps/details?id=" +packageName;
        Uri marketUri = Uri.parse(url);
        try {
            Intent it = new Intent(Intent.ACTION_VIEW, marketUri);
            it.setPackage("com.android.vending");
            activity.startActivity(it);
        } catch (Exception e) {
            Intent it = new Intent(Intent.ACTION_VIEW, marketUri);
            activity.startActivity(it);
        }
        return;
    }
    public static boolean isAppInstall(Activity  activity, String packageName) {
        final PackageManager packageManager = activity.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> packageInfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < packageInfo.size(); i++) {
            if (packageInfo.get(i).packageName.equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }
    public  static  void goStore(Activity activity,String trackUrl){
        String url=trackUrl;
        Uri marketUri = Uri.parse(url);
        try {
            Log.e(TAG, "跳转商店: " );
            Intent it = new Intent(Intent.ACTION_VIEW, marketUri);
            it.setPackage("com.android.vending");
            activity.startActivity(it);
        } catch (Exception e) {
            Intent it = new Intent(Intent.ACTION_VIEW, marketUri);
            activity.startActivity(it);
        }
        return;
    }
}
