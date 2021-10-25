package remote_webview.view;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import remote_webview.RemoteWebViewPlugin;
import remote_webview.service.RemoteServicePresenter;
import remote_webview.utils.LogUtil;

abstract public class RemoteWebViewActivity extends FlutterActivity {

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        flutterEngine.getPlugins().add(new RemoteWebViewPlugin(this));
    }

    //TODO for help information about the touch position on screen and deviate on web_view, see info below
    //    (controller as AndroidViewController).pointTransformer =
    //        (Offset position) => renderBox.globalToLocal(position);

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //todo test way
        final String id =WebViewSurfaceProducer.producer.surfaceModelCache.keySet().toArray()[0].toString();
        try {
            LogUtil.logMsg("remmote activity", "dispatchTouchEvent  " + id);
            RemoteServicePresenter.getInstance().getRemoteViewFactoryBinder().dispatchTouchEvent(id,ev);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return super.dispatchTouchEvent(ev);
    }

    //todo

//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        final String id =WebViewSurfaceProducer.producer.surfaceModelCache.keySet().toArray()[0].toString();
//        try {
//            LogUtil.logMsg("remmote activity", "dispatchKeyEvent  " + event.toString());
//            RemoteServicePresenter.getInstance().getRemoteViewFactoryBinder().dispatchKeyEvent(id,event);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//        return true;
//        //return super.dispatchKeyEvent(event);
//    }
}
