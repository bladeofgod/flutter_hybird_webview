package io.flutter.plugins.webviewflutterexample;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import remote_webview.input_hook.InputMethodHolder;
import remote_webview.input_hook.OnInputMethodListener;
import remote_webview.utils.LogUtil;

public class MyApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        
    }
}
