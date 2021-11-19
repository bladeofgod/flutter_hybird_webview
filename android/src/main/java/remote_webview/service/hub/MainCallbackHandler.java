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
    public void success(@Nullable final HashMap var1) {
        HandlerUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    LogUtil.logMsg("Process", " result callback : " +id+ "  "+ var1.toString());
                    MainBinderCommHub.getInstance().getFlutterResult(id).success(true);
                    MainBinderCommHub.getInstance().removeCacheResultCallback(id);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void error(final String var1, @Nullable final String var2, @Nullable final HashMap var3) {
        HandlerUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    MainBinderCommHub.getInstance().getFlutterResult(id).error(var1, var2, var3);
                    MainBinderCommHub.getInstance().removeCacheResultCallback(id);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                
            }
        });
        
    }

    @Override
    public void notImplemented() {
        HandlerUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    MainBinderCommHub.getInstance().getFlutterResult(id).notImplemented();
                    MainBinderCommHub.getInstance().removeCacheResultCallback(id);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
