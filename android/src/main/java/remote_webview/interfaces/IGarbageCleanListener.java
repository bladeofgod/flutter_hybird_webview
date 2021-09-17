package remote_webview.interfaces;

public interface IGarbageCleanListener {

    /**
     * Clean single web-presentation.
     * @param id surface's id
     */
    void cleanGarbage(int id);
    
    
    void cleanAll();
    
}
