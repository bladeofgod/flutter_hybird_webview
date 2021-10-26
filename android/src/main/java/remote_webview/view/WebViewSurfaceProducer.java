package remote_webview.view;

import android.os.Build;
import android.os.RemoteException;
import android.webkit.WebViewClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import remote_webview.model.ViewSurfaceModel;
import remote_webview.model.WebViewCreationParamsModel;
import remote_webview.model.WebViewSurfaceClient;
import remote_webview.service.RemoteServicePresenter;


public class WebViewSurfaceProducer {

    private static final String JS_CHANNEL_NAMES_FIELD = "javascriptChannelNames";

    public static WebViewSurfaceProducer producer = new WebViewSurfaceProducer();

    private WebViewSurfaceProducer() {
    }

    private FlutterPlugin.FlutterPluginBinding flutterPluginBinding;

    public void holdFlutterBinding(FlutterPlugin.FlutterPluginBinding binding) {
        this.flutterPluginBinding = binding;
    }

    /**
     * Hold all create surface in {@linkplain ViewSurfaceModel}.
     */
    public final HashMap<Long, ViewSurfaceModel> surfaceModelCache = new HashMap<>();

    public boolean checkViewExists(long viewId) {
        return surfaceModelCache.containsKey(viewId);
    }


    public long buildGeneralWebViewSurface(Map<String, Object> params) {

        WebViewSurfaceClient surfaceModel = new WebViewSurfaceClient.Builder(flutterPluginBinding.getApplicationContext())
                .init(flutterPluginBinding.getTextureRegistry().createSurfaceTexture())
                .build(flutterPluginBinding);
        //todo test model
//        WebViewCreationParamsModel paramsModel = new WebViewCreationParamsModel(
//                surfaceModel.getId(), false,
//                new HashMap<String, String>(),null,1,"",
//                "https://www.jd.com/"
//        );
        try {
            RemoteServicePresenter.getInstance().getRemoteViewFactoryBinder()
                    .createWithSurface(createParamsModel(surfaceModel.getId(), params)
                            ,surfaceModel.getSurface());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        cacheViewSurfaceModel(surfaceModel);

        return surfaceModel.getId();
    }


    /**
     * Create a model that can be transfer to child process.
     * It contain some params for view creation.
     * @param id surface's id.
     * @param params creation params from flutter side.
     * @return for transfer to remote.
     */
    private WebViewCreationParamsModel createParamsModel(long id, Map<String, Object> params) {

        boolean usesHybridComposition = Boolean.TRUE.equals(params.get("usesHybridComposition"));

        HashMap<String, String> settings = new HashMap<>();
        Map<String, Object> settingFromFlutter = (Map<String, Object>) params.get("settings");
        if (settingFromFlutter != null) {
            settings.putAll(buildSimpleSettings(settingFromFlutter));
        }

        List<String> names = new ArrayList<>();

        if (params.containsKey(JS_CHANNEL_NAMES_FIELD)) {
            List<String> origin = (List<String>) params.get(JS_CHANNEL_NAMES_FIELD);
            if (origin != null) {
                names.addAll(origin);
            }
        }

        Integer autoMediaPlaybackPolicy = (Integer) params.get("autoMediaPlaybackPolicy");

        String userAgent = (String) params.get("userAgent");

        String url = (String) params.get("initialUrl");

        return new WebViewCreationParamsModel(id,usesHybridComposition,
                settings,names,autoMediaPlaybackPolicy
                ,userAgent,url);
    }


    /**
     * Build a simple {@linkplain Map<String,String>} setting
     * for {@linkplain WebViewCreationParamsModel}.
     * @param settings for remote transfer.
     * @return
     */
    private Map<String, String> buildSimpleSettings(Map<String, Object> settings) {
        final Map<String, String> simpleSetting = new HashMap<>();
        for (String key : settings.keySet()) {
            switch (key) {
                case "jsMode":
                    Integer mode = (Integer) settings.get(key);
                    if (mode != null) {
                        simpleSetting.put(key,mode.toString());
                    }
                    break;
                case "hasNavigationDelegate":
                    final boolean hasNavigationDelegate = (boolean) settings.get(key);
                    simpleSetting.put("hasNavigationDelegate",Boolean.valueOf(hasNavigationDelegate).toString());
                    break;
                case "debuggingEnabled":
                    final boolean debuggingEnabled = (boolean) settings.get(key);
                    simpleSetting.put("debuggingEnabled",Boolean.valueOf(debuggingEnabled).toString());
                    break;
                case "hasProgressTracking":
                    simpleSetting.put("hasProgressTracking",Boolean.valueOf((boolean) settings.get(key)).toString());
                    break;
                case "gestureNavigationEnabled":
                    break;
                case "userAgent":
                    simpleSetting.put("userAgent",(String) settings.get(key));
                    break;
                case "allowsInlineMediaPlayback":
                    // no-op inline media playback is always allowed on Android.
                    break;
                default:
                    throw new IllegalArgumentException("Unknown WebView setting: " + key);
            }
        }
        return simpleSetting;
    }




    /**
     *
     * @param surfaceModel already created view Model that linked
     *                     a real view on remote.
     */
    public void cacheViewSurfaceModel(ViewSurfaceModel surfaceModel) {
        surfaceModelCache.put(surfaceModel.getId(), surfaceModel);
    }

    /**
     *
     * @param id mark a view model.
     */
    public void remoteViewSurfaceModel(long id) {
        surfaceModelCache.remove(id);
    }


}








