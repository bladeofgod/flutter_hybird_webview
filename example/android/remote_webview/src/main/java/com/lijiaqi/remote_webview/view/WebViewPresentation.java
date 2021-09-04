package com.lijiaqi.remote_webview.view;

import android.annotation.TargetApi;
import android.app.Presentation;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.view.Display;
import android.webkit.WebStorage;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import com.lijiaqi.remote_webview.interfaces.IMockMethodHandler;
import com.lijiaqi.remote_webview.interfaces.IMockMethodResult;
import com.lijiaqi.remote_webview.mock.MockMethodCall;
import com.lijiaqi.remote_webview.mock.MockMethodChannel;
import com.lijiaqi.remote_webview.mock.RemoteJavaScriptChannel;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class WebViewPresentation extends Presentation implements IMockMethodHandler {

    private final MockMethodChannel methodChannel;
    private final WebView webView;
    private final Handler platformThreadHandler;

    public WebViewPresentation(Context outerContext, Display display, MockMethodChannel methodChannel, WebView webView) {
        super(outerContext, display);
        this.methodChannel = methodChannel;
        this.webView = webView;
        platformThreadHandler = new Handler(outerContext.getMainLooper());
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
        result.success(webView.canGoBack());
    }

    private void canGoForward(IMockMethodResult result) {
        result.success(webView.canGoForward());
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
        result.success(webView.getUrl());
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
                        result.success(value);
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
        result.success(webView.getTitle());
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
        result.success(webView.getScrollX());
    }

    private void getScrollY(IMockMethodResult result) {
        result.success(webView.getScrollY());
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
                    new RemoteJavaScriptChannel(methodChannel, channelName, platformThreadHandler), channelName);
        }
    }

    private void updateUserAgent(String userAgent) {
        webView.getSettings().setUserAgentString(userAgent);
    }
}
