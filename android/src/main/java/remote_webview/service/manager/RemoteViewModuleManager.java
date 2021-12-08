package remote_webview.service.manager;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodChannel;
import remote_webview.interfaces.IMainProcessBinderAction;
import remote_webview.interfaces.ISoftInputCallback;
import remote_webview.interfaces.IWindowTokenExtractor;
import remote_webview.service.RemoteServicePresenter;
import remote_webview.utils.LogUtil;

/**
 * @author LiJiaqi
 * @date 2021/11/27
 * Description: Manage all remote-view module, run on main process and handle request from remote.
 * 
 *              There is link a plugin method-channel that connect to flutter's side, so make both 
 *              side can transport manage-order.
 *              
 * @see RemoteViewModuleManager#linkPluginChannel(MethodChannel) 
 */

public class RemoteViewModuleManager implements IMainProcessBinderAction {

    //check child-process is alive.
    public static final int PATROL_CP_ALIVE = 7000;

    public static final int SHOW_SOFT_INPUT = 7010;

    public static final int HIDE_SOFT_INPUT = 7020;

    private static RemoteViewModuleManager instance;

    public static RemoteViewModuleManager getInstance() {
        if(instance == null) {
            synchronized (RemoteViewModuleManager.class) {
                if(instance == null) {
                    instance = new RemoteViewModuleManager();
                }
            }
        }
        return instance;
    }


    private IWindowTokenExtractor tokenExtractor;

    public void setTokenExtractor(IWindowTokenExtractor tokenExtractor) {
        this.tokenExtractor = tokenExtractor;
    }

    public IBinder getToken() {
        if(tokenExtractor == null) return null;
        return tokenExtractor.extractorToken();
    }

    //    private IBinder token;
//
//    public void updateToken(IBinder token) {
//        LogUtil.logMsg(getClass().getSimpleName(),"window token : " + (token==null) );
//        this.token = token;
//    }

    @NonNull
    private ISoftInputCallback iSoftInputCallback;

    private PatrolDog dog = PatrolDog.getInstance();

    private ViewSavedInstance savedInstance = new ViewSavedInstance();

    private final Handler handler;

    private FlutterPluginProxy pluginProxy = new FlutterPluginProxy();

    //plugin channel
    private MethodChannel pluginChannel;

    private RemoteViewModuleManager() {
        handler = new Handler(callback);
    }

    public void linkPluginChannel(MethodChannel channel) {
        pluginChannel = channel;
    }
    
    @MainThread
    public FlutterPluginProxy getFlutterPluginProxy() {
        return pluginProxy;
    }

    public void setSoftInputCallback(ISoftInputCallback iSoftInputCallback) {
        this.iSoftInputCallback = iSoftInputCallback;
    }

    private Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what) {
                case PATROL_CP_ALIVE:
                    handleCPCheckRequest(message);

                    checkChildProcessAlive();
                    break;
                case SHOW_SOFT_INPUT:
                    iSoftInputCallback.showSoftInput();
                    break;
                case HIDE_SOFT_INPUT:
                    iSoftInputCallback.hideSoftInput();
                    break;
            }
            return true;
        }
    };


    /**
     * Handling {@link ChildProcessCheckRequest} 's result.
     */
    private void handleCPCheckRequest(Message message) {
        boolean isAlive = false;
        try {
            isAlive = (boolean)((HashMap)message.obj).get(ChildProcessCheckRequest.isAlive_Key);
        }catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.logMsg(getClass().getName(),"patrol report :  " + isAlive);
        //todo notify result
    }

    public void onAttachedToEngine(@NonNull FlutterPlugin.FlutterPluginBinding flutterPluginBinding) {
        RemoteServicePresenter.getInstance().holdContext(flutterPluginBinding.getApplicationContext());
        startConnectRemoteService();
        checkChildProcessAlive();
    }

    public void onDetachedFromEngine(@NonNull FlutterPlugin.FlutterPluginBinding flutterPluginBinding) {
        dog.clearTask();
        setTokenExtractor(null);
        linkPluginChannel(null);
    }
    
    private void startConnectRemoteService() {
        RemoteServicePresenter.getInstance().initConnectService();
    }


    /**
     * Check the child-process is alive.
     * e.g service cannot connect anyway, {@linkplain remote_webview.RemoteZygoteActivity}
     *      cannot restart.
     *      
     * Patrol every 5 seconds.
     */
    public void checkChildProcessAlive() {
        LogUtil.logMsg(getClass().getName(), "patrol : checkChildProcessAlive");
        dog.enqueue(new ChildProcessCheckRequest(this, 5*1000, PATROL_CP_ALIVE));
    }


    @Override
    public void showSoftInput() {
        Message.obtain(handler, SHOW_SOFT_INPUT).sendToTarget();
    }

    @Override
    public void hideSoftInput() {
        Message.obtain(handler, HIDE_SOFT_INPUT).sendToTarget();
    }

    /**
     *
     * @return get last saved view status.
     */
    @Override
    public Bundle getSavedInstance() {
        return savedInstance.getSavedInstance();
    }

    /**
     * Save a view status when it destroy in unpurposed.
     * @param status
     */
    @Override
    public void setSavedInstance(Bundle status) {
        savedInstance.setSavedInstance(status);
    }


    public class FlutterPluginProxy{
        public void checkTopViewId(MethodChannel.Result result) {
            pluginChannel.invokeMethod("getTopWebId", result);
        }
    }

    /**
     * Hold the view's saved instance for restore status in need.
     */
    private static class ViewSavedInstance {

        private AtomicBoolean isValid = new AtomicBoolean(false);

        private Bundle savedInstance;

        public boolean isValid() {
            return isValid.get();
        }

        public Bundle getSavedInstance() {
            return isValid.getAndSet(false) ? savedInstance : new Bundle();
        }

        public void setSavedInstance(Bundle savedInstance) {
            isValid.set(true);
            this.savedInstance = savedInstance;
        }
    }

    private static class ChildProcessCheckRequest extends PatrolRequest {

        private static final String isAlive_Key = "isAlive";

        ChildProcessCheckRequest(RemoteViewModuleManager moduleManager, long patrolTime, int questType) {
            super(moduleManager, patrolTime, questType);
        }

        @Override
        public HashMap doCheck() {
            HashMap result = new HashMap();
            result.put("request",getClass().getName());
            try {
                boolean isAlive = RemoteServicePresenter.getInstance()
                        .getRemoteProcessBinder()
                        .isZygoteActivityAlive() == 1;
                result.put(isAlive_Key, isAlive);
            } catch (Exception e) {
                result.put(isAlive_Key, false);
                e.printStackTrace();
            }
            return result;
        }
    }



    private static class PatrolDog extends Thread{

        private static final PatrolDog instance;

        static {
            instance = new PatrolDog();
            instance.start();
        }

        public static PatrolDog getInstance() {
            return instance;
        }

        private final DelayQueue<PatrolRequest> mQueue = new DelayQueue<>();

        private void patrol() {
            PatrolRequest request;
            try {
                request = mQueue.take();
            }catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
            LogUtil.logMsg(getClass().getName(),"do check...");
            HashMap result = request.doCheck();

            Message.obtain(request.manager.handler, request.questType, result).sendToTarget();
        }

        @Override
        public void run() {
            while (true){
                patrol();
            }
        }

        public void enqueue(PatrolRequest request) {
            mQueue.put(request);
        }
        
        public void clearTask() {
            mQueue.clear();
        }
    }

}
