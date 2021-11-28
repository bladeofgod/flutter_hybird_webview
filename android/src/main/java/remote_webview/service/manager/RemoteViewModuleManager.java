package remote_webview.service.manager;

import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import remote_webview.service.RemoteServicePresenter;
import remote_webview.utils.LogUtil;

/**
 * @author LiJiaqi
 * @date 2021/11/27
 * Description: Manage all remote-view module.
 */

public class RemoteViewModuleManager {

    //check child-process is alive.
    public static final int PATROL_CP_ALIVE = 7000;

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

    private PatrolDog dog = PatrolDog.getInstance();

    private ViewSavedInstance savedInstance = new ViewSavedInstance();

    Handler handler;

    private RemoteViewModuleManager() {
        handler = new Handler(callback);
    }


    private Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what) {
                case PATROL_CP_ALIVE:
                    handleCPCheckRequest(message);

                    checkChildProcessAlive();
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
        //todo notify result
    }


    public void checkChildProcessAlive() {
        LogUtil.logMsg(getClass().getName(), "patrol : checkChildProcessAlive");
        dog.enqueue(new ChildProcessCheckRequest(this, 5*1000, PATROL_CP_ALIVE));
    }


    public HashMap getSavedInstance() {
        return savedInstance.getSavedInstance();
    }

    public void setSavedInstance(HashMap status) {
        savedInstance.setSavedInstance(status);
    }


    /**
     * Hold the view's saved instance for restore status in need.
     */
    private static class ViewSavedInstance {

        private AtomicBoolean isValid = new AtomicBoolean(false);

        private HashMap savedInstance;

        public boolean isValid() {
            return isValid.get();
        }

        public HashMap getSavedInstance() {
            return isValid.getAndSet(false) ? savedInstance : new HashMap();
        }

        public void setSavedInstance(HashMap savedInstance) {
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
            } catch (RemoteException e) {
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
    }

}
