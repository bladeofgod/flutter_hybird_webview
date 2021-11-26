package remote_webview.service.hub;

import android.os.RemoteException;

import androidx.annotation.Nullable;

import java.util.HashMap;

import remote_webview.interfaces.IMockMethodResult;
import remote_webview.model.MethodModel;
import remote_webview.service.MainServicePresenter;
import remote_webview.service.RemoteServicePresenter;
import remote_webview.utils.HandlerUtil;
import remote_webview.utils.LogUtil;
import remote_webview.utils.StringUtil;

/**
 * For receive a result from flutter and send it to remote-view(another process).
 *
 * @see FlutterCallbackHandler
 */

public class MainCallbackHandler extends BaseCallbackHandler {

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
    public void success(@Nullable final HashMap var1) {
        super.success(var1);
        try {
            RemoteServicePresenter.getInstance()
                    .getRemoteResultCallbackBinder()
                    .remoteSuccess(id, StringUtil.getMapToString(var1));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void error(final String var1, @Nullable final String var2, @Nullable final HashMap var3) {
        super.error(var1, var2, var3);
        try {
            RemoteServicePresenter.getInstance()
                    .getRemoteResultCallbackBinder()
                    .remoteError(id, var1, var2, StringUtil.getMapToString(var3));
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void notImplemented() {
        super.notImplemented();
        try {
            RemoteServicePresenter.getInstance()
                    .getRemoteResultCallbackBinder()
                    .remoteNotImplemented(id);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
