// IRemoteViewFactoryBinder.aidl
package remote_webview;

// Declare any non-default types here with import statements
import remote_webview.model.WebViewCreationParamsModel;

interface IRemoteViewFactoryBinder {
    void createWithSurface(in WebViewCreationParamsModel createParams, in Surface surface);

    void dispatchTouchEvent(String surfaceId, in MotionEvent event);
}