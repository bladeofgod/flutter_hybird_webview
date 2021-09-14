package remote_webview.service.binders;

import android.os.RemoteException;

import remote_webview.IMainResultCallbackBinder;

public class MainResultCallbackBinder extends IMainResultCallbackBinder.Stub {
    @Override
    public void remoteSuccess(long id, String result) throws RemoteException {
        
    }

    @Override
    public void remoteError(long id, String var1, String var2, String info) throws RemoteException {

    }

    @Override
    public void remoteNotImplemented(long id) throws RemoteException {

    }
}
