package remote_webview.view;

import android.os.Build;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;

import androidx.annotation.BinderThread;
import androidx.annotation.RequiresApi;
import androidx.core.util.Pools;

import java.util.HashMap;
import java.util.Objects;

import remote_webview.garbage_collect.RemoteGarbageCollector;
import remote_webview.interfaces.IGarbageCleanListener;
import remote_webview.interfaces.IRemoteView;
import remote_webview.model.WebViewCreationParamsModel;
import remote_webview.utils.LogUtil;
import remote_webview.utils.HandlerUtil;
import remote_webview.view.factory.RemoteWebViewFactory;

/**
 * The {@link remote_webview.service.binders.RemoteViewFactoryBinder} processor.
 * receive the order from (main)binder and to dispatch to view.
 *
 * e.g create a real-view, dispatch event.
 *
 */

public class RemoteViewFactoryProcessor implements IGarbageCleanListener {

    private static final String TAG = "RemoteViewFactoryProcessor";

    private static volatile RemoteViewFactoryProcessor singleton;

    public static RemoteViewFactoryProcessor getInstance() {
        if(singleton == null) {
            synchronized (RemoteViewFactoryProcessor.class) {
                if(singleton == null) {
                    singleton = new RemoteViewFactoryProcessor();
                }
            }
        }
        return singleton;
    }

    private RemoteViewFactoryProcessor() {
        RemoteGarbageCollector.getInstance().registerCollectListener(this);
    }

    private final HashMap<Long, IRemoteView> viewCache = new HashMap<>();

    @BinderThread
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void createWithSurface(final WebViewCreationParamsModel creationParams, final Surface surface) {
        HandlerUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    final long surfaceId = creationParams.getSurfaceId();
                    RemoteWebViewFactory.RemoteViewModel presentation = RemoteWebViewFactory
                            .singleton
                            .generateWebViewPresentation(creationParams,surface);
                    viewCache.put(surfaceId, presentation);
                    ViewTrigger trigger = ViewTrigger.obtain(surfaceId, ViewTrigger.DO_INIT_VIEW);
                    ViewTrigger.trigger(trigger);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void dispatchTouchEvent(String surfaceId, MotionEvent event) {
        LogUtil.logMsg("view factory", " dispatchTouchEvent  id " + surfaceId
                + "  cache size :" + viewCache.size());
        try {
            Objects.requireNonNull(viewCache.get(Long.decode(surfaceId))).dispatchTouchEvent(event);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public void dispatchKeyEvent(String surfaceId, KeyEvent event) {
        LogUtil.logMsg("view factory", " dispatchKeyEvent  id " + surfaceId);
        try {
            Objects.requireNonNull(viewCache.get(Long.parseLong(surfaceId))).dispatchKeyEvent(event);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    @Override
    public void cleanGarbage(final long id) {
        //somehow clean notify will duplicate invoke
        if(viewCache.containsKey(id)) {
            ViewTrigger trigger = ViewTrigger.obtain(id, ViewTrigger.DO_RELEASE_VIEW);
            ViewTrigger.trigger(trigger);
            LogUtil.logMsg(this.toString(),"after cleanGarbage ,viewCache size: "
                    + viewCache.size());
        }

    }

    @Override
    public void cleanAll() {
        try {
            for(IRemoteView cache : viewCache.values()) {
                cache.release();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        viewCache.clear();
    }


    static class ViewTrigger implements Runnable{

        static final long NULL_LONG = -9999;

        static final int DO_INIT_VIEW = 8890;

        static final int DO_RELEASE_VIEW = 8891;

        private static Pools.SynchronizedPool<ViewTrigger> mViewTriggerPool = new Pools.SynchronizedPool<>(10);

        public static ViewTrigger obtain(long viewId, int signal) {
            ViewTrigger trigger = mViewTriggerPool.acquire();
            if(trigger == null) {
                trigger = new ViewTrigger();
            }
            trigger.signal = signal;
            trigger.viewId = viewId;
            return trigger;
        }

        public static void releaseTrigger(ViewTrigger trigger) {
            trigger.viewId = NULL_LONG;
            mViewTriggerPool.release(trigger);
        }

        public static void trigger(ViewTrigger trigger) {
            HandlerUtil.runOnUiThread(trigger);
        }

        ViewTrigger() {
        }

        int signal = -200;

        long viewId;

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void run() {
            try {
                switch (signal) {
                    case DO_INIT_VIEW:
                        Objects.requireNonNull(RemoteViewFactoryProcessor
                                .getInstance()
                                .viewCache.get(viewId)).init();
                        break;
                    case DO_RELEASE_VIEW:
                        Objects.requireNonNull(RemoteViewFactoryProcessor
                                .getInstance()
                                .viewCache.get(viewId)).release();
                        RemoteViewFactoryProcessor
                                .getInstance()
                                .viewCache.remove(viewId);
                        break;
                    default:
                        break;
                }
            }catch (Exception e) {
                //todo maybe need notify main-process
                e.printStackTrace();
            }
            
            releaseTrigger(this);
        }
    }




}









