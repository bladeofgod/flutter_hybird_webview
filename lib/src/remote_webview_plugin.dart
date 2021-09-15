import 'package:flutter/services.dart';

/// author：JiaQiLi
/// date：2021/9/15
/// remark：


class RemoteWebViewPlugin{
  static const MethodChannel _channel = MethodChannel('remote_webview_plugin');

  static Future<int> produceWebView() async {
    return await _channel.invokeMethod('produceWebView');
  }

}



















