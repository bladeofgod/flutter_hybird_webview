
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:webview_flutter/webview_flutter.dart';
import 'package:webview_flutter_example/main.dart';

/// entry page


class EntryPage extends StatefulWidget{
  @override
  State<StatefulWidget> createState() {
    return EntryPageState();
  }
}

class EntryPageState extends State<EntryPage> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Material(
        color: Colors.white,
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            ElevatedButton(
              onPressed: () {
                Navigator.of(context).push(MaterialPageRoute(builder: (_) => WebViewExample()));
              },
              child: Text('to jd', style: TextStyle(color: Colors.black,
                  fontSize: 16),),
            ),

          ],
        ),
      ),
    );
  }
}

class WebPage extends StatefulWidget{

  final String url;

  const WebPage({Key? key, required this.url}) : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return WebPageState();
  }

}

class WebPageState extends State<WebPage> {

  WebViewController? webViewController;

  @override
  Widget build(BuildContext context) {
    return WillPopScope(child: Material(
      child: Container(
        color: Colors.greenAccent,
        child: WebView(
          onWebViewCreated: (controller) {
            webViewController = controller;
          },
          initialUrl: widget.url,
        ),
      ),
    ), onWillPop: () async {
      bool canBack = await webViewController?.canGoBack() ?? false;
      debugPrint('web view  canBack : $canBack');
      if(canBack) {
        webViewController?.goBack();
      }
      return Future.value(!canBack);
    });
  }
}

















