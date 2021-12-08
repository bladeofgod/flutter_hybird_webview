package remote_webview.model;

import android.view.Surface;

import androidx.annotation.CallSuper;

import io.flutter.view.TextureRegistry;


abstract public class ViewSurfaceModel {

    private final TextureRegistry.SurfaceTextureEntry entry;

    private final Surface surface;

    protected ViewSurfaceModel(TextureRegistry.SurfaceTextureEntry entry, Surface surface) {
        this.entry = entry;
        this.surface = surface;
    }

    public long getId() {
        return entry.id();
    }

    public Surface getSurface() {
        return surface;
    }

    @CallSuper
    public void release() {
        entry.release();
    }

}
















