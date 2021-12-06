package remote_webview.interfaces;

import android.view.KeyEvent;

public interface IActivityKeyEventCallback {

    void dispatchKeyEvent(KeyEvent event);

    boolean consumeKeyEvent();
}
