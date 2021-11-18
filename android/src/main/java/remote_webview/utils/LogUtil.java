package remote_webview.utils;

import android.util.Log;

public class LogUtil {
    
    private static final String PATTERN = "=======================Message=======================";
    
    public static void logMsg(String tag, String info) {
        logPattern();
        Log.d(tag,info);
        logPattern();
    }
    
    public static void logPattern() {
        Log.e("Remote View",PATTERN);
    }
}
