package remote_webview.utils;


import android.os.Handler;
import android.os.Looper;

public class HandlerUtil extends Handler {
    private static HandlerUtil instance = new HandlerUtil(Looper.getMainLooper());

    protected HandlerUtil(Looper looper) {super(looper);}

    public static HandlerUtil getInstance() {return instance;}

    public static void runOnUiThread(Runnable runnable) {
        instance.post(runnable);
    }

}
