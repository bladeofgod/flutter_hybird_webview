package remote_webview.view;

import android.annotation.TargetApi;
import android.app.Presentation;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.plugins.webviewflutter.FlutterWebView;
import remote_webview.RemoteZygoteActivity;
import remote_webview.interfaces.IMockMethodHandler;
import remote_webview.interfaces.IMockMethodResult;
import remote_webview.mock.MockMethodCall;
import remote_webview.mock.MockMethodChannel;
import remote_webview.mock.RemoteJavaScriptChannel;
import remote_webview.model.WebViewCreationParamsModel;
import remote_webview.service.decoder.PackageHandler;
import remote_webview.service.decoder.WebViewPackageHandler;
import remote_webview.service.hub.RemoteBinderCommHub;
import remote_webview.utils.HandlerUtil;
import remote_webview.utils.LogUtil;

public class WebViewPresentation extends RemoteViewPresentation implements IMockMethodHandler {
    
    private final static String TAG = "WebViewPresentation";

    private final MockMethodChannel methodChannel;
    //Handle js
    private final Handler platformThreadHandler;

    private WebViewCreationParamsModel initialParams;
    
    private final PackageHandler packageHandler = new WebViewPackageHandler();

    private final FlutterRemoteWebViewClient flutterWebViewClient;

    public WebViewPresentation(Context outerContext, WebViewCreationParamsModel creationParamsModel,
                               Display display, long surfaceId,
                               RemoteAccessibilityEventsDelegate accessibilityEventsDelegate) {
        super(outerContext, display, accessibilityEventsDelegate, surfaceId , true);
        initialParams = creationParamsModel;
        this.methodChannel = new MockMethodChannel(surfaceId);
        platformThreadHandler = new Handler(outerContext.getMainLooper());
        flutterWebViewClient = new FlutterRemoteWebViewClient(methodChannel);
        plugInHub();
    }

    @Override
    protected void plugInHub() {
        RemoteBinderCommHub.getInstance().plugInMethodHandler(viewId,this);
    }

    @Override
    protected void plugOutHub() {
        RemoteBinderCommHub.getInstance().plugOutMethodHandler(viewId);
    }

    @Override
    public void dispose() {
        plugOutHub();
        cancel();
        detachState();
        getWebView().destroy();
        super.dispose();
        dismiss();
    }

    // Verifies that a url opened by `Window.open` has a secure url.
    private class FlutterWebChromeClient extends WebChromeClient {

        @Override
        public boolean onCreateWindow(
                final WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            final WebViewClient webViewClient =
                    new WebViewClient() {
                        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public boolean shouldOverrideUrlLoading(
                                @NonNull WebView view, @NonNull WebResourceRequest request) {
                            final String url = request.getUrl().toString();
                            if (!flutterWebViewClient.shouldOverrideUrlLoading(
                                    getWebView(), request)) {
                                getWebView().loadUrl(url);
                            }
                            return true;
                        }

                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            if (!flutterWebViewClient.shouldOverrideUrlLoading(
                                    getWebView(), url)) {
                                getWebView().loadUrl(url);
                            }
                            return true;
                        }
                    };

            final WebView newWebView = new WebView(view.getContext());
            newWebView.setWebViewClient(webViewClient);
            final WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(newWebView);
            resultMsg.sendToTarget();

            return true;
        }

