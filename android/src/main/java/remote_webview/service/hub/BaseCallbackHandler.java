package remote_webview.service.hub;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import remote_webview.interfaces.IMockMethodResult;

public class BaseCallbackHandler implements IMockMethodResult {
    
    private AtomicBoolean done = new AtomicBoolean(false);

    private void invalid() {
        if(done.getAndSet(true)) {
            throw new IllegalStateException("BaseCallbackHandler : callback already called");
        }
    }

    /**
     *
     * @return callback is already called.
     */
    public boolean isInvalid() {
        return done.get();
    }

    @CallSuper
    @Override
    public void success(@Nullable HashMap var1) {
        invalid();
    }

    @CallSuper
    @Override
    public void error(String var1, @Nullable String var2, @Nullable HashMap var3) {
        invalid();
    }

    @CallSuper
    @Override
    public void notImplemented() {
        invalid();
    }
}
