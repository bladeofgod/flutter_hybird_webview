package remote_webview.interfaces;

import android.view.KeyEvent;
import android.view.MotionEvent;

public interface IRemoteView {
    void init();
    
    void release();
    
    void dispatchTouchEvent(MotionEvent event);
    
    void dispatchKeyEvent(KeyEvent keyEvent);
    
}
