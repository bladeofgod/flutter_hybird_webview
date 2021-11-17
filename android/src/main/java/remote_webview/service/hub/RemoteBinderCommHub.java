package remote_webview.service.hub;


import remote_webview.interfaces.IGarbageCleanListener;
import remote_webview.garbage_collect.RemoteGarbageCollector;

/**
 * view's private-binder hub.
 * dispatch the order from main-process to the view according id.
 */
public class RemoteBinderCommHub extends BinderCommunicateHub<RemoteCallbackHandler> implements IGarbageCleanListener {

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
        RemoteGarbageCollector.getInstance().registerCollectListener(this);
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

    @Override
    protected RemoteCallbackHandler getCallbackHandler(long id) {
        return new RemoteCallbackHandler(id);
    }
}
