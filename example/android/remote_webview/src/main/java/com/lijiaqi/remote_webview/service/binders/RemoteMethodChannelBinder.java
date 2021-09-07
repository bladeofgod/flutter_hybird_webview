package com.lijiaqi.remote_webview.service.binders;

import android.os.RemoteException;

import com.lijiaqi.remote_webview.IRemoteMethodChannelBinder;
import com.lijiaqi.remote_webview.model.MethodModel;

public class RemoteMethodChannelBinder extends IRemoteMethodChannelBinder.Stub {
    @Override
    public void invokeMethod(MethodModel model) throws RemoteException {
        //todo
    }
}
