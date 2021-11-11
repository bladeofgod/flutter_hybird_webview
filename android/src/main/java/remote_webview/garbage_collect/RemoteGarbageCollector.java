package remote_webview.garbage_collect;

/**
 * Working on remote process, and notify to clean or clean-all caches.
 * @see remote_webview.view.RemoteViewFactoryProcessor
 * @see remote_webview.service.hub.RemoteBinderCommHub
 */
public class RemoteGarbageCollector extends GarbageCollector {

    private static RemoteGarbageCollector mInstance;

    static {
        mInstance = new RemoteGarbageCollector();
    }

    public static RemoteGarbageCollector getInstance() {return mInstance;}
    
    private RemoteGarbageCollector(){}
    
    

}










