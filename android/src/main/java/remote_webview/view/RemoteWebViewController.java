package remote_webview.view;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Map;

import io.flutter.embedding.engine.systemchannels.PlatformViewsChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.platform.PlatformViewsController;

/**
 * Control the remote view.
 * Design reference {@linkplain PlatformViewsController} and {@link PlatformViewsChannel}
 */

public class RemoteWebViewController{

    private FlutterViewAdapter adapter;
    private Context context;

    public RemoteWebViewController(Context context) {
        this.context = context;
        adapter = new FlutterViewAdapter();
    }

    public void create(@NonNull MethodCall methodCall, @NonNull MethodChannel.Result result) {
        final Map<String, Object> params = (Map<String, Object>) methodCall.arguments;
        long surfaceId = WebViewSurfaceProducer.producer.buildGeneralWebViewSurface(params);
        result.success(surfaceId);
    }

    public void touch(@NonNull MethodCall methodCall, @NonNull MethodChannel.Result result) {
        List<Object> args = (List)methodCall.arguments();
        PlatformViewsChannel.PlatformViewTouch touch = adapter.translateTouchEvent(args);

    }

    private void onTouch(PlatformViewsChannel.PlatformViewTouch touch) {
        int viewId = touch.viewId;
        float density = this.context.getResources().getDisplayMetrics().density;
        this.ensureValidAndroidVersion(20);
    }

    private void ensureValidAndroidVersion(int minSdkVersion) {
        if (Build.VERSION.SDK_INT < minSdkVersion) {
            throw new IllegalStateException("Trying to use platform views with API " + Build.VERSION.SDK_INT + ", required API level is: " + minSdkVersion);
        }
    }

    public static class FlutterViewAdapter{
        public PlatformViewsChannel.PlatformViewTouch translateTouchEvent(List<Object> args) {
            return new PlatformViewsChannel.PlatformViewTouch(
                    (Integer)args.get(0), (Number)args.get(1), (Number)args.get(2),
                    (Integer)args.get(3), (Integer)args.get(4), args.get(5), args.get(6),
                    (Integer)args.get(7), (Integer)args.get(8), ((Double)args.get(9)).floatValue(),
                    ((Double)args.get(10)).floatValue(), (Integer)args.get(11), (Integer)args.get(12),
                    (Integer)args.get(13), (Integer)args.get(14), ((Number)args.get(15)).longValue());
        }
    }

}
