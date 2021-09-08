package remote_webview.mock;

import androidx.annotation.Nullable;

import java.util.HashMap;

public class MockMethodCall {
    public final String method;

    public final HashMap arguments;

    public MockMethodCall(String method, HashMap arguments) {
        if (method == null) {
            throw new AssertionError("Parameter method must not be null.");
        } else {
            this.method = method;
            this.arguments = arguments;
        }
    }

    public HashMap arguments() {
        return this.arguments;
    }

    @Nullable
    public Object argument(String key) {
        if (this.arguments == null) {
            return null;
        } else {
            return arguments.get(key);
        }
    }

    public boolean hasArgument(String key) {
        if (this.arguments == null) {
            return false;
        } else {
            return arguments.containsKey(key);
        }
    }
}
