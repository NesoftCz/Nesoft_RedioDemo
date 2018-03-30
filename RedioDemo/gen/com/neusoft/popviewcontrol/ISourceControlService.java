/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: F:\\项目资料\\20170320\\src\\com\\neusoft\\popviewcontrol\\ISourceControlService.aidl
 */
package com.neusoft.popviewcontrol;
public interface ISourceControlService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.neusoft.popviewcontrol.ISourceControlService
{
private static final java.lang.String DESCRIPTOR = "com.neusoft.popviewcontrol.ISourceControlService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.neusoft.popviewcontrol.ISourceControlService interface,
 * generating a proxy if needed.
 */
public static com.neusoft.popviewcontrol.ISourceControlService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.neusoft.popviewcontrol.ISourceControlService))) {
return ((com.neusoft.popviewcontrol.ISourceControlService)iin);
}
return new com.neusoft.popviewcontrol.ISourceControlService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_setAppSourceMode:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
this.setAppSourceMode(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_getAppSourceMode:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.getAppSourceMode(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setStreamVolume:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
this.setStreamVolume(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
case TRANSACTION_setStreamCanBeMute:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _arg1;
_arg1 = (0!=data.readInt());
this.setStreamCanBeMute(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_setStreamMute:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _arg1;
_arg1 = (0!=data.readInt());
this.setStreamMute(_arg0, _arg1);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.neusoft.popviewcontrol.ISourceControlService
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
@Override public void setAppSourceMode(int streamType, int mode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(streamType);
_data.writeInt(mode);
mRemote.transact(Stub.TRANSACTION_setAppSourceMode, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public int getAppSourceMode(int streamType) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(streamType);
mRemote.transact(Stub.TRANSACTION_getAppSourceMode, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void setStreamVolume(int streamType, int index, int flags) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(streamType);
_data.writeInt(index);
_data.writeInt(flags);
mRemote.transact(Stub.TRANSACTION_setStreamVolume, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void setStreamCanBeMute(int streamType, boolean flag) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(streamType);
_data.writeInt(((flag)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setStreamCanBeMute, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void setStreamMute(int streamType, boolean flag) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(streamType);
_data.writeInt(((flag)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setStreamMute, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_setAppSourceMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_getAppSourceMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_setStreamVolume = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_setStreamCanBeMute = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_setStreamMute = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
}
public void setAppSourceMode(int streamType, int mode) throws android.os.RemoteException;
public int getAppSourceMode(int streamType) throws android.os.RemoteException;
public void setStreamVolume(int streamType, int index, int flags) throws android.os.RemoteException;
public void setStreamCanBeMute(int streamType, boolean flag) throws android.os.RemoteException;
public void setStreamMute(int streamType, boolean flag) throws android.os.RemoteException;
}
