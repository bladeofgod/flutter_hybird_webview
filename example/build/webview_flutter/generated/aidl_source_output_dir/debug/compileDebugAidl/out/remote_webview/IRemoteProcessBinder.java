/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: Z:\\flutter_projects\\flutter_hybird_webview\\android\\src\\main\\aidl\\remote_webview\\IRemoteProcessBinder.aidl
 */
package remote_webview;
// Declare any non-default types here with import statements

public interface IRemoteProcessBinder extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements remote_webview.IRemoteProcessBinder
{
private static final java.lang.String DESCRIPTOR = "remote_webview.IRemoteProcessBinder";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an remote_webview.IRemoteProcessBinder interface,
 * generating a proxy if needed.
 */
public static remote_webview.IRemoteProcessBinder asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof remote_webview.IRemoteProcessBinder))) {
return ((remote_webview.IRemoteProcessBinder)iin);
}
return new remote_webview.IRemoteProcessBinder.Stub.Proxy(obj);
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
case TRANSACTION_initZygoteActivity:
{
data.enforceInterface(descriptor);
this.initZygoteActivity();
reply.writeNoException();
return true;
}
case TRANSACTION_isZygoteActivityAlive:
{
data.enforceInterface(descriptor);
int _result = this.isZygoteActivityAlive();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
default:
{
return super.onTransact(code, data, reply, flags);
}
}
}
private static class Proxy implements remote_webview.IRemoteProcessBinder
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
@Override public void initZygoteActivity() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_initZygoteActivity, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public int isZygoteActivityAlive() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isZygoteActivityAlive, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_initZygoteActivity = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_isZygoteActivityAlive = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
public void initZygoteActivity() throws android.os.RemoteException;
public int isZygoteActivityAlive() throws android.os.RemoteException;
}
