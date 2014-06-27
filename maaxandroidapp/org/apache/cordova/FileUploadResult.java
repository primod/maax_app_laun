package org.apache.cordova;

import org.json.JSONException;
import org.json.JSONObject;

public class FileUploadResult
{
  private long bytesSent = 0L;
  private String objectId = null;
  private String response = null;
  private int responseCode = -1;
  
  public long getBytesSent()
  {
    return this.bytesSent;
  }
  
  public String getObjectId()
  {
    return this.objectId;
  }
  
  public String getResponse()
  {
    return this.response;
  }
  
  public int getResponseCode()
  {
    return this.responseCode;
  }
  
  public void setBytesSent(long paramLong)
  {
    this.bytesSent = paramLong;
  }
  
  public void setObjectId(String paramString)
  {
    this.objectId = paramString;
  }
  
  public void setResponse(String paramString)
  {
    this.response = paramString;
  }
  
  public void setResponseCode(int paramInt)
  {
    this.responseCode = paramInt;
  }
  
  public JSONObject toJSONObject()
    throws JSONException
  {
    return new JSONObject("{bytesSent:" + this.bytesSent + ",responseCode:" + this.responseCode + ",response:" + JSONObject.quote(this.response) + ",objectId:" + JSONObject.quote(this.objectId) + "}");
  }
}


/* Location:           C:\Users\Monitor\Videos\app\classes_dex2jar.jar
 * Qualified Name:     org.apache.cordova.FileUploadResult
 * JD-Core Version:    0.7.0.1
 */