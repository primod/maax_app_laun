package org.apache.cordova;

import android.app.Activity;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.Build.VERSION;
import android.util.Log;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaInterface;
import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.PluginResult;
import org.apache.cordova.api.PluginResult.Status;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FileTransfer
  extends CordovaPlugin
{
  public static int ABORTED_ERR = 0;
  private static final String BOUNDARY = "+++++";
  public static int CONNECTION_ERR = 0;
  private static final HostnameVerifier DO_NOT_VERIFY;
  public static int FILE_NOT_FOUND_ERR = 1;
  public static int INVALID_URL_ERR = 2;
  private static final String LINE_END = "\r\n";
  private static final String LINE_START = "--";
  private static final String LOG_TAG = "FileTransfer";
  private static final int MAX_BUFFER_SIZE = 16384;
  private static HashMap<String, RequestContext> activeRequests;
  private static final TrustManager[] trustAllCerts;
  
  static
  {
    CONNECTION_ERR = 3;
    ABORTED_ERR = 4;
    activeRequests = new HashMap();
    DO_NOT_VERIFY = new HostnameVerifier()
    {
      public boolean verify(String paramAnonymousString, SSLSession paramAnonymousSSLSession)
      {
        return true;
      }
    };
    TrustManager[] arrayOfTrustManager = new TrustManager[1];
    arrayOfTrustManager[0 = new X509TrustManager()
    {
      public void checkClientTrusted(X509Certificate[] paramAnonymousArrayOfX509Certificate, String paramAnonymousString)
        throws CertificateException
      {}
      
      public void checkServerTrusted(X509Certificate[] paramAnonymousArrayOfX509Certificate, String paramAnonymousString)
        throws CertificateException
      {}
      
      public X509Certificate[] getAcceptedIssuers()
      {
        return new X509Certificate[0];
      }
    };
    trustAllCerts = arrayOfTrustManager;
  }
  
  private void abort(String paramString)
  {
    JSONObject localJSONObject;
    synchronized (activeRequests)
    {
      localRequestContext = (RequestContext)activeRequests.remove(paramString);
      if (localRequestContext != null)
      {
        File localFile = localRequestContext.targetFile;
        if (localFile != null) {
          localFile.delete();
        }
        localJSONObject = createFileTransferError(ABORTED_ERR, localRequestContext.source, localRequestContext.target, Integer.valueOf(-1));
      }
    }
  }
  
  /* Error */
  private static JSONObject createFileTransferError(int paramInt, String paramString1, String paramString2, Integer paramInteger)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: new 179	org/json/JSONObject
    //   6: dup
    //   7: invokespecial 180	org/json/JSONObject:<init>	()V
    //   10: astore 5
    //   12: aload 5
    //   14: ldc 182
    //   16: iload_0
    //   17: invokevirtual 186	org/json/JSONObject:put	(Ljava/lang/String;I)Lorg/json/JSONObject;
    //   20: pop
    //   21: aload 5
    //   23: ldc 187
    //   25: aload_1
    //   26: invokevirtual 190	org/json/JSONObject:put	(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;
    //   29: pop
    //   30: aload 5
    //   32: ldc 191
    //   34: aload_2
    //   35: invokevirtual 190	org/json/JSONObject:put	(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;
    //   38: pop
    //   39: aload_3
    //   40: ifnull +12 -> 52
    //   43: aload 5
    //   45: ldc 193
    //   47: aload_3
    //   48: invokevirtual 190	org/json/JSONObject:put	(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;
    //   51: pop
    //   52: aload 5
    //   54: areturn
    //   55: astore 6
    //   57: ldc 25
    //   59: aload 6
    //   61: invokevirtual 197	org/json/JSONException:getMessage	()Ljava/lang/String;
    //   64: aload 6
    //   66: invokestatic 203	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   69: pop
    //   70: aload 4
    //   72: areturn
    //   73: astore 6
    //   75: aload 5
    //   77: astore 4
    //   79: goto -22 -> 57
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	82	0	paramInt	int
    //   0	82	1	paramString1	String
    //   0	82	2	paramString2	String
    //   0	82	3	paramInteger	Integer
    //   1	77	4	localObject	Object
    //   10	66	5	localJSONObject	JSONObject
    //   55	10	6	localJSONException1	JSONException
    //   73	1	6	localJSONException2	JSONException
    // Exception table:
    //   from	to	target	type
    //   3	12	55	org/json/JSONException
    //   12	39	73	org/json/JSONException
    //   43	52	73	org/json/JSONException
  }
  
  private static JSONObject createFileTransferError(int paramInt, String paramString1, String paramString2, URLConnection paramURLConnection)
  {
    i = 0;
    if (paramURLConnection != null) {}
    try
    {
      boolean bool = paramURLConnection instanceof HttpURLConnection;
      i = 0;
      if (bool)
      {
        int j = ((HttpURLConnection)paramURLConnection).getResponseCode();
        i = j;
      }
    }
    catch (IOException localIOException)
    {
      for (;;)
      {
        Log.w("FileTransfer", "Error getting HTTP status code from connection.", localIOException);
        i = 0;
      }
    }
    return createFileTransferError(paramInt, paramString1, paramString2, Integer.valueOf(i));
  }
  
  private void download(final String paramString1, final String paramString2, JSONArray paramJSONArray, CallbackContext paramCallbackContext)
    throws JSONException
  {
    Log.d("FileTransfer", "download " + paramString1 + " to " + paramString2);
    final boolean bool1 = paramJSONArray.optBoolean(2);
    final String str = paramJSONArray.getString(3);
    final URL localURL;
    final boolean bool2;
    try
    {
      localURL = new URL(paramString1);
      bool2 = localURL.getProtocol().equals("https");
      if (!Config.isUrlWhiteListed(paramString1))
      {
        Log.w("FileTransfer", "Source URL is not in white list: '" + paramString1 + "'");
        JSONObject localJSONObject1 = createFileTransferError(CONNECTION_ERR, paramString1, paramString2, Integer.valueOf(401));
        paramCallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.IO_EXCEPTION, localJSONObject1));
        return;
      }
    }
    catch (MalformedURLException localMalformedURLException)
    {
      JSONObject localJSONObject2 = createFileTransferError(INVALID_URL_ERR, paramString1, paramString2, Integer.valueOf(0));
      Log.e("FileTransfer", localJSONObject2.toString(), localMalformedURLException);
      paramCallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.IO_EXCEPTION, localJSONObject2));
      return;
    }
    final RequestContext localRequestContext = new RequestContext(paramString1, paramString2, paramCallbackContext);
    synchronized (activeRequests)
    {
      activeRequests.put(str, localRequestContext);
      this.cordova.getThreadPool().execute(new Runnable()
      {
        /* Error */
        public void run()
        {
          // Byte code:
          //   0: aload_0
          //   1: getfield 30	org/apache/cordova/FileTransfer$4:val$context	Lorg/apache/cordova/FileTransfer$RequestContext;
          //   4: getfield 59	org/apache/cordova/FileTransfer$RequestContext:aborted	Z
          //   7: ifeq +4 -> 11
          //   10: return
          //   11: aconst_null
          //   12: astore_1
          //   13: aconst_null
          //   14: astore_2
          //   15: aconst_null
          //   16: astore_3
          //   17: aconst_null
          //   18: astore 4
          //   20: aload_0
          //   21: getfield 28	org/apache/cordova/FileTransfer$4:this$0	Lorg/apache/cordova/FileTransfer;
          //   24: aload_0
          //   25: getfield 32	org/apache/cordova/FileTransfer$4:val$target	Ljava/lang/String;
          //   28: invokestatic 63	org/apache/cordova/FileTransfer:access$700	(Lorg/apache/cordova/FileTransfer;Ljava/lang/String;)Ljava/io/File;
          //   31: astore 4
          //   33: aload_0
          //   34: getfield 30	org/apache/cordova/FileTransfer$4:val$context	Lorg/apache/cordova/FileTransfer$RequestContext;
          //   37: aload 4
          //   39: putfield 67	org/apache/cordova/FileTransfer$RequestContext:targetFile	Ljava/io/File;
          //   42: aload 4
          //   44: invokevirtual 73	java/io/File:getParentFile	()Ljava/io/File;
          //   47: invokevirtual 77	java/io/File:mkdirs	()Z
          //   50: pop
          //   51: aload_0
          //   52: getfield 34	org/apache/cordova/FileTransfer$4:val$useHttps	Z
          //   55: ifeq +363 -> 418
          //   58: aload_0
          //   59: getfield 36	org/apache/cordova/FileTransfer$4:val$trustEveryone	Z
          //   62: ifne +318 -> 380
          //   65: aload_0
          //   66: getfield 38	org/apache/cordova/FileTransfer$4:val$url	Ljava/net/URL;
          //   69: invokevirtual 83	java/net/URL:openConnection	()Ljava/net/URLConnection;
          //   72: checkcast 85	javax/net/ssl/HttpsURLConnection
          //   75: astore_1
          //   76: aload_1
          //   77: instanceof 87
          //   80: ifeq +12 -> 92
          //   83: aload_1
          //   84: checkcast 87	java/net/HttpURLConnection
          //   87: ldc 89
          //   89: invokevirtual 93	java/net/HttpURLConnection:setRequestMethod	(Ljava/lang/String;)V
          //   92: invokestatic 99	android/webkit/CookieManager:getInstance	()Landroid/webkit/CookieManager;
          //   95: aload_0
          //   96: getfield 40	org/apache/cordova/FileTransfer$4:val$source	Ljava/lang/String;
          //   99: invokevirtual 103	android/webkit/CookieManager:getCookie	(Ljava/lang/String;)Ljava/lang/String;
          //   102: astore 61
          //   104: aload 61
          //   106: ifnull +11 -> 117
          //   109: aload_1
          //   110: ldc 105
          //   112: aload 61
          //   114: invokevirtual 111	java/net/URLConnection:setRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
          //   117: aload_1
          //   118: invokevirtual 114	java/net/URLConnection:connect	()V
          //   121: ldc 116
          //   123: new 118	java/lang/StringBuilder
          //   126: dup
          //   127: invokespecial 119	java/lang/StringBuilder:<init>	()V
          //   130: ldc 121
          //   132: invokevirtual 125	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   135: aload_0
          //   136: getfield 38	org/apache/cordova/FileTransfer$4:val$url	Ljava/net/URL;
          //   139: invokevirtual 128	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
          //   142: invokevirtual 132	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   145: invokestatic 138	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
          //   148: pop
          //   149: new 140	org/apache/cordova/FileProgressResult
          //   152: dup
          //   153: invokespecial 141	org/apache/cordova/FileProgressResult:<init>	()V
          //   156: astore 63
          //   158: aload_1
          //   159: invokevirtual 144	java/net/URLConnection:getContentEncoding	()Ljava/lang/String;
          //   162: ifnonnull +19 -> 181
          //   165: aload 63
          //   167: iconst_1
          //   168: invokevirtual 148	org/apache/cordova/FileProgressResult:setLengthComputable	(Z)V
          //   171: aload 63
          //   173: aload_1
          //   174: invokevirtual 152	java/net/URLConnection:getContentLength	()I
          //   177: i2l
          //   178: invokevirtual 156	org/apache/cordova/FileProgressResult:setTotal	(J)V
          //   181: aconst_null
          //   182: astore 64
          //   184: aload_1
          //   185: invokestatic 160	org/apache/cordova/FileTransfer:access$400	(Ljava/net/URLConnection;)Ljava/io/InputStream;
          //   188: astore 64
          //   190: new 162	java/io/FileOutputStream
          //   193: dup
          //   194: aload 4
          //   196: invokespecial 165	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
          //   199: astore 67
          //   201: aload_0
          //   202: getfield 30	org/apache/cordova/FileTransfer$4:val$context	Lorg/apache/cordova/FileTransfer$RequestContext;
          //   205: astore 68
          //   207: aload 68
          //   209: monitorenter
          //   210: aload_0
          //   211: getfield 30	org/apache/cordova/FileTransfer$4:val$context	Lorg/apache/cordova/FileTransfer$RequestContext;
          //   214: getfield 59	org/apache/cordova/FileTransfer$RequestContext:aborted	Z
          //   217: ifeq +220 -> 437
          //   220: aload 68
          //   222: monitorexit
          //   223: aload_0
          //   224: getfield 30	org/apache/cordova/FileTransfer$4:val$context	Lorg/apache/cordova/FileTransfer$RequestContext;
          //   227: aconst_null
          //   228: putfield 169	org/apache/cordova/FileTransfer$RequestContext:currentInputStream	Ljava/io/InputStream;
          //   231: aload 64
          //   233: invokestatic 173	org/apache/cordova/FileTransfer:access$300	(Ljava/io/Closeable;)V
          //   236: aload 67
          //   238: invokestatic 173	org/apache/cordova/FileTransfer:access$300	(Ljava/io/Closeable;)V
          //   241: invokestatic 177	org/apache/cordova/FileTransfer:access$600	()Ljava/util/HashMap;
          //   244: astore 85
          //   246: aload 85
          //   248: monitorenter
          //   249: invokestatic 177	org/apache/cordova/FileTransfer:access$600	()Ljava/util/HashMap;
          //   252: aload_0
          //   253: getfield 42	org/apache/cordova/FileTransfer$4:val$objectId	Ljava/lang/String;
          //   256: invokevirtual 183	java/util/HashMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
          //   259: pop
          //   260: aload 85
          //   262: monitorexit
          //   263: aload_1
          //   264: ifnull +35 -> 299
          //   267: aload_0
          //   268: getfield 36	org/apache/cordova/FileTransfer$4:val$trustEveryone	Z
          //   271: ifeq +28 -> 299
          //   274: aload_0
          //   275: getfield 34	org/apache/cordova/FileTransfer$4:val$useHttps	Z
          //   278: ifeq +21 -> 299
          //   281: aload_1
          //   282: checkcast 85	javax/net/ssl/HttpsURLConnection
          //   285: astore 91
          //   287: aload 91
          //   289: aload_2
          //   290: invokevirtual 187	javax/net/ssl/HttpsURLConnection:setHostnameVerifier	(Ljavax/net/ssl/HostnameVerifier;)V
          //   293: aload 91
          //   295: aload_3
          //   296: invokevirtual 191	javax/net/ssl/HttpsURLConnection:setSSLSocketFactory	(Ljavax/net/ssl/SSLSocketFactory;)V
          //   299: aconst_null
          //   300: astore 21
          //   302: iconst_0
          //   303: ifne +38 -> 341
          //   306: getstatic 197	org/apache/cordova/api/PluginResult$Status:ERROR	Lorg/apache/cordova/api/PluginResult$Status;
          //   309: astore 89
          //   311: getstatic 201	org/apache/cordova/FileTransfer:CONNECTION_ERR	I
          //   314: aload_0
          //   315: getfield 40	org/apache/cordova/FileTransfer$4:val$source	Ljava/lang/String;
          //   318: aload_0
          //   319: getfield 32	org/apache/cordova/FileTransfer$4:val$target	Ljava/lang/String;
          //   322: aload_1
          //   323: invokestatic 205	org/apache/cordova/FileTransfer:access$500	(ILjava/lang/String;Ljava/lang/String;Ljava/net/URLConnection;)Lorg/json/JSONObject;
          //   326: astore 90
          //   328: new 207	org/apache/cordova/api/PluginResult
          //   331: dup
          //   332: aload 89
          //   334: aload 90
          //   336: invokespecial 210	org/apache/cordova/api/PluginResult:<init>	(Lorg/apache/cordova/api/PluginResult$Status;Lorg/json/JSONObject;)V
          //   339: astore 21
          //   341: aload 21
          //   343: invokevirtual 213	org/apache/cordova/api/PluginResult:getStatus	()I
          //   346: getstatic 216	org/apache/cordova/api/PluginResult$Status:OK	Lorg/apache/cordova/api/PluginResult$Status;
          //   349: invokevirtual 219	org/apache/cordova/api/PluginResult$Status:ordinal	()I
          //   352: if_icmpeq +14 -> 366
          //   355: aload 4
          //   357: ifnull +9 -> 366
          //   360: aload 4
          //   362: invokevirtual 222	java/io/File:delete	()Z
          //   365: pop
          //   366: aload_0
          //   367: getfield 30	org/apache/cordova/FileTransfer$4:val$context	Lorg/apache/cordova/FileTransfer$RequestContext;
          //   370: astore 22
          //   372: aload 22
          //   374: aload 21
          //   376: invokevirtual 226	org/apache/cordova/FileTransfer$RequestContext:sendPluginResult	(Lorg/apache/cordova/api/PluginResult;)V
          //   379: return
          //   380: aload_0
          //   381: getfield 38	org/apache/cordova/FileTransfer$4:val$url	Ljava/net/URL;
          //   384: invokevirtual 83	java/net/URL:openConnection	()Ljava/net/URLConnection;
          //   387: checkcast 85	javax/net/ssl/HttpsURLConnection
          //   390: astore 92
          //   392: aload 92
          //   394: invokestatic 230	org/apache/cordova/FileTransfer:access$000	(Ljavax/net/ssl/HttpsURLConnection;)Ljavax/net/ssl/SSLSocketFactory;
          //   397: astore_3
          //   398: aload 92
          //   400: invokevirtual 234	javax/net/ssl/HttpsURLConnection:getHostnameVerifier	()Ljavax/net/ssl/HostnameVerifier;
          //   403: astore_2
          //   404: aload 92
          //   406: invokestatic 237	org/apache/cordova/FileTransfer:access$100	()Ljavax/net/ssl/HostnameVerifier;
          //   409: invokevirtual 187	javax/net/ssl/HttpsURLConnection:setHostnameVerifier	(Ljavax/net/ssl/HostnameVerifier;)V
          //   412: aload 92
          //   414: astore_1
          //   415: goto -339 -> 76
          //   418: aload_0
          //   419: getfield 38	org/apache/cordova/FileTransfer$4:val$url	Ljava/net/URL;
          //   422: invokevirtual 83	java/net/URL:openConnection	()Ljava/net/URLConnection;
          //   425: astore 60
          //   427: aload 60
          //   429: astore_1
          //   430: aconst_null
          //   431: astore_2
          //   432: aconst_null
          //   433: astore_3
          //   434: goto -358 -> 76
          //   437: aload_0
          //   438: getfield 30	org/apache/cordova/FileTransfer$4:val$context	Lorg/apache/cordova/FileTransfer$RequestContext;
          //   441: aload 64
          //   443: putfield 169	org/apache/cordova/FileTransfer$RequestContext:currentInputStream	Ljava/io/InputStream;
          //   446: aload 68
          //   448: monitorexit
          //   449: sipush 16384
          //   452: newarray byte
          //   454: astore 70
          //   456: lconst_0
          //   457: lstore 71
          //   459: aload 64
          //   461: aload 70
          //   463: invokevirtual 243	java/io/InputStream:read	([B)I
          //   466: istore 73
          //   468: iload 73
          //   470: ifle +276 -> 746
          //   473: aload 67
          //   475: aload 70
          //   477: iconst_0
          //   478: iload 73
          //   480: invokevirtual 247	java/io/FileOutputStream:write	([BII)V
          //   483: lload 71
          //   485: iload 73
          //   487: i2l
          //   488: ladd
          //   489: lstore 71
          //   491: aload 63
          //   493: lload 71
          //   495: invokevirtual 250	org/apache/cordova/FileProgressResult:setLoaded	(J)V
          //   498: new 207	org/apache/cordova/api/PluginResult
          //   501: dup
          //   502: getstatic 216	org/apache/cordova/api/PluginResult$Status:OK	Lorg/apache/cordova/api/PluginResult$Status;
          //   505: aload 63
          //   507: invokevirtual 254	org/apache/cordova/FileProgressResult:toJSONObject	()Lorg/json/JSONObject;
          //   510: invokespecial 210	org/apache/cordova/api/PluginResult:<init>	(Lorg/apache/cordova/api/PluginResult$Status;Lorg/json/JSONObject;)V
          //   513: astore 74
          //   515: aload 74
          //   517: iconst_1
          //   518: invokevirtual 257	org/apache/cordova/api/PluginResult:setKeepCallback	(Z)V
          //   521: aload_0
          //   522: getfield 30	org/apache/cordova/FileTransfer$4:val$context	Lorg/apache/cordova/FileTransfer$RequestContext;
          //   525: aload 74
          //   527: invokevirtual 226	org/apache/cordova/FileTransfer$RequestContext:sendPluginResult	(Lorg/apache/cordova/api/PluginResult;)V
          //   530: goto -71 -> 459
          //   533: astore 65
          //   535: aload 67
          //   537: astore 66
          //   539: aload_0
          //   540: getfield 30	org/apache/cordova/FileTransfer$4:val$context	Lorg/apache/cordova/FileTransfer$RequestContext;
          //   543: aconst_null
          //   544: putfield 169	org/apache/cordova/FileTransfer$RequestContext:currentInputStream	Ljava/io/InputStream;
          //   547: aload 64
          //   549: invokestatic 173	org/apache/cordova/FileTransfer:access$300	(Ljava/io/Closeable;)V
          //   552: aload 66
          //   554: invokestatic 173	org/apache/cordova/FileTransfer:access$300	(Ljava/io/Closeable;)V
          //   557: aload 65
          //   559: athrow
          //   560: astore 48
          //   562: getstatic 260	org/apache/cordova/FileTransfer:FILE_NOT_FOUND_ERR	I
          //   565: aload_0
          //   566: getfield 40	org/apache/cordova/FileTransfer$4:val$source	Ljava/lang/String;
          //   569: aload_0
          //   570: getfield 32	org/apache/cordova/FileTransfer$4:val$target	Ljava/lang/String;
          //   573: aload_1
          //   574: invokestatic 205	org/apache/cordova/FileTransfer:access$500	(ILjava/lang/String;Ljava/lang/String;Ljava/net/URLConnection;)Lorg/json/JSONObject;
          //   577: astore 49
          //   579: ldc 116
          //   581: aload 49
          //   583: invokevirtual 263	org/json/JSONObject:toString	()Ljava/lang/String;
          //   586: aload 48
          //   588: invokestatic 267	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
          //   591: pop
          //   592: new 207	org/apache/cordova/api/PluginResult
          //   595: dup
          //   596: getstatic 270	org/apache/cordova/api/PluginResult$Status:IO_EXCEPTION	Lorg/apache/cordova/api/PluginResult$Status;
          //   599: aload 49
          //   601: invokespecial 210	org/apache/cordova/api/PluginResult:<init>	(Lorg/apache/cordova/api/PluginResult$Status;Lorg/json/JSONObject;)V
          //   604: astore 51
          //   606: invokestatic 177	org/apache/cordova/FileTransfer:access$600	()Ljava/util/HashMap;
          //   609: astore 52
          //   611: aload 52
          //   613: monitorenter
          //   614: invokestatic 177	org/apache/cordova/FileTransfer:access$600	()Ljava/util/HashMap;
          //   617: aload_0
          //   618: getfield 42	org/apache/cordova/FileTransfer$4:val$objectId	Ljava/lang/String;
          //   621: invokevirtual 183	java/util/HashMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
          //   624: pop
          //   625: aload 52
          //   627: monitorexit
          //   628: aload_1
          //   629: ifnull +35 -> 664
          //   632: aload_0
          //   633: getfield 36	org/apache/cordova/FileTransfer$4:val$trustEveryone	Z
          //   636: ifeq +28 -> 664
          //   639: aload_0
          //   640: getfield 34	org/apache/cordova/FileTransfer$4:val$useHttps	Z
          //   643: ifeq +21 -> 664
          //   646: aload_1
          //   647: checkcast 85	javax/net/ssl/HttpsURLConnection
          //   650: astore 58
          //   652: aload 58
          //   654: aload_2
          //   655: invokevirtual 187	javax/net/ssl/HttpsURLConnection:setHostnameVerifier	(Ljavax/net/ssl/HostnameVerifier;)V
          //   658: aload 58
          //   660: aload_3
          //   661: invokevirtual 191	javax/net/ssl/HttpsURLConnection:setSSLSocketFactory	(Ljavax/net/ssl/SSLSocketFactory;)V
          //   664: aload 51
          //   666: ifnonnull +1033 -> 1699
          //   669: getstatic 197	org/apache/cordova/api/PluginResult$Status:ERROR	Lorg/apache/cordova/api/PluginResult$Status;
          //   672: astore 56
          //   674: getstatic 201	org/apache/cordova/FileTransfer:CONNECTION_ERR	I
          //   677: aload_0
          //   678: getfield 40	org/apache/cordova/FileTransfer$4:val$source	Ljava/lang/String;
          //   681: aload_0
          //   682: getfield 32	org/apache/cordova/FileTransfer$4:val$target	Ljava/lang/String;
          //   685: aload_1
          //   686: invokestatic 205	org/apache/cordova/FileTransfer:access$500	(ILjava/lang/String;Ljava/lang/String;Ljava/net/URLConnection;)Lorg/json/JSONObject;
          //   689: astore 57
          //   691: new 207	org/apache/cordova/api/PluginResult
          //   694: dup
          //   695: aload 56
          //   697: aload 57
          //   699: invokespecial 210	org/apache/cordova/api/PluginResult:<init>	(Lorg/apache/cordova/api/PluginResult$Status;Lorg/json/JSONObject;)V
          //   702: astore 21
          //   704: aload 21
          //   706: invokevirtual 213	org/apache/cordova/api/PluginResult:getStatus	()I
          //   709: getstatic 216	org/apache/cordova/api/PluginResult$Status:OK	Lorg/apache/cordova/api/PluginResult$Status;
          //   712: invokevirtual 219	org/apache/cordova/api/PluginResult$Status:ordinal	()I
          //   715: if_icmpeq +14 -> 729
          //   718: aload 4
          //   720: ifnull +9 -> 729
          //   723: aload 4
          //   725: invokevirtual 222	java/io/File:delete	()Z
          //   728: pop
          //   729: aload_0
          //   730: getfield 30	org/apache/cordova/FileTransfer$4:val$context	Lorg/apache/cordova/FileTransfer$RequestContext;
          //   733: astore 22
          //   735: goto -363 -> 372
          //   738: astore 69
          //   740: aload 68
          //   742: monitorexit
          //   743: aload 69
          //   745: athrow
          //   746: aload_0
          //   747: getfield 30	org/apache/cordova/FileTransfer$4:val$context	Lorg/apache/cordova/FileTransfer$RequestContext;
          //   750: aconst_null
          //   751: putfield 169	org/apache/cordova/FileTransfer$RequestContext:currentInputStream	Ljava/io/InputStream;
          //   754: aload 64
          //   756: invokestatic 173	org/apache/cordova/FileTransfer:access$300	(Ljava/io/Closeable;)V
          //   759: aload 67
          //   761: invokestatic 173	org/apache/cordova/FileTransfer:access$300	(Ljava/io/Closeable;)V
          //   764: ldc 116
          //   766: new 118	java/lang/StringBuilder
          //   769: dup
          //   770: invokespecial 119	java/lang/StringBuilder:<init>	()V
          //   773: ldc_w 272
          //   776: invokevirtual 125	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   779: aload_0
          //   780: getfield 32	org/apache/cordova/FileTransfer$4:val$target	Ljava/lang/String;
          //   783: invokevirtual 125	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   786: invokevirtual 132	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   789: invokestatic 138	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
          //   792: pop
          //   793: new 274	org/apache/cordova/FileUtils
          //   796: dup
          //   797: invokespecial 275	org/apache/cordova/FileUtils:<init>	()V
          //   800: aload 4
          //   802: invokevirtual 279	org/apache/cordova/FileUtils:getEntry	(Ljava/io/File;)Lorg/json/JSONObject;
          //   805: astore 76
          //   807: new 207	org/apache/cordova/api/PluginResult
          //   810: dup
          //   811: getstatic 216	org/apache/cordova/api/PluginResult$Status:OK	Lorg/apache/cordova/api/PluginResult$Status;
          //   814: aload 76
          //   816: invokespecial 210	org/apache/cordova/api/PluginResult:<init>	(Lorg/apache/cordova/api/PluginResult$Status;Lorg/json/JSONObject;)V
          //   819: astore 77
          //   821: invokestatic 177	org/apache/cordova/FileTransfer:access$600	()Ljava/util/HashMap;
          //   824: astore 78
          //   826: aload 78
          //   828: monitorenter
          //   829: invokestatic 177	org/apache/cordova/FileTransfer:access$600	()Ljava/util/HashMap;
          //   832: aload_0
          //   833: getfield 42	org/apache/cordova/FileTransfer$4:val$objectId	Ljava/lang/String;
          //   836: invokevirtual 183	java/util/HashMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
          //   839: pop
          //   840: aload 78
          //   842: monitorexit
          //   843: aload_1
          //   844: ifnull +35 -> 879
          //   847: aload_0
          //   848: getfield 36	org/apache/cordova/FileTransfer$4:val$trustEveryone	Z
          //   851: ifeq +28 -> 879
          //   854: aload_0
          //   855: getfield 34	org/apache/cordova/FileTransfer$4:val$useHttps	Z
          //   858: ifeq +21 -> 879
          //   861: aload_1
          //   862: checkcast 85	javax/net/ssl/HttpsURLConnection
          //   865: astore 84
          //   867: aload 84
          //   869: aload_2
          //   870: invokevirtual 187	javax/net/ssl/HttpsURLConnection:setHostnameVerifier	(Ljavax/net/ssl/HostnameVerifier;)V
          //   873: aload 84
          //   875: aload_3
          //   876: invokevirtual 191	javax/net/ssl/HttpsURLConnection:setSSLSocketFactory	(Ljavax/net/ssl/SSLSocketFactory;)V
          //   879: aload 77
          //   881: ifnonnull +790 -> 1671
          //   884: getstatic 197	org/apache/cordova/api/PluginResult$Status:ERROR	Lorg/apache/cordova/api/PluginResult$Status;
          //   887: astore 82
          //   889: getstatic 201	org/apache/cordova/FileTransfer:CONNECTION_ERR	I
          //   892: aload_0
          //   893: getfield 40	org/apache/cordova/FileTransfer$4:val$source	Ljava/lang/String;
          //   896: aload_0
          //   897: getfield 32	org/apache/cordova/FileTransfer$4:val$target	Ljava/lang/String;
          //   900: aload_1
          //   901: invokestatic 205	org/apache/cordova/FileTransfer:access$500	(ILjava/lang/String;Ljava/lang/String;Ljava/net/URLConnection;)Lorg/json/JSONObject;
          //   904: astore 83
          //   906: new 207	org/apache/cordova/api/PluginResult
          //   909: dup
          //   910: aload 82
          //   912: aload 83
          //   914: invokespecial 210	org/apache/cordova/api/PluginResult:<init>	(Lorg/apache/cordova/api/PluginResult$Status;Lorg/json/JSONObject;)V
          //   917: astore 21
          //   919: aload 21
          //   921: invokevirtual 213	org/apache/cordova/api/PluginResult:getStatus	()I
          //   924: getstatic 216	org/apache/cordova/api/PluginResult$Status:OK	Lorg/apache/cordova/api/PluginResult$Status;
          //   927: invokevirtual 219	org/apache/cordova/api/PluginResult$Status:ordinal	()I
          //   930: if_icmpeq +14 -> 944
          //   933: aload 4
          //   935: ifnull +9 -> 944
          //   938: aload 4
          //   940: invokevirtual 222	java/io/File:delete	()Z
          //   943: pop
          //   944: aload_0
          //   945: getfield 30	org/apache/cordova/FileTransfer$4:val$context	Lorg/apache/cordova/FileTransfer$RequestContext;
          //   948: astore 22
          //   950: goto -578 -> 372
          //   953: astore 37
          //   955: getstatic 201	org/apache/cordova/FileTransfer:CONNECTION_ERR	I
          //   958: aload_0
          //   959: getfield 40	org/apache/cordova/FileTransfer$4:val$source	Ljava/lang/String;
          //   962: aload_0
          //   963: getfield 32	org/apache/cordova/FileTransfer$4:val$target	Ljava/lang/String;
          //   966: aload_1
          //   967: invokestatic 205	org/apache/cordova/FileTransfer:access$500	(ILjava/lang/String;Ljava/lang/String;Ljava/net/URLConnection;)Lorg/json/JSONObject;
          //   970: astore 38
          //   972: ldc 116
          //   974: aload 38
          //   976: invokevirtual 263	org/json/JSONObject:toString	()Ljava/lang/String;
          //   979: aload 37
          //   981: invokestatic 267	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
          //   984: pop
          //   985: new 207	org/apache/cordova/api/PluginResult
          //   988: dup
          //   989: getstatic 270	org/apache/cordova/api/PluginResult$Status:IO_EXCEPTION	Lorg/apache/cordova/api/PluginResult$Status;
          //   992: aload 38
          //   994: invokespecial 210	org/apache/cordova/api/PluginResult:<init>	(Lorg/apache/cordova/api/PluginResult$Status;Lorg/json/JSONObject;)V
          //   997: astore 40
          //   999: invokestatic 177	org/apache/cordova/FileTransfer:access$600	()Ljava/util/HashMap;
          //   1002: astore 41
          //   1004: aload 41
          //   1006: monitorenter
          //   1007: invokestatic 177	org/apache/cordova/FileTransfer:access$600	()Ljava/util/HashMap;
          //   1010: aload_0
          //   1011: getfield 42	org/apache/cordova/FileTransfer$4:val$objectId	Ljava/lang/String;
          //   1014: invokevirtual 183	java/util/HashMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
          //   1017: pop
          //   1018: aload 41
          //   1020: monitorexit
          //   1021: aload_1
          //   1022: ifnull +35 -> 1057
          //   1025: aload_0
          //   1026: getfield 36	org/apache/cordova/FileTransfer$4:val$trustEveryone	Z
          //   1029: ifeq +28 -> 1057
          //   1032: aload_0
          //   1033: getfield 34	org/apache/cordova/FileTransfer$4:val$useHttps	Z
          //   1036: ifeq +21 -> 1057
          //   1039: aload_1
          //   1040: checkcast 85	javax/net/ssl/HttpsURLConnection
          //   1043: astore 47
          //   1045: aload 47
          //   1047: aload_2
          //   1048: invokevirtual 187	javax/net/ssl/HttpsURLConnection:setHostnameVerifier	(Ljavax/net/ssl/HostnameVerifier;)V
          //   1051: aload 47
          //   1053: aload_3
          //   1054: invokevirtual 191	javax/net/ssl/HttpsURLConnection:setSSLSocketFactory	(Ljavax/net/ssl/SSLSocketFactory;)V
          //   1057: aload 40
          //   1059: ifnonnull +633 -> 1692
          //   1062: getstatic 197	org/apache/cordova/api/PluginResult$Status:ERROR	Lorg/apache/cordova/api/PluginResult$Status;
          //   1065: astore 45
          //   1067: getstatic 201	org/apache/cordova/FileTransfer:CONNECTION_ERR	I
          //   1070: aload_0
          //   1071: getfield 40	org/apache/cordova/FileTransfer$4:val$source	Ljava/lang/String;
          //   1074: aload_0
          //   1075: getfield 32	org/apache/cordova/FileTransfer$4:val$target	Ljava/lang/String;
          //   1078: aload_1
          //   1079: invokestatic 205	org/apache/cordova/FileTransfer:access$500	(ILjava/lang/String;Ljava/lang/String;Ljava/net/URLConnection;)Lorg/json/JSONObject;
          //   1082: astore 46
          //   1084: new 207	org/apache/cordova/api/PluginResult
          //   1087: dup
          //   1088: aload 45
          //   1090: aload 46
          //   1092: invokespecial 210	org/apache/cordova/api/PluginResult:<init>	(Lorg/apache/cordova/api/PluginResult$Status;Lorg/json/JSONObject;)V
          //   1095: astore 21
          //   1097: aload 21
          //   1099: invokevirtual 213	org/apache/cordova/api/PluginResult:getStatus	()I
          //   1102: getstatic 216	org/apache/cordova/api/PluginResult$Status:OK	Lorg/apache/cordova/api/PluginResult$Status;
          //   1105: invokevirtual 219	org/apache/cordova/api/PluginResult$Status:ordinal	()I
          //   1108: if_icmpeq +14 -> 1122
          //   1111: aload 4
          //   1113: ifnull +9 -> 1122
          //   1116: aload 4
          //   1118: invokevirtual 222	java/io/File:delete	()Z
          //   1121: pop
          //   1122: aload_0
          //   1123: getfield 30	org/apache/cordova/FileTransfer$4:val$context	Lorg/apache/cordova/FileTransfer$RequestContext;
          //   1126: astore 22
          //   1128: goto -756 -> 372
          //   1131: astore 27
          //   1133: ldc 116
          //   1135: aload 27
          //   1137: invokevirtual 282	org/json/JSONException:getMessage	()Ljava/lang/String;
          //   1140: aload 27
          //   1142: invokestatic 267	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
          //   1145: pop
          //   1146: new 207	org/apache/cordova/api/PluginResult
          //   1149: dup
          //   1150: getstatic 285	org/apache/cordova/api/PluginResult$Status:JSON_EXCEPTION	Lorg/apache/cordova/api/PluginResult$Status;
          //   1153: invokespecial 288	org/apache/cordova/api/PluginResult:<init>	(Lorg/apache/cordova/api/PluginResult$Status;)V
          //   1156: astore 29
          //   1158: invokestatic 177	org/apache/cordova/FileTransfer:access$600	()Ljava/util/HashMap;
          //   1161: astore 30
          //   1163: aload 30
          //   1165: monitorenter
          //   1166: invokestatic 177	org/apache/cordova/FileTransfer:access$600	()Ljava/util/HashMap;
          //   1169: aload_0
          //   1170: getfield 42	org/apache/cordova/FileTransfer$4:val$objectId	Ljava/lang/String;
          //   1173: invokevirtual 183	java/util/HashMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
          //   1176: pop
          //   1177: aload 30
          //   1179: monitorexit
          //   1180: aload_1
          //   1181: ifnull +35 -> 1216
          //   1184: aload_0
          //   1185: getfield 36	org/apache/cordova/FileTransfer$4:val$trustEveryone	Z
          //   1188: ifeq +28 -> 1216
          //   1191: aload_0
          //   1192: getfield 34	org/apache/cordova/FileTransfer$4:val$useHttps	Z
          //   1195: ifeq +21 -> 1216
          //   1198: aload_1
          //   1199: checkcast 85	javax/net/ssl/HttpsURLConnection
          //   1202: astore 36
          //   1204: aload 36
          //   1206: aload_2
          //   1207: invokevirtual 187	javax/net/ssl/HttpsURLConnection:setHostnameVerifier	(Ljavax/net/ssl/HostnameVerifier;)V
          //   1210: aload 36
          //   1212: aload_3
          //   1213: invokevirtual 191	javax/net/ssl/HttpsURLConnection:setSSLSocketFactory	(Ljavax/net/ssl/SSLSocketFactory;)V
          //   1216: aload 29
          //   1218: ifnonnull +467 -> 1685
          //   1221: getstatic 197	org/apache/cordova/api/PluginResult$Status:ERROR	Lorg/apache/cordova/api/PluginResult$Status;
          //   1224: astore 34
          //   1226: getstatic 201	org/apache/cordova/FileTransfer:CONNECTION_ERR	I
          //   1229: aload_0
          //   1230: getfield 40	org/apache/cordova/FileTransfer$4:val$source	Ljava/lang/String;
          //   1233: aload_0
          //   1234: getfield 32	org/apache/cordova/FileTransfer$4:val$target	Ljava/lang/String;
          //   1237: aload_1
          //   1238: invokestatic 205	org/apache/cordova/FileTransfer:access$500	(ILjava/lang/String;Ljava/lang/String;Ljava/net/URLConnection;)Lorg/json/JSONObject;
          //   1241: astore 35
          //   1243: new 207	org/apache/cordova/api/PluginResult
          //   1246: dup
          //   1247: aload 34
          //   1249: aload 35
          //   1251: invokespecial 210	org/apache/cordova/api/PluginResult:<init>	(Lorg/apache/cordova/api/PluginResult$Status;Lorg/json/JSONObject;)V
          //   1254: astore 21
          //   1256: aload 21
          //   1258: invokevirtual 213	org/apache/cordova/api/PluginResult:getStatus	()I
          //   1261: getstatic 216	org/apache/cordova/api/PluginResult$Status:OK	Lorg/apache/cordova/api/PluginResult$Status;
          //   1264: invokevirtual 219	org/apache/cordova/api/PluginResult$Status:ordinal	()I
          //   1267: if_icmpeq +14 -> 1281
          //   1270: aload 4
          //   1272: ifnull +9 -> 1281
          //   1275: aload 4
          //   1277: invokevirtual 222	java/io/File:delete	()Z
          //   1280: pop
          //   1281: aload_0
          //   1282: getfield 30	org/apache/cordova/FileTransfer$4:val$context	Lorg/apache/cordova/FileTransfer$RequestContext;
          //   1285: astore 22
          //   1287: goto -915 -> 372
          //   1290: astore 14
          //   1292: getstatic 201	org/apache/cordova/FileTransfer:CONNECTION_ERR	I
          //   1295: aload_0
          //   1296: getfield 40	org/apache/cordova/FileTransfer$4:val$source	Ljava/lang/String;
          //   1299: aload_0
          //   1300: getfield 32	org/apache/cordova/FileTransfer$4:val$target	Ljava/lang/String;
          //   1303: aload_1
          //   1304: invokestatic 205	org/apache/cordova/FileTransfer:access$500	(ILjava/lang/String;Ljava/lang/String;Ljava/net/URLConnection;)Lorg/json/JSONObject;
          //   1307: astore 15
          //   1309: ldc 116
          //   1311: aload 15
          //   1313: invokevirtual 263	org/json/JSONObject:toString	()Ljava/lang/String;
          //   1316: aload 14
          //   1318: invokestatic 267	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
          //   1321: pop
          //   1322: new 207	org/apache/cordova/api/PluginResult
          //   1325: dup
          //   1326: getstatic 270	org/apache/cordova/api/PluginResult$Status:IO_EXCEPTION	Lorg/apache/cordova/api/PluginResult$Status;
          //   1329: aload 15
          //   1331: invokespecial 210	org/apache/cordova/api/PluginResult:<init>	(Lorg/apache/cordova/api/PluginResult$Status;Lorg/json/JSONObject;)V
          //   1334: astore 17
          //   1336: invokestatic 177	org/apache/cordova/FileTransfer:access$600	()Ljava/util/HashMap;
          //   1339: astore 18
          //   1341: aload 18
          //   1343: monitorenter
          //   1344: invokestatic 177	org/apache/cordova/FileTransfer:access$600	()Ljava/util/HashMap;
          //   1347: aload_0
          //   1348: getfield 42	org/apache/cordova/FileTransfer$4:val$objectId	Ljava/lang/String;
          //   1351: invokevirtual 183	java/util/HashMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
          //   1354: pop
          //   1355: aload 18
          //   1357: monitorexit
          //   1358: aload_1
          //   1359: ifnull +35 -> 1394
          //   1362: aload_0
          //   1363: getfield 36	org/apache/cordova/FileTransfer$4:val$trustEveryone	Z
          //   1366: ifeq +28 -> 1394
          //   1369: aload_0
          //   1370: getfield 34	org/apache/cordova/FileTransfer$4:val$useHttps	Z
          //   1373: ifeq +21 -> 1394
          //   1376: aload_1
          //   1377: checkcast 85	javax/net/ssl/HttpsURLConnection
          //   1380: astore 26
          //   1382: aload 26
          //   1384: aload_2
          //   1385: invokevirtual 187	javax/net/ssl/HttpsURLConnection:setHostnameVerifier	(Ljavax/net/ssl/HostnameVerifier;)V
          //   1388: aload 26
          //   1390: aload_3
          //   1391: invokevirtual 191	javax/net/ssl/HttpsURLConnection:setSSLSocketFactory	(Ljavax/net/ssl/SSLSocketFactory;)V
          //   1394: aload 17
          //   1396: ifnonnull +282 -> 1678
          //   1399: getstatic 197	org/apache/cordova/api/PluginResult$Status:ERROR	Lorg/apache/cordova/api/PluginResult$Status;
          //   1402: astore 24
          //   1404: getstatic 201	org/apache/cordova/FileTransfer:CONNECTION_ERR	I
          //   1407: aload_0
          //   1408: getfield 40	org/apache/cordova/FileTransfer$4:val$source	Ljava/lang/String;
          //   1411: aload_0
          //   1412: getfield 32	org/apache/cordova/FileTransfer$4:val$target	Ljava/lang/String;
          //   1415: aload_1
          //   1416: invokestatic 205	org/apache/cordova/FileTransfer:access$500	(ILjava/lang/String;Ljava/lang/String;Ljava/net/URLConnection;)Lorg/json/JSONObject;
          //   1419: astore 25
          //   1421: new 207	org/apache/cordova/api/PluginResult
          //   1424: dup
          //   1425: aload 24
          //   1427: aload 25
          //   1429: invokespecial 210	org/apache/cordova/api/PluginResult:<init>	(Lorg/apache/cordova/api/PluginResult$Status;Lorg/json/JSONObject;)V
          //   1432: astore 21
          //   1434: aload 21
          //   1436: invokevirtual 213	org/apache/cordova/api/PluginResult:getStatus	()I
          //   1439: getstatic 216	org/apache/cordova/api/PluginResult$Status:OK	Lorg/apache/cordova/api/PluginResult$Status;
          //   1442: invokevirtual 219	org/apache/cordova/api/PluginResult$Status:ordinal	()I
          //   1445: if_icmpeq +14 -> 1459
          //   1448: aload 4
          //   1450: ifnull +9 -> 1459
          //   1453: aload 4
          //   1455: invokevirtual 222	java/io/File:delete	()Z
          //   1458: pop
          //   1459: aload_0
          //   1460: getfield 30	org/apache/cordova/FileTransfer$4:val$context	Lorg/apache/cordova/FileTransfer$RequestContext;
          //   1463: astore 22
          //   1465: goto -1093 -> 372
          //   1468: astore 5
          //   1470: invokestatic 177	org/apache/cordova/FileTransfer:access$600	()Ljava/util/HashMap;
          //   1473: astore 6
          //   1475: aload 6
          //   1477: monitorenter
          //   1478: invokestatic 177	org/apache/cordova/FileTransfer:access$600	()Ljava/util/HashMap;
          //   1481: aload_0
          //   1482: getfield 42	org/apache/cordova/FileTransfer$4:val$objectId	Ljava/lang/String;
          //   1485: invokevirtual 183	java/util/HashMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
          //   1488: pop
          //   1489: aload 6
          //   1491: monitorexit
          //   1492: aload_1
          //   1493: ifnull +35 -> 1528
          //   1496: aload_0
          //   1497: getfield 36	org/apache/cordova/FileTransfer$4:val$trustEveryone	Z
          //   1500: ifeq +28 -> 1528
          //   1503: aload_0
          //   1504: getfield 34	org/apache/cordova/FileTransfer$4:val$useHttps	Z
          //   1507: ifeq +21 -> 1528
          //   1510: aload_1
          //   1511: checkcast 85	javax/net/ssl/HttpsURLConnection
          //   1514: astore 13
          //   1516: aload 13
          //   1518: aload_2
          //   1519: invokevirtual 187	javax/net/ssl/HttpsURLConnection:setHostnameVerifier	(Ljavax/net/ssl/HostnameVerifier;)V
          //   1522: aload 13
          //   1524: aload_3
          //   1525: invokevirtual 191	javax/net/ssl/HttpsURLConnection:setSSLSocketFactory	(Ljavax/net/ssl/SSLSocketFactory;)V
          //   1528: aconst_null
          //   1529: astore 9
          //   1531: iconst_0
          //   1532: ifne +38 -> 1570
          //   1535: getstatic 197	org/apache/cordova/api/PluginResult$Status:ERROR	Lorg/apache/cordova/api/PluginResult$Status;
          //   1538: astore 11
          //   1540: getstatic 201	org/apache/cordova/FileTransfer:CONNECTION_ERR	I
          //   1543: aload_0
          //   1544: getfield 40	org/apache/cordova/FileTransfer$4:val$source	Ljava/lang/String;
          //   1547: aload_0
          //   1548: getfield 32	org/apache/cordova/FileTransfer$4:val$target	Ljava/lang/String;
          //   1551: aload_1
          //   1552: invokestatic 205	org/apache/cordova/FileTransfer:access$500	(ILjava/lang/String;Ljava/lang/String;Ljava/net/URLConnection;)Lorg/json/JSONObject;
          //   1555: astore 12
          //   1557: new 207	org/apache/cordova/api/PluginResult
          //   1560: dup
          //   1561: aload 11
          //   1563: aload 12
          //   1565: invokespecial 210	org/apache/cordova/api/PluginResult:<init>	(Lorg/apache/cordova/api/PluginResult$Status;Lorg/json/JSONObject;)V
          //   1568: astore 9
          //   1570: aload 9
          //   1572: invokevirtual 213	org/apache/cordova/api/PluginResult:getStatus	()I
          //   1575: getstatic 216	org/apache/cordova/api/PluginResult$Status:OK	Lorg/apache/cordova/api/PluginResult$Status;
          //   1578: invokevirtual 219	org/apache/cordova/api/PluginResult$Status:ordinal	()I
          //   1581: if_icmpeq +14 -> 1595
          //   1584: aload 4
          //   1586: ifnull +9 -> 1595
          //   1589: aload 4
          //   1591: invokevirtual 222	java/io/File:delete	()Z
          //   1594: pop
          //   1595: aload_0
          //   1596: getfield 30	org/apache/cordova/FileTransfer$4:val$context	Lorg/apache/cordova/FileTransfer$RequestContext;
          //   1599: aload 9
          //   1601: invokevirtual 226	org/apache/cordova/FileTransfer$RequestContext:sendPluginResult	(Lorg/apache/cordova/api/PluginResult;)V
          //   1604: aload 5
          //   1606: athrow
          //   1607: astore 7
          //   1609: aload 6
          //   1611: monitorexit
          //   1612: aload 7
          //   1614: athrow
          //   1615: astore 53
          //   1617: aload 52
          //   1619: monitorexit
          //   1620: aload 53
          //   1622: athrow
          //   1623: astore 42
          //   1625: aload 41
          //   1627: monitorexit
          //   1628: aload 42
          //   1630: athrow
          //   1631: astore 31
          //   1633: aload 30
          //   1635: monitorexit
          //   1636: aload 31
          //   1638: athrow
          //   1639: astore 19
          //   1641: aload 18
          //   1643: monitorexit
          //   1644: aload 19
          //   1646: athrow
          //   1647: astore 86
          //   1649: aload 85
          //   1651: monitorexit
          //   1652: aload 86
          //   1654: athrow
          //   1655: astore 79
          //   1657: aload 78
          //   1659: monitorexit
          //   1660: aload 79
          //   1662: athrow
          //   1663: astore 65
          //   1665: aconst_null
          //   1666: astore 66
          //   1668: goto -1129 -> 539
          //   1671: aload 77
          //   1673: astore 21
          //   1675: goto -756 -> 919
          //   1678: aload 17
          //   1680: astore 21
          //   1682: goto -248 -> 1434
          //   1685: aload 29
          //   1687: astore 21
          //   1689: goto -433 -> 1256
          //   1692: aload 40
          //   1694: astore 21
          //   1696: goto -599 -> 1097
          //   1699: aload 51
          //   1701: astore 21
          //   1703: goto -999 -> 704
          // Local variable table:
          //   start	length	slot	name	signature
          //   0	1706	0	this	4
          //   12	1540	1	localObject1	Object
          //   14	1505	2	localHostnameVerifier	HostnameVerifier
          //   16	1509	3	localSSLSocketFactory	SSLSocketFactory
          //   18	1572	4	localFile	File
          //   1468	137	5	localObject2	Object
          //   1607	6	7	localObject3	Object
          //   1529	71	9	localPluginResult1	PluginResult
          //   1538	24	11	localStatus1	PluginResult.Status
          //   1555	9	12	localJSONObject1	JSONObject
          //   1514	9	13	localHttpsURLConnection1	HttpsURLConnection
          //   1290	27	14	localThrowable	java.lang.Throwable
          //   1307	23	15	localJSONObject2	JSONObject
          //   1334	345	17	localPluginResult2	PluginResult
          //   1639	6	19	localObject4	Object
          //   300	1402	21	localObject5	Object
          //   370	1094	22	localRequestContext1	FileTransfer.RequestContext
          //   1402	24	24	localStatus2	PluginResult.Status
          //   1419	9	25	localJSONObject3	JSONObject
          //   1380	9	26	localHttpsURLConnection2	HttpsURLConnection
          //   1131	10	27	localJSONException	JSONException
          //   1156	530	29	localPluginResult3	PluginResult
          //   1631	6	31	localObject6	Object
          //   1224	24	34	localStatus3	PluginResult.Status
          //   1241	9	35	localJSONObject4	JSONObject
          //   1202	9	36	localHttpsURLConnection3	HttpsURLConnection
          //   953	27	37	localIOException	IOException
          //   970	23	38	localJSONObject5	JSONObject
          //   997	696	40	localPluginResult4	PluginResult
          //   1623	6	42	localObject7	Object
          //   1065	24	45	localStatus4	PluginResult.Status
          //   1082	9	46	localJSONObject6	JSONObject
          //   1043	9	47	localHttpsURLConnection4	HttpsURLConnection
          //   560	27	48	localFileNotFoundException	FileNotFoundException
          //   577	23	49	localJSONObject7	JSONObject
          //   604	1096	51	localPluginResult5	PluginResult
          //   1615	6	53	localObject8	Object
          //   672	24	56	localStatus5	PluginResult.Status
          //   689	9	57	localJSONObject8	JSONObject
          //   650	9	58	localHttpsURLConnection5	HttpsURLConnection
          //   425	3	60	localURLConnection	URLConnection
          //   102	11	61	str	String
          //   156	350	63	localFileProgressResult	FileProgressResult
          //   182	573	64	localInputStream	InputStream
          //   533	25	65	localObject9	Object
          //   1663	1	65	localObject10	Object
          //   537	1130	66	localFileOutputStream1	java.io.FileOutputStream
          //   199	561	67	localFileOutputStream2	java.io.FileOutputStream
          //   738	6	69	localObject11	Object
          //   454	22	70	arrayOfByte	byte[]
          //   457	37	71	l	long
          //   466	20	73	i	int
          //   513	13	74	localPluginResult6	PluginResult
          //   805	10	76	localJSONObject9	JSONObject
          //   819	853	77	localPluginResult7	PluginResult
          //   1655	6	79	localObject12	Object
          //   887	24	82	localStatus6	PluginResult.Status
          //   904	9	83	localJSONObject10	JSONObject
          //   865	9	84	localHttpsURLConnection6	HttpsURLConnection
          //   1647	6	86	localObject13	Object
          //   309	24	89	localStatus7	PluginResult.Status
          //   326	9	90	localJSONObject11	JSONObject
          //   285	9	91	localHttpsURLConnection7	HttpsURLConnection
          //   390	23	92	localHttpsURLConnection8	HttpsURLConnection
          // Exception table:
          //   from	to	target	type
          //   201	210	533	finally
          //   449	456	533	finally
          //   459	468	533	finally
          //   473	483	533	finally
          //   491	530	533	finally
          //   743	746	533	finally
          //   20	76	560	java/io/FileNotFoundException
          //   76	92	560	java/io/FileNotFoundException
          //   92	104	560	java/io/FileNotFoundException
          //   109	117	560	java/io/FileNotFoundException
          //   117	181	560	java/io/FileNotFoundException
          //   223	241	560	java/io/FileNotFoundException
          //   380	412	560	java/io/FileNotFoundException
          //   418	427	560	java/io/FileNotFoundException
          //   539	560	560	java/io/FileNotFoundException
          //   746	821	560	java/io/FileNotFoundException
          //   210	223	738	finally
          //   437	449	738	finally
          //   740	743	738	finally
          //   20	76	953	java/io/IOException
          //   76	92	953	java/io/IOException
          //   92	104	953	java/io/IOException
          //   109	117	953	java/io/IOException
          //   117	181	953	java/io/IOException
          //   223	241	953	java/io/IOException
          //   380	412	953	java/io/IOException
          //   418	427	953	java/io/IOException
          //   539	560	953	java/io/IOException
          //   746	821	953	java/io/IOException
          //   20	76	1131	org/json/JSONException
          //   76	92	1131	org/json/JSONException
          //   92	104	1131	org/json/JSONException
          //   109	117	1131	org/json/JSONException
          //   117	181	1131	org/json/JSONException
          //   223	241	1131	org/json/JSONException
          //   380	412	1131	org/json/JSONException
          //   418	427	1131	org/json/JSONException
          //   539	560	1131	org/json/JSONException
          //   746	821	1131	org/json/JSONException
          //   20	76	1290	java/lang/Throwable
          //   76	92	1290	java/lang/Throwable
          //   92	104	1290	java/lang/Throwable
          //   109	117	1290	java/lang/Throwable
          //   117	181	1290	java/lang/Throwable
          //   223	241	1290	java/lang/Throwable
          //   380	412	1290	java/lang/Throwable
          //   418	427	1290	java/lang/Throwable
          //   539	560	1290	java/lang/Throwable
          //   746	821	1290	java/lang/Throwable
          //   20	76	1468	finally
          //   76	92	1468	finally
          //   92	104	1468	finally
          //   109	117	1468	finally
          //   117	181	1468	finally
          //   223	241	1468	finally
          //   380	412	1468	finally
          //   418	427	1468	finally
          //   539	560	1468	finally
          //   562	606	1468	finally
          //   746	821	1468	finally
          //   955	999	1468	finally
          //   1133	1158	1468	finally
          //   1292	1336	1468	finally
          //   1478	1492	1607	finally
          //   1609	1612	1607	finally
          //   614	628	1615	finally
          //   1617	1620	1615	finally
          //   1007	1021	1623	finally
          //   1625	1628	1623	finally
          //   1166	1180	1631	finally
          //   1633	1636	1631	finally
          //   1344	1358	1639	finally
          //   1641	1644	1639	finally
          //   249	263	1647	finally
          //   1649	1652	1647	finally
          //   829	843	1655	finally
          //   1657	1660	1655	finally
          //   184	201	1663	finally
        }
      });
      return;
    }
  }
  
  private static String getArgument(JSONArray paramJSONArray, int paramInt, String paramString)
  {
    String str = paramString;
    if (paramJSONArray.length() >= paramInt)
    {
      str = paramJSONArray.optString(paramInt);
      if ((str == null) || ("null".equals(str))) {
        str = paramString;
      }
    }
    return str;
  }
  
  private File getFileFromPath(String paramString)
    throws FileNotFoundException
  {
    if (paramString.startsWith("file://")) {}
    for (File localFile = new File(paramString.substring("file://".length())); localFile.getParent() == null; localFile = new File(paramString)) {
      throw new FileNotFoundException();
    }
    return localFile;
  }
  
  private static InputStream getInputStream(URLConnection paramURLConnection)
    throws IOException
  {
    if (Build.VERSION.SDK_INT < 11) {
      return new DoneHandlerInputStream(paramURLConnection.getInputStream());
    }
    return paramURLConnection.getInputStream();
  }
  
  private InputStream getPathFromUri(String paramString)
    throws FileNotFoundException
  {
    if (paramString.startsWith("content:"))
    {
      Uri localUri = Uri.parse(paramString);
      return this.cordova.getActivity().getContentResolver().openInputStream(localUri);
    }
    if (paramString.startsWith("file://"))
    {
      int i = paramString.indexOf("?");
      if (i == -1) {
        return new FileInputStream(paramString.substring(7));
      }
      return new FileInputStream(paramString.substring(7, i));
    }
    return new FileInputStream(paramString);
  }
  
  private static void safeClose(Closeable paramCloseable)
  {
    if (paramCloseable != null) {}
    try
    {
      paramCloseable.close();
      return;
    }
    catch (IOException localIOException) {}
  }
  
  private static SSLSocketFactory trustAllHosts(HttpsURLConnection paramHttpsURLConnection)
  {
    SSLSocketFactory localSSLSocketFactory = paramHttpsURLConnection.getSSLSocketFactory();
    try
    {
      SSLContext localSSLContext = SSLContext.getInstance("TLS");
      localSSLContext.init(null, trustAllCerts, new SecureRandom());
      paramHttpsURLConnection.setSSLSocketFactory(localSSLContext.getSocketFactory());
      return localSSLSocketFactory;
    }
    catch (Exception localException)
    {
      Log.e("FileTransfer", localException.getMessage(), localException);
    }
    return localSSLSocketFactory;
  }
  
  /* Error */
  private void upload(final String paramString1, final String paramString2, JSONArray paramJSONArray, CallbackContext paramCallbackContext)
    throws JSONException
  {
    // Byte code:
    //   0: ldc 25
    //   2: new 220	java/lang/StringBuilder
    //   5: dup
    //   6: invokespecial 221	java/lang/StringBuilder:<init>	()V
    //   9: ldc_w 405
    //   12: invokevirtual 227	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   15: aload_1
    //   16: invokevirtual 227	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   19: ldc 229
    //   21: invokevirtual 227	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   24: aload_2
    //   25: invokevirtual 227	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   28: invokevirtual 232	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   31: invokestatic 236	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   34: pop
    //   35: aload_3
    //   36: iconst_2
    //   37: ldc_w 407
    //   40: invokestatic 409	org/apache/cordova/FileTransfer:getArgument	(Lorg/json/JSONArray;ILjava/lang/String;)Ljava/lang/String;
    //   43: astore 6
    //   45: aload_3
    //   46: iconst_3
    //   47: ldc_w 411
    //   50: invokestatic 409	org/apache/cordova/FileTransfer:getArgument	(Lorg/json/JSONArray;ILjava/lang/String;)Ljava/lang/String;
    //   53: astore 7
    //   55: aload_3
    //   56: iconst_4
    //   57: ldc_w 413
    //   60: invokestatic 409	org/apache/cordova/FileTransfer:getArgument	(Lorg/json/JSONArray;ILjava/lang/String;)Ljava/lang/String;
    //   63: astore 8
    //   65: aload_3
    //   66: iconst_5
    //   67: invokevirtual 417	org/json/JSONArray:optJSONObject	(I)Lorg/json/JSONObject;
    //   70: ifnonnull +388 -> 458
    //   73: new 179	org/json/JSONObject
    //   76: dup
    //   77: invokespecial 180	org/json/JSONObject:<init>	()V
    //   80: astore 9
    //   82: aload_3
    //   83: bipush 6
    //   85: invokevirtual 242	org/json/JSONArray:optBoolean	(I)Z
    //   88: istore 10
    //   90: aload_3
    //   91: bipush 7
    //   93: invokevirtual 242	org/json/JSONArray:optBoolean	(I)Z
    //   96: ifne +12 -> 108
    //   99: aload_3
    //   100: bipush 7
    //   102: invokevirtual 420	org/json/JSONArray:isNull	(I)Z
    //   105: ifeq +363 -> 468
    //   108: iconst_1
    //   109: istore 11
    //   111: aload_3
    //   112: bipush 8
    //   114: invokevirtual 417	org/json/JSONArray:optJSONObject	(I)Lorg/json/JSONObject;
    //   117: ifnonnull +357 -> 474
    //   120: aload 9
    //   122: ldc_w 422
    //   125: invokevirtual 425	org/json/JSONObject:optJSONObject	(Ljava/lang/String;)Lorg/json/JSONObject;
    //   128: astore 12
    //   130: aload_3
    //   131: bipush 9
    //   133: invokevirtual 246	org/json/JSONArray:getString	(I)Ljava/lang/String;
    //   136: astore 13
    //   138: ldc 25
    //   140: new 220	java/lang/StringBuilder
    //   143: dup
    //   144: invokespecial 221	java/lang/StringBuilder:<init>	()V
    //   147: ldc_w 427
    //   150: invokevirtual 227	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   153: aload 6
    //   155: invokevirtual 227	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   158: invokevirtual 232	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   161: invokestatic 236	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   164: pop
    //   165: ldc 25
    //   167: new 220	java/lang/StringBuilder
    //   170: dup
    //   171: invokespecial 221	java/lang/StringBuilder:<init>	()V
    //   174: ldc_w 429
    //   177: invokevirtual 227	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   180: aload 7
    //   182: invokevirtual 227	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   185: invokevirtual 232	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   188: invokestatic 236	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   191: pop
    //   192: ldc 25
    //   194: new 220	java/lang/StringBuilder
    //   197: dup
    //   198: invokespecial 221	java/lang/StringBuilder:<init>	()V
    //   201: ldc_w 431
    //   204: invokevirtual 227	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   207: aload 8
    //   209: invokevirtual 227	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   212: invokevirtual 232	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   215: invokestatic 236	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   218: pop
    //   219: ldc 25
    //   221: new 220	java/lang/StringBuilder
    //   224: dup
    //   225: invokespecial 221	java/lang/StringBuilder:<init>	()V
    //   228: ldc_w 433
    //   231: invokevirtual 227	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   234: aload 9
    //   236: invokevirtual 436	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   239: invokevirtual 232	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   242: invokestatic 236	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   245: pop
    //   246: ldc 25
    //   248: new 220	java/lang/StringBuilder
    //   251: dup
    //   252: invokespecial 221	java/lang/StringBuilder:<init>	()V
    //   255: ldc_w 438
    //   258: invokevirtual 227	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   261: iload 10
    //   263: invokevirtual 441	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   266: invokevirtual 232	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   269: invokestatic 236	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   272: pop
    //   273: ldc 25
    //   275: new 220	java/lang/StringBuilder
    //   278: dup
    //   279: invokespecial 221	java/lang/StringBuilder:<init>	()V
    //   282: ldc_w 443
    //   285: invokevirtual 227	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   288: iload 11
    //   290: invokevirtual 441	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   293: invokevirtual 232	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   296: invokestatic 236	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   299: pop
    //   300: ldc 25
    //   302: new 220	java/lang/StringBuilder
    //   305: dup
    //   306: invokespecial 221	java/lang/StringBuilder:<init>	()V
    //   309: ldc_w 445
    //   312: invokevirtual 227	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   315: aload 12
    //   317: invokevirtual 436	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   320: invokevirtual 232	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   323: invokestatic 236	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   326: pop
    //   327: ldc 25
    //   329: new 220	java/lang/StringBuilder
    //   332: dup
    //   333: invokespecial 221	java/lang/StringBuilder:<init>	()V
    //   336: ldc_w 447
    //   339: invokevirtual 227	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   342: aload 13
    //   344: invokevirtual 227	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   347: invokevirtual 232	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   350: invokestatic 236	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   353: pop
    //   354: new 248	java/net/URL
    //   357: dup
    //   358: aload_2
    //   359: invokespecial 250	java/net/URL:<init>	(Ljava/lang/String;)V
    //   362: astore 22
    //   364: aload 22
    //   366: invokevirtual 253	java/net/URL:getProtocol	()Ljava/lang/String;
    //   369: ldc 255
    //   371: invokevirtual 261	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   374: istore 23
    //   376: new 70	org/apache/cordova/FileTransfer$RequestContext
    //   379: dup
    //   380: aload_1
    //   381: aload_2
    //   382: aload 4
    //   384: invokespecial 283	org/apache/cordova/FileTransfer$RequestContext:<init>	(Ljava/lang/String;Ljava/lang/String;Lorg/apache/cordova/api/CallbackContext;)V
    //   387: astore 24
    //   389: getstatic 49	org/apache/cordova/FileTransfer:activeRequests	Ljava/util/HashMap;
    //   392: astore 25
    //   394: aload 25
    //   396: monitorenter
    //   397: getstatic 49	org/apache/cordova/FileTransfer:activeRequests	Ljava/util/HashMap;
    //   400: aload 13
    //   402: aload 24
    //   404: invokevirtual 286	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   407: pop
    //   408: aload 25
    //   410: monitorexit
    //   411: aload_0
    //   412: getfield 119	org/apache/cordova/FileTransfer:cordova	Lorg/apache/cordova/api/CordovaInterface;
    //   415: invokeinterface 125 1 0
    //   420: new 449	org/apache/cordova/FileTransfer$1
    //   423: dup
    //   424: aload_0
    //   425: aload 24
    //   427: iload 23
    //   429: iload 10
    //   431: aload 22
    //   433: aload_2
    //   434: aload 12
    //   436: aload 9
    //   438: aload 6
    //   440: aload 7
    //   442: aload 8
    //   444: aload_1
    //   445: iload 11
    //   447: aload 13
    //   449: invokespecial 452	org/apache/cordova/FileTransfer$1:<init>	(Lorg/apache/cordova/FileTransfer;Lorg/apache/cordova/FileTransfer$RequestContext;ZZLjava/net/URL;Ljava/lang/String;Lorg/json/JSONObject;Lorg/json/JSONObject;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLjava/lang/String;)V
    //   452: invokeinterface 136 2 0
    //   457: return
    //   458: aload_3
    //   459: iconst_5
    //   460: invokevirtual 417	org/json/JSONArray:optJSONObject	(I)Lorg/json/JSONObject;
    //   463: astore 9
    //   465: goto -383 -> 82
    //   468: iconst_0
    //   469: istore 11
    //   471: goto -360 -> 111
    //   474: aload_3
    //   475: bipush 8
    //   477: invokevirtual 417	org/json/JSONArray:optJSONObject	(I)Lorg/json/JSONObject;
    //   480: astore 12
    //   482: goto -352 -> 130
    //   485: astore 28
    //   487: getstatic 38	org/apache/cordova/FileTransfer:INVALID_URL_ERR	I
    //   490: aload_1
    //   491: aload_2
    //   492: iconst_0
    //   493: invokestatic 92	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   496: invokestatic 96	org/apache/cordova/FileTransfer:createFileTransferError	(ILjava/lang/String;Ljava/lang/String;Ljava/lang/Integer;)Lorg/json/JSONObject;
    //   499: astore 29
    //   501: ldc 25
    //   503: aload 29
    //   505: invokevirtual 280	org/json/JSONObject:toString	()Ljava/lang/String;
    //   508: aload 28
    //   510: invokestatic 203	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   513: pop
    //   514: aload 4
    //   516: new 98	org/apache/cordova/api/PluginResult
    //   519: dup
    //   520: getstatic 276	org/apache/cordova/api/PluginResult$Status:IO_EXCEPTION	Lorg/apache/cordova/api/PluginResult$Status;
    //   523: aload 29
    //   525: invokespecial 107	org/apache/cordova/api/PluginResult:<init>	(Lorg/apache/cordova/api/PluginResult$Status;Lorg/json/JSONObject;)V
    //   528: invokevirtual 279	org/apache/cordova/api/CallbackContext:sendPluginResult	(Lorg/apache/cordova/api/PluginResult;)V
    //   531: return
    //   532: astore 26
    //   534: aload 25
    //   536: monitorexit
    //   537: aload 26
    //   539: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	540	0	this	FileTransfer
    //   0	540	1	paramString1	String
    //   0	540	2	paramString2	String
    //   0	540	3	paramJSONArray	JSONArray
    //   0	540	4	paramCallbackContext	CallbackContext
    //   43	396	6	str1	String
    //   53	388	7	str2	String
    //   63	380	8	str3	String
    //   80	384	9	localJSONObject1	JSONObject
    //   88	342	10	bool1	boolean
    //   109	361	11	bool2	boolean
    //   128	353	12	localJSONObject2	JSONObject
    //   136	312	13	str4	String
    //   362	70	22	localURL	URL
    //   374	54	23	bool3	boolean
    //   387	39	24	localRequestContext	RequestContext
    //   532	6	26	localObject	Object
    //   485	24	28	localMalformedURLException	MalformedURLException
    //   499	25	29	localJSONObject3	JSONObject
    // Exception table:
    //   from	to	target	type
    //   354	364	485	java/net/MalformedURLException
    //   397	411	532	finally
    //   534	537	532	finally
  }
  
  public boolean execute(String paramString, JSONArray paramJSONArray, CallbackContext paramCallbackContext)
    throws JSONException
  {
    if ((paramString.equals("upload")) || (paramString.equals("download")))
    {
      String str1 = paramJSONArray.getString(0);
      String str2 = paramJSONArray.getString(1);
      if (paramString.equals("upload")) {
        try
        {
          upload(URLDecoder.decode(str1, "UTF-8"), str2, paramJSONArray, paramCallbackContext);
          return true;
        }
        catch (UnsupportedEncodingException localUnsupportedEncodingException)
        {
          paramCallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.MALFORMED_URL_EXCEPTION, "UTF-8 error."));
          return true;
        }
      }
      download(str1, str2, paramJSONArray, paramCallbackContext);
      return true;
    }
    if (paramString.equals("abort"))
    {
      abort(paramJSONArray.getString(0));
      paramCallbackContext.success();
      return true;
    }
    return false;
  }
  
  private static final class DoneHandlerInputStream
    extends FilterInputStream
  {
    private boolean done;
    
    public DoneHandlerInputStream(InputStream paramInputStream)
    {
      super();
    }
    
    public int read()
      throws IOException
    {
      int i;
      if (this.done)
      {
        i = -1;
        if (i != -1) {
          break label31;
        }
      }
      label31:
      for (boolean bool = true;; bool = false)
      {
        this.done = bool;
        return i;
        i = super.read();
        break;
      }
    }
    
    public int read(byte[] paramArrayOfByte)
      throws IOException
    {
      int i;
      if (this.done)
      {
        i = -1;
        if (i != -1) {
          break label32;
        }
      }
      label32:
      for (boolean bool = true;; bool = false)
      {
        this.done = bool;
        return i;
        i = super.read(paramArrayOfByte);
        break;
      }
    }
    
    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      int i;
      if (this.done)
      {
        i = -1;
        if (i != -1) {
          break label40;
        }
      }
      label40:
      for (boolean bool = true;; bool = false)
      {
        this.done = bool;
        return i;
        i = super.read(paramArrayOfByte, paramInt1, paramInt2);
        break;
      }
    }
  }
  
  private static final class RequestContext
  {
    boolean aborted;
    CallbackContext callbackContext;
    InputStream currentInputStream;
    OutputStream currentOutputStream;
    String source;
    String target;
    File targetFile;
    
    RequestContext(String paramString1, String paramString2, CallbackContext paramCallbackContext)
    {
      this.source = paramString1;
      this.target = paramString2;
      this.callbackContext = paramCallbackContext;
    }
    
    void sendPluginResult(PluginResult paramPluginResult)
    {
      try
      {
        if (!this.aborted) {
          this.callbackContext.sendPluginResult(paramPluginResult);
        }
        return;
      }
      finally {}
    }
  }
}


/* Location:           C:\Users\Monitor\Videos\app\classes_dex2jar.jar
 * Qualified Name:     org.apache.cordova.FileTransfer
 * JD-Core Version:    0.7.0.1
 */