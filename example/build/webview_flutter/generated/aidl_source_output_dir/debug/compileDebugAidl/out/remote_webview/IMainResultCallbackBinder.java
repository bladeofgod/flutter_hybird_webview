/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: Z:\\flutter_projects\\flutter_hybird_webview\\android\\src\\main\\aidl\\remote_webview\\IMainResultCallbackBinder.aidl
 */
package remote_webview;
// Declare any non-default types here with import statements

public interface IMainResultCallbackBinder extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements remote_webview.IMainResultCallbackBinder
{
private static final java.lang.String DESCRIPTOR = "remote_webview.IMainResultCallbackBinder";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an remote_webview.IMainResultCallbackBinder interface,
 * generating a proxy if needed.
 */
public static remote_webview.IMainResultCallbackBinder asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof remote_webview.IMainResultCallbackBinder))) {
return ((remote_webview.IMainResultCallbackBinder)iin);
}
return new remote_webview.IMainResultCallbackBinder.Stub.Proxy(obj);
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
case TRANSACTION_remoteSuccess:
{
data.enforceInterface(descriptor);
long _arg0;
_arg0 = data.readLong();
java.lang.String _arg1;
_arg1 = data.readString();
this.remoteSuccess(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_remoteError:
{
data.enforceInterface(descriptor);
long _arg0;
_arg0 = data.readLong();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
java.lang.String _arg3;
_arg3 = data.readString();
this.remoteError(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
case TRANSACTION_remoteNotImplemented:
{
data.enforceInterface(descriptor);
long _arg0;
_arg0 = data.readLong();
this.remoteNotImplemented(_arg0);
reply.writeNoException();
return true;
}
default:
{
return super.onTransact(code, data, reply, flags);
}
}
}
private static class Proxy implements remote_webview.IMainResultCallbackBinder
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
@Override public void remoteSuccess(long id, java.lang.String result) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(id);
_data.writeString(result);
mRemote.transact(Stub.TRANSACTION_remoteSuccess, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void remoteError(long id, java.lang.String var1, java.lang.String var2, java.lang.String info) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(id);
_data.writeString(var1);
_data.writeString(var2);
_data.writeString(info);
mRemote.transact(Stub.TRANSACTION_remoteError, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void remoteNotImplemented(long id) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(id);
mRemote.transact(Stub.TRANSACTION_remoteNotImplemented, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_remoteSuccess = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_remoteError = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_remoteNotImplemented = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
}
public void remoteSuccess(long id, java.lang.String result) throws android.os.RemoteException;
public void remoteError(long id, java.lang.String var1, java.lang.String var2, java.lang.String info) throws android.os.RemoteException;
public void remoteNotImplemented(long id) throws android.os.RemoteException;
}
