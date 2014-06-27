package org.apache.cordova.api;

import android.app.Activity;
import android.content.Intent;
import java.util.concurrent.ExecutorService;

public abstract interface CordovaInterface
{
  public abstract Activity getActivity();
  
  public abstract ExecutorService getThreadPool();
  
  public abstract Object onMessage(String paramString, Object paramObject);
  
  public abstract void setActivityResultCallback(CordovaPlugin paramCordovaPlugin);
  
  public abstract void startActivityForResult(CordovaPlugin paramCordovaPlugin, Intent paramIntent, int paramInt);
}


/* Location:           C:\Users\Monitor\Videos\app\classes_dex2jar.jar
 * Qualified Name:     org.apache.cordova.api.CordovaInterface
 * JD-Core Version:    0.7.0.1
 */