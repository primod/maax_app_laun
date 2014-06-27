package org.apache.cordova;

import android.app.Activity;
import android.content.res.AssetManager;
import android.net.Uri;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import java.io.IOException;
import org.apache.cordova.api.CordovaInterface;
import org.apache.cordova.api.LOG;

public class IceCreamCordovaWebViewClient
  extends CordovaWebViewClient
{
  public IceCreamCordovaWebViewClient(CordovaInterface paramCordovaInterface)
  {
    super(paramCordovaInterface);
  }
  
  public IceCreamCordovaWebViewClient(CordovaInterface paramCordovaInterface, CordovaWebView paramCordovaWebView)
  {
    super(paramCordovaInterface, paramCordovaWebView);
  }
  
  private WebResourceResponse generateWebResourceResponse(String paramString)
  {
    if (paramString.startsWith("file:///android_asset/"))
    {
      String str1 = paramString.replaceFirst("file:///android_asset/", "");
      if (str1.contains("?")) {
        str1 = str1.split("\\?")[0];
      }
      for (;;)
      {
        boolean bool = str1.endsWith(".html");
        String str2 = null;
        if (bool) {
          str2 = "text/html";
        }
        try
        {
          WebResourceResponse localWebResourceResponse = new WebResourceResponse(str2, "UTF-8", this.cordova.getActivity().getAssets().open(Uri.parse(str1).getPath(), 2));
          return localWebResourceResponse;
        }
        catch (IOException localIOException)
        {
          LOG.e("generateWebResourceResponse", localIOException.getMessage(), localIOException);
        }
        if (str1.contains("#")) {
          str1 = str1.split("#")[0];
        }
      }
    }
    return null;
  }
  
  public WebResourceResponse shouldInterceptRequest(WebView paramWebView, String paramString)
  {
    if ((paramString.contains("?")) || (paramString.contains("#"))) {
      return generateWebResourceResponse(paramString);
    }
    return super.shouldInterceptRequest(paramWebView, paramString);
  }
}


/* Location:           C:\Users\Monitor\Videos\app\classes_dex2jar.jar
 * Qualified Name:     org.apache.cordova.IceCreamCordovaWebViewClient
 * JD-Core Version:    0.7.0.1
 */