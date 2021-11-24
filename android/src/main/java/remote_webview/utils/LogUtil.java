package remote_webview.utils;

import android.util.Log;

import java.util.Arrays;

public class LogUtil {
    
    private static final String PATTERN = "=======================Diagnosis Info=======================";
    
    public static void logMsg(String tag, String ...info) {
        logPattern();
        Log.d(tag, Arrays.toString(info));
        logPattern();
    }
    
    public static void logPattern() {
        Log.e("Remote View",PATTERN);
    }
}
