package remote_webview.view;

import java.util.HashMap;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import remote_webview.model.ViewSurfaceModel;
import remote_webview.model.WebViewSurfaceClient;


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

        final WebViewSurfaceClient surfaceModel = new WebViewSurfaceClient.Builder(flutterPluginBinding.getApplicationContext())
                .init(flutterPluginBinding.getTextureRegistry().createSurfaceTexture())
                .build(flutterPluginBinding);
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








