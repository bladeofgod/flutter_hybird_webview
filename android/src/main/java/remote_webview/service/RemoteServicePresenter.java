package remote_webview.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import remote_webview.IBinderPool;
import remote_webview.IRemoteMethodChannelBinder;
import remote_webview.IRemoteViewFactoryBinder;
import remote_webview.model.MethodModel;
import remote_webview.service.binders.RemoteMethodChannelBinder;
import remote_webview.service.binders.RemoteViewFactoryBinder;


/**
 * Represents remote service binder.
 * 
 * communicate with remote-process will by this presenter.
 */

public class RemoteServicePresenter extends ProcessServicePresenter {

    /**
     * Remote Binder's code
     */
    public static final int BINDER_VIEW_FACTORY = 609;

    private static volatile RemoteServicePresenter singleton;

    public static RemoteServicePresenter getInstance(Context context) {
        if(singleton == null) {
            synchronized (RemoteServicePresenter.class) {
                if(singleton == null) {
                    singleton = new RemoteServicePresenter(context);
                }
            }
        }
        return singleton;
    }

    private RemoteServicePresenter(Context context) {
        super(context);
    }

    @Override
    public Class<? extends Service> getServiceClass() {
        return RemoteWebService.class;
    }

    /**
     * Directly fetch {@link IRemoteMethodChannelBinder} for easy to use.
     * @return invoke method to control web-view
     */
    IRemoteMethodChannelBinder getRemoteChannelBinder() {
        IRemoteMethodChannelBinder binder = null;
        try {
            binder = IRemoteMethodChannelBinder.Stub.asInterface(mBinderPool.queryBinder(BINDER_METHOD_CHANNEL));
        }catch (Exception e) {
            e.printStackTrace();
        }
        return binder;
    }

    /**
     * Directly fetch {@link IRemoteViewFactoryBinder} for easy to use.
     * @reture IBinder to create platform-view.
     */
    public IRemoteViewFactoryBinder getRemoteViewFactory() {
        IRemoteViewFactoryBinder binder = null;
        try {
            binder = IRemoteViewFactoryBinder.Stub.asInterface(mBinderPool.queryBinder(BINDER_VIEW_FACTORY));
        }catch (Exception e) {
            e.printStackTrace();
        }
        return binder;
    }


    public static class RemoteBinderPoolImpl extends IBinderPool.Stub{

        private Context context;

        public RemoteBinderPoolImpl(Context context) {
            this.context = context;
        }

        @Override
        public IBinder queryBinder(int binderCode) throws RemoteException {
            IBinder binder = null;
            switch (binderCode) {
                case BINDER_METHOD_CHANNEL:
                    binder = new RemoteMethodChannelBinder();
                    break;
                case BINDER_VIEW_FACTORY:
                    binder = new RemoteViewFactoryBinder();
                    break;
            }
            return binder;
        }
    }
    
}


