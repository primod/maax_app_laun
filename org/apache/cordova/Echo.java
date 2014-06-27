package org.apache.cordova;

import java.util.concurrent.ExecutorService;
import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaInterface;
import org.apache.cordova.api.CordovaPlugin;
import org.json.JSONException;

public class Echo
  extends CordovaPlugin
{
  public boolean execute(String paramString, CordovaArgs paramCordovaArgs, final CallbackContext paramCallbackContext)
    throws JSONException
  {
    if ("echo".equals(paramString))
    {
      boolean bool2 = paramCordovaArgs.isNull(0);
      String str2 = null;
      if (bool2) {}
      for (;;)
      {
        paramCallbackContext.success(str2);
        return true;
        str2 = paramCordovaArgs.getString(0);
      }
    }
    if ("echoAsync".equals(paramString))
    {
      boolean bool1 = paramCordovaArgs.isNull(0);
      final String str1 = null;
      if (bool1) {}
      for (;;)
      {
        this.cordova.getThreadPool().execute(new Runnable()
        {
          public void run()
          {
            paramCallbackContext.success(str1);
          }
        });
        return true;
        str1 = paramCordovaArgs.getString(0);
      }
    }
    if ("echoArrayBuffer".equals(paramString))
    {
      paramCallbackContext.success(paramCordovaArgs.getArrayBuffer(0));
      return true;
    }
    return false;
  }
}


/* Location:           C:\Users\Monitor\Videos\app\classes_dex2jar.jar
 * Qualified Name:     org.apache.cordova.Echo
 * JD-Core Version:    0.7.0.1
 */