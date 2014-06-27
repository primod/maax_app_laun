package org.apache.cordova;

import android.os.Bundle;

public class StandAlone
  extends DroidGap
{
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    super.loadUrl("file:///android_asset/www/index.html");
  }
}


/* Location:           C:\Users\Monitor\Videos\app\classes_dex2jar.jar
 * Qualified Name:     org.apache.cordova.StandAlone
 * JD-Core Version:    0.7.0.1
 */