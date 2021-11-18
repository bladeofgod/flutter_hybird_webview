package remote_webview.service.binders;

import android.os.RemoteException;
import android.util.Log;

import remote_webview.IMainMethodChannelBinder;
import remote_webview.model.MethodModel;
import remote_webview.service.hub.MainBinderCommHub;
import remote_webview.utils.LogUtil;


/**
 * Working on main process for handle remote-view's order.
 * like js call.
 */

public class MainMethodChannelBinder extends IMainMethodChannelBinder.Stub {
    @Override
    public void invokeMethod(MethodModel model) throws RemoteException {
        LogUtil.logMsg("Remote Call :", model.toString());
        MainBinderCommHub.getInstance().invokeMethod(model);
    }
}
