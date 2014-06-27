package com.primod.maaxframe;

import android.os.Bundle;
import org.apache.cordova.Config;
import org.apache.cordova.DroidGap;

public class MaaxFrame
  extends DroidGap
{
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (0 != 0)
    {
      super.loadUrl(Config.getStartUrl(), 5000);
      return;
    }
    super.loadUrl(Config.getStartUrl());
  }
}


/* Location:           C:\Users\Monitor\Videos\app\classes_dex2jar.jar
 * Qualified Name:     com.primod.maaxframe.MaaxFrame
 * JD-Core Version:    0.7.0.1
 */