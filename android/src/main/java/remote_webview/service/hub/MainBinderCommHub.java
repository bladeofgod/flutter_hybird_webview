package remote_webview.service.hub;

import remote_webview.garbage_collect.MainGarbageCollector;
import remote_webview.interfaces.IGarbageCleanListener;

public class MainBinderCommHub extends BinderCommunicateHub implements IGarbageCleanListener {
    
    private static volatile MainBinderCommHub singleton;
    
    public static MainBinderCommHub getInstance() {
        if(singleton == null) {
            synchronized (MainBinderCommHub.class) {
                if(singleton == null) {
                    singleton = new MainBinderCommHub();
                }
            }
        }
        return singleton;
    }
    
    private MainBinderCommHub() {
        MainGarbageCollector.getInstance().registerCollectListener(this);
    }
    
    
    @Override
    public void cleanGarbage(long id) {
        plugOutMethodHandler(id);
    }

    @Override
    public void cleanAll() {
        cleanAllMethodHandler();
        cleanAllMethodResultCallback();
    }
}
