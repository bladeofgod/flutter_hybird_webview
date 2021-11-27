package remote_webview.service.manager;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author LiJiaqi
 * @date 2021/11/27
 * Description: Manage all remote-view module.
 */

public class RemoteViewModuleManager {

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

    private RemoteViewModuleManager() {}

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

    private static class PatrolDog extends Thread{

        private static final PatrolDog instance;

        static {
            instance = new PatrolDog();
            instance.start();
        }

        public static PatrolDog getInstance() {
            return instance;
        }

        private void patrol() {}

        @Override
        public void run() {
            while (true){
                patrol();
            }
        }
    }

}
