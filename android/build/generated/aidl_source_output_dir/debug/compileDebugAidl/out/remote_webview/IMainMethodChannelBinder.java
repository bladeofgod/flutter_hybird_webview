/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\flutterplugin\\flutter_hybird_webview\\android\\src\\main\\aidl\\remote_webview\\IMainMethodChannelBinder.aidl
 */
package remote_webview;
public interface IMainMethodChannelBinder extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements remote_webview.IMainMethodChannelBinder
{
private static final java.lang.String DESCRIPTOR = "remote_webview.IMainMethodChannelBinder";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an remote_webview.IMainMethodChannelBinder interface,
 * generating a proxy if needed.
 */
public static remote_webview.IMainMethodChannelBinder asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof remote_webview.IMainMethodChannelBinder))) {
return ((remote_webview.IMainMethodChannelBinder)iin);
}
return new remote_webview.IMainMethodChannelBinder.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
java.lang.String descriptor = DESCRIPTOR;
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(descriptor);
return true;
}
case TRANSACTION_invokeMethod:
{
data.enforceInterface(descriptor);
remote_webview.model.MethodModel _arg0;
if ((0!=data.readInt())) {
_arg0 = remote_webview.model.MethodModel.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
java.lang.String _result = this.invokeMethod(_arg0);
reply.writeNoException();
reply.writeString(_result);
return true;
}
default:
{
return super.onTransact(code, data, reply, flags);
}
}
}
private static class Proxy implements remote_webview.IMainMethodChannelBinder
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public java.lang.String invokeMethod(remote_webview.model.MethodModel model) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((model!=null)) {
_data.writeInt(1);
model.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_invokeMethod, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_invokeMethod = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public java.lang.String invokeMethod(remote_webview.model.MethodModel model) throws android.os.RemoteException;
}
