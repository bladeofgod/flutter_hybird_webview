package remote_webview.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.concurrent.CountDownLatch;

import remote_webview.IBinderPool;
import remote_webview.service.binders.RemoteMethodChannelBinder;

public class RemoteServicePresenter {

    /**
     * Binder's code
     */
    public static final int BINDER_METHOD_CHANNEL = 109;
    
    private Context mAppContext;

    private IBinderPool mBinderPool;

    private static volatile RemoteServicePresenter singleton;

    private CountDownLatch mConnectionBinderPoolCountDownLatch;

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
        mAppContext = context.getApplicationContext();
        connectRemoteService();
    }

    /**
     *
     * @param binderCode represents a Binder from remote-web-service
     * @return IBinder
     */
    public IBinder queryBinderByCode(int binderCode) {
        IBinder binder = null;
        try {
            if(mBinderPool != null) {
                binder = mBinderPool.queryBinder(binderCode);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return binder;
    }

    private synchronized void connectRemoteService() {
        mConnectionBinderPoolCountDownLatch = new CountDownLatch(1);
        Intent service = new Intent(mAppContext, RemoteWebService.class);
        mAppContext.bindService(service,serviceConnection,Context.BIND_AUTO_CREATE);
        try {
            mConnectionBinderPoolCountDownLatch.await();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinderPool = IBinderPool.Stub.asInterface(service);
            try {
                mBinderPool.asBinder().linkToDeath(mBinderPoolDeathRecipient,0);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    /**
     * TODO : think about really need do this at this scene;
     */
    private final IBinder.DeathRecipient mBinderPoolDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            mBinderPool.asBinder().unlinkToDeath(mBinderPoolDeathRecipient,0);
            mBinderPool = null;
            connectRemoteService();
        }
    };


    public static class BinderPoolImpl extends IBinderPool.Stub{

        //remote context
        private Context context;

        public BinderPoolImpl(Context context) {
            this.context = context;
        }

        @Override
        public IBinder queryBinder(int binderCode) throws RemoteException {
            IBinder binder = null;
            switch (binderCode) {
                case BINDER_METHOD_CHANNEL:
                    binder = new RemoteMethodChannelBinder();
                    break;
            }
            return binder;
        }
    }
    
}


