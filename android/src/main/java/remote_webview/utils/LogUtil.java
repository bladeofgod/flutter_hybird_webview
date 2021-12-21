package remote_webview.utils;

import android.util.Log;

import java.util.Arrays;
import java.util.Map;

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
            Log.d(tag,
                    "getClassName   " + stackTraceElements[i].getClassName()+ '\n' +
                    "getFileName   " + stackTraceElements[i].getFileName()+ '\n' +
                    "getMethodName   " + stackTraceElements[i].getMethodName()+ '\n' +
                    "getLineNumber   " + stackTraceElements[i].getLineNumber()+ '\n'
            );
        }
        logPattern();
    }

    public static void logAllThreadStackTree() {
        Map<Thread, StackTraceElement[]> t =  Thread.getAllStackTraces();
        for(Thread tt : t.keySet()) {
            logStackTree(tt.getName(), t.get(tt));
        }
    }
    
    public static void logPattern() {
        Log.e("Remote View",PATTERN);
    }
}
