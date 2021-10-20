package remote_webview.view;

import android.annotation.TargetApi;
import android.app.Presentation;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import remote_webview.RemoteZygoteActivity;
import remote_webview.interfaces.IMockMethodHandler;
import remote_webview.interfaces.IMockMethodResult;
import remote_webview.mock.MockMethodCall;
import remote_webview.mock.MockMethodChannel;
import remote_webview.mock.RemoteJavaScriptChannel;
import remote_webview.model.WebViewCreationParamsModel;
import remote_webview.service.hub.RemoteBinderCommHub;
import remote_webview.utils.LogUtil;

public class WebViewPresentation extends Presentation implements IMockMethodHandler {
    
    private final static String TAG = "WebViewPresentation";

    private final MockMethodChannel methodChannel;
    private final Handler platformThreadHandler;
    private final long surfaceId;
    private WebView webView;

    private WebViewCreationParamsModel initialParams;

    public WebViewPresentation(Context outerContext, Display display, MockMethodChannel methodChannel
            , long surfaceId) {
        super(outerContext, display);
        this.surfaceId = surfaceId;
        this.methodChannel = methodChannel;
        this.webView = createWebView();
        platformThreadHandler = new Handler(outerContext.getMainLooper());

        plugInHub();
    }

    private void plugInHub() {
        RemoteBinderCommHub.getInstance().plugInMethodHandler(surfaceId,this);
    }

    private void plugOutHub() {
        RemoteBinderCommHub.getInstance().plugOutMethodHandler(surfaceId);
    }

    public void dispose() {
        //todo clean up
        plugOutHub();
    }

