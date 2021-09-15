package remote_webview.model;

import android.view.Surface;


abstract public class ViewSurfaceModel {
    
    private final int id;
    
    private final Surface surface;

    protected ViewSurfaceModel(int id, Surface surface) {
        this.id = id;
        this.surface = surface;
    }

    public int getId() {
        return id;
    }

    public Surface getSurface() {
        return surface;
    }

}
















