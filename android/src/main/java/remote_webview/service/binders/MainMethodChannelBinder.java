package remote_webview.service.binders;

import android.os.RemoteException;
import android.util.Log;

import remote_webview.IMainMethodChannelBinder;
import remote_webview.model.MethodModel;


/**
 * handle web-view's order.
 * like js call
 */

public class MainMethodChannelBinder extends IMainMethodChannelBinder.Stub {
    @Override
    public String invokeMethod(MethodModel model) throws RemoteException {
        Log.e("main service", model.toString());
        return "main service call back";
    }
}
