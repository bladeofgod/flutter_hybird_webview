package com.lijiaqi.remote_webview.interfaces;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

import com.lijiaqi.remote_webview.mock.MockMethodCall;

public interface IMockMethodHandler {
    @UiThread
    void onMethodCall(@NonNull MockMethodCall methodCall, @NonNull IMockMethodResult result);
}