    private WebView createWebView() {
        WebView webView = new WebView(RemoteZygoteActivity.zygoteActivity);
        //todo update web view init params  see -> WebViewCreationParamsModel
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.e("webview","onPageStarted  " + url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.e("webview","onPageFinished  " + url);
            }
        });
        return webView;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void createWithOrders(WebViewCreationParamsModel creationParamsModel) {
        initialParams = creationParamsModel;
        create();
    }

    public void showWithUrl() {
        LogUtil.logMsg(TAG,"showWithUrl");
        if(initialParams.getUrl() != null && !initialParams.getUrl().isEmpty()) {
            webView.loadUrl(initialParams.getUrl());
        }
        show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(webView);
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        webView.dispatchTouchEvent(ev);
        //return super.dispatchTouchEvent(ev);
        return true;
    }


    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        webView.dispatchKeyEvent(event);
        //return super.dispatchKeyEvent(event);
        return true;
    }

    @Override
    public void onMethodCall(@NonNull MockMethodCall methodCall, @NonNull IMockMethodResult result) {
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
    private void loadUrl(MockMethodCall methodCall, IMockMethodResult result) {
        Map<String, Object> request = (Map<String, Object>) methodCall.arguments;
        String url = (String) request.get("url");
        Map<String, String> headers = (Map<String, String>) request.get("headers");
        if (headers == null) {
            headers = Collections.emptyMap();
        }
        webView.loadUrl(url, headers);
        result.success(null);
    }

    private void canGoBack(IMockMethodResult result) {
        HashMap<String,String> p = new HashMap<>();
        p.put("canGoBack",webView.canGoBack()? "true" : "false");
        result.success(p);
    }

    private void canGoForward(IMockMethodResult result) {
        HashMap<String,String> p = new HashMap<>();
        p.put("canGoForward",webView.canGoForward()? "true" : "false");
        result.success(p);
    }

    private void goBack(IMockMethodResult result) {
        if (webView.canGoBack()) {
            webView.goBack();
        }
        result.success(null);
    }

    private void goForward(IMockMethodResult result) {
        if (webView.canGoForward()) {
            webView.goForward();
        }
        result.success(null);
    }

    private void reload(IMockMethodResult result) {
        webView.reload();
        result.success(null);
    }

    private void currentUrl(IMockMethodResult result) {
        HashMap<String,String> p = new HashMap<>();
        p.put("canGoForward",webView.getUrl());
        result.success(p);
    }

    @SuppressWarnings("unchecked")
    private void updateSettings(MockMethodCall methodCall, IMockMethodResult result) {
        applySettings((Map<String, Object>) methodCall.arguments);
        result.success(null);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void evaluateJavaScript(MockMethodCall methodCall, final IMockMethodResult result) {
        String jsString = (String) methodCall.arguments.get("jsString");
        if (jsString == null) {
            throw new UnsupportedOperationException("JavaScript string cannot be null");
        }
        webView.evaluateJavascript(
                jsString,
                new android.webkit.ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        HashMap<String,String> p = new HashMap<>();
                        p.put("evaluateJavaScript",value);
                        result.success(p);
                    }
                });
    }

    @SuppressWarnings("unchecked")
    private void addJavaScriptChannels(MockMethodCall methodCall, IMockMethodResult result) {
        List<String> channelNames = (List<String>) methodCall.arguments;
        registerJavaScriptChannelNames(channelNames);
        result.success(null);
    }

    @SuppressWarnings("unchecked")
    private void removeJavaScriptChannels(MockMethodCall methodCall, IMockMethodResult result) {
        List<String> channelNames = (List<String>) methodCall.arguments;
        for (String channelName : channelNames) {
            webView.removeJavascriptInterface(channelName);
        }
        result.success(null);
    }

    private void clearCache(IMockMethodResult result) {
        webView.clearCache(true);
        WebStorage.getInstance().deleteAllData();
        result.success(null);
    }

    private void getTitle(IMockMethodResult result) {
        HashMap<String,String> p = new HashMap<>();
        p.put("getTitle",webView.getTitle());
        result.success(p);
    }

    private void scrollTo(MockMethodCall methodCall, IMockMethodResult result) {
        Map<String, Object> request = methodCall.arguments();
        int x = (int) request.get("x");
        int y = (int) request.get("y");

        webView.scrollTo(x, y);

        result.success(null);
    }

    private void scrollBy(MockMethodCall methodCall, IMockMethodResult result) {
        Map<String, Object> request = methodCall.arguments();
        int x = (int) request.get("x");
        int y = (int) request.get("y");

        webView.scrollBy(x, y);
        result.success(null);
    }

    private void getScrollX(IMockMethodResult result) {
        HashMap<String,String> p = new HashMap<>();
        p.put("getScrollX",webView.getScrollX() + "");
        result.success(p);
    }

    private void getScrollY(IMockMethodResult result) {
        HashMap<String,String> p = new HashMap<>();
        p.put("getScrollY",webView.getScrollY() + "");
        result.success(p);
    }

    private void applySettings(Map<String, Object> settings) {
//        for (String key : settings.keySet()) {
//            switch (key) {
//                case "jsMode":
//                    Integer mode = (Integer) settings.get(key);
//                    if (mode != null) {
//                        updateJsMode(mode);
//                    }
//                    break;
//                case "hasNavigationDelegate":
//                    final boolean hasNavigationDelegate = (boolean) settings.get(key);
//
//                    final WebViewClient webViewClient =
//                            flutterWebViewClient.createWebViewClient(hasNavigationDelegate);
//
//                    webView.setWebViewClient(webViewClient);
//                    break;
//                case "debuggingEnabled":
//                    final boolean debuggingEnabled = (boolean) settings.get(key);
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                        webView.setWebContentsDebuggingEnabled(debuggingEnabled);
//                    }
//                    break;
//                case "hasProgressTracking":
//                    flutterWebViewClient.hasProgressTracking = (boolean) settings.get(key);
//                    break;
//                case "gestureNavigationEnabled":
//                    break;
//                case "userAgent":
//                    updateUserAgent((String) settings.get(key));
//                    break;
//                case "allowsInlineMediaPlayback":
//                    // no-op inline media playback is always allowed on Android.
//                    break;
//                default:
//                    throw new IllegalArgumentException("Unknown WebView setting: " + key);
//            }
//        }
    }

    private void updateJsMode(int mode) {
        switch (mode) {
            case 0: // disabled
                webView.getSettings().setJavaScriptEnabled(false);
                break;
            case 1: // unrestricted
                webView.getSettings().setJavaScriptEnabled(true);
                break;
            default:
                throw new IllegalArgumentException("Trying to set unknown JavaScript mode: " + mode);
        }
    }

    private void updateAutoMediaPlaybackPolicy(int mode) {
        // This is the index of the AutoMediaPlaybackPolicy enum, index 1 is always_allow, for all
        // other values we require a user gesture.
        boolean requireUserGesture = mode != 1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webView.getSettings().setMediaPlaybackRequiresUserGesture(requireUserGesture);
        }
    }

    private void registerJavaScriptChannelNames(List<String> channelNames) {
        for (String channelName : channelNames) {
            webView.addJavascriptInterface(
                    new RemoteJavaScriptChannel(methodChannel,
                            channelName, platformThreadHandler)
                    , channelName);
        }
    }

    private void updateUserAgent(String userAgent) {
        webView.getSettings().setUserAgentString(userAgent);
    }
}
