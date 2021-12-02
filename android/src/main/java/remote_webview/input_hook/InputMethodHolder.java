package remote_webview.input_hook;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Method;

import remote_webview.input_hook.hook.InputMethodManagerHook;
import remote_webview.utils.LogUtil;


public class InputMethodHolder {

    private static final String TAG = "InputMethodHolder";

    private static InputMethodManagerHook inputMethodManagerHook;

    private static InputMethodListener mInputMethodListener;

    public static void registerListener(OnInputMethodListener listener) {
        if(mInputMethodListener != null) {
            mInputMethodListener.registerListener(listener);
        }
    }

    public static void unregisterListener(OnInputMethodListener listener) {
        if (mInputMethodListener != null) {
            mInputMethodListener.unregisterListener(listener);
        }
    }

    public static void init(final Context context) {
        if (inputMethodManagerHook != null) {
            return;
        }
        try {
            mInputMethodListener = new InputMethodListener();
            inputMethodManagerHook = new InputMethodManagerHook(context);
            inputMethodManagerHook.onHook(context.getClassLoader());
            LogUtil.logMsg("inputMethodManagerHook", "hook");
            inputMethodManagerHook.setMethodInvokeListener(new InputMethodManagerHook.MethodInvokeListener() {
                @Override
                public void onMethod(Object obj, Method method, Object result) {
                    LogUtil.logMsg("inputMethodManagerHook", method.getName());
                    if (mInputMethodListener != null) {
                        mInputMethodListener.onMethod(obj, method, result);
                    }
                }
            });
        } catch (Throwable throwable) {
            Log.w(TAG, "hook failed! detail:" + Log.getStackTraceString(throwable));
        }
    }

    public static void release() {
        mInputMethodListener.clear();
        inputMethodManagerHook.setMethodInvokeListener(null);
        inputMethodManagerHook = null;
        mInputMethodListener = null;
    }

}
