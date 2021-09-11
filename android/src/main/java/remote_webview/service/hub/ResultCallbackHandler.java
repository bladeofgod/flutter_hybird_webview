package remote_webview.service.hub;

import androidx.annotation.Nullable;

import java.util.HashMap;

import remote_webview.interfaces.IMockMethodResult;
import remote_webview.model.MethodModel;

public class ResultCallbackHandler implements IMockMethodResult {

    /**
     * identifier this handle and link to a {@link MethodModel}
     */
    private final long id;

    public ResultCallbackHandler(long id) {
        this.id = id;
    }

    @Override
    public void success(@Nullable HashMap var1) {
        
    }

    @Override
    public void error(String var1, @Nullable String var2, @Nullable HashMap var3) {

    }

    @Override
    public void notImplemented() {

    }
}
