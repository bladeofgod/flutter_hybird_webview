package remote_webview.service.hub;

import android.os.RemoteException;

import androidx.annotation.Nullable;

import java.util.HashMap;

import remote_webview.interfaces.IMockMethodResult;
import remote_webview.model.MethodModel;
import remote_webview.service.MainServicePresenter;
import remote_webview.service.RemoteServicePresenter;
import remote_webview.utils.StringUtil;

public class MainCallbackHandler implements IMockMethodResult {

    /**
     * identifier this handle and link to a {@link MethodModel}
     */
    private final long id;

    /**
     * @param id is invoke timestamp {@linkplain MethodModel}.getInvokeTimeStamp()
     */
    public MainCallbackHandler(long id) {
        this.id = id;
    }

    @Override
    public void success(@Nullable HashMap var1) {
        try {
            MainBinderCommHub.getInstance().getFlutterResult(id).success(var1);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void error(String var1, @Nullable String var2, @Nullable HashMap var3) {
        try {
            MainBinderCommHub.getInstance().getFlutterResult(id).error(var1, var2, var3);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void notImplemented() {
        try {
            MainBinderCommHub.getInstance().getFlutterResult(id).notImplemented();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
