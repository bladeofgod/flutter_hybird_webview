package remote_webview.interfaces;

import androidx.annotation.BinderThread;
import androidx.annotation.MainThread;

public interface ISoftInputCallback {

    @MainThread
    void showSoftInput();

    @MainThread
    void hideSoftInput();

}
