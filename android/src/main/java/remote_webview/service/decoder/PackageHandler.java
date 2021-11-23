package remote_webview.service.decoder;

import java.util.HashMap;

abstract public class PackageHandler {
    /**
     * Decode a package that from remote-process, to a flutter-result.
     * @param methodName called method name.
     * @param rawArgs raw result from remote-process.
     * @return flutter-result
     */
    abstract public Object decodeToFlutterResult(String methodName, HashMap rawArgs);


    /**
     * Mark a method name to a package.
     * @param methodName relate method.
     * @return result package with relate method-name.
     */
    abstract public HashMap markPackageWithMethodName(String methodName, HashMap rawArgs);

}
