package remote_webview.activity;

import android.view.MotionEvent;

import io.flutter.embedding.android.FlutterActivity;

/**
 * App's Main-activity must extends this class.
 * Working for remote-web, like handle remote-service connect,
 * touchEvent dispatch, and so on.
 */

abstract public class RemoteWebActivity extends FlutterActivity {

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }
}
