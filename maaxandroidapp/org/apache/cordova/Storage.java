package org.apache.cordova;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import java.io.File;
import java.io.PrintStream;
import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaInterface;
import org.apache.cordova.api.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Storage
  extends CordovaPlugin
{
  private static final String ALTER = "alter";
  private static final String CREATE = "create";
  private static final String DROP = "drop";
  private static final String TRUNCATE = "truncate";
  String dbName = null;
  SQLiteDatabase myDb = null;
  String path = null;
  
  private boolean isDDL(String paramString)
  {
    String str = paramString.toLowerCase();
    return (str.startsWith("drop")) || (str.startsWith("create")) || (str.startsWith("alter")) || (str.startsWith("truncate"));
  }
  
  public boolean execute(String paramString, JSONArray paramJSONArray, CallbackContext paramCallbackContext)
    throws JSONException
  {
    boolean bool2;
    if (paramString.equals("openDatabase"))
    {
      openDatabase(paramJSONArray.getString(0), paramJSONArray.getString(1), paramJSONArray.getString(2), paramJSONArray.getLong(3));
      paramCallbackContext.success();
      bool2 = true;
    }
    boolean bool1;
    do
    {
      return bool2;
      bool1 = paramString.equals("executeSql");
      bool2 = false;
    } while (!bool1);
    String[] arrayOfString;
    if (paramJSONArray.isNull(1)) {
      arrayOfString = new String[0];
    }
    for (;;)
    {
      executeSql(paramJSONArray.getString(0), arrayOfString, paramJSONArray.getString(2));
      break;
      JSONArray localJSONArray = paramJSONArray.getJSONArray(1);
      int i = localJSONArray.length();
      arrayOfString = new String[i];
      for (int j = 0; j < i; j++) {
        arrayOfString[j] = localJSONArray.getString(j);
      }
    }
  }
  
  public void executeSql(String paramString1, String[] paramArrayOfString, String paramString2)
  {
    try
    {
      if (isDDL(paramString1))
      {
        this.myDb.execSQL(paramString1);
        this.webView.sendJavascript("cordova.require('cordova/plugin/android/storage').completeQuery('" + paramString2 + "', '');");
        return;
      }
      Cursor localCursor = this.myDb.rawQuery(paramString1, paramArrayOfString);
      processResults(localCursor, paramString2);
      localCursor.close();
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      localSQLiteException.printStackTrace();
      System.out.println("Storage.executeSql(): Error=" + localSQLiteException.getMessage());
      this.webView.sendJavascript("cordova.require('cordova/plugin/android/storage').failQuery('" + localSQLiteException.getMessage() + "','" + paramString2 + "');");
    }
  }
  
  public void onDestroy()
  {
    if (this.myDb != null)
    {
      this.myDb.close();
      this.myDb = null;
    }
  }
  
  public void onReset()
  {
    onDestroy();
  }
  
  public void openDatabase(String paramString1, String paramString2, String paramString3, long paramLong)
  {
    if (this.myDb != null) {
      this.myDb.close();
    }
    if (this.path == null) {
      this.path = this.cordova.getActivity().getApplicationContext().getDir("database", 0).getPath();
    }
    this.dbName = (this.path + File.separator + paramString1 + ".db");
    File localFile1 = new File(this.path + File.pathSeparator + paramString1 + ".db");
    if (localFile1.exists())
    {
      File localFile2 = new File(this.path);
      File localFile3 = new File(this.dbName);
      localFile2.mkdirs();
      localFile1.renameTo(localFile3);
    }
    this.myDb = SQLiteDatabase.openOrCreateDatabase(this.dbName, null);
  }
  
  public void processResults(Cursor paramCursor, String paramString)
  {
    String str = "[]";
    if (paramCursor.moveToFirst())
    {
      JSONArray localJSONArray = new JSONArray();
      int i = paramCursor.getColumnCount();
      do
      {
        JSONObject localJSONObject = new JSONObject();
        int j = 0;
        for (;;)
        {
          if (j < i) {}
          try
          {
            localJSONObject.put(paramCursor.getColumnName(j), paramCursor.getString(j));
            j++;
          }
          catch (JSONException localJSONException)
          {
            for (;;)
            {
              localJSONException.printStackTrace();
            }
          }
        }
        localJSONArray.put(localJSONObject);
      } while (paramCursor.moveToNext());
      str = localJSONArray.toString();
    }
    this.webView.sendJavascript("cordova.require('cordova/plugin/android/storage').completeQuery('" + paramString + "', " + str + ");");
  }
}


/* Location:           C:\Users\Monitor\Videos\app\classes_dex2jar.jar
 * Qualified Name:     org.apache.cordova.Storage
 * JD-Core Version:    0.7.0.1
 */