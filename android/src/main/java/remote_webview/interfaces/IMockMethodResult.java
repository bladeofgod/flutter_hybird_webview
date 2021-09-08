package remote_webview.interfaces;

import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

import java.util.HashMap;

public interface IMockMethodResult {

    @UiThread
    void success(@Nullable HashMap var1);

    @UiThread
    void error(String var1, @Nullable String var2, @Nullable HashMap var3);

    @UiThread
    void notImplemented();
    
}
