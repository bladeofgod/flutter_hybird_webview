package remote_webview;

import android.app.Application;
import android.content.Context;

import remote_webview.input_hook.InputMethodHolder;
import remote_webview.utils.LogUtil;

public class RemoteApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        LogUtil.logMsg("RemoteApplication", "attachBaseContext");
        //InputMethodHolder.init(base);
        super.attachBaseContext(base);
    }
}
