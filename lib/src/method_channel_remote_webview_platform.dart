


import 'package:webview_flutter/platform_interface.dart';




///A [MethodChannelRemoteWebViewPlatform] that used a method channel to control
///the remote-webview.
class MethodChannelRemoteWebViewPlatform implements WebViewPlatformController{
  @override
  Future<void> addJavascriptChannels(Set<String> javascriptChannelNames) {
    // TODO: implement addJavascriptChannels
    throw UnimplementedError();
  }

  @override
  Future<bool> canGoBack() {
    // TODO: implement canGoBack
    throw UnimplementedError();
  }

  @override
  Future<bool> canGoForward() {
    // TODO: implement canGoForward
    throw UnimplementedError();
  }

  @override
  Future<void> clearCache() {
    // TODO: implement clearCache
    throw UnimplementedError();
  }

  @override
  Future<String?> currentUrl() {
    // TODO: implement currentUrl
    throw UnimplementedError();
  }

  @override
  Future<String> evaluateJavascript(String javascriptString) {
    // TODO: implement evaluateJavascript
    throw UnimplementedError();
  }

  @override
  Future<int> getScrollX() {
    // TODO: implement getScrollX
    throw UnimplementedError();
  }

  @override
  Future<int> getScrollY() {
    // TODO: implement getScrollY
    throw UnimplementedError();
  }

  @override
  Future<String?> getTitle() {
    // TODO: implement getTitle
    throw UnimplementedError();
  }

  @override
  Future<void> goBack() {
    // TODO: implement goBack
    throw UnimplementedError();
  }

  @override
  Future<void> goForward() {
    // TODO: implement goForward
    throw UnimplementedError();
  }

  @override
  Future<void> loadUrl(String url, Map<String, String>? headers) {
    // TODO: implement loadUrl
    throw UnimplementedError();
  }

  @override
  Future<void> reload() {
    // TODO: implement reload
    throw UnimplementedError();
  }

  @override
  Future<void> removeJavascriptChannels(Set<String> javascriptChannelNames) {
    // TODO: implement removeJavascriptChannels
    throw UnimplementedError();
  }

  @override
  Future<void> scrollBy(int x, int y) {
    // TODO: implement scrollBy
    throw UnimplementedError();
  }

  @override
  Future<void> scrollTo(int x, int y) {
    // TODO: implement scrollTo
    throw UnimplementedError();
  }

  @override
  Future<void> updateSettings(WebSettings setting) {
    // TODO: implement updateSettings
    throw UnimplementedError();
  }

}



























