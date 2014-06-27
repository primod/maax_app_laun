package org.apache.cordova;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpHandler
{
  private HttpEntity getHttpEntity(String paramString)
  {
    try
    {
      HttpEntity localHttpEntity = new DefaultHttpClient().execute(new HttpGet(paramString)).getEntity();
      return localHttpEntity;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return null;
  }
  
  private void writeToDisk(HttpEntity paramHttpEntity, String paramString)
    throws IllegalStateException, IOException
  {
    String str = "/sdcard/" + paramString;
    InputStream localInputStream = paramHttpEntity.getContent();
    byte[] arrayOfByte = new byte[1024];
    FileOutputStream localFileOutputStream = new FileOutputStream(str);
    for (;;)
    {
      int i = localInputStream.read(arrayOfByte);
      if (i <= 0)
      {
        localFileOutputStream.flush();
        localFileOutputStream.close();
        return;
      }
      localFileOutputStream.write(arrayOfByte, 0, i);
    }
  }
  
  protected Boolean get(String paramString1, String paramString2)
  {
    HttpEntity localHttpEntity = getHttpEntity(paramString1);
    try
    {
      writeToDisk(localHttpEntity, paramString2);
      return Boolean.valueOf(false);
    }
    catch (Exception localException1)
    {
      try
      {
        localHttpEntity.consumeContent();
        return Boolean.valueOf(true);
      }
      catch (Exception localException2)
      {
        localException2.printStackTrace();
      }
      localException1 = localException1;
      localException1.printStackTrace();
      return Boolean.valueOf(false);
    }
  }
}


/* Location:           C:\Users\Monitor\Videos\app\classes_dex2jar.jar
 * Qualified Name:     org.apache.cordova.HttpHandler
 * JD-Core Version:    0.7.0.1
 */