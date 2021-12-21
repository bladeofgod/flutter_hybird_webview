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

    public static void logStackTree(String tag, StackTraceElement[] stackTraceElements) {
        logPattern();
        for(int i = 0; i < stackTraceElements.length; i++) {
            LogUtil.logMsg(tag,
                    "getClassName   " + stackTraceElements[i].getClassName(),
                    "getFileName   " + stackTraceElements[i].getFileName(),
                    "getMethodName   " + stackTraceElements[i].getMethodName(),
                    "getLineNumber   " + stackTraceElements[i].getLineNumber()
            );
        }
        logPattern();
    }
    
    public static void logPattern() {
        Log.e("Remote View",PATTERN);
    }
}
