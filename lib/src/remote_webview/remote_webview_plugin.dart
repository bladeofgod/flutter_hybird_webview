import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';

/// author：JiaQiLi
/// date：2021/9/15
/// remark：

const String webViewTag = 'viewId';

_WebViewRegister webViewRegister = _WebViewRegister();

class RemoteWebViewPlugin{
  static const MethodChannel _channel = MethodChannel('remote_webview_plugin');

  RemoteWebViewPlugin() {
    _channel.setMethodCallHandler(_onMethodCall);
  }

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
    return await _channel.invokeMethod('dispose', {webViewTag:'$id'});
  }

  ///Dispose all remote-view.
  /// * It will invalid all related texture's content.
  /// * Use careful!
  static Future<void> disposeAll() async {
    return await _channel.invokeMethod('disposeAll');
  }


  Future<dynamic> _onMethodCall(MethodCall call) async {
    switch(call.method) {
      case "getTopWebId" :
        return webViewRegister.getTopWebId();
    }
  }

}


class WebDisposableContext extends DisposableBuildContext{

  WebDisposableContext(State<StatefulWidget> state) : super(state);

  final Map<String, dynamic> _setting = {};

  void loadSetting(Map<String, dynamic> setting) {
    _setting.addAll(setting);
  }

  int getViewId() {
    return context == null ? -20 : _setting[webViewTag];
  }

}


class _WebViewRegister{

  final List<WebDisposableContext> _contextRecord = [];

  void registerContext(WebDisposableContext context) {
    _contextRecord.add(context);
  }

  void unRegisterContext(WebDisposableContext context) {
    _contextRecord.remove(context);
  }

  ///get the viewId of top route, if there is non return -20.
  int getTopWebId() {
    WebDisposableContext webCtx = _contextRecord.lastWhere((ctx) {
      if(ctx.context != null) {
        return ModalRoute.of(ctx.context!)?.isCurrent ?? false;
      } else {
        return false;
      }
    }, orElse: ()=>_contextRecord.last);
    return webCtx.getViewId();
  }

}



























