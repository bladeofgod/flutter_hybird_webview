// IRemoteMethodChannelBinder.aidl
package remote_webview;

// Declare any non-default types here with import statements
import remote_webview.model.MethodModel;

interface IRemoteMethodChannelBinder {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);
    void invokeMethod(in MethodModel model);
}