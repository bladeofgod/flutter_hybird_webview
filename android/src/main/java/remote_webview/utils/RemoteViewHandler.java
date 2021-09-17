package remote_webview.utils;


import android.os.Handler;
import android.os.Looper;

public class RemoteViewHandler extends Handler {
    private static RemoteViewHandler instance = new RemoteViewHandler(Looper.getMainLooper());

    protected RemoteViewHandler(Looper looper) {super(looper);}

    public static RemoteViewHandler getInstance() {return instance;}

    public static void runOnUiThread(Runnable runnable) {
        instance.post(runnable);
    }

}
