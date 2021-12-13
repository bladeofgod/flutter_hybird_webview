package remote_webview.view;

import android.app.AlertDialog;
import android.app.Presentation;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.CallSuper;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import io.flutter.Log;

import remote_webview.input_hook.InputMethodHolder;
import remote_webview.input_hook.OnInputMethodListener;
import remote_webview.input_hook.compat.IInputMethodManagerCompat;
import remote_webview.input_hook.hook.InputMethodManagerHook;
import remote_webview.utils.LogUtil;


public abstract class RemoteViewPresentation extends Presentation {
    
    private static final String TAG = "RemoteViewPresentation";

    private final RemoteAccessibilityEventsDelegate accessibilityEventsDelegate;
    protected long viewId;
    //not useful
    //private Object createParams;
    private RemoteViewPresentation.AccessibilityDelegatingFrameLayout rootView;
    private FrameLayout container;
    protected RemoteViewPresentation.PresentationState state;
    private boolean startFocused = false;
    private final Context outerContext;

    private final View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            LogUtil.logMsg(TAG," has focus : " + hasFocus);
            //todo send focus to flutter side
//            if(hasFocus) {
//                setInputConnectionTarget(state.childView);
//            }
        }
    };

    public RemoteViewPresentation(Context outerContext, Display display,  RemoteAccessibilityEventsDelegate accessibilityEventsDelegate, long viewId, boolean startFocused) {
        super(new ImmContext(outerContext), display);
        this.accessibilityEventsDelegate = accessibilityEventsDelegate;
        this.viewId = viewId;
        this.outerContext = outerContext;
        this.state = new PresentationState();
        this.getWindow()
                .setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        if (Build.VERSION.SDK_INT >= 19) {
            this.getWindow().setType(WindowManager.LayoutParams.TYPE_PRIVATE_PRESENTATION);
        }
        this.startFocused = startFocused;
    }

    public RemoteViewPresentation(Context outerContext, Display display, RemoteAccessibilityEventsDelegate accessibilityEventsDelegate, PresentationState state, boolean startFocused) {
        super(new ImmContext(outerContext), display);
        this.accessibilityEventsDelegate = accessibilityEventsDelegate;
        this.state = state;
        this.outerContext = outerContext;
        this.getWindow()
                .setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        this.startFocused = startFocused;
    }

    abstract protected void plugInHub();

    abstract protected void plugOutHub();

    @MainThread
    @CallSuper
    public void dispose() {
        detachState();
    }

    protected View getContainerView() {
        return container;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (this.state.fakeWindowViewGroup == null) {
            this.state.fakeWindowViewGroup = new FakeWindowViewGroup(this.getContext());
        }

        if (this.state.windowManagerHandler == null) {
            WindowManager windowManagerDelegate = (WindowManager)this.getContext().getSystemService(Context.WINDOW_SERVICE);
            this.state.windowManagerHandler = new WindowManagerHandler(windowManagerDelegate, this.state.fakeWindowViewGroup);
        }

        this.container = new FrameLayout(this.getContext());
        Context context = new PresentationContext(this.getContext(), this.state.windowManagerHandler, this.outerContext);
        if (this.state.childView == null) {
            this.state.childView = createChildView(container);
        }

        View embeddedView = this.state.childView;
        this.container.addView(embeddedView);
        this.rootView = new AccessibilityDelegatingFrameLayout(this.getContext(), this.accessibilityEventsDelegate, embeddedView);
        this.rootView.addView(this.container);
        this.rootView.addView(this.state.fakeWindowViewGroup);
        embeddedView.setOnFocusChangeListener(this.focusChangeListener);
        this.rootView.setFocusableInTouchMode(true);
        if (this.startFocused) {
            embeddedView.requestFocus();
        } else {
            this.rootView.requestFocus();
        }

        this.setContentView(this.rootView);
    }

    public PresentationState detachState() {
        this.container.removeAllViews();
        this.rootView.removeAllViews();
        return this.state;
    }

    public View getView() {
        return this.state.childView == null ? null : this.state.childView;
    }

    abstract public View createChildView(View containerView);

    private static class AccessibilityDelegatingFrameLayout extends FrameLayout {
        private final RemoteAccessibilityEventsDelegate accessibilityEventsDelegate;
        private final View embeddedView;

        public AccessibilityDelegatingFrameLayout(Context context, RemoteAccessibilityEventsDelegate accessibilityEventsDelegate, View embeddedView) {
            super(context);
            this.accessibilityEventsDelegate = accessibilityEventsDelegate;
            this.embeddedView = embeddedView;
        }

        public boolean requestSendAccessibilityEvent(View child, AccessibilityEvent event) {
            return this.accessibilityEventsDelegate.requestSendAccessibilityEvent(this.embeddedView, child, event);
        }
    }


    static class WindowManagerHandler implements InvocationHandler {
        private static final String TAG = "PlatformViewsController";
        private final WindowManager delegate;
        RemoteViewPresentation.FakeWindowViewGroup fakeWindowRootView;

        WindowManagerHandler(WindowManager delegate, RemoteViewPresentation.FakeWindowViewGroup fakeWindowViewGroup) {
            this.delegate = delegate;
            this.fakeWindowRootView = fakeWindowViewGroup;
        }

        public WindowManager getWindowManager() {
            return (WindowManager) Proxy.newProxyInstance(WindowManager.class.getClassLoader(), new Class[]{WindowManager.class}, this);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            switch (method.getName()) {
                case "addView":
                    addView(args);
                    return null;
                case "removeView":
                    removeView(args);
                    return null;
                case "removeViewImmediate":
                    removeViewImmediate(args);
                    return null;
                case "updateViewLayout":
                    updateViewLayout(args);
                    return null;
            }
            try {
                return method.invoke(delegate, args);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }

        private void addView(Object[] args) {
            if (this.fakeWindowRootView == null) {
                Log.w("PlatformViewsController", "Embedded view called addView while detached from presentation");
            } else {
                View view = (View)args[0];
                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams)args[1];
                this.fakeWindowRootView.addView(view, layoutParams);
            }
        }

        private void removeView(Object[] args) {
            if (this.fakeWindowRootView == null) {
                Log.w("PlatformViewsController", "Embedded view called removeView while detached from presentation");
            } else {
                View view = (View)args[0];
                this.fakeWindowRootView.removeView(view);
            }
        }

        private void removeViewImmediate(Object[] args) {
            if (this.fakeWindowRootView == null) {
                Log.w("PlatformViewsController", "Embedded view called removeViewImmediate while detached from presentation");
            } else {
                View view = (View)args[0];
                view.clearAnimation();
                this.fakeWindowRootView.removeView(view);
            }
        }

        private void updateViewLayout(Object[] args) {
            if (this.fakeWindowRootView == null) {
                Log.w("PlatformViewsController", "Embedded view called updateViewLayout while detached from presentation");
            } else {
                View view = (View)args[0];
                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams)args[1];
                this.fakeWindowRootView.updateViewLayout(view, layoutParams);
            }
        }
    }

    private static class PresentationContext extends ContextWrapper {
        @NonNull
        private final RemoteViewPresentation.WindowManagerHandler windowManagerHandler;
        @Nullable
        private WindowManager windowManager;
        private final Context flutterAppWindowContext;

        PresentationContext(Context base, @NonNull RemoteViewPresentation.WindowManagerHandler windowManagerHandler, Context flutterAppWindowContext) {
            super(base);
            this.windowManagerHandler = windowManagerHandler;
            this.flutterAppWindowContext = flutterAppWindowContext;
        }

        public Object getSystemService(String name) {
            if ("window".equals(name)) {
                return this.isCalledFromAlertDialog() ? this.flutterAppWindowContext.getSystemService(name) : this.getWindowManager();
            } else {
                return super.getSystemService(name);
            }
        }

        private WindowManager getWindowManager() {
            if (this.windowManager == null) {
                this.windowManager = this.windowManagerHandler.getWindowManager();
            }

            return this.windowManager;
        }

        private boolean isCalledFromAlertDialog() {
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

            for(int i = 0; i < stackTraceElements.length && i < 11; ++i) {
                if (stackTraceElements[i].getClassName().equals(AlertDialog.class.getCanonicalName()) && stackTraceElements[i].getMethodName().equals("<init>")) {
                    return true;
                }
            }

            return false;
        }
    }

    static class InputMethodManagerHandler implements InvocationHandler{
        private static final String TAG = "InputMethodManagerHandler";

        private final Context context;
        private final Object originObj;

        InputMethodManagerHandler(Context context, InputMethodManager delegate) {
            this.context = context;
            originObj = delegate;
        }

        /**
         * Make a proxy, to watch soft input status.
         *
         * @see InputMethodManager
         * @see IInputMethodManager
         * @see InputMethodManager#createRealInstance
         */
        InputMethodManager getIMM() {
            //todo remove imm-context's input method holder to here, and return this.hook.getProxyInputMethodInterface()
            return (InputMethodManager) Proxy.newProxyInstance(context.getClassLoader(),
                    new Class[]{originObj.getClass()}, this);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            LogUtil.logMsg(TAG, "invoke call  ", method.getName());
            return method.invoke(originObj, args);
        }
    }

    private static class ImmContext extends ContextWrapper {
        @NonNull
        private final Object inputMethodManager;

        //private final InputMethodManagerHandler proxy;
        InputMethodManagerHook hook;

        ImmContext(Context base) {
            this(base, (InputMethodManager)null);
        }

        private ImmContext(final Context base, @Nullable Object inputMethodManager) {
            super(base);

            //way 1
            //proxy = new InputMethodManagerHandler(this, inputMethodManager);
            //way 2
            InputMethodHolder.init(this);
            InputMethodHolder.registerListener(new OnInputMethodListener() {
                @Override
                public void onShow(boolean result) {
                    Toast.makeText(base, "on show", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onHide(boolean result) {
                    Toast.makeText(base, "on hide", Toast.LENGTH_SHORT).show();
                }
            });
            hook = InputMethodHolder.inputMethodManagerHook;

            this.inputMethodManager = inputMethodManager != null ?
                    inputMethodManager
                    : base.getSystemService(Context.INPUT_METHOD_SERVICE);
            Toast.makeText(base, "on ImmContext", Toast.LENGTH_SHORT).show();
        }

        public Object getSystemService(String name) {
            LogUtil.logMsg(TAG, "get system service from imm context");
            return "input_method".equals(name) ? this.hook.getProxyInputMethodInterface(): super.getSystemService(name);
        }

        public Context createDisplayContext(Display display) {
            Context displayContext = super.createDisplayContext(display);
            return new RemoteViewPresentation.ImmContext(displayContext, this.hook.getProxyInputMethodInterface());
        }
    }

    static class FakeWindowViewGroup extends ViewGroup {
        private final Rect viewBounds = new Rect();
        private final Rect childRect = new Rect();

        public FakeWindowViewGroup(Context context) {
            super(context);
        }

        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            for(int i = 0; i < this.getChildCount(); ++i) {
                View child = this.getChildAt(i);
                WindowManager.LayoutParams params = (WindowManager.LayoutParams)child.getLayoutParams();
                this.viewBounds.set(l, t, r, b);
                Gravity.apply(params.gravity, child.getMeasuredWidth(), child.getMeasuredHeight(), this.viewBounds, params.x, params.y, this.childRect);
                child.layout(this.childRect.left, this.childRect.top, this.childRect.right, this.childRect.bottom);
            }

        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            for(int i = 0; i < this.getChildCount(); ++i) {
                View child = this.getChildAt(i);
                child.measure(atMost(widthMeasureSpec), atMost(heightMeasureSpec));
            }

            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        private static int atMost(int measureSpec) {
            return MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(measureSpec), MeasureSpec.AT_MOST);
        }
    }


    static class PresentationState {
        protected View childView;
        private RemoteViewPresentation.WindowManagerHandler windowManagerHandler;
        private RemoteViewPresentation.FakeWindowViewGroup fakeWindowViewGroup;

        PresentationState() {
        }
    }

}
