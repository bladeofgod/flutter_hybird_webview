/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\flutterplugin\\flutter_hybird_webview\\android\\src\\main\\aidl\\remote_webview\\IRemoteViewFactoryBinder.aidl
 */
package remote_webview;
public interface IRemoteViewFactoryBinder extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements remote_webview.IRemoteViewFactoryBinder
{
private static final java.lang.String DESCRIPTOR = "remote_webview.IRemoteViewFactoryBinder";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an remote_webview.IRemoteViewFactoryBinder interface,
 * generating a proxy if needed.
 */
public static remote_webview.IRemoteViewFactoryBinder asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof remote_webview.IRemoteViewFactoryBinder))) {
return ((remote_webview.IRemoteViewFactoryBinder)iin);
}
return new remote_webview.IRemoteViewFactoryBinder.Stub.Proxy(obj);
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
case TRANSACTION_createWithSurface:
{
data.enforceInterface(descriptor);
remote_webview.model.WebViewCreationParamsModel _arg0;
if ((0!=data.readInt())) {
_arg0 = remote_webview.model.WebViewCreationParamsModel.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
android.view.Surface _arg1;
if ((0!=data.readInt())) {
_arg1 = android.view.Surface.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
this.createWithSurface(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_dispatchTouchEvent:
{
data.enforceInterface(descriptor);
java.lang.String _arg0;
_arg0 = data.readString();
android.view.MotionEvent _arg1;
if ((0!=data.readInt())) {
_arg1 = android.view.MotionEvent.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
this.dispatchTouchEvent(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_dispatchKeyEvent:
{
data.enforceInterface(descriptor);
java.lang.String _arg0;
_arg0 = data.readString();
android.view.KeyEvent _arg1;
if ((0!=data.readInt())) {
_arg1 = android.view.KeyEvent.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
this.dispatchKeyEvent(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_dispose:
{
data.enforceInterface(descriptor);
long _arg0;
_arg0 = data.readLong();
this.dispose(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_disposeAll:
{
data.enforceInterface(descriptor);
this.disposeAll();
reply.writeNoException();
return true;
}
default:
{
return super.onTransact(code, data, reply, flags);
}
}
}
private static class Proxy implements remote_webview.IRemoteViewFactoryBinder
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
@Override public void createWithSurface(remote_webview.model.WebViewCreationParamsModel createParams, android.view.Surface surface) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((createParams!=null)) {
_data.writeInt(1);
createParams.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
if ((surface!=null)) {
_data.writeInt(1);
surface.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_createWithSurface, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void dispatchTouchEvent(java.lang.String surfaceId, android.view.MotionEvent event) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(surfaceId);
if ((event!=null)) {
_data.writeInt(1);
event.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_dispatchTouchEvent, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void dispatchKeyEvent(java.lang.String surfaceId, android.view.KeyEvent keyEvent) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(surfaceId);
if ((keyEvent!=null)) {
_data.writeInt(1);
keyEvent.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_dispatchKeyEvent, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void dispose(long viewId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(viewId);
mRemote.transact(Stub.TRANSACTION_dispose, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void disposeAll() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_disposeAll, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_createWithSurface = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_dispatchTouchEvent = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_dispatchKeyEvent = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_dispose = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_disposeAll = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
}
public void createWithSurface(remote_webview.model.WebViewCreationParamsModel createParams, android.view.Surface surface) throws android.os.RemoteException;
public void dispatchTouchEvent(java.lang.String surfaceId, android.view.MotionEvent event) throws android.os.RemoteException;
public void dispatchKeyEvent(java.lang.String surfaceId, android.view.KeyEvent keyEvent) throws android.os.RemoteException;
public void dispose(long viewId) throws android.os.RemoteException;
public void disposeAll() throws android.os.RemoteException;
}