        @Override
        public void onProgressChanged(WebView view, int progress) {
            flutterWebViewClient.onLoadingProgress(progress);
        }
    }

    private static WebView createWebView(RemoteWebViewBuilder webViewBuilder, WebChromeClient webChromeClient) {
        webViewBuilder
                .setDomStorageEnabled(true) // Always enable DOM storage API.
                .setJavaScriptCanOpenWindowsAutomatically(
                        true) // Always allow automatically opening of windows.
                .setSupportMultipleWindows(true) // Always support multiple windows.
                .setWebChromeClient(
                        webChromeClient);
        return webViewBuilder.build();
    }

    @Override
    public void show() {
        if(initialParams.getUrl() != null && !initialParams.getUrl().isEmpty()) {
            getWebView().loadUrl(initialParams.getUrl());
        }
        super.show();
    }

    private WebView getWebView() {
        return (WebView) state.childView;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }


    @Override
    public View createChildView(final View containerView) {

        return createTestWebView(containerView);
//        return createWebView(new RemoteWebViewBuilder(RemoteZygoteActivity.zygoteActivity),
//                new FlutterWebChromeClient());
    }

    //todo this code is just for dev.
    private WebView createTestWebView(View containerView) {
        WebView webView = new WebView(RemoteZygoteActivity.zygoteActivity);
        //todo update web view init params  see -> WebViewCreationParamsModel
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setSupportMultipleWindows(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.e("webview","onPageStarted  " + url);
                flutterWebViewClient.onPageStarted(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.e("webview","onPageFinished  " + url);
                flutterWebViewClient.onPageFinished(view, url);
                final Bundle bundle = new Bundle();
                bundle.putString("url", url);
                //when web page finished, we send a bundle to main process for save.
                //in some exception case, we can restore by it.
                saveViewStateInstance(bundle);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                flutterWebViewClient.onLoadingProgress(newProgress);
            }
        });
        return webView;
    }


    /**
     * 
     * @param methodCall method model with name and argument
     * @param result result call back, must ensure call it after method-call.
     */
    @Override
    public void onMethodCall(@NonNull MockMethodCall methodCall, @NonNull IMockMethodResult result) {
        LogUtil.logMsg(this.toString(), methodCall.toString());
        switch (methodCall.method) {
            case "loadUrl":
                loadUrl(methodCall, result);
                break;
            case "updateSettings":
                updateSettings(methodCall, result);
                break;
            case "canGoBack":
                canGoBack(methodCall, result);
                break;
            case "canGoForward":
                canGoForward(methodCall, result);
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
                currentUrl(methodCall, result);
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
                getTitle(methodCall, result);
                break;
            case "scrollTo":
                scrollTo(methodCall, result);
                break;
            case "scrollBy":
                scrollBy(methodCall, result);
                break;
            case "getScrollX":
                getScrollX(methodCall, result);
                break;
            case "getScrollY":
                getScrollY(methodCall, result);
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
        getWebView().loadUrl(url, headers);
        result.success(null);
    }

    private void canGoBack(MockMethodCall methodCall, IMockMethodResult result) {
        HashMap<String,String> p = new HashMap<>();
        p.put("canGoBack",getWebView().canGoBack()? "true" : "false");
        result.success(packageHandler.markPackageWithMethodName(methodCall.method, p));
    }

    private void canGoForward(MockMethodCall methodCall, IMockMethodResult result) {
        HashMap<String,String> p = new HashMap<>();
        p.put("canGoForward",getWebView().canGoForward()? "true" : "false");
        result.success(packageHandler.markPackageWithMethodName(methodCall.method, p));
    }

    private void goBack(IMockMethodResult result) {
        if (getWebView().canGoBack()) {
            getWebView().goBack();
        }
        result.success(null);
    }

    private void goForward(IMockMethodResult result) {
        if (getWebView().canGoForward()) {
            getWebView().goForward();
        }
        result.success(null);
    }

    private void reload(IMockMethodResult result) {
        getWebView().reload();
        result.success(null);
    }

    private void currentUrl(MockMethodCall methodCall, IMockMethodResult result) {
        HashMap<String,String> p = new HashMap<>();
        p.put("currentUrl",getWebView().getUrl());
        result.success(packageHandler.markPackageWithMethodName(methodCall.method, p));
    }

    @SuppressWarnings("unchecked")
    private void updateSettings(MockMethodCall methodCall, IMockMethodResult result) {
        applySettings((Map<String, Object>) methodCall.arguments);
        result.success(null);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void evaluateJavaScript(final MockMethodCall methodCall, final IMockMethodResult result) {
        String jsString = (String) methodCall.arguments.get("jsString");
        if (jsString == null) {
            throw new UnsupportedOperationException("JavaScript string cannot be null");
        }
        getWebView().evaluateJavascript(
                jsString,
                new android.webkit.ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        HashMap<String,String> p = new HashMap<>();
                        p.put("evaluateJavaScript",value);
                        result.success(packageHandler.markPackageWithMethodName(methodCall.method, p));
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
            getWebView().removeJavascriptInterface(channelName);
        }
        result.success(null);
    }

    private void clearCache(IMockMethodResult result) {
        getWebView().clearCache(true);
        WebStorage.getInstance().deleteAllData();
        result.success(null);
    }

    private void getTitle(MockMethodCall methodCall, IMockMethodResult result) {
        HashMap<String,String> p = new HashMap<>();
        p.put("getTitle",getWebView().getTitle());
        result.success(packageHandler.markPackageWithMethodName(methodCall.method, p));
    }

    private void scrollTo(MockMethodCall methodCall, IMockMethodResult result) {
        Map<String, Object> request = methodCall.arguments();
        int x = (int) request.get("x");
        int y = (int) request.get("y");

        getWebView().scrollTo(x, y);

        result.success(null);
    }

    private void scrollBy(MockMethodCall methodCall, IMockMethodResult result) {
        Map<String, Object> request = methodCall.arguments();
        int x = (int) request.get("x");
        int y = (int) request.get("y");

        getWebView().scrollBy(x, y);
        result.success(null);
    }

    private void getScrollX(MockMethodCall methodCall, IMockMethodResult result) {
        HashMap<String,String> p = new HashMap<>();
        p.put("getScrollX",getWebView().getScrollX() + "");
        result.success(packageHandler.markPackageWithMethodName(methodCall.method, p));
    }

    private void getScrollY(MockMethodCall methodCall, IMockMethodResult result) {
        HashMap<String,String> p = new HashMap<>();
        p.put("getScrollY",getWebView().getScrollY() + "");
        result.success(packageHandler.markPackageWithMethodName(methodCall.method, p));
    }

    private void applySettings(Map<String, Object> settings) {
        //TODO
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
//                    getWebView().setWebViewClient(webViewClient);
//                    break;
//                case "debuggingEnabled":
//                    final boolean debuggingEnabled = (boolean) settings.get(key);
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                        getWebView().setWebContentsDebuggingEnabled(debuggingEnabled);
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
                getWebView().getSettings().setJavaScriptEnabled(false);
                break;
            case 1: // unrestricted
                getWebView().getSettings().setJavaScriptEnabled(true);
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
            getWebView().getSettings().setMediaPlaybackRequiresUserGesture(requireUserGesture);
        }
    }

    private void registerJavaScriptChannelNames(List<String> channelNames) {
        for (String channelName : channelNames) {
            getWebView().addJavascriptInterface(
                    new RemoteJavaScriptChannel(methodChannel,
                            channelName, platformThreadHandler)
                    , channelName);
        }
    }

    private void updateUserAgent(String userAgent) {
        getWebView().getSettings().setUserAgentString(userAgent);
    }
}
