package remote_webview.service.hub;

import androidx.annotation.Nullable;

import java.util.HashMap;

import remote_webview.model.MethodModel;
import remote_webview.utils.HandlerUtil;
import remote_webview.utils.LogUtil;

/**
 * @author LiJiaqi
 * @date 2021/11/26
 * Description: For receive a result from remote and send it to flutter.
 *
 * @see MainCallbackHandler
 */
public class FlutterCallbackHandler extends BaseCallbackHandler {


    /**
     * identifier this handle and link to a {@link MethodModel}
     */
    private final long id;

    /**
     * @param id is invoke timestamp {@linkplain MethodModel}.getInvokeTimeStamp()
     */
    public FlutterCallbackHandler(long id) {
        this.id = id;
    }

    @Override
    public void success(@Nullable final HashMap var1) {
        super.success(var1);
        HandlerUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    LogUtil.logMsg("Process", " result callback : " + id + "  " + var1.toString());
                    Object flutterResult = MainBinderCommHub.getInstance()
                            .getDecoder()
                            .decodeToFlutterResult((String) var1.get("methodName"), var1);
                    MainBinderCommHub.getInstance().getFlutterResult(id).success(flutterResult);
                    MainBinderCommHub.getInstance().removeCacheResultCallback(id);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void error(final String var1, @Nullable final String var2, @Nullable final HashMap var3) {
        super.error(var1, var2, var3);
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
        super.notImplemented();
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
