package remote_webview.service;

import android.app.Service;
import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;

import remote_webview.IBinderPool;
import remote_webview.IMainMethodChannelBinder;
import remote_webview.IRemoteMethodChannelBinder;
import remote_webview.service.binders.MainMethodChannelBinder;


/**
 * Represents  main service binder.
 *
 * communicate with main-process will by this presenter.
 */

public class MainServicePresenter extends ProcessServicePresenter {

    public static final int BINDER_REMOTE_METHOD_CHANNEL = 609;

    private static volatile MainServicePresenter singleton;

    public static MainServicePresenter getInstance(Context context) {
        if(singleton == null) {
            synchronized (MainServicePresenter.class) {
                if(singleton == null) {
                    singleton = new MainServicePresenter(context);
                }
            }
        }
        return singleton;
    }

    
    private MainServicePresenter(Context context) {
        super(context);
    }

    @Override
    public Class<? extends Service> getServiceClass() {
        return MainWebService.class;
    }

    /**
     * Directly fetch {@link IMainMethodChannelBinder} for easy to use.
     * @return invoke method to order main-app
     */
    IMainMethodChannelBinder getRemoteChannelBinder() {
        IMainMethodChannelBinder binder = null;
        try {
            binder = IMainMethodChannelBinder.Stub.asInterface(mBinderPool.queryBinder(BINDER_METHOD_CHANNEL));
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
            }
            return binder;
        }
    }


}
