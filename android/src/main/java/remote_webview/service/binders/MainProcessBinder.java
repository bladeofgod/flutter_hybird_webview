package remote_webview.service.binders;

import android.os.Bundle;
import android.os.RemoteException;

import remote_webview.IMainProcessBinder;

public class MainProcessBinder extends IMainProcessBinder.Stub {
    @Override
    public void showSoftInput(long viewId) throws RemoteException {

    }

    @Override
    public void hideSoftInput(long viewId) throws RemoteException {

    }

    @Override
    public Bundle getSavedInstance() throws RemoteException {
        return null;
    }

    @Override
    public void setSavedInstance(Bundle status) throws RemoteException {

    }
}
