package remote_webview.interfaces;

public interface IGarbageCleanListener {

    /**
     * Clean single web-presentation.
     * @param id surface's id
     */
    void cleanGarbage(long id);
    
    
    void cleanAll();
    
}
