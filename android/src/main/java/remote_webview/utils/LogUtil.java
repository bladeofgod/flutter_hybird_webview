package remote_webview.utils;

import android.util.Log;

import java.util.Arrays;

public class LogUtil {
    
    private static final String PATTERN = "=======================Diagnosis Info=======================";
    
    public static void logMsg(String tag, String ...info) {
        logPattern();
        for(String s : info) {
            Log.d(tag, s + '\n');
        }
        logPattern();
    }
    
    public static void logPattern() {
        Log.e("Remote View",PATTERN);
    }
}
