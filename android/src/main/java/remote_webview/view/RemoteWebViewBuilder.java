package remote_webview.view;

import android.content.Context;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;



/** Builder used to create {@link android.webkit.WebView} objects. */
public class RemoteWebViewBuilder {

    /** Factory used to create a new {@link android.webkit.WebView} instance. */
    static class WebViewFactory {

        /**
         * Creates a new {@link android.webkit.WebView} instance.
         *
         */
        static WebView create(Context context) {
            return new WebView(context);
        }
    }

    private final Context context;

    private boolean enableDomStorage;
    private boolean javaScriptCanOpenWindowsAutomatically;
    private boolean supportMultipleWindows;
    private WebChromeClient webChromeClient;

    /**
     * Constructs a new {@link io.flutter.plugins.webviewflutter.WebViewBuilder} object with a custom
     * implementation of the {@link WebViewFactory} object.
     *
     * @param context an Activity Context to access application assets. This value cannot be null.
     */
    RemoteWebViewBuilder(@NonNull final Context context) {
        this.context = context;
    }

    /**
     * Sets whether the DOM storage API is enabled. The default value is {@code false}.
     *
     * @param flag {@code true} is {@link android.webkit.WebView} should use the DOM storage API.
     * @return This builder. This value cannot be {@code null}.
     */
    public RemoteWebViewBuilder setDomStorageEnabled(boolean flag) {
        this.enableDomStorage = flag;
        return this;
    }

    /**
     * Sets whether JavaScript is allowed to open windows automatically. This applies to the
     * JavaScript function {@code window.open()}. The default value is {@code false}.
     *
     * @param flag {@code true} if JavaScript is allowed to open windows automatically.
     * @return This builder. This value cannot be {@code null}.
     */
    public RemoteWebViewBuilder setJavaScriptCanOpenWindowsAutomatically(boolean flag) {
        this.javaScriptCanOpenWindowsAutomatically = flag;
        return this;
    }

    /**
     * Sets whether the {@link WebView} supports multiple windows. If set to {@code true}, {@link
     * WebChromeClient#onCreateWindow} must be implemented by the host application. The default is
     * {@code false}.
     *
     * @param flag {@code true} if multiple windows are supported.
     * @return This builder. This value cannot be {@code null}.
     */
    public RemoteWebViewBuilder setSupportMultipleWindows(boolean flag) {
        this.supportMultipleWindows = flag;
        return this;
    }

    /**
     * Sets the chrome handler. This is an implementation of WebChromeClient for use in handling
     * JavaScript dialogs, favicons, titles, and the progress. This will replace the current handler.
     *
     * @param webChromeClient an implementation of WebChromeClient This value may be null.
     * @return This builder. This value cannot be {@code null}.
     */
    public RemoteWebViewBuilder setWebChromeClient(@Nullable WebChromeClient webChromeClient) {
        this.webChromeClient = webChromeClient;
        return this;
    }

    /**
     * Build the {@link android.webkit.WebView} using the current settings.
     *
     * @return The {@link android.webkit.WebView} using the current settings.
     */
    public WebView build() {
        WebView webView = WebViewFactory.create(context);

        WebSettings webSettings = webView.getSettings();
        webSettings.setDomStorageEnabled(enableDomStorage);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(javaScriptCanOpenWindowsAutomatically);
        webSettings.setSupportMultipleWindows(supportMultipleWindows);
        webView.setWebChromeClient(webChromeClient);

        return webView;
    }
}