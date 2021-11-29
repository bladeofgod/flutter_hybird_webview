package remote_webview.service.binders;

import android.os.RemoteException;

import java.util.HashMap;

import remote_webview.IMainResultCallbackBinder;
import remote_webview.service.hub.BaseCallbackHandler;
import remote_webview.service.hub.MainBinderCommHub;
import remote_webview.service.hub.MainCallbackHandler;
import remote_webview.utils.StringUtil;

public class MainResultCallbackBinder extends IMainResultCallbackBinder.Stub {
    @Override
    public void remoteSuccess(long id, String result) throws RemoteException {
        try {
            BaseCallbackHandler handler = MainBinderCommHub.getInstance().fetchCallbackHandler(id);
            handler.success((HashMap) StringUtil.getStringToMap(result));
            MainBinderCommHub.getInstance().removeMethodResultCallbackById(id);
        }catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remoteError(long id, String var1, String var2, String info) throws RemoteException {
        try {
            BaseCallbackHandler handler = MainBinderCommHub.getInstance().fetchCallbackHandler(id);
            handler.error(var1, var2, (HashMap) StringUtil.getStringToMap(info));
            MainBinderCommHub.getInstance().removeMethodResultCallbackById(id);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remoteNotImplemented(long id) throws RemoteException {
        try {
            BaseCallbackHandler handler = MainBinderCommHub.getInstance().fetchCallbackHandler(id);
            handler.notImplemented();
            MainBinderCommHub.getInstance().removeMethodResultCallbackById(id);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}
