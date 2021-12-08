

import 'dart:typed_data';

import 'package:flutter/cupertino.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:webview_flutter/platform_interface.dart';
import 'package:webview_flutter/src/remote_webview/remote_texture.dart';
import 'package:webview_flutter/src/webview_android.dart';
import 'package:webview_flutter/webview_flutter.dart';

import 'method_channel_remote_webview_platform.dart';
import 'remote_webview_plugin.dart';

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
      gestureRecognizers: gestureRecognizers ?? const <Factory<OneSequenceGestureRecognizer>>{},
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
    required this.gestureRecognizers,
    this.onWebViewPlatformCreated}) :
        assert(creationParams is Map, "creationParams must be Map"),
        super(key: key);


  /// Which gestures should be consumed by the web view.
  ///
  /// It is possible for other gesture recognizers to be competing with the web view on pointer
  /// events, e.g if the web view is inside a [ListView] the [ListView] will want to handle
  /// vertical drags. The web view will claim gestures that are recognized by any of the
  /// recognizers on this list.
  ///
  /// When this set is empty or null, the web view will only handle pointer events for gestures that
  /// were not claimed by any other gesture recognizer.
  final Set<Factory<OneSequenceGestureRecognizer>> gestureRecognizers;

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

  TextureAndroidRemoteController? _remoteController;

  late WebDisposableContext _webDisposableContext;

  @override
  void initState() {
    super.initState();
    RemoteWebViewPlugin.getInstance();
    _webDisposableContext = WebDisposableContext(this);
    _createWebView();
    webViewRegister.registerContext(_webDisposableContext);
  }

  void _createWebView() {
    final Map<String, dynamic> args = <String, dynamic>{
      //AndroidViewController._getAndroidDirection(_layoutDirection),
      //TODO set default value, do it later or needed it.
      'direction': 'ltr',
    };
    // final ByteData paramsByteData =
    // widget.creationParamsCodec.encodeMessage(widget.creationParams)!;
    // args['params'] = Uint8List.view(
    //   paramsByteData.buffer,
    //   0,
    //   paramsByteData.lengthInBytes,
    // );
    Future.delayed(const Duration(milliseconds: 16*4), () {
      final Rect paintBounds = context.findRenderObject()!.paintBounds;
      final int physicalWidthP = (paintBounds.width * MediaQuery.of(context).devicePixelRatio).floor();
      final int physicalHeightP = (paintBounds.height * MediaQuery.of(context).devicePixelRatio).floor();

      RemoteWebViewPlugin.createWebView((widget.creationParams as Map<String,dynamic>)..addAll({
        'physicalWidth' : physicalWidthP,
        'physicalHeight' : physicalHeightP,
      })).then((value) {
        _webDisposableContext.setViewId(value);
        _remoteController = TextureAndroidRemoteController(textureId: value);
        setState(() {
          textureId = value;
        });
        if (widget.onWebViewPlatformCreated == null) {
          return;
        }
        widget.onWebViewPlatformCreated!(MethodChannelRemoteWebViewPlatform(
            value, widget.webViewPlatformCallbacksHandler));

      });
    });

  }

  @override
  void dispose() {
    _webDisposableContext.dispose();
    webViewRegister.unRegisterContext(_webDisposableContext);
    _remoteController?.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return textureId == null
        ? Center(child: CircularProgressIndicator(color: Colors.blue,),)
        : RemoteTexture(
          textureId: textureId!,
          controller: _remoteController!,
          gestureRecognizers: widget.gestureRecognizers,);
  }
}























