package remote_webview.activity;

import android.view.KeyEvent;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import remote_webview.RemoteWebViewPlugin;
import remote_webview.interfaces.IActivityKeyEventCallback;

/**
 * App's Main-activity must extends this class.
 * Working for remote-web, like handle remote-service connect,
 * touchEvent dispatch, and so on.
 */

abstract public class RemoteWebActivity extends FlutterActivity {

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        flutterEngine.getPlugins().add(new RemoteWebViewPlugin(this));
    }

    @NonNull
    private IActivityKeyEventCallback keyEventCallback;

    public void setKeyEventCallback(@NonNull IActivityKeyEventCallback keyEventCallback) {
        this.keyEventCallback = keyEventCallback;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(keyEventCallback.consumeKeyEvent()) {
            keyEventCallback.dispatchKeyEvent(event);
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
