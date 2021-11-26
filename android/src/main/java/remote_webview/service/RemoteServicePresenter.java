package remote_webview.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import remote_webview.IBinderPool;
import remote_webview.IMainResultCallbackBinder;
import remote_webview.IRemoteMethodChannelBinder;
import remote_webview.IRemoteProcessBinder;
import remote_webview.IRemoteViewFactoryBinder;
import remote_webview.model.MethodModel;
import remote_webview.service.binders.RemoteMethodChannelBinder;
import remote_webview.service.binders.RemoteProcessBinder;
import remote_webview.service.binders.RemoteResultCallbackBinder;
import remote_webview.service.binders.RemoteViewFactoryBinder;
import remote_webview.utils.LogUtil;
import remote_webview.view.RemoteWebViewActivity;


/**
 * Represents remote service binder.
 * 
 * communicate with remote-process will by this presenter.
 *
 * Tip: this is run on main-app's UI-thread, do not block it!
 */

public class RemoteServicePresenter extends ProcessServicePresenter {

    /**
     * Remote Binder's code
     */
    public static final int BINDER_VIEW_FACTORY = 609;

    public static final int BINDER_REMOTE_PROCESS = 619;

    public static final int BINDER_REMOTE_RESULT_CALLBACK = 629;

    private static volatile RemoteServicePresenter singleton;

    public static RemoteServicePresenter getInstance() {
        if(singleton == null) {
            synchronized (RemoteServicePresenter.class) {
                if(singleton == null) {
                    singleton = new RemoteServicePresenter();
                }
            }
        }
        return singleton;
    }

    private RemoteServicePresenter() {
    }

    @Override
    protected Class<? extends Service> getServiceClass() {
        return RemoteWebService.class;
    }

    @Override
    protected void serviceConnectedCallback() {
        LogUtil.logMsg("RemoteServicePresenter", "remote serviceConnected");
        try {
            getRemoteProcessBinder().initZygoteActivity();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void serviceDisConnectedCallback() {
        LogUtil.logMsg("RemoteServicePresenter", "remote serviceDisConnected");
    }

    /**
     * Directly fetch {@link IRemoteMethodChannelBinder} for easy to use.
     * @return invoke method to control web-view
     */
    public IRemoteMethodChannelBinder getRemoteChannelBinder() {
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
    public IRemoteViewFactoryBinder getRemoteViewFactoryBinder() {
        IRemoteViewFactoryBinder binder = null;
        try {
            binder = IRemoteViewFactoryBinder.Stub.asInterface(mBinderPool.queryBinder(BINDER_VIEW_FACTORY));
        }catch (Exception e) {
            e.printStackTrace();
        }
        return binder;
    }


    /**
     * Directly fetch {@link IRemoteProcessBinder} for easy to use.
     * @reture IBinder to create platform-view.
     */
    public IRemoteProcessBinder getRemoteProcessBinder() {
        IRemoteProcessBinder binder = null;
        try {
            binder = IRemoteProcessBinder.Stub.asInterface(mBinderPool.queryBinder(BINDER_REMOTE_PROCESS));
        }catch (Exception e) {
            e.printStackTrace();
        }
        return binder;
    }


    /**
     * Get remote result callback binder, for send result back to remote-view.
     * @return result callback binder
     */
    public IMainResultCallbackBinder getRemoteResultCallbackBinder() {
        IMainResultCallbackBinder binder = null;
        try {
            binder = IMainResultCallbackBinder.Stub.asInterface(mBinderPool.queryBinder(BINDER_REMOTE_RESULT_CALLBACK));
        }catch (Exception e) {
            e.printStackTrace();
        }
        return binder;
    }


    public static class RemoteBinderPoolImpl extends IBinderPool.Stub{

        private Context context;

        public RemoteBinderPoolImpl(Context context) {
            this.context = context;
            MainServicePresenter.getInstance().holdContext(context.getApplicationContext());
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
                case BINDER_REMOTE_PROCESS:
                    binder = new RemoteProcessBinder();
                    break;
                case BINDER_REMOTE_RESULT_CALLBACK:
                    binder = new RemoteResultCallbackBinder();
            }
            return binder;
        }
    }
    
}


