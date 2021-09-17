package remote_webview.service.hub;


import remote_webview.interfaces.IGarbageCleanListener;
import remote_webview.utils.RemoteGarbageCollect;

/**
 * view's sub binder hub.
 * dispatch the order from main-process to the view according id.
 */
public class RemoteBinderCommHub extends BinderCommunicateHub implements IGarbageCleanListener {

    private static volatile RemoteBinderCommHub singleton;

    public static RemoteBinderCommHub getInstance() {
        if(singleton == null) {
            synchronized (RemoteBinderCommHub.class) {
                if(singleton == null) {
                    singleton = new RemoteBinderCommHub();
                }
            }
        }
        return singleton;
    }
    
    private RemoteBinderCommHub(){
        RemoteGarbageCollect.getInstance().registerCollectListener(this);
    }

    @Override
    public void cleanGarbage(int id) {
        
    }

    @Override
    public void cleanAll() {

    }
}