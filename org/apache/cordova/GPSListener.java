package org.apache.cordova;

import android.location.LocationManager;

public class GPSListener
  extends CordovaLocationListener
{
  public GPSListener(LocationManager paramLocationManager, GeoBroker paramGeoBroker)
  {
    super(paramLocationManager, paramGeoBroker, "[Cordova GPSListener]");
  }
  
  protected void start()
  {
    if (!this.running)
    {
      if (this.locationManager.getProvider("gps") != null)
      {
        this.running = true;
        this.locationManager.requestLocationUpdates("gps", 60000L, 0.0F, this);
      }
    }
    else {
      return;
    }
    fail(CordovaLocationListener.POSITION_UNAVAILABLE, "GPS provider is not available.");
  }
}


/* Location:           C:\Users\Monitor\Videos\app\classes_dex2jar.jar
 * Qualified Name:     org.apache.cordova.GPSListener
 * JD-Core Version:    0.7.0.1
 */