package org.apache.cordova;

import android.location.LocationManager;

public class NetworkListener
  extends CordovaLocationListener
{
  public NetworkListener(LocationManager paramLocationManager, GeoBroker paramGeoBroker)
  {
    super(paramLocationManager, paramGeoBroker, "[Cordova NetworkListener]");
  }
}


/* Location:           C:\Users\Monitor\Videos\app\classes_dex2jar.jar
 * Qualified Name:     org.apache.cordova.NetworkListener
 * JD-Core Version:    0.7.0.1
 */