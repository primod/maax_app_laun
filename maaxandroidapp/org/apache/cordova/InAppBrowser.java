package org.apache.cordova;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage.QuotaUpdater;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import java.io.File;
import java.util.HashMap;
import java.util.StringTokenizer;
import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaInterface;
import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.LOG;
import org.apache.cordova.api.PluginResult;
import org.apache.cordova.api.PluginResult.Status;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint({"SetJavaScriptEnabled"})
public class InAppBrowser
  extends CordovaPlugin
{
  private static final String EXIT_EVENT = "exit";
  private static final String LOAD_START_EVENT = "loadstart";
  private static final String LOAD_STOP_EVENT = "loadstop";
  private static final String LOCATION = "location";
  protected static final String LOG_TAG = "InAppBrowser";
  private static final String NULL = "null";
  private static final String SELF = "_self";
  private static final String SYSTEM = "_system";
  private long MAX_QUOTA = 104857600L;
  private CallbackContext callbackContext;
  private Dialog dialog;
  private EditText edittext;
  private WebView inAppWebView;
  private boolean showLocationBar = true;
  
  private void closeDialog()
  {
    try
    {
      JSONObject localJSONObject = new JSONObject();
      localJSONObject.put("type", "exit");
      sendUpdate(localJSONObject, false);
      if (this.dialog != null) {
        this.dialog.dismiss();
      }
      return;
    }
    catch (JSONException localJSONException)
    {
      for (;;)
      {
        Log.d("InAppBrowser", "Should never happen");
      }
    }
  }
  
  private boolean getShowLocationBar()
  {
    return this.showLocationBar;
  }
  
  private void goBack()
  {
    if (this.inAppWebView.canGoBack()) {
      this.inAppWebView.goBack();
    }
  }
  
  private void goForward()
  {
    if (this.inAppWebView.canGoForward()) {
      this.inAppWebView.goForward();
    }
  }
  
  private void navigate(String paramString)
  {
    ((InputMethodManager)this.cordova.getActivity().getSystemService("input_method")).hideSoftInputFromWindow(this.edittext.getWindowToken(), 0);
    if ((!paramString.startsWith("http")) && (!paramString.startsWith("file:"))) {
      this.inAppWebView.loadUrl("http://" + paramString);
    }
    for (;;)
    {
      this.inAppWebView.requestFocus();
      return;
      this.inAppWebView.loadUrl(paramString);
    }
  }
  
  private HashMap<String, Boolean> parseFeature(String paramString)
  {
    if (paramString.equals("null"))
    {
      localObject = null;
      return localObject;
    }
    Object localObject = new HashMap();
    StringTokenizer localStringTokenizer1 = new StringTokenizer(paramString, ",");
    label32:
    String str;
    while (localStringTokenizer1.hasMoreElements())
    {
      StringTokenizer localStringTokenizer2 = new StringTokenizer(localStringTokenizer1.nextToken(), "=");
      if (localStringTokenizer2.hasMoreElements())
      {
        str = localStringTokenizer2.nextToken();
        if (!localStringTokenizer2.nextToken().equals("no")) {
          break label99;
        }
      }
    }
    label99:
    for (Boolean localBoolean = Boolean.FALSE;; localBoolean = Boolean.TRUE)
    {
      ((HashMap)localObject).put(str, localBoolean);
      break label32;
      break;
    }
  }
  
  private void sendUpdate(JSONObject paramJSONObject, boolean paramBoolean)
  {
    PluginResult localPluginResult = new PluginResult(PluginResult.Status.OK, paramJSONObject);
    localPluginResult.setKeepCallback(paramBoolean);
    this.callbackContext.sendPluginResult(localPluginResult);
  }
  
  private String updateUrl(String paramString)
  {
    if (Uri.parse(paramString).isRelative()) {
      paramString = this.webView.getUrl().substring(0, 1 + this.webView.getUrl().lastIndexOf("/")) + paramString;
    }
    return paramString;
  }
  
  public boolean execute(String paramString, JSONArray paramJSONArray, CallbackContext paramCallbackContext)
    throws JSONException
  {
    PluginResult.Status localStatus = PluginResult.Status.OK;
    String str1 = "";
    this.callbackContext = paramCallbackContext;
    label388:
    label440:
    for (;;)
    {
      String str3;
      HashMap localHashMap;
      String str4;
      try
      {
        if (!paramString.equals("open")) {
          break label388;
        }
        String str2 = paramJSONArray.getString(0);
        str3 = paramJSONArray.optString(1);
        if ((str3 == null) || (str3.equals("")) || (str3.equals("null"))) {
          break label440;
        }
        localHashMap = parseFeature(paramJSONArray.optString(2));
        Log.d("InAppBrowser", "target = " + str3);
        str4 = updateUrl(str2);
        if (!"_self".equals(str3)) {
          break label336;
        }
        Log.d("InAppBrowser", "in self");
        if ((str4.startsWith("file://")) || (str4.startsWith("javascript:")) || (Config.isUrlWhiteListed(str4)))
        {
          this.webView.loadUrl(str4);
          PluginResult localPluginResult1 = new PluginResult(localStatus, str1);
          localPluginResult1.setKeepCallback(true);
          this.callbackContext.sendPluginResult(localPluginResult1);
          return true;
        }
        boolean bool = str4.startsWith("tel:");
        if (bool)
        {
          try
          {
            Intent localIntent = new Intent("android.intent.action.DIAL");
            localIntent.setData(Uri.parse(str4));
            this.cordova.getActivity().startActivity(localIntent);
          }
          catch (ActivityNotFoundException localActivityNotFoundException)
          {
            LOG.e("InAppBrowser", "Error dialing " + str4 + ": " + localActivityNotFoundException.toString());
          }
          continue;
        }
        str1 = showWebPage(str4, localHashMap);
      }
      catch (JSONException localJSONException)
      {
        this.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.JSON_EXCEPTION));
        return true;
      }
      continue;
      label336:
      if ("_system".equals(str3))
      {
        Log.d("InAppBrowser", "in system");
        str1 = openExternal(str4);
      }
      else
      {
        Log.d("InAppBrowser", "in blank");
        str1 = showWebPage(str4, localHashMap);
        continue;
        if (paramString.equals("close"))
        {
          closeDialog();
          PluginResult localPluginResult2 = new PluginResult(PluginResult.Status.OK);
          localPluginResult2.setKeepCallback(false);
          this.callbackContext.sendPluginResult(localPluginResult2);
        }
        else
        {
          localStatus = PluginResult.Status.INVALID_ACTION;
          continue;
          str3 = "_self";
        }
      }
    }
  }
  
  public String openExternal(String paramString)
  {
    try
    {
      Intent localIntent = new Intent("android.intent.action.VIEW");
      Log.d("InAppBrowser", "InAppBrowser: Error loading url " + paramString + ":" + localActivityNotFoundException1.toString());
    }
    catch (ActivityNotFoundException localActivityNotFoundException1)
    {
      try
      {
        localIntent.setData(Uri.parse(paramString));
        this.cordova.getActivity().startActivity(localIntent);
        return "";
      }
      catch (ActivityNotFoundException localActivityNotFoundException2)
      {
        break label38;
      }
      localActivityNotFoundException1 = localActivityNotFoundException1;
    }
    label38:
    return localActivityNotFoundException1.toString();
  }
  
  public String showWebPage(final String paramString, HashMap<String, Boolean> paramHashMap)
  {
    this.showLocationBar = true;
    if (paramHashMap != null) {
      this.showLocationBar = ((Boolean)paramHashMap.get("location")).booleanValue();
    }
    Runnable local1 = new Runnable()
    {
      private int dpToPixels(int paramAnonymousInt)
      {
        return (int)TypedValue.applyDimension(1, paramAnonymousInt, InAppBrowser.this.cordova.getActivity().getResources().getDisplayMetrics());
      }
      
      public void run()
      {
        InAppBrowser.access$002(InAppBrowser.this, new Dialog(InAppBrowser.this.cordova.getActivity(), 16973830));
        InAppBrowser.this.dialog.getWindow().getAttributes().windowAnimations = 16973826;
        InAppBrowser.this.dialog.requestWindowFeature(1);
        InAppBrowser.this.dialog.setCancelable(true);
        Dialog localDialog = InAppBrowser.this.dialog;
        DialogInterface.OnDismissListener local1 = new DialogInterface.OnDismissListener()
        {
          public void onDismiss(DialogInterface paramAnonymous2DialogInterface)
          {
            try
            {
              JSONObject localJSONObject = new JSONObject();
              localJSONObject.put("type", "exit");
              InAppBrowser.this.sendUpdate(localJSONObject, false);
              return;
            }
            catch (JSONException localJSONException)
            {
              Log.d("InAppBrowser", "Should never happen");
            }
          }
        };
        localDialog.setOnDismissListener(local1);
        LinearLayout localLinearLayout = new LinearLayout(InAppBrowser.this.cordova.getActivity());
        localLinearLayout.setOrientation(1);
        RelativeLayout localRelativeLayout1 = new RelativeLayout(InAppBrowser.this.cordova.getActivity());
        localRelativeLayout1.setLayoutParams(new RelativeLayout.LayoutParams(-1, dpToPixels(44)));
        localRelativeLayout1.setPadding(dpToPixels(2), dpToPixels(2), dpToPixels(2), dpToPixels(2));
        localRelativeLayout1.setHorizontalGravity(3);
        localRelativeLayout1.setVerticalGravity(48);
        RelativeLayout localRelativeLayout2 = new RelativeLayout(InAppBrowser.this.cordova.getActivity());
        localRelativeLayout2.setLayoutParams(new RelativeLayout.LayoutParams(-2, -2));
        localRelativeLayout2.setHorizontalGravity(3);
        localRelativeLayout2.setVerticalGravity(16);
        localRelativeLayout2.setId(1);
        Button localButton1 = new Button(InAppBrowser.this.cordova.getActivity());
        RelativeLayout.LayoutParams localLayoutParams1 = new RelativeLayout.LayoutParams(-2, -1);
        localLayoutParams1.addRule(5);
        localButton1.setLayoutParams(localLayoutParams1);
        localButton1.setContentDescription("Back Button");
        localButton1.setId(2);
        localButton1.setText("<");
        View.OnClickListener local2 = new View.OnClickListener()
        {
          public void onClick(View paramAnonymous2View)
          {
            InAppBrowser.this.goBack();
          }
        };
        localButton1.setOnClickListener(local2);
        Button localButton2 = new Button(InAppBrowser.this.cordova.getActivity());
        RelativeLayout.LayoutParams localLayoutParams2 = new RelativeLayout.LayoutParams(-2, -1);
        localLayoutParams2.addRule(1, 2);
        localButton2.setLayoutParams(localLayoutParams2);
        localButton2.setContentDescription("Forward Button");
        localButton2.setId(3);
        localButton2.setText(">");
        View.OnClickListener local3 = new View.OnClickListener()
        {
          public void onClick(View paramAnonymous2View)
          {
            InAppBrowser.this.goForward();
          }
        };
        localButton2.setOnClickListener(local3);
        InAppBrowser.access$402(InAppBrowser.this, new EditText(InAppBrowser.this.cordova.getActivity()));
        RelativeLayout.LayoutParams localLayoutParams3 = new RelativeLayout.LayoutParams(-1, -1);
        localLayoutParams3.addRule(1, 1);
        localLayoutParams3.addRule(0, 5);
        InAppBrowser.this.edittext.setLayoutParams(localLayoutParams3);
        InAppBrowser.this.edittext.setId(4);
        InAppBrowser.this.edittext.setSingleLine(true);
        InAppBrowser.this.edittext.setText(paramString);
        InAppBrowser.this.edittext.setInputType(16);
        InAppBrowser.this.edittext.setImeOptions(2);
        InAppBrowser.this.edittext.setInputType(0);
        EditText localEditText = InAppBrowser.this.edittext;
        View.OnKeyListener local4 = new View.OnKeyListener()
        {
          public boolean onKey(View paramAnonymous2View, int paramAnonymous2Int, KeyEvent paramAnonymous2KeyEvent)
          {
            if ((paramAnonymous2KeyEvent.getAction() == 0) && (paramAnonymous2Int == 66))
            {
              InAppBrowser.this.navigate(InAppBrowser.this.edittext.getText().toString());
              return true;
            }
            return false;
          }
        };
        localEditText.setOnKeyListener(local4);
        Button localButton3 = new Button(InAppBrowser.this.cordova.getActivity());
        RelativeLayout.LayoutParams localLayoutParams4 = new RelativeLayout.LayoutParams(-2, -1);
        localLayoutParams4.addRule(11);
        localButton3.setLayoutParams(localLayoutParams4);
        localButton2.setContentDescription("Close Button");
        localButton3.setId(5);
        localButton3.setText("Done");
        View.OnClickListener local5 = new View.OnClickListener()
        {
          public void onClick(View paramAnonymous2View)
          {
            InAppBrowser.this.closeDialog();
          }
        };
        localButton3.setOnClickListener(local5);
        InAppBrowser.access$702(InAppBrowser.this, new WebView(InAppBrowser.this.cordova.getActivity()));
        InAppBrowser.this.inAppWebView.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        InAppBrowser.this.inAppWebView.setWebChromeClient(new InAppBrowser.InAppChromeClient(InAppBrowser.this));
        InAppBrowser.InAppBrowserClient localInAppBrowserClient = new InAppBrowser.InAppBrowserClient(InAppBrowser.this, this.val$thatWebView, InAppBrowser.this.edittext);
        InAppBrowser.this.inAppWebView.setWebViewClient(localInAppBrowserClient);
        WebSettings localWebSettings = InAppBrowser.this.inAppWebView.getSettings();
        localWebSettings.setJavaScriptEnabled(true);
        localWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        localWebSettings.setBuiltInZoomControls(true);
        localWebSettings.setPluginsEnabled(true);
        localWebSettings.setDatabaseEnabled(true);
        localWebSettings.setDatabasePath(InAppBrowser.this.cordova.getActivity().getApplicationContext().getDir("inAppBrowserDB", 0).getPath());
        localWebSettings.setDomStorageEnabled(true);
        InAppBrowser.this.inAppWebView.loadUrl(paramString);
        InAppBrowser.this.inAppWebView.setId(6);
        InAppBrowser.this.inAppWebView.getSettings().setLoadWithOverviewMode(true);
        InAppBrowser.this.inAppWebView.getSettings().setUseWideViewPort(true);
        InAppBrowser.this.inAppWebView.requestFocus();
        InAppBrowser.this.inAppWebView.requestFocusFromTouch();
        localRelativeLayout2.addView(localButton1);
        localRelativeLayout2.addView(localButton2);
        localRelativeLayout1.addView(localRelativeLayout2);
        localRelativeLayout1.addView(InAppBrowser.this.edittext);
        localRelativeLayout1.addView(localButton3);
        if (InAppBrowser.this.getShowLocationBar()) {
          localLinearLayout.addView(localRelativeLayout1);
        }
        localLinearLayout.addView(InAppBrowser.this.inAppWebView);
        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.copyFrom(InAppBrowser.this.dialog.getWindow().getAttributes());
        localLayoutParams.width = -1;
        localLayoutParams.height = -1;
        InAppBrowser.this.dialog.setContentView(localLinearLayout);
        InAppBrowser.this.dialog.show();
        InAppBrowser.this.dialog.getWindow().setAttributes(localLayoutParams);
      }
    };
    this.cordova.getActivity().runOnUiThread(local1);
    return "";
  }
  
  public class InAppBrowserClient
    extends WebViewClient
  {
    EditText edittext;
    CordovaWebView webView;
    
    public InAppBrowserClient(CordovaWebView paramCordovaWebView, EditText paramEditText)
    {
      this.webView = paramCordovaWebView;
      this.edittext = paramEditText;
    }
    
    public void onPageFinished(WebView paramWebView, String paramString)
    {
      super.onPageFinished(paramWebView, paramString);
      try
      {
        JSONObject localJSONObject = new JSONObject();
        localJSONObject.put("type", "loadstop");
        localJSONObject.put("url", paramString);
        InAppBrowser.this.sendUpdate(localJSONObject, true);
        return;
      }
      catch (JSONException localJSONException)
      {
        Log.d("InAppBrowser", "Should never happen");
      }
    }
    
    public void onPageStarted(WebView paramWebView, String paramString, Bitmap paramBitmap)
    {
      super.onPageStarted(paramWebView, paramString, paramBitmap);
      if ((paramString.startsWith("http:")) || (paramString.startsWith("https:")) || (paramString.startsWith("file:"))) {}
      for (String str = paramString;; str = "http://" + paramString)
      {
        if (!str.equals(this.edittext.getText().toString())) {
          this.edittext.setText(str);
        }
        try
        {
          JSONObject localJSONObject = new JSONObject();
          localJSONObject.put("type", "loadstart");
          localJSONObject.put("url", str);
          InAppBrowser.this.sendUpdate(localJSONObject, true);
          return;
        }
        catch (JSONException localJSONException)
        {
          Log.d("InAppBrowser", "Should never happen");
        }
      }
    }
  }
  
  public class InAppChromeClient
    extends WebChromeClient
  {
    public InAppChromeClient() {}
    
    public void onExceededDatabaseQuota(String paramString1, String paramString2, long paramLong1, long paramLong2, long paramLong3, WebStorage.QuotaUpdater paramQuotaUpdater)
    {
      Object[] arrayOfObject1 = new Object[3];
      arrayOfObject1[0] = Long.valueOf(paramLong2);
      arrayOfObject1[1] = Long.valueOf(paramLong1);
      arrayOfObject1[2] = Long.valueOf(paramLong3);
      LOG.d("InAppBrowser", "onExceededDatabaseQuota estimatedSize: %d  currentQuota: %d  totalUsedQuota: %d", arrayOfObject1);
      if (paramLong2 < InAppBrowser.this.MAX_QUOTA)
      {
        Object[] arrayOfObject2 = new Object[1];
        arrayOfObject2[0] = Long.valueOf(paramLong2);
        LOG.d("InAppBrowser", "calling quotaUpdater.updateQuota newQuota: %d", arrayOfObject2);
        paramQuotaUpdater.updateQuota(paramLong2);
        return;
      }
      paramQuotaUpdater.updateQuota(paramLong1);
    }
  }
}


/* Location:           C:\Users\Monitor\Videos\app\classes_dex2jar.jar
 * Qualified Name:     org.apache.cordova.InAppBrowser
 * JD-Core Version:    0.7.0.1
 */