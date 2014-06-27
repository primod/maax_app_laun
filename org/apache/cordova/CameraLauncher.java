package org.apache.cordova;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.Images.Media;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.codec.binary.Base64;
import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaInterface;
import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.LOG;
import org.apache.cordova.api.PluginResult;
import org.apache.cordova.api.PluginResult.Status;
import org.json.JSONArray;
import org.json.JSONException;

public class CameraLauncher
  extends CordovaPlugin
  implements MediaScannerConnection.MediaScannerConnectionClient
{
  private static final int ALLMEDIA = 2;
  private static final int CAMERA = 1;
  private static final int DATA_URL = 0;
  private static final int FILE_URI = 1;
  private static final String GET_All = "Get All";
  private static final String GET_PICTURE = "Get Picture";
  private static final String GET_VIDEO = "Get Video";
  private static final int JPEG = 0;
  private static final String LOG_TAG = "CameraLauncher";
  private static final int NATIVE_URI = 2;
  private static final int PHOTOLIBRARY = 0;
  private static final int PICTURE = 0;
  private static final int PNG = 1;
  private static final int SAVEDPHOTOALBUM = 2;
  private static final int VIDEO = 1;
  public CallbackContext callbackContext;
  private MediaScannerConnection conn;
  private boolean correctOrientation;
  private int encodingType;
  private Uri imageUri;
  private int mQuality;
  private int mediaType;
  private int numPics;
  private boolean saveToPhotoAlbum;
  private Uri scanMe;
  private int targetHeight;
  private int targetWidth;
  
  public static int calculateSampleSize(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (paramInt1 / paramInt2 > paramInt3 / paramInt4) {
      return paramInt1 / paramInt3;
    }
    return paramInt2 / paramInt4;
  }
  
  private void checkForDuplicateImage(int paramInt)
  {
    int i = 1;
    Uri localUri1 = whichContentStore();
    Cursor localCursor = queryImgDB(localUri1);
    int j = localCursor.getCount();
    if ((paramInt == 1) && (this.saveToPhotoAlbum)) {
      i = 2;
    }
    if (j - this.numPics == i)
    {
      localCursor.moveToLast();
      int k = Integer.valueOf(localCursor.getString(localCursor.getColumnIndex("_id"))).intValue();
      if (i == 2) {
        k--;
      }
      Uri localUri2 = Uri.parse(localUri1 + "/" + k);
      this.cordova.getActivity().getContentResolver().delete(localUri2, null, null);
    }
  }
  
  private void cleanup(int paramInt, Uri paramUri1, Uri paramUri2, Bitmap paramBitmap)
  {
    if (paramBitmap != null) {
      paramBitmap.recycle();
    }
    new File(FileUtils.stripFileProtocol(paramUri1.toString())).delete();
    checkForDuplicateImage(paramInt);
    if ((this.saveToPhotoAlbum) && (paramUri2 != null)) {
      scanForGallery(paramUri2);
    }
    System.gc();
  }
  
  private File createCaptureFile(int paramInt)
  {
    if (paramInt == 0) {
      return new File(DirectoryManager.getTempDirectoryPath(this.cordova.getActivity()), ".Pic.jpg");
    }
    if (paramInt == 1) {
      return new File(DirectoryManager.getTempDirectoryPath(this.cordova.getActivity()), ".Pic.png");
    }
    throw new IllegalArgumentException("Invalid Encoding Type: " + paramInt);
  }
  
  private Bitmap getRotatedBitmap(int paramInt, Bitmap paramBitmap, ExifHelper paramExifHelper)
  {
    Matrix localMatrix = new Matrix();
    if (paramInt == 180) {
      localMatrix.setRotate(paramInt);
    }
    for (;;)
    {
      Bitmap localBitmap = Bitmap.createBitmap(paramBitmap, 0, 0, paramBitmap.getWidth(), paramBitmap.getHeight(), localMatrix, true);
      paramExifHelper.resetOrientation();
      return localBitmap;
      localMatrix.setRotate(paramInt, paramBitmap.getWidth() / 2.0F, paramBitmap.getHeight() / 2.0F);
    }
  }
  
  private Bitmap getScaledBitmap(String paramString)
  {
    Bitmap localBitmap1;
    if ((this.targetWidth <= 0) && (this.targetHeight <= 0)) {
      localBitmap1 = BitmapFactory.decodeFile(paramString);
    }
    int[] arrayOfInt;
    Bitmap localBitmap2;
    do
    {
      BitmapFactory.Options localOptions;
      int j;
      do
      {
        int i;
        do
        {
          return localBitmap1;
          localOptions = new BitmapFactory.Options();
          localOptions.inJustDecodeBounds = true;
          BitmapFactory.decodeFile(paramString, localOptions);
          i = localOptions.outWidth;
          localBitmap1 = null;
        } while (i == 0);
        j = localOptions.outHeight;
        localBitmap1 = null;
      } while (j == 0);
      arrayOfInt = calculateAspectRatio(localOptions.outWidth, localOptions.outHeight);
      localOptions.inJustDecodeBounds = false;
      localOptions.inSampleSize = calculateSampleSize(localOptions.outWidth, localOptions.outHeight, this.targetWidth, this.targetHeight);
      localBitmap2 = BitmapFactory.decodeFile(paramString, localOptions);
      localBitmap1 = null;
    } while (localBitmap2 == null);
    return Bitmap.createScaledBitmap(localBitmap2, arrayOfInt[0], arrayOfInt[1], true);
  }
  
  private Uri getUriFromMediaStore()
  {
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("mime_type", "image/jpeg");
    try
    {
      Uri localUri2 = this.cordova.getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, localContentValues);
      return localUri2;
    }
    catch (UnsupportedOperationException localUnsupportedOperationException1)
    {
      LOG.d("CameraLauncher", "Can't write to external media storage.");
      try
      {
        Uri localUri1 = this.cordova.getActivity().getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, localContentValues);
        return localUri1;
      }
      catch (UnsupportedOperationException localUnsupportedOperationException2)
      {
        LOG.d("CameraLauncher", "Can't write to internal media storage.");
      }
    }
    return null;
  }
  
  private Cursor queryImgDB(Uri paramUri)
  {
    return this.cordova.getActivity().getContentResolver().query(paramUri, new String[] { "_id" }, null, null, null);
  }
  
  private void scanForGallery(Uri paramUri)
  {
    this.scanMe = paramUri;
    if (this.conn != null) {
      this.conn.disconnect();
    }
    this.conn = new MediaScannerConnection(this.cordova.getActivity().getApplicationContext(), this);
    this.conn.connect();
  }
  
  private Uri whichContentStore()
  {
    if (Environment.getExternalStorageState().equals("mounted")) {
      return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }
    return MediaStore.Images.Media.INTERNAL_CONTENT_URI;
  }
  
  private void writeUncompressedImage(Uri paramUri)
    throws FileNotFoundException, IOException
  {
    FileInputStream localFileInputStream = new FileInputStream(FileUtils.stripFileProtocol(this.imageUri.toString()));
    OutputStream localOutputStream = this.cordova.getActivity().getContentResolver().openOutputStream(paramUri);
    byte[] arrayOfByte = new byte[4096];
    for (;;)
    {
      int i = localFileInputStream.read(arrayOfByte);
      if (i == -1) {
        break;
      }
      localOutputStream.write(arrayOfByte, 0, i);
    }
    localOutputStream.flush();
    localOutputStream.close();
    localFileInputStream.close();
  }
  
  public int[] calculateAspectRatio(int paramInt1, int paramInt2)
  {
    int i = this.targetWidth;
    int j = this.targetHeight;
    if ((i <= 0) && (j <= 0))
    {
      i = paramInt1;
      j = paramInt2;
    }
    for (;;)
    {
      return new int[] { i, j };
      if ((i > 0) && (j <= 0))
      {
        j = i * paramInt2 / paramInt1;
      }
      else if ((i <= 0) && (j > 0))
      {
        i = j * paramInt1 / paramInt2;
      }
      else
      {
        double d1 = i / j;
        double d2 = paramInt1 / paramInt2;
        if (d2 > d1) {
          j = i * paramInt2 / paramInt1;
        } else if (d2 < d1) {
          i = j * paramInt1 / paramInt2;
        }
      }
    }
  }
  
  public boolean execute(String paramString, JSONArray paramJSONArray, CallbackContext paramCallbackContext)
    throws JSONException
  {
    this.callbackContext = paramCallbackContext;
    if (paramString.equals("takePicture"))
    {
      this.saveToPhotoAlbum = false;
      this.targetHeight = 0;
      this.targetWidth = 0;
      this.encodingType = 0;
      this.mediaType = 0;
      this.mQuality = 80;
      this.mQuality = paramJSONArray.getInt(0);
      int i = paramJSONArray.getInt(1);
      int j = paramJSONArray.getInt(2);
      this.targetWidth = paramJSONArray.getInt(3);
      this.targetHeight = paramJSONArray.getInt(4);
      this.encodingType = paramJSONArray.getInt(5);
      this.mediaType = paramJSONArray.getInt(6);
      this.correctOrientation = paramJSONArray.getBoolean(8);
      this.saveToPhotoAlbum = paramJSONArray.getBoolean(9);
      if (this.targetWidth < 1) {
        this.targetWidth = -1;
      }
      if (this.targetHeight < 1) {
        this.targetHeight = -1;
      }
      if (j == 1) {
        takePicture(i, this.encodingType);
      }
      for (;;)
      {
        PluginResult localPluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
        localPluginResult.setKeepCallback(true);
        paramCallbackContext.sendPluginResult(localPluginResult);
        return true;
        if ((j == 0) || (j == 2)) {
          getImage(j, i);
        }
      }
    }
    return false;
  }
  
  public void failPicture(String paramString)
  {
    this.callbackContext.error(paramString);
  }
  
  public void getImage(int paramInt1, int paramInt2)
  {
    Intent localIntent = new Intent();
    String str = "Get Picture";
    if (this.mediaType == 0) {
      localIntent.setType("image/*");
    }
    for (;;)
    {
      localIntent.setAction("android.intent.action.GET_CONTENT");
      localIntent.addCategory("android.intent.category.OPENABLE");
      if (this.cordova != null) {
        this.cordova.startActivityForResult(this, Intent.createChooser(localIntent, new String(str)), 1 + (paramInt2 + 16 * (paramInt1 + 1)));
      }
      return;
      if (this.mediaType == 1)
      {
        localIntent.setType("video/*");
        str = "Get Video";
      }
      else if (this.mediaType == 2)
      {
        localIntent.setType("*/*");
        str = "Get All";
      }
    }
  }
  
  /* Error */
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    // Byte code:
    //   0: iconst_m1
    //   1: iload_1
    //   2: bipush 16
    //   4: idiv
    //   5: iadd
    //   6: istore 4
    //   8: iconst_m1
    //   9: iload_1
    //   10: bipush 16
    //   12: irem
    //   13: iadd
    //   14: istore 5
    //   16: iload 4
    //   18: iconst_1
    //   19: if_icmpne +525 -> 544
    //   22: iload_2
    //   23: iconst_m1
    //   24: if_icmpne +500 -> 524
    //   27: new 219	org/apache/cordova/ExifHelper
    //   30: dup
    //   31: invokespecial 459	org/apache/cordova/ExifHelper:<init>	()V
    //   34: astore 26
    //   36: aload_0
    //   37: getfield 375	org/apache/cordova/CameraLauncher:encodingType	I
    //   40: istore 38
    //   42: iconst_0
    //   43: istore 29
    //   45: iload 38
    //   47: ifne +55 -> 102
    //   50: aload 26
    //   52: new 101	java/lang/StringBuilder
    //   55: dup
    //   56: invokespecial 102	java/lang/StringBuilder:<init>	()V
    //   59: aload_0
    //   60: getfield 128	org/apache/cordova/CameraLauncher:cordova	Lorg/apache/cordova/api/CordovaInterface;
    //   63: invokeinterface 134 1 0
    //   68: invokestatic 186	org/apache/cordova/DirectoryManager:getTempDirectoryPath	(Landroid/content/Context;)Ljava/lang/String;
    //   71: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   74: ldc_w 461
    //   77: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   80: invokevirtual 118	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   83: invokevirtual 464	org/apache/cordova/ExifHelper:createInFile	(Ljava/lang/String;)V
    //   86: aload 26
    //   88: invokevirtual 467	org/apache/cordova/ExifHelper:readExifData	()V
    //   91: aload 26
    //   93: invokevirtual 470	org/apache/cordova/ExifHelper:getOrientation	()I
    //   96: istore 39
    //   98: iload 39
    //   100: istore 29
    //   102: aconst_null
    //   103: astore 30
    //   105: aconst_null
    //   106: astore 31
    //   108: iload 5
    //   110: ifne +1032 -> 1142
    //   113: aload_0
    //   114: aload_0
    //   115: getfield 343	org/apache/cordova/CameraLauncher:imageUri	Landroid/net/Uri;
    //   118: invokevirtual 156	android/net/Uri:toString	()Ljava/lang/String;
    //   121: invokestatic 162	org/apache/cordova/FileUtils:stripFileProtocol	(Ljava/lang/String;)Ljava/lang/String;
    //   124: invokespecial 472	org/apache/cordova/CameraLauncher:getScaledBitmap	(Ljava/lang/String;)Landroid/graphics/Bitmap;
    //   127: astore 30
    //   129: aload 30
    //   131: ifnonnull +18 -> 149
    //   134: aload_3
    //   135: invokevirtual 476	android/content/Intent:getExtras	()Landroid/os/Bundle;
    //   138: ldc_w 478
    //   141: invokevirtual 484	android/os/Bundle:get	(Ljava/lang/String;)Ljava/lang/Object;
    //   144: checkcast 150	android/graphics/Bitmap
    //   147: astore 30
    //   149: aload 30
    //   151: ifnonnull +48 -> 199
    //   154: ldc 28
    //   156: ldc_w 486
    //   159: invokestatic 491	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   162: pop
    //   163: aload_0
    //   164: ldc_w 493
    //   167: invokevirtual 495	org/apache/cordova/CameraLauncher:failPicture	(Ljava/lang/String;)V
    //   170: return
    //   171: astore 27
    //   173: aload 27
    //   175: invokevirtual 498	java/io/IOException:printStackTrace	()V
    //   178: iconst_0
    //   179: istore 29
    //   181: goto -79 -> 102
    //   184: astore 28
    //   186: aload 28
    //   188: invokevirtual 498	java/io/IOException:printStackTrace	()V
    //   191: aload_0
    //   192: ldc_w 500
    //   195: invokevirtual 495	org/apache/cordova/CameraLauncher:failPicture	(Ljava/lang/String;)V
    //   198: return
    //   199: iload 29
    //   201: ifeq +22 -> 223
    //   204: aload_0
    //   205: getfield 391	org/apache/cordova/CameraLauncher:correctOrientation	Z
    //   208: ifeq +15 -> 223
    //   211: aload_0
    //   212: iload 29
    //   214: aload 30
    //   216: aload 26
    //   218: invokespecial 502	org/apache/cordova/CameraLauncher:getRotatedBitmap	(ILandroid/graphics/Bitmap;Lorg/apache/cordova/ExifHelper;)Landroid/graphics/Bitmap;
    //   221: astore 30
    //   223: aload_0
    //   224: aload 30
    //   226: invokevirtual 506	org/apache/cordova/CameraLauncher:processPicture	(Landroid/graphics/Bitmap;)V
    //   229: aload_0
    //   230: iconst_0
    //   231: invokespecial 169	org/apache/cordova/CameraLauncher:checkForDuplicateImage	(I)V
    //   234: aload_0
    //   235: iconst_1
    //   236: aload_0
    //   237: getfield 343	org/apache/cordova/CameraLauncher:imageUri	Landroid/net/Uri;
    //   240: aload 31
    //   242: aload 30
    //   244: invokespecial 508	org/apache/cordova/CameraLauncher:cleanup	(ILandroid/net/Uri;Landroid/net/Uri;Landroid/graphics/Bitmap;)V
    //   247: return
    //   248: aload_0
    //   249: getfield 74	org/apache/cordova/CameraLauncher:saveToPhotoAlbum	Z
    //   252: ifne +126 -> 378
    //   255: new 155	java/io/File
    //   258: dup
    //   259: aload_0
    //   260: getfield 128	org/apache/cordova/CameraLauncher:cordova	Lorg/apache/cordova/api/CordovaInterface;
    //   263: invokeinterface 134 1 0
    //   268: invokestatic 186	org/apache/cordova/DirectoryManager:getTempDirectoryPath	(Landroid/content/Context;)Ljava/lang/String;
    //   271: new 101	java/lang/StringBuilder
    //   274: dup
    //   275: invokespecial 102	java/lang/StringBuilder:<init>	()V
    //   278: invokestatic 512	java/lang/System:currentTimeMillis	()J
    //   281: invokevirtual 515	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   284: ldc_w 517
    //   287: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   290: invokevirtual 118	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   293: invokespecial 191	java/io/File:<init>	(Ljava/lang/String;Ljava/lang/String;)V
    //   296: invokestatic 521	android/net/Uri:fromFile	(Ljava/io/File;)Landroid/net/Uri;
    //   299: astore 31
    //   301: aload 31
    //   303: ifnonnull +10 -> 313
    //   306: aload_0
    //   307: ldc_w 523
    //   310: invokevirtual 495	org/apache/cordova/CameraLauncher:failPicture	(Ljava/lang/String;)V
    //   313: aload_0
    //   314: getfield 231	org/apache/cordova/CameraLauncher:targetHeight	I
    //   317: iconst_m1
    //   318: if_icmpne +69 -> 387
    //   321: aload_0
    //   322: getfield 229	org/apache/cordova/CameraLauncher:targetWidth	I
    //   325: iconst_m1
    //   326: if_icmpne +61 -> 387
    //   329: aload_0
    //   330: getfield 379	org/apache/cordova/CameraLauncher:mQuality	I
    //   333: bipush 100
    //   335: if_icmpne +52 -> 387
    //   338: aload_0
    //   339: getfield 391	org/apache/cordova/CameraLauncher:correctOrientation	Z
    //   342: ifne +45 -> 387
    //   345: aload_0
    //   346: aload 31
    //   348: invokespecial 525	org/apache/cordova/CameraLauncher:writeUncompressedImage	(Landroid/net/Uri;)V
    //   351: aload_0
    //   352: getfield 371	org/apache/cordova/CameraLauncher:callbackContext	Lorg/apache/cordova/api/CallbackContext;
    //   355: aload 31
    //   357: invokevirtual 156	android/net/Uri:toString	()Ljava/lang/String;
    //   360: invokevirtual 528	org/apache/cordova/api/CallbackContext:success	(Ljava/lang/String;)V
    //   363: aload_0
    //   364: getfield 371	org/apache/cordova/CameraLauncher:callbackContext	Lorg/apache/cordova/api/CallbackContext;
    //   367: aload 31
    //   369: invokevirtual 156	android/net/Uri:toString	()Ljava/lang/String;
    //   372: invokevirtual 528	org/apache/cordova/api/CallbackContext:success	(Ljava/lang/String;)V
    //   375: goto -141 -> 234
    //   378: aload_0
    //   379: invokespecial 530	org/apache/cordova/CameraLauncher:getUriFromMediaStore	()Landroid/net/Uri;
    //   382: astore 31
    //   384: goto -83 -> 301
    //   387: aload_0
    //   388: aload_0
    //   389: getfield 343	org/apache/cordova/CameraLauncher:imageUri	Landroid/net/Uri;
    //   392: invokevirtual 156	android/net/Uri:toString	()Ljava/lang/String;
    //   395: invokestatic 162	org/apache/cordova/FileUtils:stripFileProtocol	(Ljava/lang/String;)Ljava/lang/String;
    //   398: invokespecial 472	org/apache/cordova/CameraLauncher:getScaledBitmap	(Ljava/lang/String;)Landroid/graphics/Bitmap;
    //   401: astore 30
    //   403: iload 29
    //   405: ifeq +22 -> 427
    //   408: aload_0
    //   409: getfield 391	org/apache/cordova/CameraLauncher:correctOrientation	Z
    //   412: ifeq +15 -> 427
    //   415: aload_0
    //   416: iload 29
    //   418: aload 30
    //   420: aload 26
    //   422: invokespecial 502	org/apache/cordova/CameraLauncher:getRotatedBitmap	(ILandroid/graphics/Bitmap;Lorg/apache/cordova/ExifHelper;)Landroid/graphics/Bitmap;
    //   425: astore 30
    //   427: aload_0
    //   428: getfield 128	org/apache/cordova/CameraLauncher:cordova	Lorg/apache/cordova/api/CordovaInterface;
    //   431: invokeinterface 134 1 0
    //   436: invokevirtual 140	android/app/Activity:getContentResolver	()Landroid/content/ContentResolver;
    //   439: aload 31
    //   441: invokevirtual 348	android/content/ContentResolver:openOutputStream	(Landroid/net/Uri;)Ljava/io/OutputStream;
    //   444: astore 32
    //   446: aload 30
    //   448: getstatic 535	android/graphics/Bitmap$CompressFormat:JPEG	Landroid/graphics/Bitmap$CompressFormat;
    //   451: aload_0
    //   452: getfield 379	org/apache/cordova/CameraLauncher:mQuality	I
    //   455: aload 32
    //   457: invokevirtual 539	android/graphics/Bitmap:compress	(Landroid/graphics/Bitmap$CompressFormat;ILjava/io/OutputStream;)Z
    //   460: pop
    //   461: aload 32
    //   463: invokevirtual 364	java/io/OutputStream:close	()V
    //   466: aload_0
    //   467: getfield 375	org/apache/cordova/CameraLauncher:encodingType	I
    //   470: ifne -107 -> 363
    //   473: aload_0
    //   474: getfield 74	org/apache/cordova/CameraLauncher:saveToPhotoAlbum	Z
    //   477: ifeq +33 -> 510
    //   480: aload_0
    //   481: getfield 128	org/apache/cordova/CameraLauncher:cordova	Lorg/apache/cordova/api/CordovaInterface;
    //   484: astore 36
    //   486: aload 31
    //   488: aload 36
    //   490: invokestatic 543	org/apache/cordova/FileUtils:getRealPathFromURI	(Landroid/net/Uri;Lorg/apache/cordova/api/CordovaInterface;)Ljava/lang/String;
    //   493: astore 35
    //   495: aload 26
    //   497: aload 35
    //   499: invokevirtual 546	org/apache/cordova/ExifHelper:createOutFile	(Ljava/lang/String;)V
    //   502: aload 26
    //   504: invokevirtual 549	org/apache/cordova/ExifHelper:writeExifData	()V
    //   507: goto -144 -> 363
    //   510: aload 31
    //   512: invokevirtual 552	android/net/Uri:getPath	()Ljava/lang/String;
    //   515: astore 34
    //   517: aload 34
    //   519: astore 35
    //   521: goto -26 -> 495
    //   524: iload_2
    //   525: ifne +11 -> 536
    //   528: aload_0
    //   529: ldc_w 554
    //   532: invokevirtual 495	org/apache/cordova/CameraLauncher:failPicture	(Ljava/lang/String;)V
    //   535: return
    //   536: aload_0
    //   537: ldc_w 556
    //   540: invokevirtual 495	org/apache/cordova/CameraLauncher:failPicture	(Ljava/lang/String;)V
    //   543: return
    //   544: iload 4
    //   546: ifeq +9 -> 555
    //   549: iload 4
    //   551: iconst_2
    //   552: if_icmpne +589 -> 1141
    //   555: iload_2
    //   556: iconst_m1
    //   557: if_icmpne +565 -> 1122
    //   560: aload_3
    //   561: invokevirtual 559	android/content/Intent:getData	()Landroid/net/Uri;
    //   564: astore 6
    //   566: aload_0
    //   567: getfield 377	org/apache/cordova/CameraLauncher:mediaType	I
    //   570: ifeq +16 -> 586
    //   573: aload_0
    //   574: getfield 371	org/apache/cordova/CameraLauncher:callbackContext	Lorg/apache/cordova/api/CallbackContext;
    //   577: aload 6
    //   579: invokevirtual 156	android/net/Uri:toString	()Ljava/lang/String;
    //   582: invokevirtual 528	org/apache/cordova/api/CallbackContext:success	(Ljava/lang/String;)V
    //   585: return
    //   586: aload_0
    //   587: getfield 231	org/apache/cordova/CameraLauncher:targetHeight	I
    //   590: iconst_m1
    //   591: if_icmpne +43 -> 634
    //   594: aload_0
    //   595: getfield 229	org/apache/cordova/CameraLauncher:targetWidth	I
    //   598: iconst_m1
    //   599: if_icmpne +35 -> 634
    //   602: iload 5
    //   604: iconst_1
    //   605: if_icmpeq +9 -> 614
    //   608: iload 5
    //   610: iconst_2
    //   611: if_icmpne +23 -> 634
    //   614: aload_0
    //   615: getfield 391	org/apache/cordova/CameraLauncher:correctOrientation	Z
    //   618: ifne +16 -> 634
    //   621: aload_0
    //   622: getfield 371	org/apache/cordova/CameraLauncher:callbackContext	Lorg/apache/cordova/api/CallbackContext;
    //   625: aload 6
    //   627: invokevirtual 156	android/net/Uri:toString	()Ljava/lang/String;
    //   630: invokevirtual 528	org/apache/cordova/api/CallbackContext:success	(Ljava/lang/String;)V
    //   633: return
    //   634: aload 6
    //   636: aload_0
    //   637: getfield 128	org/apache/cordova/CameraLauncher:cordova	Lorg/apache/cordova/api/CordovaInterface;
    //   640: invokestatic 543	org/apache/cordova/FileUtils:getRealPathFromURI	(Landroid/net/Uri;Lorg/apache/cordova/api/CordovaInterface;)Ljava/lang/String;
    //   643: astore 7
    //   645: aload 7
    //   647: invokestatic 562	org/apache/cordova/FileUtils:getMimeType	(Ljava/lang/String;)Ljava/lang/String;
    //   650: astore 8
    //   652: aload 7
    //   654: ifnull +30 -> 684
    //   657: aload 8
    //   659: ifnull +25 -> 684
    //   662: aload 8
    //   664: ldc_w 274
    //   667: invokevirtual 566	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   670: ifne +31 -> 701
    //   673: aload 8
    //   675: ldc_w 568
    //   678: invokevirtual 566	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   681: ifne +20 -> 701
    //   684: ldc 28
    //   686: ldc_w 486
    //   689: invokestatic 491	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   692: pop
    //   693: aload_0
    //   694: ldc_w 570
    //   697: invokevirtual 495	org/apache/cordova/CameraLauncher:failPicture	(Ljava/lang/String;)V
    //   700: return
    //   701: aload_0
    //   702: aload 7
    //   704: invokespecial 472	org/apache/cordova/CameraLauncher:getScaledBitmap	(Ljava/lang/String;)Landroid/graphics/Bitmap;
    //   707: astore 10
    //   709: aload 10
    //   711: ifnonnull +20 -> 731
    //   714: ldc 28
    //   716: ldc_w 486
    //   719: invokestatic 491	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   722: pop
    //   723: aload_0
    //   724: ldc_w 493
    //   727: invokevirtual 495	org/apache/cordova/CameraLauncher:failPicture	(Ljava/lang/String;)V
    //   730: return
    //   731: aload_0
    //   732: getfield 391	org/apache/cordova/CameraLauncher:correctOrientation	Z
    //   735: ifeq +127 -> 862
    //   738: iconst_1
    //   739: anewarray 300	java/lang/String
    //   742: dup
    //   743: iconst_0
    //   744: ldc_w 572
    //   747: aastore
    //   748: astore 11
    //   750: aload_0
    //   751: getfield 128	org/apache/cordova/CameraLauncher:cordova	Lorg/apache/cordova/api/CordovaInterface;
    //   754: invokeinterface 134 1 0
    //   759: invokevirtual 140	android/app/Activity:getContentResolver	()Landroid/content/ContentResolver;
    //   762: aload_3
    //   763: invokevirtual 559	android/content/Intent:getData	()Landroid/net/Uri;
    //   766: aload 11
    //   768: aconst_null
    //   769: aconst_null
    //   770: aconst_null
    //   771: invokevirtual 304	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   774: astore 12
    //   776: iconst_0
    //   777: istore 13
    //   779: aload 12
    //   781: ifnull +29 -> 810
    //   784: aload 12
    //   786: iconst_0
    //   787: invokeinterface 575 2 0
    //   792: pop
    //   793: aload 12
    //   795: iconst_0
    //   796: invokeinterface 576 2 0
    //   801: istore 13
    //   803: aload 12
    //   805: invokeinterface 577 1 0
    //   810: iload 13
    //   812: ifeq +50 -> 862
    //   815: new 202	android/graphics/Matrix
    //   818: dup
    //   819: invokespecial 203	android/graphics/Matrix:<init>	()V
    //   822: astore 14
    //   824: aload 14
    //   826: iload 13
    //   828: i2f
    //   829: invokevirtual 207	android/graphics/Matrix:setRotate	(F)V
    //   832: aload 10
    //   834: invokevirtual 210	android/graphics/Bitmap:getWidth	()I
    //   837: istore 15
    //   839: aload 10
    //   841: invokevirtual 213	android/graphics/Bitmap:getHeight	()I
    //   844: istore 16
    //   846: aload 10
    //   848: iconst_0
    //   849: iconst_0
    //   850: iload 15
    //   852: iload 16
    //   854: aload 14
    //   856: iconst_1
    //   857: invokestatic 217	android/graphics/Bitmap:createBitmap	(Landroid/graphics/Bitmap;IIIILandroid/graphics/Matrix;Z)Landroid/graphics/Bitmap;
    //   860: astore 10
    //   862: iload 5
    //   864: ifne +23 -> 887
    //   867: aload_0
    //   868: aload 10
    //   870: invokevirtual 506	org/apache/cordova/CameraLauncher:processPicture	(Landroid/graphics/Bitmap;)V
    //   873: aload 10
    //   875: ifnull +8 -> 883
    //   878: aload 10
    //   880: invokevirtual 153	android/graphics/Bitmap:recycle	()V
    //   883: invokestatic 178	java/lang/System:gc	()V
    //   886: return
    //   887: iload 5
    //   889: iconst_1
    //   890: if_icmpeq +9 -> 899
    //   893: iload 5
    //   895: iconst_2
    //   896: if_icmpne -23 -> 873
    //   899: aload_0
    //   900: getfield 231	org/apache/cordova/CameraLauncher:targetHeight	I
    //   903: ifle +204 -> 1107
    //   906: aload_0
    //   907: getfield 229	org/apache/cordova/CameraLauncher:targetWidth	I
    //   910: ifle +197 -> 1107
    //   913: new 101	java/lang/StringBuilder
    //   916: dup
    //   917: invokespecial 102	java/lang/StringBuilder:<init>	()V
    //   920: aload_0
    //   921: getfield 128	org/apache/cordova/CameraLauncher:cordova	Lorg/apache/cordova/api/CordovaInterface;
    //   924: invokeinterface 134 1 0
    //   929: invokestatic 186	org/apache/cordova/DirectoryManager:getTempDirectoryPath	(Landroid/content/Context;)Ljava/lang/String;
    //   932: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   935: ldc_w 579
    //   938: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   941: invokevirtual 118	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   944: astore 18
    //   946: new 219	org/apache/cordova/ExifHelper
    //   949: dup
    //   950: invokespecial 459	org/apache/cordova/ExifHelper:<init>	()V
    //   953: astore 19
    //   955: aload_0
    //   956: getfield 375	org/apache/cordova/CameraLauncher:encodingType	I
    //   959: ifne +21 -> 980
    //   962: aload 19
    //   964: aload 18
    //   966: invokevirtual 464	org/apache/cordova/ExifHelper:createInFile	(Ljava/lang/String;)V
    //   969: aload 19
    //   971: invokevirtual 467	org/apache/cordova/ExifHelper:readExifData	()V
    //   974: aload 19
    //   976: invokevirtual 470	org/apache/cordova/ExifHelper:getOrientation	()I
    //   979: pop
    //   980: new 581	java/io/FileOutputStream
    //   983: dup
    //   984: aload 18
    //   986: invokespecial 582	java/io/FileOutputStream:<init>	(Ljava/lang/String;)V
    //   989: astore 21
    //   991: aload 10
    //   993: getstatic 535	android/graphics/Bitmap$CompressFormat:JPEG	Landroid/graphics/Bitmap$CompressFormat;
    //   996: aload_0
    //   997: getfield 379	org/apache/cordova/CameraLauncher:mQuality	I
    //   1000: aload 21
    //   1002: invokevirtual 539	android/graphics/Bitmap:compress	(Landroid/graphics/Bitmap$CompressFormat;ILjava/io/OutputStream;)Z
    //   1005: pop
    //   1006: aload 21
    //   1008: invokevirtual 364	java/io/OutputStream:close	()V
    //   1011: aload_0
    //   1012: getfield 375	org/apache/cordova/CameraLauncher:encodingType	I
    //   1015: ifne +22 -> 1037
    //   1018: aload 19
    //   1020: aload 6
    //   1022: aload_0
    //   1023: getfield 128	org/apache/cordova/CameraLauncher:cordova	Lorg/apache/cordova/api/CordovaInterface;
    //   1026: invokestatic 543	org/apache/cordova/FileUtils:getRealPathFromURI	(Landroid/net/Uri;Lorg/apache/cordova/api/CordovaInterface;)Ljava/lang/String;
    //   1029: invokevirtual 546	org/apache/cordova/ExifHelper:createOutFile	(Ljava/lang/String;)V
    //   1032: aload 19
    //   1034: invokevirtual 549	org/apache/cordova/ExifHelper:writeExifData	()V
    //   1037: aload_0
    //   1038: getfield 371	org/apache/cordova/CameraLauncher:callbackContext	Lorg/apache/cordova/api/CallbackContext;
    //   1041: new 101	java/lang/StringBuilder
    //   1044: dup
    //   1045: invokespecial 102	java/lang/StringBuilder:<init>	()V
    //   1048: ldc_w 584
    //   1051: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1054: aload 18
    //   1056: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1059: ldc_w 586
    //   1062: invokevirtual 111	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1065: invokestatic 512	java/lang/System:currentTimeMillis	()J
    //   1068: invokevirtual 515	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   1071: invokevirtual 118	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1074: invokevirtual 528	org/apache/cordova/api/CallbackContext:success	(Ljava/lang/String;)V
    //   1077: goto -204 -> 873
    //   1080: astore 17
    //   1082: aload 17
    //   1084: invokevirtual 587	java/lang/Exception:printStackTrace	()V
    //   1087: aload_0
    //   1088: ldc_w 589
    //   1091: invokevirtual 495	org/apache/cordova/CameraLauncher:failPicture	(Ljava/lang/String;)V
    //   1094: goto -221 -> 873
    //   1097: astore 20
    //   1099: aload 20
    //   1101: invokevirtual 498	java/io/IOException:printStackTrace	()V
    //   1104: goto -124 -> 980
    //   1107: aload_0
    //   1108: getfield 371	org/apache/cordova/CameraLauncher:callbackContext	Lorg/apache/cordova/api/CallbackContext;
    //   1111: aload 6
    //   1113: invokevirtual 156	android/net/Uri:toString	()Ljava/lang/String;
    //   1116: invokevirtual 528	org/apache/cordova/api/CallbackContext:success	(Ljava/lang/String;)V
    //   1119: goto -246 -> 873
    //   1122: iload_2
    //   1123: ifne +11 -> 1134
    //   1126: aload_0
    //   1127: ldc_w 591
    //   1130: invokevirtual 495	org/apache/cordova/CameraLauncher:failPicture	(Ljava/lang/String;)V
    //   1133: return
    //   1134: aload_0
    //   1135: ldc_w 593
    //   1138: invokevirtual 495	org/apache/cordova/CameraLauncher:failPicture	(Ljava/lang/String;)V
    //   1141: return
    //   1142: iload 5
    //   1144: iconst_1
    //   1145: if_icmpeq -897 -> 248
    //   1148: aconst_null
    //   1149: astore 30
    //   1151: aconst_null
    //   1152: astore 31
    //   1154: iload 5
    //   1156: iconst_2
    //   1157: if_icmpne -923 -> 234
    //   1160: goto -912 -> 248
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1163	0	this	CameraLauncher
    //   0	1163	1	paramInt1	int
    //   0	1163	2	paramInt2	int
    //   0	1163	3	paramIntent	Intent
    //   6	547	4	i	int
    //   14	1144	5	j	int
    //   564	548	6	localUri1	Uri
    //   643	60	7	str1	String
    //   650	24	8	str2	String
    //   707	285	10	localBitmap1	Bitmap
    //   748	19	11	arrayOfString	String[]
    //   774	30	12	localCursor	Cursor
    //   777	50	13	k	int
    //   822	33	14	localMatrix	Matrix
    //   837	14	15	m	int
    //   844	9	16	n	int
    //   1080	3	17	localException	Exception
    //   944	111	18	str3	String
    //   953	80	19	localExifHelper1	ExifHelper
    //   1097	3	20	localIOException1	IOException
    //   989	18	21	localFileOutputStream	java.io.FileOutputStream
    //   34	469	26	localExifHelper2	ExifHelper
    //   171	3	27	localIOException2	IOException
    //   184	3	28	localIOException3	IOException
    //   43	374	29	i1	int
    //   103	1047	30	localBitmap2	Bitmap
    //   106	1047	31	localUri2	Uri
    //   444	18	32	localOutputStream	OutputStream
    //   515	3	34	str4	String
    //   493	27	35	localObject	java.lang.Object
    //   484	5	36	localCordovaInterface	CordovaInterface
    //   40	6	38	i2	int
    //   96	3	39	i3	int
    // Exception table:
    //   from	to	target	type
    //   36	42	171	java/io/IOException
    //   50	98	171	java/io/IOException
    //   27	36	184	java/io/IOException
    //   113	129	184	java/io/IOException
    //   134	149	184	java/io/IOException
    //   154	170	184	java/io/IOException
    //   173	178	184	java/io/IOException
    //   204	223	184	java/io/IOException
    //   223	234	184	java/io/IOException
    //   234	247	184	java/io/IOException
    //   248	301	184	java/io/IOException
    //   306	313	184	java/io/IOException
    //   313	363	184	java/io/IOException
    //   363	375	184	java/io/IOException
    //   378	384	184	java/io/IOException
    //   387	403	184	java/io/IOException
    //   408	427	184	java/io/IOException
    //   427	495	184	java/io/IOException
    //   495	507	184	java/io/IOException
    //   510	517	184	java/io/IOException
    //   913	955	1080	java/lang/Exception
    //   955	980	1080	java/lang/Exception
    //   980	1037	1080	java/lang/Exception
    //   1037	1077	1080	java/lang/Exception
    //   1099	1104	1080	java/lang/Exception
    //   955	980	1097	java/io/IOException
  }
  
  public void onMediaScannerConnected()
  {
    try
    {
      this.conn.scanFile(this.scanMe.toString(), "image/*");
      return;
    }
    catch (IllegalStateException localIllegalStateException)
    {
      LOG.e("CameraLauncher", "Can't scan file in MediaScanner after taking picture");
    }
  }
  
  public void onScanCompleted(String paramString, Uri paramUri)
  {
    this.conn.disconnect();
  }
  
  public void processPicture(Bitmap paramBitmap)
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    try
    {
      if (paramBitmap.compress(Bitmap.CompressFormat.JPEG, this.mQuality, localByteArrayOutputStream))
      {
        String str = new String(Base64.encodeBase64(localByteArrayOutputStream.toByteArray()));
        this.callbackContext.success(str);
      }
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        failPicture("Error compressing image.");
      }
    }
  }
  
  public void takePicture(int paramInt1, int paramInt2)
  {
    this.numPics = queryImgDB(whichContentStore()).getCount();
    Intent localIntent = new Intent("android.media.action.IMAGE_CAPTURE");
    File localFile = createCaptureFile(paramInt2);
    localIntent.putExtra("output", Uri.fromFile(localFile));
    this.imageUri = Uri.fromFile(localFile);
    if (this.cordova != null) {
      this.cordova.startActivityForResult(this, localIntent, 1 + (paramInt1 + 32));
    }
  }
}


/* Location:           C:\Users\Monitor\Videos\app\classes_dex2jar.jar
 * Qualified Name:     org.apache.cordova.CameraLauncher
 * JD-Core Version:    0.7.0.1
 */