package remote_webview.garbage_collect;

/**
 * Working on main process.
 */
public class MainGarbageCollector extends GarbageCollector {
    
    private static MainGarbageCollector mInstance;
    
    static {
        mInstance = new MainGarbageCollector();
    }
    
    public static MainGarbageCollector getInstance() {return mInstance;}
    
    private MainGarbageCollector(){}
    
}
