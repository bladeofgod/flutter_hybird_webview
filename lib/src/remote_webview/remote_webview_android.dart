

import 'dart:typed_data';

import 'package:flutter/foundation.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:webview_flutter/platform_interface.dart';
import 'package:webview_flutter/src/webview_android.dart';
import 'package:webview_flutter/webview_flutter.dart';

import '../method_channel_remote_webview_platform.dart';
import 'remote_webview_plugin.dart';
import '../webview_method_channel.dart';

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
      creationParamsCodec: const StandardMessageCodec(),
    );
  }
}

class RemoteAndroidWebView extends StatefulWidget{

  const RemoteAndroidWebView({
    Key? key,
    required this.creationParams,
    required this.webViewPlatformCallbacksHandler,
    required this.creationParamsCodec,
    this.onWebViewPlatformCreated}) : super(key: key);

  ///Creation params that for platform web-view
  final dynamic creationParams;

  ///Platform web-view's callback.
  ///e.g [WebViewPlatformCallbacksHandler.onPageStarted] , [WebViewPlatformCallbacksHandler.onPageFinished]
  final WebViewPlatformCallbacksHandler webViewPlatformCallbacksHandler;

  ///When view is ready , creation the private channel to associate web-widget
  /// and platform web-view.
  final WebViewPlatformCreatedCallback? onWebViewPlatformCreated;

  /// The codec used to encode `creationParams` before sending it to the
  /// platform side. It should match the codec passed to the constructor of [PlatformViewFactory](/javadoc/io/flutter/plugin/platform/PlatformViewFactory.html#PlatformViewFactory-io.flutter.plugin.common.MessageCodec-).
  ///
  /// This is typically one of: [StandardMessageCodec], [JSONMessageCodec], [StringCodec], or [BinaryCodec].
  ///
  /// This must not be null if [creationParams] is not null.
  final MessageCodec<dynamic> creationParamsCodec;


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
    _createWebView();
  }

  void _createWebView() {
    final Map<String, dynamic> args = <String, dynamic>{
      //AndroidViewController._getAndroidDirection(_layoutDirection),
      //TODO set default value, do it later or needed it.
      'direction': 'ltr',
    };
    final ByteData paramsByteData =
    widget.creationParamsCodec.encodeMessage(widget.creationParamsCodec)!;
    args['params'] = Uint8List.view(
      paramsByteData.buffer,
      0,
      paramsByteData.lengthInBytes,
    );
    RemoteWebViewPlugin.createWebView(args).then((value) {
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
  void dispose() {
    if(textureId != null) {
      RemoteWebViewPlugin.dispose(textureId!);
    }
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return textureId == null
        ? Container(color: Colors.red,) : Texture(textureId: textureId!);
  }
}























