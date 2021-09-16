package remote_webview.view;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import remote_webview.RemoteWebViewPlugin;
import remote_webview.utils.LogUtil;

abstract public class RemoteWebViewActivity extends FlutterActivity {

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        flutterEngine.getPlugins().add(new RemoteWebViewPlugin(this));
    }
}
