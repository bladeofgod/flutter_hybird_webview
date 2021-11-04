package remote_webview.view;

import android.app.AlertDialog;
import android.app.Presentation;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager.LayoutParams;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import io.flutter.Log;

import io.flutter.plugin.platform.PlatformView;



public abstract class RemoteViewPresentation extends Presentation {

    private final RemoteAccessibilityEventsDelegate accessibilityEventsDelegate;
    private final View.OnFocusChangeListener focusChangeListener;
    protected long viewId;
    //not useful
    //private Object createParams;
    private RemoteViewPresentation.AccessibilityDelegatingFrameLayout rootView;
    private FrameLayout container;
    protected RemoteViewPresentation.PresentationState state;
    private boolean startFocused = false;
    private final Context outerContext;

    public RemoteViewPresentation(Context outerContext, Display display,  RemoteAccessibilityEventsDelegate accessibilityEventsDelegate, long viewId, View.OnFocusChangeListener focusChangeListener) {
        super(new ImmContext(outerContext), display);
        this.accessibilityEventsDelegate = accessibilityEventsDelegate;
        this.viewId = viewId;
        this.focusChangeListener = focusChangeListener;
        this.outerContext = outerContext;
        this.state = new PresentationState();
        this.getWindow().setFlags(8, 8);
        if (Build.VERSION.SDK_INT >= 19) {
            this.getWindow().setType(2030);
        }
    }

    public RemoteViewPresentation(Context outerContext, Display display, RemoteAccessibilityEventsDelegate accessibilityEventsDelegate, PresentationState state, View.OnFocusChangeListener focusChangeListener, boolean startFocused) {
        super(new ImmContext(outerContext), display);
        this.accessibilityEventsDelegate = accessibilityEventsDelegate;
        this.state = state;
        this.focusChangeListener = focusChangeListener;
        this.outerContext = outerContext;
        this.getWindow().setFlags(8, 8);
        this.startFocused = startFocused;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(0));
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
            this.state.childView = createChildView();
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

    abstract public View createChildView();

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

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String var4 = method.getName();
            byte var5 = -1;
            switch(var4.hashCode()) {
                case -1148522778:
                    if (var4.equals("addView")) {
                        var5 = 0;
                    }
                    break;
                case 542766184:
                    if (var4.equals("removeViewImmediate")) {
                        var5 = 2;
                    }
                    break;
                case 931413976:
                    if (var4.equals("updateViewLayout")) {
                        var5 = 3;
                    }
                    break;
                case 1098630473:
                    if (var4.equals("removeView")) {
                        var5 = 1;
                    }
            }

            switch(var5) {
                case 0:
                    this.addView(args);
                    return null;
                case 1:
                    this.removeView(args);
                    return null;
                case 2:
                    this.removeViewImmediate(args);
                    return null;
                case 3:
                    this.updateViewLayout(args);
                    return null;
                default:
                    try {
                        return method.invoke(this.delegate, args);
                    } catch (InvocationTargetException var6) {
                        throw var6.getCause();
                    }
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

    private static class ImmContext extends ContextWrapper {
        @NonNull
        private final InputMethodManager inputMethodManager;

        ImmContext(Context base) {
            this(base, (InputMethodManager)null);
        }

        private ImmContext(Context base, @Nullable InputMethodManager inputMethodManager) {
            super(base);
            this.inputMethodManager = inputMethodManager != null ?
                    inputMethodManager
                    : (InputMethodManager)base.getSystemService(Context.INPUT_METHOD_SERVICE);
        }

        public Object getSystemService(String name) {
            return "input_method".equals(name) ? this.inputMethodManager : super.getSystemService(name);
        }

        public Context createDisplayContext(Display display) {
            Context displayContext = super.createDisplayContext(display);
            return new RemoteViewPresentation.ImmContext(displayContext, this.inputMethodManager);
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
