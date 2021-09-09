/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\flutterplugin\\flutter_hybird_webview\\android\\src\\main\\aidl\\remote_webview\\IBinderPool.aidl
 */
package remote_webview;
// Declare any non-default types here with import statements

public interface IBinderPool extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements remote_webview.IBinderPool
{
private static final java.lang.String DESCRIPTOR = "remote_webview.IBinderPool";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an remote_webview.IBinderPool interface,
 * generating a proxy if needed.
 */
public static remote_webview.IBinderPool asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof remote_webview.IBinderPool))) {
return ((remote_webview.IBinderPool)iin);
}
return new remote_webview.IBinderPool.Stub.Proxy(obj);
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
case TRANSACTION_queryBinder:
{
data.enforceInterface(descriptor);
int _arg0;
_arg0 = data.readInt();
android.os.IBinder _result = this.queryBinder(_arg0);
reply.writeNoException();
reply.writeStrongBinder(_result);
return true;
}
default:
{
return super.onTransact(code, data, reply, flags);
}
}
}
private static class Proxy implements remote_webview.IBinderPool
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
@Override public android.os.IBinder queryBinder(int binderCode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.os.IBinder _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(binderCode);
mRemote.transact(Stub.TRANSACTION_queryBinder, _data, _reply, 0);
_reply.readException();
_result = _reply.readStrongBinder();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_queryBinder = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public android.os.IBinder queryBinder(int binderCode) throws android.os.RemoteException;
}
