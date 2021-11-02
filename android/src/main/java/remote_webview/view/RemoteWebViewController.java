package remote_webview.view;

import android.content.Context;
import android.os.Build;
import android.os.RemoteException;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.flutter.embedding.android.MotionEventTracker;
import io.flutter.embedding.engine.systemchannels.PlatformViewsChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.platform.PlatformViewsController;
import remote_webview.service.RemoteServicePresenter;
import remote_webview.utils.LogUtil;

/**
 * Control the remote view.
 * Design reference {@linkplain PlatformViewsController} and {@link PlatformViewsChannel}
 */

public class RemoteWebViewController{

    private FlutterViewAdapter adapter;
    private Context context;

    private final MotionEventTracker motionEventTracker = MotionEventTracker.getInstance();

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
        try {
            onTouch(touch);
        }catch (IllegalStateException e) {
            result.error("error",detailedExceptionString(e),null);
        }catch (RemoteException e) {
            result.error("error","occur an error on Dispatch touch event to remote !",null);
        }
    }

    private void onTouch(PlatformViewsChannel.PlatformViewTouch touch) throws RemoteException {
        int viewId = touch.viewId;
        float density = this.context.getResources().getDisplayMetrics().density;
        this.ensureValidAndroidVersion(20);
        MotionEvent event;
        if(WebViewSurfaceProducer.producer.checkViewExists(viewId)) {
            event = toMotionEvent(density, touch, true);
            RemoteServicePresenter.getInstance().getRemoteViewFactoryBinder()
                    .dispatchTouchEvent(""+viewId, event);
        } else {
            throw new IllegalStateException("Sending touch to an unknown view with id: " + viewId);
        }
    }



    public MotionEvent toMotionEvent(float density, PlatformViewsChannel.PlatformViewTouch touch, boolean usingVirtualDiplays) {
        MotionEventTracker.MotionEventId motionEventId = MotionEventTracker.MotionEventId.from(touch.motionEventId);
        MotionEvent trackedEvent = this.motionEventTracker.pop(motionEventId);
        MotionEvent.PointerProperties[] pointerProperties = (MotionEvent.PointerProperties[])parsePointerPropertiesList(touch.rawPointerPropertiesList).toArray(new MotionEvent.PointerProperties[touch.pointerCount]);
        MotionEvent.PointerCoords[] pointerCoords = (MotionEvent.PointerCoords[])parsePointerCoordsList(touch.rawPointerCoords, density).toArray(new MotionEvent.PointerCoords[touch.pointerCount]);
        return !usingVirtualDiplays && trackedEvent != null ? MotionEvent.obtain(trackedEvent.getDownTime(), trackedEvent.getEventTime(), trackedEvent.getAction(), touch.pointerCount, pointerProperties, pointerCoords, trackedEvent.getMetaState(), trackedEvent.getButtonState(), trackedEvent.getXPrecision(), trackedEvent.getYPrecision(), trackedEvent.getDeviceId(), trackedEvent.getEdgeFlags(), trackedEvent.getSource(), trackedEvent.getFlags()) : MotionEvent.obtain(touch.downTime.longValue(), touch.eventTime.longValue(), touch.action, touch.pointerCount, pointerProperties, pointerCoords, touch.metaState, touch.buttonState, touch.xPrecision, touch.yPrecision, touch.deviceId, touch.edgeFlags, touch.source, touch.flags);
    }

    private void ensureValidAndroidVersion(int minSdkVersion) {
        if (Build.VERSION.SDK_INT < minSdkVersion) {
            throw new IllegalStateException("Trying to use platform views with API " + Build.VERSION.SDK_INT + ", required API level is: " + minSdkVersion);
        }
    }


    private static List<MotionEvent.PointerCoords> parsePointerCoordsList(Object rawCoordsList, float density) {
        List<Object> rawCoords = (List)rawCoordsList;
        List<MotionEvent.PointerCoords> pointerCoords = new ArrayList();
        Iterator var4 = rawCoords.iterator();

        while(var4.hasNext()) {
            Object o = var4.next();
            pointerCoords.add(parsePointerCoords(o, density));
        }

        return pointerCoords;
    }

    private static MotionEvent.PointerCoords parsePointerCoords(Object rawCoords, float density) {
        List<Object> coordsList = (List)rawCoords;
        MotionEvent.PointerCoords coords = new MotionEvent.PointerCoords();
        coords.orientation = ((Double)coordsList.get(0)).floatValue();
        coords.pressure = ((Double)coordsList.get(1)).floatValue();
        coords.size = ((Double)coordsList.get(2)).floatValue();
        coords.toolMajor = ((Double)coordsList.get(3)).floatValue() * density;
        coords.toolMinor = ((Double)coordsList.get(4)).floatValue() * density;
        coords.touchMajor = ((Double)coordsList.get(5)).floatValue() * density;
        coords.touchMinor = ((Double)coordsList.get(6)).floatValue() * density;
        coords.x = ((Double)coordsList.get(7)).floatValue() * density;
        coords.y = ((Double)coordsList.get(8)).floatValue() * density;
        return coords;
    }




    /**
     * Get error string from exception that throws.
     * @param exception {@linkplain RemoteWebViewController} throws.
     * @return
     */
    private static String detailedExceptionString(Exception exception) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        exception.printStackTrace(printWriter);
        return stringWriter.toString();
    }

    private static List<MotionEvent.PointerProperties> parsePointerPropertiesList(Object rawPropertiesList) {
        List<Object> rawProperties = (List)rawPropertiesList;
        List<MotionEvent.PointerProperties> pointerProperties = new ArrayList();
        Iterator var3 = rawProperties.iterator();

        while(var3.hasNext()) {
            Object o = var3.next();
            pointerProperties.add(parsePointerProperties(o));
        }

        return pointerProperties;
    }

    private static MotionEvent.PointerProperties parsePointerProperties(Object rawProperties) {
        List<Object> propertiesList = (List)rawProperties;
        MotionEvent.PointerProperties properties = new MotionEvent.PointerProperties();
        properties.id = (Integer)propertiesList.get(0);
        properties.toolType = (Integer)propertiesList.get(1);
        return properties;
    }



    public static class FlutterViewAdapter{


        /**
         * Transfer flutter's touch event to platform event.
         * @param args flutter's touch event packet.
         * @return
         */
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
