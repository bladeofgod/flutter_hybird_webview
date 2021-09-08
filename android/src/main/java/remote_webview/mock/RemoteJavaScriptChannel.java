package remote_webview.mock;

import android.os.Handler;
import android.os.Looper;
import android.webkit.JavascriptInterface;

import java.util.HashMap;

public class RemoteJavaScriptChannel {

    private final MockMethodChannel methodChannel;
    private final String javaScriptChannelName;
    private final Handler platformThreadHandler;

    /**
     * @param methodChannel the Flutter WebView method channel to which JS messages are sent
     * @param javaScriptChannelName the name of the JavaScript channel, this is sent over the method
     *     channel with each message to let the Dart code know which JavaScript channel the message
     *     was sent through
     */
    public RemoteJavaScriptChannel(
            MockMethodChannel methodChannel, String javaScriptChannelName, Handler platformThreadHandler) {
        this.methodChannel = methodChannel;
        this.javaScriptChannelName = javaScriptChannelName;
        this.platformThreadHandler = platformThreadHandler;
    }

    // Suppressing unused warning as this is invoked from JavaScript.
    @SuppressWarnings("unused")
    @JavascriptInterface
    public void postMessage(final String message) {
        Runnable postMessageRunnable =
                new Runnable() {
                    @Override
                    public void run() {
                        HashMap<String, String> arguments = new HashMap<>();
                        arguments.put("channel", javaScriptChannelName);
                        arguments.put("message", message);
                        methodChannel.invokeMethod("javascriptChannelMessage", arguments);
                    }
                };
        if (platformThreadHandler.getLooper() == Looper.myLooper()) {
            postMessageRunnable.run();
        } else {
            platformThreadHandler.post(postMessageRunnable);
        }
    }
    
    
}
