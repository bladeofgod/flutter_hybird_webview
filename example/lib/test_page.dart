import 'dart:math';

import 'package:flutter/material.dart';

/// 作者：李佳奇
/// 日期：2021/11/9
/// 备注：

class TestPage extends StatefulWidget{

  final String symbol;

  const TestPage({Key? key, required this.symbol}) : super(key: key);

  @override
  State<StatefulWidget> createState() {
    return TestPageState();
  }

}

class TestPageState extends State<TestPage> {
  @override
  Widget build(BuildContext context) {
    return Material(
      color: Colors.white,
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          ElevatedButton(onPressed: () {
            Navigator.of(context).push(MaterialPageRoute(builder: (_)=>TestPage(symbol: '123')));
          }, child: Text('${widget.symbol}- ${Random().nextInt(100)}', style: TextStyle(fontSize: 20, color: Colors.white),))

        ],
      ),
    );
  }
}















