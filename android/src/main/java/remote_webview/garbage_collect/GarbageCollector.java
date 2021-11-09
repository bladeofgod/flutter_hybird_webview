package remote_webview.garbage_collect;

import java.util.ArrayList;
import java.util.List;

import remote_webview.interfaces.IGarbageCleanListener;

/**
 * Receive dispose order and dispatch to {@linkplain IGarbageCleanListener}
 *
 * Do not register listener in view directly.
 */
abstract public class GarbageCollector {

    private final List<IGarbageCleanListener> cleanListeners = new ArrayList<>();

    public void registerCollectListener(IGarbageCleanListener listener) {
        cleanListeners.add(listener);
    }

    public void removeCollectListener(IGarbageCleanListener listener) {
        cleanListeners.remove(listener);
    }

    public void notifyClean(long id) {
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
