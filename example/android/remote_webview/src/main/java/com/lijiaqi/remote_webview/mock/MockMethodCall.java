package com.lijiaqi.remote_webview.mock;

import androidx.annotation.Nullable;

import org.json.JSONObject;

import java.util.Map;

public class MockMethodCall {
    public final String method;
    /**
     * Because this is mock class, so it force {@link arguments} to Map
     */
    public final Map arguments;

    public MockMethodCall(String method, Map arguments) {
        if (method == null) {
            throw new AssertionError("Parameter method must not be null.");
        } else {
            this.method = method;
            this.arguments = arguments;
        }
    }

    public Map arguments() {
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
