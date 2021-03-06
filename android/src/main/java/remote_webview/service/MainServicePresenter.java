package remote_webview.service;

import android.app.Service;
import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import remote_webview.IBinderPool;
import remote_webview.IMainMethodChannelBinder;
import remote_webview.IMainProcessBinder;
import remote_webview.IMainResultCallbackBinder;
import remote_webview.IRemoteMethodChannelBinder;
import remote_webview.service.binders.MainMethodChannelBinder;
import remote_webview.service.binders.MainProcessBinder;
import remote_webview.service.binders.MainResultCallbackBinder;
import remote_webview.service.manager.RemoteViewModuleManager;


/**
 * Represents  main service binder.
 *
 * communicate with main-process will by this presenter.
 */

public class MainServicePresenter extends ProcessServicePresenter {

    public static final int BINDER_MAIN_RESULT_CALLBACK = 809;

    public static final int BINDER_MAIN_WINDOW_TOKEN = 810;

    public static final int BINDER_MAIN_PROCESS = 811;

    private static volatile MainServicePresenter singleton;

    public static MainServicePresenter getInstance() {
        if(singleton == null) {
            synchronized (MainServicePresenter.class) {
                if(singleton == null) {
                    singleton = new MainServicePresenter();
                }
            }
        }
        return singleton;
    }

    
    private MainServicePresenter() {
    }

    @Override
    protected Class<? extends Service> getServiceClass() {
        return MainWebService.class;
    }

    @Override
    protected void serviceConnectedCallback() {
        Log.e("MainServicePresenter", "main serviceConnected");
    }

    @Override
    protected void serviceDisConnectedCallback() {
        Log.e("MainServicePresenter", "main serviceDisConnected");
    }

    /**
     * Directly fetch {@link IMainMethodChannelBinder} for easy to use.
     * @return invoke method to order main-app
     */
    public IMainMethodChannelBinder getMainChannelBinder() {
        IMainMethodChannelBinder binder = null;
        try {
            binder = IMainMethodChannelBinder.Stub.asInterface(mBinderPool.queryBinder(BINDER_METHOD_CHANNEL));
        }catch (Exception e) {
            e.printStackTrace();
        }
        return binder;
    }

    public IMainResultCallbackBinder getMainResultCallbackBinder() {
        IMainResultCallbackBinder binder = null;
        try {
            binder = IMainResultCallbackBinder.Stub.asInterface(mBinderPool.queryBinder(BINDER_MAIN_RESULT_CALLBACK));
        }catch (Exception e) {
            e.printStackTrace();
        }
        return binder;
    }

    /**
     * Get a binder from main process, and send some request.
     *
     * @return main-process binder
     */
    public IMainProcessBinder getMainProcessBinder() {
        IMainProcessBinder binder = null;
        try {
            binder = IMainProcessBinder.Stub.asInterface(mBinderPool.queryBinder(BINDER_MAIN_PROCESS));
        }catch (Exception e) {
            e.printStackTrace();
        }
        return binder;
    }


    public IBinder getMainWindowToken() {
        IBinder binder = null;
        try {
            binder = mBinderPool.queryBinder(BINDER_MAIN_WINDOW_TOKEN);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return binder;
    }


    public static class MainBinderPoolImpl extends IBinderPool.Stub{

        private Context context;

        public MainBinderPoolImpl(Context context) {
            this.context = context;
        }

        @Override
        public IBinder queryBinder(int binderCode) throws RemoteException {
            IBinder binder = null;
            switch (binderCode) {
                case BINDER_METHOD_CHANNEL:
                    binder = new MainMethodChannelBinder();
                    break;
                case BINDER_MAIN_RESULT_CALLBACK:
                    binder = new MainResultCallbackBinder();
                    break;
                case BINDER_MAIN_WINDOW_TOKEN:
                    binder = RemoteViewModuleManager.getInstance().getToken();
                    break;
                case BINDER_MAIN_PROCESS:
                    binder = new MainProcessBinder();
                    break;
            }
            return binder;
        }
    }


}
