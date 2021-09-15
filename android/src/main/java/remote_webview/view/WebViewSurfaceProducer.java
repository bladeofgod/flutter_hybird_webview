package remote_webview.view;

import java.util.HashMap;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import remote_webview.model.ViewSurfaceModel;
import remote_webview.model.WebViewSurfaceModel;


public class WebViewSurfaceProducer {

    public static WebViewSurfaceProducer producer = new WebViewSurfaceProducer();

    private WebViewSurfaceProducer() {
    }

    private FlutterPlugin.FlutterPluginBinding flutterPluginBinding;

    public void holdFlutterBinding(FlutterPlugin.FlutterPluginBinding binding) {
        this.flutterPluginBinding = binding;
    }

    private final HashMap<Integer, ViewSurfaceModel> surfaceModelCache = new HashMap<>();


    public int buildGeneralWebViewSurface() {

        final WebViewSurfaceModel surfaceModel = new WebViewSurfaceModel.Builder(flutterPluginBinding.getApplicationContext())
                .init(flutterPluginBinding.getTextureRegistry().createSurfaceTexture())
                .build();
        cacheViewSurfaceModel(surfaceModel);

        return surfaceModel.getId();
    }

    public void cacheViewSurfaceModel(ViewSurfaceModel surfaceModel) {
        surfaceModelCache.put(surfaceModel.getId(), surfaceModel);
    }

    public void remoteViewSurfaceModel(int id) {
        surfaceModelCache.remove(id);
    }


}








