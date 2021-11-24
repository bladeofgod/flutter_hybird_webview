package remote_webview.view;

import android.os.RemoteException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import remote_webview.garbage_collect.MainGarbageCollector;
import remote_webview.interfaces.IGarbageCleanListener;
import remote_webview.model.ViewSurfaceModel;
import remote_webview.model.WebViewCreationParamsModel;
import remote_webview.model.WebViewSurfaceClient;
import remote_webview.service.RemoteServicePresenter;
import remote_webview.utils.LogUtil;

/**
 * Produce a Remote-view and cache it in {@linkplain WebViewSurfaceProducer#surfaceModelCache}.
 */

public class WebViewSurfaceProducer implements IGarbageCleanListener {

    private static final String JS_CHANNEL_NAMES_FIELD = "javascriptChannelNames";

    public static WebViewSurfaceProducer producer = new WebViewSurfaceProducer();

    private WebViewSurfaceProducer() {
        MainGarbageCollector.getInstance().registerCollectListener(this);
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

        // real size of device, physical width/height.
        // calculate from flutter's paintBounds.
        int pWidth = (int) params.get("physicalWidth");

        int pHeight = (int) params.get("physicalHeight");
        
        WebViewSurfaceClient surfaceModel = new WebViewSurfaceClient.Builder(flutterPluginBinding)
                .create(flutterPluginBinding.getTextureRegistry())
                .setSurfaceDefaultBufferSize(pWidth,pHeight)
                .build();

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
        if(autoMediaPlaybackPolicy == null) {
            autoMediaPlaybackPolicy = 0;
        }

        String userAgent = (String) params.get("userAgent");
        if(userAgent == null) {
            userAgent = "";
        }
        
        int pWidth = (int) params.get("physicalWidth");
        
        int pHeight = (int) params.get("physicalHeight");

        String url = (String) params.get("initialUrl");
        LogUtil.logMsg("initialUrl",url);

        return new WebViewCreationParamsModel(id,usesHybridComposition,
                settings,names,autoMediaPlaybackPolicy
                ,userAgent,url, pWidth, pHeight);
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
    private void cacheViewSurfaceModel(ViewSurfaceModel surfaceModel) {
        surfaceModelCache.put(surfaceModel.getId(), surfaceModel);
    }

    /**
     *
     * @param id mark a view model.
     */
    private void removeViewSurfaceModel(long id) {
        try {
            Objects.requireNonNull(surfaceModelCache.get(id)).release();
        }catch (Exception e) {
            e.printStackTrace();
        }
        surfaceModelCache.remove(id);
    }


    @Override
    public void cleanGarbage(long id) {
        LogUtil.logMsg(this.toString(),"remove surface model  id : %s", String.valueOf(id));
        removeViewSurfaceModel(id);
    }

    @Override
    public void cleanAll() {
        surfaceModelCache.clear();
    }
}








