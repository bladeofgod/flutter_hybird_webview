

import 'package:flutter/foundation.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:webview_flutter/platform_interface.dart';
import 'package:webview_flutter/src/webview_android.dart';
import 'package:webview_flutter/webview_flutter.dart';

import 'method_channel_remote_webview_platform.dart';
import 'remote_webview_plugin.dart';
import 'webview_method_channel.dart';

/// author：JiaQiLi
/// date：2021/9/15
/// remark：

/// Android [WebViewPlatform] that uses [Texture] to build the [WebView] Widget.
///
/// This implementation uses [Texture] widget, with render's data is from another process,
/// and render the [WebView] on Android finally.
class TextureAndroidWebView extends AndroidWebView{
  
  @override
  Widget build(
      {required BuildContext context,
      required CreationParams creationParams,
      required WebViewPlatformCallbacksHandler webViewPlatformCallbacksHandler,
      WebViewPlatformCreatedCallback? onWebViewPlatformCreated,
      Set<Factory<OneSequenceGestureRecognizer>>? gestureRecognizers}) {
    return RemoteAndroidWebView(
      creationParams: MethodChannelRemoteWebViewPlatform.creationParamsToMap(creationParams),
      webViewPlatformCallbacksHandler: webViewPlatformCallbacksHandler,
      onWebViewPlatformCreated: onWebViewPlatformCreated,
    );
  }
}

class RemoteAndroidWebView extends StatefulWidget{

  const RemoteAndroidWebView({
    Key? key,
    required this.creationParams,
    required this.webViewPlatformCallbacksHandler,
    this.onWebViewPlatformCreated}) : super(key: key);

  ///Creation params that for platform web-view
  final dynamic creationParams;

  ///Platform web-view's callback.
  ///e.g [WebViewPlatformCallbacksHandler.onPageStarted] , [WebViewPlatformCallbacksHandler.onPageFinished]
  final WebViewPlatformCallbacksHandler webViewPlatformCallbacksHandler;

  ///When view is ready , creation the private channel to associate web-widget
  /// and platform web-view.
  final WebViewPlatformCreatedCallback? onWebViewPlatformCreated;


  @override
  State<StatefulWidget> createState() {
    return RemoteAndroidWebViewState();
  }

}

class RemoteAndroidWebViewState extends State<RemoteAndroidWebView> {

  int? textureId;

  @override
  void initState() {
    super.initState();
    RemoteWebViewPlugin.produceWebView({'url':''}).then((value) {
      debugPrint('surface id  $value');
      setState(() {
        textureId = value;
      });
      if (widget.onWebViewPlatformCreated == null) {
        return;
      }
      widget.onWebViewPlatformCreated!(MethodChannelWebViewPlatform(
          value, widget.webViewPlatformCallbacksHandler));

    });
  }

  @override
  Widget build(BuildContext context) {
    return textureId == null
        ? Container(color: Colors.red,) : Texture(textureId: textureId!);
  }
}























