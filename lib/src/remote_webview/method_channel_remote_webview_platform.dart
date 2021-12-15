


import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:webview_flutter/platform_interface.dart';

const String pluginRootName = 'hybird.flutter';

///A [MethodChannelRemoteWebViewPlatform] that used a method channel to control
///the remote-webview.
class MethodChannelRemoteWebViewPlatform implements WebViewPlatformController{

  MethodChannelRemoteWebViewPlatform(this.id, this._platformCallbacksHandler)
    : _channel = MethodChannel('$pluginRootName/webview_$id') {
    _channel.setMethodCallHandler(_onMethodCall);
  }

  final int id;

  final WebViewPlatformCallbacksHandler _platformCallbacksHandler;

  final MethodChannel _channel;

  static const MethodChannel _cookieManagerChannel =
  MethodChannel('plugins.flutter.io/cookie_manager');

  Future<bool?> _onMethodCall(MethodCall call) async {
    debugPrint('_onMethodCall  $call');
    switch (call.method) {
      case 'javascriptChannelMessage':
        final String channel = call.arguments['channel']!;
        final String message = call.arguments['message']!;
        _platformCallbacksHandler.onJavaScriptChannelMessage(channel, message);
        return true;
      case 'navigationRequest':
        return await _platformCallbacksHandler.onNavigationRequest(
          url: call.arguments['url']!,
          isForMainFrame: call.arguments['isForMainFrame']!,
        );
      case 'onPageFinished':
        _platformCallbacksHandler.onPageFinished(call.arguments['url']!);
        return null;
      case 'onProgress':
        _platformCallbacksHandler.onProgress(call.arguments['progress']);
        return null;
      case 'onPageStarted':
        _platformCallbacksHandler.onPageStarted(call.arguments['url']!);
        return null;
      case 'onWebResourceError':
        _platformCallbacksHandler.onWebResourceError(
          WebResourceError(
            errorCode: call.arguments['errorCode']!,
            description: call.arguments['description']!,
            // iOS doesn't support `failingUrl`.
            failingUrl: call.arguments['failingUrl'],
            domain: call.arguments['domain'],
            errorType: call.arguments['errorType'] == null
                ? null
                : WebResourceErrorType.values.firstWhere(
                  (WebResourceErrorType type) {
                return type.toString() ==
                    '$WebResourceErrorType.${call.arguments['errorType']}';
              },
            ),
          ),
        );
        return null;
    }

    throw MissingPluginException(
      '${call.method} was invoked but has no handler',
    );
  }


  @override
  Future<void> loadUrl(
      String url,
      Map<String, String>? headers,
      ) async {
    assert(url != null);
    return _channel.invokeMethod<void>('loadUrl', <String, dynamic>{
      'url': url,
      'headers': headers,
    });
  }

  @override
  Future<String?> currentUrl() => _channel.invokeMethod<String>('currentUrl');

  @override
  Future<bool> canGoBack() =>
      _channel.invokeMethod<bool>("canGoBack").then((result) => result!);

  @override
  Future<bool> canGoForward() =>
      _channel.invokeMethod<bool>("canGoForward").then((result) => result!);

  @override
  Future<void> goBack() => _channel.invokeMethod<void>("goBack");

  @override
  Future<void> goForward() => _channel.invokeMethod<void>("goForward");

  @override
  Future<void> reload() => _channel.invokeMethod<void>("reload");

  @override
  Future<void> clearCache() => _channel.invokeMethod<void>("clearCache");

  @override
  Future<void> updateSettings(WebSettings settings) async {
    final Map<String, dynamic> updatesMap = _webSettingsToMap(settings);
    if (updatesMap.isNotEmpty) {
      await _channel.invokeMethod<void>('updateSettings', updatesMap);
    }
  }

  @override
  Future<String> evaluateJavascript(String javascriptString) {
    return _channel
        .invokeMethod<String>('evaluateJavascript', javascriptString)
        .then((result) => result!);
  }

  @override
  Future<void> addJavascriptChannels(Set<String> javascriptChannelNames) {
    return _channel.invokeMethod<void>(
        'addJavascriptChannels', javascriptChannelNames.toList());
  }

  @override
  Future<void> removeJavascriptChannels(Set<String> javascriptChannelNames) {
    return _channel.invokeMethod<void>(
        'removeJavascriptChannels', javascriptChannelNames.toList());
  }

  @override
  Future<String?> getTitle() => _channel.invokeMethod<String>("getTitle");

  @override
  Future<void> scrollTo(int x, int y) {
    return _channel.invokeMethod<void>('scrollTo', <String, int>{
      'x': x,
      'y': y,
    });
  }

  @override
  Future<void> scrollBy(int x, int y) {
    return _channel.invokeMethod<void>('scrollBy', <String, int>{
      'x': x,
      'y': y,
    });
  }

  @override
  Future<int> getScrollX() =>
      _channel.invokeMethod<int>("getScrollX").then((result) => result!);

  @override
  Future<int> getScrollY() =>
      _channel.invokeMethod<int>("getScrollY").then((result) => result!);

  /// Method channel implementation for [WebViewPlatform.clearCookies].
  static Future<bool> clearCookies() {
    return _cookieManagerChannel
        .invokeMethod<bool>('clearCookies')
        .then<bool>((dynamic result) => result!);
  }

  static Map<String, dynamic> _webSettingsToMap(WebSettings? settings) {
    final Map<String, dynamic> map = <String, dynamic>{};
    void _addIfNonNull(String key, dynamic value) {
      if (value == null) {
        return;
      }
      map[key] = value;
    }

    void _addSettingIfPresent<T>(String key, WebSetting<T> setting) {
      if (!setting.isPresent) {
        return;
      }
      map[key] = setting.value;
    }

    _addIfNonNull('jsMode', settings!.javascriptMode?.index);
    _addIfNonNull('hasNavigationDelegate', settings.hasNavigationDelegate);
    _addIfNonNull('hasProgressTracking', settings.hasProgressTracking);
    _addIfNonNull('debuggingEnabled', settings.debuggingEnabled);
    _addIfNonNull(
        'gestureNavigationEnabled', settings.gestureNavigationEnabled);
    _addIfNonNull(
        'allowsInlineMediaPlayback', settings.allowsInlineMediaPlayback);
    _addSettingIfPresent('userAgent', settings.userAgent);
    return map;
  }

  /// Converts a [CreationParams] object to a map as expected by `platform_views` channel.
  ///
  /// This is used for the `creationParams` argument of the platform views created by
  /// [AndroidWebViewBuilder] and [CupertinoWebViewBuilder].
  static Map<String, dynamic> creationParamsToMap(
      CreationParams creationParams, {
        bool usesHybridComposition = false,
      }) {
    return <String, dynamic>{
      'initialUrl': creationParams.initialUrl,
      'settings': _webSettingsToMap(creationParams.webSettings),
      'javascriptChannelNames': creationParams.javascriptChannelNames.toList(),
      'userAgent': creationParams.userAgent,
      'autoMediaPlaybackPolicy': creationParams.autoMediaPlaybackPolicy.index,
      'usesHybridComposition': usesHybridComposition,
    };
  }

}



























