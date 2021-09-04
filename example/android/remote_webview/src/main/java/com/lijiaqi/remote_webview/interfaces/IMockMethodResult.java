package com.lijiaqi.remote_webview.interfaces;

import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

public interface IMockMethodResult {

    @UiThread
    void success(@Nullable Object var1);

    @UiThread
    void error(String var1, @Nullable String var2, @Nullable Object var3);

    @UiThread
    void notImplemented();
    
}
