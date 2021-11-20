package remote_webview.service.decoder;

import java.util.HashMap;

public class WebViewDecoder extends PackageDecoder {

    /**
     * When get a package(HashMap) from remote web-view, need to decode it as same as flutter-web.
     * @param methodName name of method
     * @param rawArgs args from remote, type is HashMap.
     * @return
     */
    @Override
    public Object decodeToFlutterResult(String methodName, HashMap rawArgs) {
        switch (methodName) {
            case "canGoBack":
                String canGoBack = (String) rawArgs.get("canGoBack");
                return Boolean.parseBoolean(canGoBack);
            case "canGoForward":
                String canGoForward = (String) rawArgs.get("canGoForward");
                return Boolean.parseBoolean(canGoForward);
            case "currentUrl":
                return (String) rawArgs.get("currentUrl");
            case "evaluateJavascript":
                return (String) rawArgs.get("evaluateJavascript");
            case "getTitle":
                return (String) rawArgs.get("getTitle");
            case "getScrollX":
                double x = 0.0;
                try {
                    x = Double.parseDouble((String)rawArgs.get("getScrollX"));
                }catch (Exception e) {
                    e.printStackTrace();
                }
                return x;
            case "getScrollY":
                double y = 0.0;
                try {
                    y = Double.parseDouble((String)rawArgs.get("getScrollY"));
                }catch (Exception e) {
                    e.printStackTrace();
                }
                return y;
            default:
                return null;
        }

    }

}
