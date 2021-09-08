package remote_webview.mock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

import java.util.HashMap;

public class MockMethodChannel {
    /**
     * some private channel's name of webview
     */
    private final String name;

    public MockMethodChannel(String name) {
        this.name = name;
    }

    @UiThread
    public void invokeMethod(@NonNull String method, @Nullable HashMap arguments) {
        
    }
    
}
