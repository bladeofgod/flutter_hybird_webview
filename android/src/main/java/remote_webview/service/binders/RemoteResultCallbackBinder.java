package remote_webview.service.binders;

import android.os.RemoteException;

import java.util.HashMap;

import remote_webview.IMainResultCallbackBinder;
import remote_webview.service.hub.BaseCallbackHandler;
import remote_webview.service.hub.RemoteBinderCommHub;
import remote_webview.service.hub.RemoteCallbackHandler;
import remote_webview.utils.StringUtil;

/**
 * @author LiJiaqi
 * @date 2021/11/26
 * Description:
 */
public class RemoteResultCallbackBinder extends IMainResultCallbackBinder.Stub {
    @Override
    public void remoteSuccess(long id, String result) throws RemoteException {
        BaseCallbackHandler handler = RemoteBinderCommHub.getInstance().fetchCallbackHandler(id);
        handler.success((HashMap) StringUtil.getStringToMap(result));
        RemoteBinderCommHub.getInstance().removeMethodResultCallbackById(id);
    }

    @Override
    public void remoteError(long id, String var1, String var2, String info) throws RemoteException {
        BaseCallbackHandler handler = RemoteBinderCommHub.getInstance().fetchCallbackHandler(id);
        handler.error(var1, var2, (HashMap) StringUtil.getStringToMap(info));
        RemoteBinderCommHub.getInstance().removeMethodResultCallbackById(id);
    }

    @Override
    public void remoteNotImplemented(long id) throws RemoteException {
        BaseCallbackHandler handler = RemoteBinderCommHub.getInstance().fetchCallbackHandler(id);
        handler.notImplemented();
        RemoteBinderCommHub.getInstance().removeMethodResultCallbackById(id);

    }
}
