/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package com.lijiaqi.remote_webview;
public interface IMainMethodChannelBinder extends android.os.IInterface
{
  /** Default implementation for IMainMethodChannelBinder. */
  public static class Default implements com.lijiaqi.remote_webview.IMainMethodChannelBinder
  {
    /**
         * Demonstrates some basic types that you can use as parameters
         * and return values in AIDL.
         *///    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
    //            double aDouble, String aString);

    @Override public void invokeMethod(com.lijiaqi.remote_webview.model.MethodModel model) throws android.os.RemoteException
    {
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements com.lijiaqi.remote_webview.IMainMethodChannelBinder
  {
    private static final java.lang.String DESCRIPTOR = "com.lijiaqi.remote_webview.IMainMethodChannelBinder";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an com.lijiaqi.remote_webview.IMainMethodChannelBinder interface,
     * generating a proxy if needed.
     */
    public static com.lijiaqi.remote_webview.IMainMethodChannelBinder asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof com.lijiaqi.remote_webview.IMainMethodChannelBinder))) {
        return ((com.lijiaqi.remote_webview.IMainMethodChannelBinder)iin);
      }
      return new com.lijiaqi.remote_webview.IMainMethodChannelBinder.Stub.Proxy(obj);
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
          com.lijiaqi.remote_webview.model.MethodModel _arg0;
          if ((0!=data.readInt())) {
            _arg0 = com.lijiaqi.remote_webview.model.MethodModel.CREATOR.createFromParcel(data);
          }
          else {
            _arg0 = null;
          }
          this.invokeMethod(_arg0);
          reply.writeNoException();
          return true;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements com.lijiaqi.remote_webview.IMainMethodChannelBinder
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
      /**
           * Demonstrates some basic types that you can use as parameters
           * and return values in AIDL.
           *///    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
      //            double aDouble, String aString);

      @Override public void invokeMethod(com.lijiaqi.remote_webview.model.MethodModel model) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          if ((model!=null)) {
            _data.writeInt(1);
            model.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_invokeMethod, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().invokeMethod(model);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      public static com.lijiaqi.remote_webview.IMainMethodChannelBinder sDefaultImpl;
    }
    static final int TRANSACTION_invokeMethod = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    public static boolean setDefaultImpl(com.lijiaqi.remote_webview.IMainMethodChannelBinder impl) {
      // Only one user of this interface can use this function
      // at a time. This is a heuristic to detect if two different
      // users in the same process use this function.
      if (Stub.Proxy.sDefaultImpl != null) {
        throw new IllegalStateException("setDefaultImpl() called twice");
      }
      if (impl != null) {
        Stub.Proxy.sDefaultImpl = impl;
        return true;
      }
      return false;
    }
    public static com.lijiaqi.remote_webview.IMainMethodChannelBinder getDefaultImpl() {
      return Stub.Proxy.sDefaultImpl;
    }
  }
  /**
       * Demonstrates some basic types that you can use as parameters
       * and return values in AIDL.
       *///    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
  //            double aDouble, String aString);

  public void invokeMethod(com.lijiaqi.remote_webview.model.MethodModel model) throws android.os.RemoteException;
}
