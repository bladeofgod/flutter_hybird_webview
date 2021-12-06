import 'package:flutter/services.dart';

/// author：JiaQiLi
/// date：2021/9/15
/// remark：


class RemoteWebViewPlugin{
  static const MethodChannel _channel = MethodChannel('remote_webview_plugin');

  ///Create webView.
  /// * will trigger platform producer to generate a remote web_view.
  static Future<int> createWebView(Map<String,dynamic> args) async {
    return await _channel.invokeMethod('create', args);
  }

  @Deprecated('Test method')
  static Future<int> produceWebView(Map<String,String> params) async {
    return await _channel.invokeMethod('produceWebView', params);
  }

  static Future<void> sendMotionEvent(List<dynamic> motionEvent) async {
    return await _channel.invokeMethod('touch', motionEvent);
  }


  ///Dispose the view
  ///[id] textureId
  static Future<void> dispose(int id) async {
    return await _channel.invokeMethod('dispose', {'viewId':id});
  }

  ///Dispose all remote-view.
  /// * It will invalid all related texture's content.
  /// * Use careful!
  static Future<void> disposeAll() async {
    return await _channel.invokeMethod('disposeAll');
  }

}



















