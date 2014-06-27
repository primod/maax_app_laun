package org.apache.cordova;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaInterface;
import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.LOG;
import org.apache.cordova.api.PluginResult;
import org.apache.cordova.api.PluginResult.Status;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Capture
  extends CordovaPlugin
{
  private static final String AUDIO_3GPP = "audio/3gpp";
  private static final int CAPTURE_AUDIO = 0;
  private static final int CAPTURE_IMAGE = 1;
  private static final int CAPTURE_INTERNAL_ERR = 0;
  private static final int CAPTURE_NO_MEDIA_FILES = 3;
  private static final int CAPTURE_VIDEO = 2;
  private static final String IMAGE_JPEG = "image/jpeg";
  private static final String LOG_TAG = "Capture";
  private static final String VIDEO_3GPP = "video/3gpp";
  private static final String VIDEO_MP4 = "video/mp4";
  private CallbackContext callbackContext;
  private double duration;
  private long limit;
  private int numPics;
  private JSONArray results;
  
  private void captureAudio()
  {
    Intent localIntent = new Intent("android.provider.MediaStore.RECORD_SOUND");
    this.cordova.startActivityForResult(this, localIntent, 0);
  }
  
  private void captureImage()
  {
    this.numPics = queryImgDB(whichContentStore()).getCount();
    Intent localIntent = new Intent("android.media.action.IMAGE_CAPTURE");
    localIntent.putExtra("output", Uri.fromFile(new File(DirectoryManager.getTempDirectoryPath(this.cordova.getActivity()), "Capture.jpg")));
    this.cordova.startActivityForResult(this, localIntent, 1);
  }
  
  private void captureVideo(double paramDouble)
  {
    Intent localIntent = new Intent("android.media.action.VIDEO_CAPTURE");
    this.cordova.startActivityForResult(this, localIntent, 2);
  }
  
  private void checkForDuplicateImage()
  {
    Uri localUri1 = whichContentStore();
    Cursor localCursor = queryImgDB(localUri1);
    if (localCursor.getCount() - this.numPics == 2)
    {
      localCursor.moveToLast();
      int i = -1 + Integer.valueOf(localCursor.getString(localCursor.getColumnIndex("_id"))).intValue();
      Uri localUri2 = Uri.parse(localUri1 + "/" + i);
      this.cordova.getActivity().getContentResolver().delete(localUri2, null, null);
    }
  }
  
  private JSONObject createErrorObject(int paramInt, String paramString)
  {
    JSONObject localJSONObject = new JSONObject();
    try
    {
      localJSONObject.put("code", paramInt);
      localJSONObject.put("message", paramString);
      return localJSONObject;
    }
    catch (JSONException localJSONException) {}
    return localJSONObject;
  }
  
  private JSONObject createMediaFile(Uri paramUri)
  {
    File localFile = new File(FileUtils.getRealPathFromURI(paramUri, this.cordova));
    JSONObject localJSONObject = new JSONObject();
    for (;;)
    {
      try
      {
        localJSONObject.put("name", localFile.getName());
        localJSONObject.put("fullPath", "file://" + localFile.getAbsolutePath());
        if ((localFile.getAbsoluteFile().toString().endsWith(".3gp")) || (localFile.getAbsoluteFile().toString().endsWith(".3gpp")))
        {
          if (paramUri.toString().contains("/audio/"))
          {
            localJSONObject.put("type", "audio/3gpp");
            localJSONObject.put("lastModifiedDate", localFile.lastModified());
            localJSONObject.put("size", localFile.length());
            return localJSONObject;
          }
          localJSONObject.put("type", "video/3gpp");
          continue;
        }
        localJSONObject.put("type", FileUtils.getMimeType(localFile.getAbsolutePath()));
      }
      catch (JSONException localJSONException)
      {
        localJSONException.printStackTrace();
        return localJSONObject;
      }
    }
  }
  
  private JSONObject getAudioVideoData(String paramString, JSONObject paramJSONObject, boolean paramBoolean)
    throws JSONException
  {
    MediaPlayer localMediaPlayer = new MediaPlayer();
    try
    {
      localMediaPlayer.setDataSource(paramString);
      localMediaPlayer.prepare();
      paramJSONObject.put("duration", localMediaPlayer.getDuration() / 1000);
      if (paramBoolean)
      {
        paramJSONObject.put("height", localMediaPlayer.getVideoHeight());
        paramJSONObject.put("width", localMediaPlayer.getVideoWidth());
      }
      return paramJSONObject;
    }
    catch (IOException localIOException)
    {
      Log.d("Capture", "Error: loading video file");
    }
    return paramJSONObject;
  }
  
  private JSONObject getFormatData(String paramString1, String paramString2)
    throws JSONException
  {
    JSONObject localJSONObject = new JSONObject();
    localJSONObject.put("height", 0);
    localJSONObject.put("width", 0);
    localJSONObject.put("bitrate", 0);
    localJSONObject.put("duration", 0);
    localJSONObject.put("codecs", "");
    if ((paramString2 == null) || (paramString2.equals("")) || ("null".equals(paramString2))) {
      paramString2 = FileUtils.getMimeType(paramString1);
    }
    Log.d("Capture", "Mime type = " + paramString2);
    if ((paramString2.equals("image/jpeg")) || (paramString1.endsWith(".jpg"))) {
      localJSONObject = getImageData(paramString1, localJSONObject);
    }
    do
    {
      return localJSONObject;
      if (paramString2.endsWith("audio/3gpp")) {
        return getAudioVideoData(paramString1, localJSONObject, false);
      }
    } while ((!paramString2.equals("video/3gpp")) && (!paramString2.equals("video/mp4")));
    return getAudioVideoData(paramString1, localJSONObject, true);
  }
  
  private JSONObject getImageData(String paramString, JSONObject paramJSONObject)
    throws JSONException
  {
    BitmapFactory.Options localOptions = new BitmapFactory.Options();
    localOptions.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(FileUtils.stripFileProtocol(paramString), localOptions);
    paramJSONObject.put("height", localOptions.outHeight);
    paramJSONObject.put("width", localOptions.outWidth);
    return paramJSONObject;
  }
  
  private Cursor queryImgDB(Uri paramUri)
  {
    return this.cordova.getActivity().getContentResolver().query(paramUri, new String[] { "_id" }, null, null, null);
  }
  
  private Uri whichContentStore()
  {
    if (Environment.getExternalStorageState().equals("mounted")) {
      return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }
    return MediaStore.Images.Media.INTERNAL_CONTENT_URI;
  }
  
  public boolean execute(String paramString, JSONArray paramJSONArray, CallbackContext paramCallbackContext)
    throws JSONException
  {
    this.callbackContext = paramCallbackContext;
    this.limit = 1L;
    this.duration = 0.0D;
    this.results = new JSONArray();
    JSONObject localJSONObject = paramJSONArray.optJSONObject(0);
    if (localJSONObject != null)
    {
      this.limit = localJSONObject.optLong("limit", 1L);
      this.duration = localJSONObject.optDouble("duration", 0.0D);
    }
    if (paramString.equals("getFormatData"))
    {
      paramCallbackContext.success(getFormatData(paramJSONArray.getString(0), paramJSONArray.getString(1)));
      return true;
    }
    if (paramString.equals("captureAudio"))
    {
      captureAudio();
      return true;
    }
    if (paramString.equals("captureImage"))
    {
      captureImage();
      return true;
    }
    if (paramString.equals("captureVideo"))
    {
      captureVideo(this.duration);
      return true;
    }
    return false;
  }
  
  public void fail(JSONObject paramJSONObject)
  {
    this.callbackContext.error(paramJSONObject);
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if (paramInt2 == -1)
    {
      if (paramInt1 == 0)
      {
        Uri localUri4 = paramIntent.getData();
        this.results.put(createMediaFile(localUri4));
        if (this.results.length() >= this.limit) {
          this.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, this.results));
        }
      }
      ContentValues localContentValues;
      Object localObject;
      FileInputStream localFileInputStream;
      OutputStream localOutputStream;
      do
      {
        return;
        captureAudio();
        return;
        if (paramInt1 == 1)
        {
          try
          {
            localContentValues = new ContentValues();
            localContentValues.put("mime_type", "image/jpeg");
          }
          catch (IOException localIOException)
          {
            Uri localUri3;
            byte[] arrayOfByte;
            localIOException.printStackTrace();
            fail(createErrorObject(0, "Error capturing image."));
            return;
          }
          try
          {
            localUri3 = this.cordova.getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, localContentValues);
            localObject = localUri3;
          }
          catch (UnsupportedOperationException localUnsupportedOperationException1)
          {
            for (;;)
            {
              LOG.d("Capture", "Can't write to external media storage.");
              try
              {
                Uri localUri2 = this.cordova.getActivity().getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, localContentValues);
                localObject = localUri2;
              }
              catch (UnsupportedOperationException localUnsupportedOperationException2)
              {
                LOG.d("Capture", "Can't write to internal media storage.");
                fail(createErrorObject(0, "Error capturing image - no media storage found."));
                return;
              }
            }
            localOutputStream.flush();
            localOutputStream.close();
            localFileInputStream.close();
            this.results.put(createMediaFile((Uri)localObject));
            checkForDuplicateImage();
            if (this.results.length() < this.limit) {
              continue;
            }
            this.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, this.results));
            return;
            captureImage();
            return;
          }
          localFileInputStream = new FileInputStream(DirectoryManager.getTempDirectoryPath(this.cordova.getActivity()) + "/Capture.jpg");
          localOutputStream = this.cordova.getActivity().getContentResolver().openOutputStream((Uri)localObject);
          arrayOfByte = new byte[4096];
          for (;;)
          {
            int i = localFileInputStream.read(arrayOfByte);
            if (i == -1) {
              break;
            }
            localOutputStream.write(arrayOfByte, 0, i);
          }
        }
      } while (paramInt1 != 2);
      Uri localUri1 = paramIntent.getData();
      this.results.put(createMediaFile(localUri1));
      if (this.results.length() >= this.limit)
      {
        this.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, this.results));
        return;
      }
      captureVideo(this.duration);
      return;
    }
    if (paramInt2 == 0)
    {
      if (this.results.length() > 0)
      {
        this.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, this.results));
        return;
      }
      fail(createErrorObject(3, "Canceled."));
      return;
    }
    if (this.results.length() > 0)
    {
      this.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, this.results));
      return;
    }
    fail(createErrorObject(3, "Did not complete!"));
  }
}


/* Location:           C:\Users\Monitor\Videos\app\classes_dex2jar.jar
 * Qualified Name:     org.apache.cordova.Capture
 * JD-Core Version:    0.7.0.1
 */