package remote_webview.interfaces;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

import remote_webview.mock.MockMethodCall;

public interface IMockMethodHandler {

    /**
     *
     * @param methodCall method model with name and argument
     * @param result result call back, must ensure call it after method-call.
     */
    @UiThread
    void onMethodCall(@NonNull MockMethodCall methodCall, @NonNull IMockMethodResult result);
}
