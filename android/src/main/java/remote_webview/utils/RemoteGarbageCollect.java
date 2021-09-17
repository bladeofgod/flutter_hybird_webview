package remote_webview.utils;

import java.util.ArrayList;
import java.util.List;

import remote_webview.interfaces.IGarbageCleanListener;

public class RemoteGarbageCollect {

    private static RemoteGarbageCollect mInstance;

    static {
        mInstance = new RemoteGarbageCollect();
    }

    public static RemoteGarbageCollect getInstance() {return mInstance;}
    
    private final List<IGarbageCleanListener> cleanListeners = new ArrayList<>();
    
    public void registerCollectListener(IGarbageCleanListener listener) {
        cleanListeners.add(listener);
    }
    
    public void removeCollectListener(IGarbageCleanListener listener) {
        cleanListeners.remove(listener);
    }
    
    public void notifyClean(int id) {
        for (IGarbageCleanListener listener : cleanListeners) {
            listener.cleanGarbage(id);
        }
    }
    
    public void notifyCleanAll() {
        for (IGarbageCleanListener listener : cleanListeners) {
            listener.cleanAll();
        }
    }
    
    

}










