package remote_webview.service.hub;

import android.os.RemoteException;

import androidx.annotation.Nullable;

import java.util.HashMap;

import remote_webview.interfaces.IMockMethodResult;
import remote_webview.model.MethodModel;
import remote_webview.service.MainServicePresenter;
import remote_webview.utils.StringUtil;

public class RemoteCallbackHandler extends BaseCallbackHandler {

    /**
     * identifier this handle and link to a {@link MethodModel}
     */
    private final long id;

    /**
     * @param id is invoke timestamp {@linkplain MethodModel}.getInvokeTimeStamp()
     */
    public RemoteCallbackHandler(long id) {
        this.id = id;
    }

    @Override
    public void success(@Nullable HashMap var1) {
        super.success(var1);
        try {
            MainServicePresenter.getInstance()
                    .getMainResultCallbackBinder()
                    .remoteSuccess(id, StringUtil.getMapToString(var1));
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void error(String var1, @Nullable String var2, @Nullable HashMap var3) {
        super.error(var1, var2, var3);
        try {
            MainServicePresenter.getInstance()
                    .getMainResultCallbackBinder()
                    .remoteError(id, var1, var2, StringUtil.getMapToString(var3));
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void notImplemented() {
        super.notImplemented();
        try {
            MainServicePresenter.getInstance()
                    .getMainResultCallbackBinder()
                    .remoteNotImplemented(id);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
