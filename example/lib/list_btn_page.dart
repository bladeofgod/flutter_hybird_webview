import 'package:flutter/material.dart';
import 'package:webview_flutter/webview_flutter.dart';
import 'package:webview_flutter_example/entry_page.dart';
import 'package:webview_flutter_example/main.dart';

/// 作者：李佳奇
/// 日期：2021/12/6
/// 备注：

class ListBtnPage extends StatefulWidget{
  @override
  State<StatefulWidget> createState() {
    return ListBtnPageState();
  }

}

class ListBtnPageState extends State<ListBtnPage> {



  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('list btn page', style: TextStyle(color: Colors.black),),
      ),
      body: Container(
        width: double.infinity,
        color: Colors.lightBlueAccent,
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            buildBnt('open drawer', () {
              Scaffold.of(context).openDrawer();
            }),
            SizedBox(height: 30,),
            buildBnt('open zhihu web', () {
              Navigator.of(context).push(MaterialPageRoute(builder: (_)=>WebPage(url: zhihu)));
            }),
            SizedBox(height: 30,),
            buildBnt('open meituan web', () {
              Navigator.of(context).push(MaterialPageRoute(builder: (_)=>WebPage(url: zhihu)));
            }),
          ],
        ),
      ),
      drawer: Drawer(
        elevation: 16,
        child: Stack(
          children: [
            WebView(
              initialUrl: zhihu,
            ),
          ],
        ),
      ),

    );
  }

  Widget buildBnt(String title, VoidCallback onTap) {
    return ElevatedButton(
      onPressed: onTap,
      child: Text(title, style: TextStyle(color: Colors.black,
          fontSize: 16),),
    );
  }
}















