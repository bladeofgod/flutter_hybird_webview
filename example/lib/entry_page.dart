
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
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
    return Material(
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
    );
  }
}

















