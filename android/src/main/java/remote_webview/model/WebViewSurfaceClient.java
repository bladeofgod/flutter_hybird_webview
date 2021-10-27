package remote_webview.model;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.webkit.WebStorage;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.view.TextureRegistry;
import remote_webview.utils.LogUtil;

/**
 * The client of remote web view, has a private channel to flutter.
 * accept flutter's control order and send to remote.
 */

public class WebViewSurfaceClient extends ViewSurfaceModel implements MethodChannel.MethodCallHandler {

    private static final String CHANNEL_NAME_HEAD = "plugins.flutter.io/webview_";

    private final MethodChannel methodChannel;

    protected WebViewSurfaceClient(long id, Surface surface, BinaryMessenger binaryMessenger) {
        super(id, surface);
        methodChannel = new MethodChannel(binaryMessenger,CHANNEL_NAME_HEAD+id);
        methodChannel.setMethodCallHandler(this);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall methodCall, MethodChannel.Result result) {
        switch (methodCall.method) {
            case "loadUrl":
                loadUrl(methodCall, result);
                break;
            case "updateSettings":
                updateSettings(methodCall, result);
                break;
            case "canGoBack":
                canGoBack(result);
                break;
            case "canGoForward":
                canGoForward(result);
                break;
            case "goBack":
                goBack(result);
                break;
            case "goForward":
                goForward(result);
                break;
            case "reload":
                reload(result);
                break;
            case "currentUrl":
                currentUrl(result);
                break;
            case "evaluateJavascript":
                evaluateJavaScript(methodCall, result);
                break;
            case "addJavascriptChannels":
                addJavaScriptChannels(methodCall, result);
                break;
            case "removeJavascriptChannels":
                removeJavaScriptChannels(methodCall, result);
                break;
            case "clearCache":
                clearCache(result);
                break;
            case "getTitle":
                getTitle(result);
                break;
            case "scrollTo":
                scrollTo(methodCall, result);
                break;
            case "scrollBy":
                scrollBy(methodCall, result);
                break;
            case "getScrollX":
                getScrollX(result);
                break;
            case "getScrollY":
                getScrollY(result);
                break;
            default:
                result.notImplemented();
        }

    }


    @SuppressWarnings("unchecked")
    private void loadUrl(MethodCall methodCall, MethodChannel.Result result) {
        Map<String, Object> request = (Map<String, Object>) methodCall.arguments;
        String url = (String) request.get("url");
        Map<String, String> headers = (Map<String, String>) request.get("headers");
        if (headers == null) {
            headers = Collections.emptyMap();
        }
        //todo webView.loadUrl(url, headers);
        result.success(null);
    }

    private void canGoBack(MethodChannel.Result result) {
        //todo result.success(webView.canGoBack());
    }

    private void canGoForward(MethodChannel.Result result) {
        //todo result.success(webView.canGoForward());
    }

    private void goBack(MethodChannel.Result result) {
        //todo
//        if (webView.canGoBack()) {
//            webView.goBack();
//        }
        result.success(null);
    }

    private void goForward(MethodChannel.Result result) {
        //todo
//        if (webView.canGoForward()) {
//            webView.goForward();
//        }
        result.success(null);
    }

    private void reload(MethodChannel.Result result) {
        //todo webView.reload();
        result.success(null);
    }

    private void currentUrl(MethodChannel.Result result) {
        //todo result.success(webView.getUrl());
    }

