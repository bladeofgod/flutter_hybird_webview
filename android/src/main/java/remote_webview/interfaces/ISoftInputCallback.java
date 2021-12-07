package remote_webview.interfaces;

import androidx.annotation.BinderThread;
import androidx.annotation.MainThread;

public interface ISoftInputCallback {

    @MainThread
    void showSoftInput(long viewId);

    @MainThread
    void hideSoftInput(long viewId);

}
