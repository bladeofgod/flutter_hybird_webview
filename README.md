# WebView for Flutter

This is based on [webview_flutter](https://pub.dev/packages/webview_flutter) development.

And expanded a remote-hybird-webview, for avoid the impact of webview on the main process
and decrease main-process's memory usage.

For now, it only supported on android.

## Branches Tip

stable      : can use at product.
main        : for development.
dev/feat    : child branch of main.

## Usage

Here just introduct remote usage, for common usage please check [webview_flutter](https://pub.dev/packages/webview_flutter). :)

### Using Remote-Hybrid WebView

For example:

    ```java
    class YourMainActivity extends RemoteWebActivity{
        ...
    }
    
    ```

    ```dart
    import 'dart:io';
    
    import 'package:webview_flutter/webview_flutter.dart';

    class WebViewExample extends StatefulWidget {
      @override
      WebViewExampleState createState() => WebViewExampleState();
    }
    
    class WebViewExampleState extends State<WebViewExample> {
      @override
      void initState() {
        super.initState();
            // Enable remote-hybrid web view.
    if (Platform.isAndroid) WebView.platform = TextureAndroidWebView();
      }

      @override
      Widget build(BuildContext context) {
        return WebView(
          initialUrl: 'https://flutter.dev',
        );
      }
    }
    ```
