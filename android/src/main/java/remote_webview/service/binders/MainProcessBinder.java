package remote_webview.service.binders;

import android.os.Bundle;
import android.os.RemoteException;

import remote_webview.IMainProcessBinder;
import remote_webview.service.manager.RemoteViewModuleManager;

public class MainProcessBinder extends IMainProcessBinder.Stub {
    @Override
    public void showSoftInput(long viewId) throws RemoteException {
        RemoteViewModuleManager.getInstance().showSoftInput(viewId);
    }

    @Override
    public void hideSoftInput(long viewId) throws RemoteException {
        RemoteViewModuleManager.getInstance().hideSoftInput(viewId);
    }

    @Override
    public Bundle getSavedInstance() throws RemoteException {
        return RemoteViewModuleManager.getInstance().getSavedInstance();
    }

    @Override
    public void setSavedInstance(Bundle status) throws RemoteException {
        RemoteViewModuleManager.getInstance().setSavedInstance(status);
    }
}
