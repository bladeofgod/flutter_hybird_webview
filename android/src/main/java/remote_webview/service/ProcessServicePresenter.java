package remote_webview.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.util.concurrent.CountDownLatch;

import remote_webview.IBinderPool;
import remote_webview.utils.LogUtil;

public abstract class ProcessServicePresenter {

    /**
     * Binder's code
     */
    public static final int BINDER_METHOD_CHANNEL = 109;

    private Context mAppContext;

    protected IBinderPool mBinderPool;

    //private CountDownLatch mConnectionBinderPoolCountDownLatch;

    ProcessServicePresenter(){

    }
    
    public void holdContext(Context context) {
        this.mAppContext = context;
    }
    
    public Context getContext() {
        return mAppContext;
    }
    

    /**
     * the service.class that will connect
     * @return service.class
     */
    protected abstract Class<? extends Service> getServiceClass();

    
    protected abstract void serviceConnectedCallback();
    
    protected abstract void serviceDisConnectedCallback();

    /**
     * connect remote service.
     *
     * it's recommended to connect it in a new thread.
     */
    public void initConnectService() {
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


    /**
     * connect remote service.
     */
    private void connectRemoteService() {
        Intent service = new Intent(mAppContext, getServiceClass());
        mAppContext.bindService(service,serviceConnection,Context.BIND_AUTO_CREATE);
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.logMsg("serviceConnection", "  onServiceConnected");
            mBinderPool = IBinderPool.Stub.asInterface(service);
            serviceConnectedCallback();
            try {
                mBinderPool.asBinder().linkToDeath(mBinderPoolDeathRecipient,0);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.logMsg("serviceConnection", "  onServiceDisconnected");
            serviceDisConnectedCallback();
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


}
