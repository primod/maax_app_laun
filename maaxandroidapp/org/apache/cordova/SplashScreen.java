package org.apache.cordova;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.json.JSONArray;

public class SplashScreen
  extends CordovaPlugin
{
  public boolean execute(String paramString, JSONArray paramJSONArray, CallbackContext paramCallbackContext)
  {
    if (paramString.equals("hide")) {
      this.webView.postMessage("splashscreen", "hide");
    }
    for (;;)
    {
      paramCallbackContext.success();
      return true;
      if (!paramString.equals("show")) {
        break;
      }
      this.webView.postMessage("splashscreen", "show");
    }
    return false;
  }
}


/* Location:           C:\Users\Monitor\Videos\app\classes_dex2jar.jar
 * Qualified Name:     org.apache.cordova.SplashScreen
 * JD-Core Version:    0.7.0.1
 */