    @SuppressWarnings("unchecked")
    private void updateSettings(MethodCall methodCall, MethodChannel.Result result) {
        applySettings((Map<String, Object>) methodCall.arguments);
        result.success(null);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void evaluateJavaScript(MethodCall methodCall, final MethodChannel.Result result) {
        String jsString = (String) methodCall.arguments;
        if (jsString == null) {
            throw new UnsupportedOperationException("JavaScript string cannot be null");
        }
        //todo
//        webView.evaluateJavascript(
//                jsString,
//                new android.webkit.ValueCallback<String>() {
//                    @Override
//                    public void onReceiveValue(String value) {
//                        result.success(value);
//                    }
//                });
    }

    @SuppressWarnings("unchecked")
    private void addJavaScriptChannels(MethodCall methodCall, MethodChannel.Result result) {
        List<String> channelNames = (List<String>) methodCall.arguments;
        //todo registerJavaScriptChannelNames(channelNames);
        result.success(null);
    }

    @SuppressWarnings("unchecked")
    private void removeJavaScriptChannels(MethodCall methodCall, MethodChannel.Result result) {
        List<String> channelNames = (List<String>) methodCall.arguments;
        for (String channelName : channelNames) {
            //todo webView.removeJavascriptInterface(channelName);
        }
        result.success(null);
    }

    private void clearCache(MethodChannel.Result result) {
        //todo webView.clearCache(true);
        WebStorage.getInstance().deleteAllData();
        result.success(null);
    }

    private void getTitle(MethodChannel.Result result) {
        //todo result.success(webView.getTitle());
    }

    private void scrollTo(MethodCall methodCall, MethodChannel.Result result) {
        Map<String, Object> request = methodCall.arguments();
        int x = (int) request.get("x");
        int y = (int) request.get("y");

        //todo webView.scrollTo(x, y);

        result.success(null);
    }

    private void scrollBy(MethodCall methodCall, MethodChannel.Result result) {
        Map<String, Object> request = methodCall.arguments();
        int x = (int) request.get("x");
        int y = (int) request.get("y");

        //todo webView.scrollBy(x, y);
        result.success(null);
    }

    private void getScrollX(MethodChannel.Result result) {
        //todo result.success(webView.getScrollX());
    }

    private void getScrollY(MethodChannel.Result result) {
        //todo result.success(webView.getScrollY());
    }

    private void applySettings(Map<String, Object> settings) {
        for (String key : settings.keySet()) {
            switch (key) {
                case "jsMode":
                    Integer mode = (Integer) settings.get(key);
//                    if (mode != null) {
//                        updateJsMode(mode);
//                    }
                    break;
                case "hasNavigationDelegate":
                    final boolean hasNavigationDelegate = (boolean) settings.get(key);

//                    final WebViewClient webViewClient =
//                            flutterWebViewClient.createWebViewClient(hasNavigationDelegate);

                    //webView.setWebViewClient(webViewClient);
                    break;
                case "debuggingEnabled":
                    final boolean debuggingEnabled = (boolean) settings.get(key);

//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                        webView.setWebContentsDebuggingEnabled(debuggingEnabled);
//                    }
                    break;
                case "hasProgressTracking":
                    //flutterWebViewClient.hasProgressTracking = (boolean) settings.get(key);
                    break;
                case "gestureNavigationEnabled":
                    break;
                case "userAgent":
                    //updateUserAgent((String) settings.get(key));
                    break;
                case "allowsInlineMediaPlayback":
                    // no-op inline media playback is always allowed on Android.
                    break;
                default:
                    throw new IllegalArgumentException("Unknown WebView setting: " + key);
            }
        }
    }

    public static class Builder{
        public static final String TAG = "Builder";

        private final Context mPppContext;

        public Builder(Context appContext) {
            this.mPppContext = appContext;
            userScreenSize();
        }


        private long id;

        private SurfaceTexture mSurfaceTexture;

        private Surface mSurface;

        public Builder init(TextureRegistry.SurfaceTextureEntry textureEntry) {
            mSurfaceTexture = textureEntry.surfaceTexture();
            id = textureEntry.id();
            return this;
        }

        /**
         * The surfaceTexture's DefaultBufferSize
         *
         * Use screen's width and height in default.
         */
        private int width;
        private int height;

        /**
         * Set surfaceTexture's DefaultBufferSize
         * @param width
         * @param height
         * @return
         */
        public Builder setSurfaceDefaultBufferSize(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public WebViewSurfaceClient build(FlutterPlugin.FlutterPluginBinding flutterPluginBinding) {
            LogUtil.logMsg(TAG," size : " + width + "  " + height);
            mSurfaceTexture.setDefaultBufferSize(width, height);
            mSurface = new Surface(mSurfaceTexture);
            return new WebViewSurfaceClient(id, mSurface, flutterPluginBinding.getBinaryMessenger());
        }

        private void userScreenSize() {
            DisplayMetrics dm = mPppContext.getResources().getDisplayMetrics();
            width = dm.widthPixels;
            height = dm.heightPixels;
        }
        
    }
}
