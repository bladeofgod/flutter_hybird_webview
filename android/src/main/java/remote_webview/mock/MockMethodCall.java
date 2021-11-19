package remote_webview.mock;

import androidx.annotation.Nullable;

import java.util.HashMap;

import remote_webview.model.MethodModel;

public class MockMethodCall {
    
    //surface's id.
    public final long id;
    
    public final String method;

    public final HashMap arguments;

    /**
     * @see MethodModel#getNeedCallback() 
     */
    public final byte needCallback;

    public MockMethodCall(long id, String method, HashMap arguments, byte needCallback) {
        if (method == null) {
            throw new AssertionError("Parameter method must not be null.");
        } else {
            this.method = method;
            this.arguments = arguments;
            this.id = id;
            this.needCallback = needCallback;
        }
    }

    public HashMap arguments() {
        return this.arguments;
    }

    @Override
    public String toString() {
        return "MockMethodCall{" +
                "id=" + id +
                ", method='" + method + '\'' +
                ", arguments=" + arguments + '\'' +
                ", needCallback=" + needCallback +
                '}';
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
