package remote_webview.garbage_collect;

/**
 * Working on remote process.
 */
public class RemoteGarbageCollector extends GarbageCollector {

    private static RemoteGarbageCollector mInstance;

    static {
        mInstance = new RemoteGarbageCollector();
    }

    public static RemoteGarbageCollector getInstance() {return mInstance;}
    
    private RemoteGarbageCollector(){}
    
    

}










