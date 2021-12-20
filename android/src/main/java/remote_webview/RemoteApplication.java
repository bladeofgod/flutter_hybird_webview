package remote_webview;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import remote_webview.input_hook.InputMethodHolder;
import remote_webview.input_hook.OnInputMethodListener;
import remote_webview.utils.LogUtil;
import remote_webview.view.controller.InputToggleDelegate;

public class RemoteApplication extends Application {
    OnInputMethodListener onInputMethodListener;

    /**
     * About the code in this {@link #getSystemService(String)}.
     * @see InputToggleDelegate#inputServiceCall() 
     *
     */
    @Override
    protected void attachBaseContext(Context base) {
        int pid = android.os.Process.myPid();
        LogUtil.logMsg("RemoteApplication", "attachBaseContext  " + pid);
//        InputMethodHolder.init(base);
        super.attachBaseContext(base);
//        onInputMethodListener = new OnInputMethodListener() {
//            @Override
//            public void onShow(boolean result) {
//                LogUtil.logMsg("RemoteApplication","Show input method! " + pid +"  " + result);
//            }
//
//            @Override
//            public void onHide(boolean result) {
//                LogUtil.logMsg("RemoteApplication","Hide input method! " + pid +"  "+ result);
//            }
//        };
//        InputMethodHolder.registerListener(onInputMethodListener);
    }
}
