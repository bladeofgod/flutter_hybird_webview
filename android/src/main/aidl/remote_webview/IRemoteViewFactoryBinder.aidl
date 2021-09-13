// IRemoteViewFactoryBinder.aidl
package remote_webview;

// Declare any non-default types here with import statements

interface IRemoteViewFactoryBinder {
    void createWithSurface(String orders, in Surface surface);

    void dispatchTouchEvent(String surfaceId, in MotionEvent event);
}