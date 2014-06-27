package org.apache.cordova;

import android.annotation.TargetApi;
import android.os.Build.VERSION;
import android.text.format.Time;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.PluginResult;
import org.apache.cordova.api.PluginResult.Status;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Globalization
  extends CordovaPlugin
{
  public static final String CURRENCY = "currency";
  public static final String CURRENCYCODE = "currencyCode";
  public static final String DATE = "date";
  public static final String DATESTRING = "dateString";
  public static final String DATETOSTRING = "dateToString";
  public static final String DAYS = "days";
  public static final String FORMATLENGTH = "formatLength";
  public static final String FULL = "full";
  public static final String GETCURRENCYPATTERN = "getCurrencyPattern";
  public static final String GETDATENAMES = "getDateNames";
  public static final String GETDATEPATTERN = "getDatePattern";
  public static final String GETFIRSTDAYOFWEEK = "getFirstDayOfWeek";
  public static final String GETLOCALENAME = "getLocaleName";
  public static final String GETNUMBERPATTERN = "getNumberPattern";
  public static final String GETPREFERREDLANGUAGE = "getPreferredLanguage";
  public static final String ISDAYLIGHTSAVINGSTIME = "isDayLightSavingsTime";
  public static final String ITEM = "item";
  public static final String LONG = "long";
  public static final String MEDIUM = "medium";
  public static final String MONTHS = "months";
  public static final String NARROW = "narrow";
  public static final String NUMBER = "number";
  public static final String NUMBERSTRING = "numberString";
  public static final String NUMBERTOSTRING = "numberToString";
  public static final String OPTIONS = "options";
  public static final String PERCENT = "percent";
  public static final String SELECTOR = "selector";
  public static final String STRINGTODATE = "stringToDate";
  public static final String STRINGTONUMBER = "stringToNumber";
  public static final String TIME = "time";
  public static final String TYPE = "type";
  public static final String WIDE = "wide";
  
  private JSONObject getCurrencyPattern(JSONArray paramJSONArray)
    throws GlobalizationError
  {
    JSONObject localJSONObject = new JSONObject();
    try
    {
      String str = paramJSONArray.getJSONObject(0).getString("currencyCode");
      DecimalFormat localDecimalFormat = (DecimalFormat)DecimalFormat.getCurrencyInstance(Locale.getDefault());
      Currency localCurrency = Currency.getInstance(str);
      localDecimalFormat.setCurrency(localCurrency);
      localJSONObject.put("pattern", localDecimalFormat.toPattern());
      localJSONObject.put("code", localCurrency.getCurrencyCode());
      localJSONObject.put("fraction", localDecimalFormat.getMinimumFractionDigits());
      localJSONObject.put("rounding", new Integer(0));
      localJSONObject.put("decimal", String.valueOf(localDecimalFormat.getDecimalFormatSymbols().getDecimalSeparator()));
      localJSONObject.put("grouping", String.valueOf(localDecimalFormat.getDecimalFormatSymbols().getGroupingSeparator()));
      return localJSONObject;
    }
    catch (Exception localException)
    {
      throw new GlobalizationError("FORMATTING_ERROR");
    }
  }
  
  @TargetApi(9)
  private JSONObject getDateNames(JSONArray paramJSONArray)
    throws GlobalizationError
  {
    JSONObject localJSONObject1 = new JSONObject();
    JSONArray localJSONArray = new JSONArray();
    ArrayList localArrayList = new ArrayList();
    final Map localMap;
    for (;;)
    {
      int m;
      try
      {
        int i = paramJSONArray.getJSONObject(0).length();
        int j = 0;
        int k = 0;
        if (i > 0)
        {
          boolean bool1 = ((JSONObject)paramJSONArray.getJSONObject(0).get("options")).isNull("type");
          k = 0;
          if (!bool1)
          {
            boolean bool2 = ((String)((JSONObject)paramJSONArray.getJSONObject(0).get("options")).get("type")).equalsIgnoreCase("narrow");
            k = 0;
            if (bool2) {
              k = 0 + 1;
            }
          }
          boolean bool3 = ((JSONObject)paramJSONArray.getJSONObject(0).get("options")).isNull("item");
          j = 0;
          if (!bool3)
          {
            boolean bool4 = ((String)((JSONObject)paramJSONArray.getJSONObject(0).get("options")).get("item")).equalsIgnoreCase("days");
            j = 0;
            if (bool4) {
              j = 0 + 10;
            }
          }
        }
        m = j + k;
        if (m == 1)
        {
          localMap = Calendar.getInstance().getDisplayNames(2, 1, Locale.getDefault());
          Iterator localIterator = localMap.keySet().iterator();
          if (!localIterator.hasNext()) {
            break;
          }
          localArrayList.add((String)localIterator.next());
          continue;
        }
        if (m != 10) {
          break label293;
        }
      }
      catch (Exception localException)
      {
        throw new GlobalizationError("UNKNOWN_ERROR");
      }
      localMap = Calendar.getInstance().getDisplayNames(7, 2, Locale.getDefault());
      continue;
      label293:
      if (m == 11) {
        localMap = Calendar.getInstance().getDisplayNames(7, 1, Locale.getDefault());
      } else {
        localMap = Calendar.getInstance().getDisplayNames(2, 2, Locale.getDefault());
      }
    }
    Collections.sort(localArrayList, new Comparator()
    {
      public int compare(String paramAnonymousString1, String paramAnonymousString2)
      {
        return ((Integer)localMap.get(paramAnonymousString1)).compareTo((Integer)localMap.get(paramAnonymousString2));
      }
    });
    for (int n = 0; n < localArrayList.size(); n++) {
      localJSONArray.put(localArrayList.get(n));
    }
    JSONObject localJSONObject2 = localJSONObject1.put("value", localJSONArray);
    return localJSONObject2;
  }
  
  /* Error */
  private JSONObject getDatePattern(JSONArray paramJSONArray)
    throws GlobalizationError
  {
    // Byte code:
    //   0: new 112	org/json/JSONObject
    //   3: dup
    //   4: invokespecial 113	org/json/JSONObject:<init>	()V
    //   7: astore_2
    //   8: aload_0
    //   9: getfield 289	org/apache/cordova/Globalization:cordova	Lorg/apache/cordova/api/CordovaInterface;
    //   12: invokeinterface 295 1 0
    //   17: invokestatic 301	android/text/format/DateFormat:getDateFormat	(Landroid/content/Context;)Ljava/text/DateFormat;
    //   20: checkcast 303	java/text/SimpleDateFormat
    //   23: astore 4
    //   25: aload_0
    //   26: getfield 289	org/apache/cordova/Globalization:cordova	Lorg/apache/cordova/api/CordovaInterface;
    //   29: invokeinterface 295 1 0
    //   34: invokestatic 306	android/text/format/DateFormat:getTimeFormat	(Landroid/content/Context;)Ljava/text/DateFormat;
    //   37: checkcast 303	java/text/SimpleDateFormat
    //   40: astore 5
    //   42: new 308	java/lang/StringBuilder
    //   45: dup
    //   46: invokespecial 309	java/lang/StringBuilder:<init>	()V
    //   49: aload 4
    //   51: invokevirtual 312	java/text/SimpleDateFormat:toLocalizedPattern	()Ljava/lang/String;
    //   54: invokevirtual 316	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   57: ldc_w 318
    //   60: invokevirtual 316	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   63: aload 5
    //   65: invokevirtual 312	java/text/SimpleDateFormat:toLocalizedPattern	()Ljava/lang/String;
    //   68: invokevirtual 316	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   71: invokevirtual 321	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   74: astore 6
    //   76: aload_1
    //   77: iconst_0
    //   78: invokevirtual 119	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   81: invokevirtual 214	org/json/JSONObject:length	()I
    //   84: iconst_1
    //   85: if_icmple +169 -> 254
    //   88: aload_1
    //   89: iconst_0
    //   90: invokevirtual 119	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   93: ldc 80
    //   95: invokevirtual 218	org/json/JSONObject:get	(Ljava/lang/String;)Ljava/lang/Object;
    //   98: checkcast 112	org/json/JSONObject
    //   101: ldc 26
    //   103: invokevirtual 222	org/json/JSONObject:isNull	(Ljava/lang/String;)Z
    //   106: ifne +53 -> 159
    //   109: aload_1
    //   110: iconst_0
    //   111: invokevirtual 119	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   114: ldc 80
    //   116: invokevirtual 218	org/json/JSONObject:get	(Ljava/lang/String;)Ljava/lang/Object;
    //   119: checkcast 112	org/json/JSONObject
    //   122: ldc 26
    //   124: invokevirtual 218	org/json/JSONObject:get	(Ljava/lang/String;)Ljava/lang/Object;
    //   127: checkcast 190	java/lang/String
    //   130: astore 14
    //   132: aload 14
    //   134: ldc 62
    //   136: invokevirtual 225	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   139: ifeq +193 -> 332
    //   142: aload_0
    //   143: getfield 289	org/apache/cordova/Globalization:cordova	Lorg/apache/cordova/api/CordovaInterface;
    //   146: invokeinterface 295 1 0
    //   151: invokestatic 324	android/text/format/DateFormat:getMediumDateFormat	(Landroid/content/Context;)Ljava/text/DateFormat;
    //   154: checkcast 303	java/text/SimpleDateFormat
    //   157: astore 4
    //   159: new 308	java/lang/StringBuilder
    //   162: dup
    //   163: invokespecial 309	java/lang/StringBuilder:<init>	()V
    //   166: aload 4
    //   168: invokevirtual 312	java/text/SimpleDateFormat:toLocalizedPattern	()Ljava/lang/String;
    //   171: invokevirtual 316	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   174: ldc_w 318
    //   177: invokevirtual 316	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   180: aload 5
    //   182: invokevirtual 312	java/text/SimpleDateFormat:toLocalizedPattern	()Ljava/lang/String;
    //   185: invokevirtual 316	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   188: invokevirtual 321	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   191: astore 6
    //   193: aload_1
    //   194: iconst_0
    //   195: invokevirtual 119	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   198: ldc 80
    //   200: invokevirtual 218	org/json/JSONObject:get	(Ljava/lang/String;)Ljava/lang/Object;
    //   203: checkcast 112	org/json/JSONObject
    //   206: ldc 86
    //   208: invokevirtual 222	org/json/JSONObject:isNull	(Ljava/lang/String;)Z
    //   211: ifne +43 -> 254
    //   214: aload_1
    //   215: iconst_0
    //   216: invokevirtual 119	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   219: ldc 80
    //   221: invokevirtual 218	org/json/JSONObject:get	(Ljava/lang/String;)Ljava/lang/Object;
    //   224: checkcast 112	org/json/JSONObject
    //   227: ldc 86
    //   229: invokevirtual 218	org/json/JSONObject:get	(Ljava/lang/String;)Ljava/lang/Object;
    //   232: checkcast 190	java/lang/String
    //   235: astore 12
    //   237: aload 12
    //   239: ldc 14
    //   241: invokevirtual 225	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   244: ifeq +128 -> 372
    //   247: aload 4
    //   249: invokevirtual 312	java/text/SimpleDateFormat:toLocalizedPattern	()Ljava/lang/String;
    //   252: astore 6
    //   254: invokestatic 329	android/text/format/Time:getCurrentTimezone	()Ljava/lang/String;
    //   257: invokestatic 335	java/util/TimeZone:getTimeZone	(Ljava/lang/String;)Ljava/util/TimeZone;
    //   260: astore 7
    //   262: aload_2
    //   263: ldc 147
    //   265: aload 6
    //   267: invokevirtual 155	org/json/JSONObject:put	(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;
    //   270: pop
    //   271: aload_2
    //   272: ldc_w 337
    //   275: aload 7
    //   277: aload 7
    //   279: invokestatic 230	java/util/Calendar:getInstance	()Ljava/util/Calendar;
    //   282: invokevirtual 341	java/util/Calendar:getTime	()Ljava/util/Date;
    //   285: invokevirtual 345	java/util/TimeZone:inDaylightTime	(Ljava/util/Date;)Z
    //   288: iconst_0
    //   289: invokevirtual 349	java/util/TimeZone:getDisplayName	(ZI)Ljava/lang/String;
    //   292: invokevirtual 155	org/json/JSONObject:put	(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;
    //   295: pop
    //   296: aload_2
    //   297: ldc_w 351
    //   300: aload 7
    //   302: invokevirtual 354	java/util/TimeZone:getRawOffset	()I
    //   305: sipush 1000
    //   308: idiv
    //   309: invokevirtual 169	org/json/JSONObject:put	(Ljava/lang/String;I)Lorg/json/JSONObject;
    //   312: pop
    //   313: aload_2
    //   314: ldc_w 356
    //   317: aload 7
    //   319: invokevirtual 359	java/util/TimeZone:getDSTSavings	()I
    //   322: sipush 1000
    //   325: idiv
    //   326: invokevirtual 169	org/json/JSONObject:put	(Ljava/lang/String;I)Lorg/json/JSONObject;
    //   329: pop
    //   330: aload_2
    //   331: areturn
    //   332: aload 14
    //   334: ldc 59
    //   336: invokevirtual 225	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   339: ifne +13 -> 352
    //   342: aload 14
    //   344: ldc 29
    //   346: invokevirtual 225	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   349: ifeq -190 -> 159
    //   352: aload_0
    //   353: getfield 289	org/apache/cordova/Globalization:cordova	Lorg/apache/cordova/api/CordovaInterface;
    //   356: invokeinterface 295 1 0
    //   361: invokestatic 362	android/text/format/DateFormat:getLongDateFormat	(Landroid/content/Context;)Ljava/text/DateFormat;
    //   364: checkcast 303	java/text/SimpleDateFormat
    //   367: astore 4
    //   369: goto -210 -> 159
    //   372: aload 12
    //   374: ldc 95
    //   376: invokevirtual 225	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   379: ifeq -125 -> 254
    //   382: aload 5
    //   384: invokevirtual 312	java/text/SimpleDateFormat:toLocalizedPattern	()Ljava/lang/String;
    //   387: astore 13
    //   389: aload 13
    //   391: astore 6
    //   393: goto -139 -> 254
    //   396: astore_3
    //   397: new 108	org/apache/cordova/GlobalizationError
    //   400: dup
    //   401: ldc_w 364
    //   404: invokespecial 204	org/apache/cordova/GlobalizationError:<init>	(Ljava/lang/String;)V
    //   407: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	408	0	this	Globalization
    //   0	408	1	paramJSONArray	JSONArray
    //   7	324	2	localJSONObject	JSONObject
    //   396	1	3	localException	Exception
    //   23	345	4	localSimpleDateFormat1	SimpleDateFormat
    //   40	343	5	localSimpleDateFormat2	SimpleDateFormat
    //   74	318	6	localObject	Object
    //   260	58	7	localTimeZone	TimeZone
    //   235	138	12	str1	String
    //   387	3	13	str2	String
    //   130	213	14	str3	String
    // Exception table:
    //   from	to	target	type
    //   8	159	396	java/lang/Exception
    //   159	254	396	java/lang/Exception
    //   254	330	396	java/lang/Exception
    //   332	352	396	java/lang/Exception
    //   352	369	396	java/lang/Exception
    //   372	389	396	java/lang/Exception
  }
  
  private JSONObject getDateToString(JSONArray paramJSONArray)
    throws GlobalizationError
  {
    JSONObject localJSONObject1 = new JSONObject();
    try
    {
      Date localDate = new Date(((Long)paramJSONArray.getJSONObject(0).get("date")).longValue());
      JSONObject localJSONObject2 = localJSONObject1.put("value", new SimpleDateFormat(getDatePattern(paramJSONArray).getString("pattern")).format(localDate));
      return localJSONObject2;
    }
    catch (Exception localException)
    {
      throw new GlobalizationError("FORMATTING_ERROR");
    }
  }
  
  private JSONObject getFirstDayOfWeek(JSONArray paramJSONArray)
    throws GlobalizationError
  {
    JSONObject localJSONObject1 = new JSONObject();
    try
    {
      JSONObject localJSONObject2 = localJSONObject1.put("value", Calendar.getInstance(Locale.getDefault()).getFirstDayOfWeek());
      return localJSONObject2;
    }
    catch (Exception localException)
    {
      throw new GlobalizationError("UNKNOWN_ERROR");
    }
  }
  
  private JSONObject getIsDayLightSavingsTime(JSONArray paramJSONArray)
    throws GlobalizationError
  {
    JSONObject localJSONObject1 = new JSONObject();
    try
    {
      Date localDate = new Date(((Long)paramJSONArray.getJSONObject(0).get("date")).longValue());
      JSONObject localJSONObject2 = localJSONObject1.put("dst", TimeZone.getTimeZone(Time.getCurrentTimezone()).inDaylightTime(localDate));
      return localJSONObject2;
    }
    catch (Exception localException)
    {
      throw new GlobalizationError("UNKNOWN_ERROR");
    }
  }
  
  private JSONObject getLocaleName()
    throws GlobalizationError
  {
    JSONObject localJSONObject = new JSONObject();
    try
    {
      localJSONObject.put("value", Locale.getDefault().toString());
      return localJSONObject;
    }
    catch (Exception localException)
    {
      throw new GlobalizationError("UNKNOWN_ERROR");
    }
  }
  
  private DecimalFormat getNumberFormatInstance(JSONArray paramJSONArray)
    throws JSONException
  {
    DecimalFormat localDecimalFormat1 = (DecimalFormat)DecimalFormat.getInstance(Locale.getDefault());
    try
    {
      if ((paramJSONArray.getJSONObject(0).length() > 1) && (!((JSONObject)paramJSONArray.getJSONObject(0).get("options")).isNull("type")))
      {
        String str = (String)((JSONObject)paramJSONArray.getJSONObject(0).get("options")).get("type");
        if (str.equalsIgnoreCase("currency")) {
          return (DecimalFormat)DecimalFormat.getCurrencyInstance(Locale.getDefault());
        }
        if (str.equalsIgnoreCase("percent"))
        {
          DecimalFormat localDecimalFormat2 = (DecimalFormat)DecimalFormat.getPercentInstance(Locale.getDefault());
          return localDecimalFormat2;
        }
      }
    }
    catch (JSONException localJSONException) {}
    return localDecimalFormat1;
  }
  
  /* Error */
  private JSONObject getNumberPattern(JSONArray paramJSONArray)
    throws GlobalizationError
  {
    // Byte code:
    //   0: new 112	org/json/JSONObject
    //   3: dup
    //   4: invokespecial 113	org/json/JSONObject:<init>	()V
    //   7: astore_2
    //   8: invokestatic 129	java/util/Locale:getDefault	()Ljava/util/Locale;
    //   11: invokestatic 402	java/text/DecimalFormat:getInstance	(Ljava/util/Locale;)Ljava/text/NumberFormat;
    //   14: checkcast 131	java/text/DecimalFormat
    //   17: astore 4
    //   19: aload 4
    //   21: invokevirtual 182	java/text/DecimalFormat:getDecimalFormatSymbols	()Ljava/text/DecimalFormatSymbols;
    //   24: invokevirtual 188	java/text/DecimalFormatSymbols:getDecimalSeparator	()C
    //   27: invokestatic 194	java/lang/String:valueOf	(C)Ljava/lang/String;
    //   30: astore 5
    //   32: aload_1
    //   33: iconst_0
    //   34: invokevirtual 119	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   37: invokevirtual 214	org/json/JSONObject:length	()I
    //   40: ifle +78 -> 118
    //   43: aload_1
    //   44: iconst_0
    //   45: invokevirtual 119	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   48: ldc 80
    //   50: invokevirtual 218	org/json/JSONObject:get	(Ljava/lang/String;)Ljava/lang/Object;
    //   53: checkcast 112	org/json/JSONObject
    //   56: ldc 98
    //   58: invokevirtual 222	org/json/JSONObject:isNull	(Ljava/lang/String;)Z
    //   61: ifne +57 -> 118
    //   64: aload_1
    //   65: iconst_0
    //   66: invokevirtual 119	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
    //   69: ldc 80
    //   71: invokevirtual 218	org/json/JSONObject:get	(Ljava/lang/String;)Ljava/lang/Object;
    //   74: checkcast 112	org/json/JSONObject
    //   77: ldc 98
    //   79: invokevirtual 218	org/json/JSONObject:get	(Ljava/lang/String;)Ljava/lang/Object;
    //   82: checkcast 190	java/lang/String
    //   85: astore 14
    //   87: aload 14
    //   89: ldc 8
    //   91: invokevirtual 225	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   94: ifeq +137 -> 231
    //   97: invokestatic 129	java/util/Locale:getDefault	()Ljava/util/Locale;
    //   100: invokestatic 135	java/text/DecimalFormat:getCurrencyInstance	(Ljava/util/Locale;)Ljava/text/NumberFormat;
    //   103: checkcast 131	java/text/DecimalFormat
    //   106: astore 4
    //   108: aload 4
    //   110: invokevirtual 182	java/text/DecimalFormat:getDecimalFormatSymbols	()Ljava/text/DecimalFormatSymbols;
    //   113: invokevirtual 408	java/text/DecimalFormatSymbols:getCurrencySymbol	()Ljava/lang/String;
    //   116: astore 5
    //   118: aload_2
    //   119: ldc 147
    //   121: aload 4
    //   123: invokevirtual 151	java/text/DecimalFormat:toPattern	()Ljava/lang/String;
    //   126: invokevirtual 155	org/json/JSONObject:put	(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;
    //   129: pop
    //   130: aload_2
    //   131: ldc_w 410
    //   134: aload 5
    //   136: invokevirtual 155	org/json/JSONObject:put	(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;
    //   139: pop
    //   140: aload_2
    //   141: ldc 162
    //   143: aload 4
    //   145: invokevirtual 166	java/text/DecimalFormat:getMinimumFractionDigits	()I
    //   148: invokevirtual 169	org/json/JSONObject:put	(Ljava/lang/String;I)Lorg/json/JSONObject;
    //   151: pop
    //   152: aload_2
    //   153: ldc 171
    //   155: new 173	java/lang/Integer
    //   158: dup
    //   159: iconst_0
    //   160: invokespecial 176	java/lang/Integer:<init>	(I)V
    //   163: invokevirtual 155	org/json/JSONObject:put	(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;
    //   166: pop
    //   167: aload_2
    //   168: ldc_w 412
    //   171: aload 4
    //   173: invokevirtual 415	java/text/DecimalFormat:getPositivePrefix	()Ljava/lang/String;
    //   176: invokevirtual 155	org/json/JSONObject:put	(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;
    //   179: pop
    //   180: aload_2
    //   181: ldc_w 417
    //   184: aload 4
    //   186: invokevirtual 420	java/text/DecimalFormat:getNegativePrefix	()Ljava/lang/String;
    //   189: invokevirtual 155	org/json/JSONObject:put	(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;
    //   192: pop
    //   193: aload_2
    //   194: ldc 178
    //   196: aload 4
    //   198: invokevirtual 182	java/text/DecimalFormat:getDecimalFormatSymbols	()Ljava/text/DecimalFormatSymbols;
    //   201: invokevirtual 188	java/text/DecimalFormatSymbols:getDecimalSeparator	()C
    //   204: invokestatic 194	java/lang/String:valueOf	(C)Ljava/lang/String;
    //   207: invokevirtual 155	org/json/JSONObject:put	(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;
    //   210: pop
    //   211: aload_2
    //   212: ldc 196
    //   214: aload 4
    //   216: invokevirtual 182	java/text/DecimalFormat:getDecimalFormatSymbols	()Ljava/text/DecimalFormatSymbols;
    //   219: invokevirtual 199	java/text/DecimalFormatSymbols:getGroupingSeparator	()C
    //   222: invokestatic 194	java/lang/String:valueOf	(C)Ljava/lang/String;
    //   225: invokevirtual 155	org/json/JSONObject:put	(Ljava/lang/String;Ljava/lang/Object;)Lorg/json/JSONObject;
    //   228: pop
    //   229: aload_2
    //   230: areturn
    //   231: aload 14
    //   233: ldc 83
    //   235: invokevirtual 225	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   238: ifeq -120 -> 118
    //   241: invokestatic 129	java/util/Locale:getDefault	()Ljava/util/Locale;
    //   244: invokestatic 405	java/text/DecimalFormat:getPercentInstance	(Ljava/util/Locale;)Ljava/text/NumberFormat;
    //   247: checkcast 131	java/text/DecimalFormat
    //   250: astore 4
    //   252: aload 4
    //   254: invokevirtual 182	java/text/DecimalFormat:getDecimalFormatSymbols	()Ljava/text/DecimalFormatSymbols;
    //   257: invokevirtual 423	java/text/DecimalFormatSymbols:getPercent	()C
    //   260: invokestatic 194	java/lang/String:valueOf	(C)Ljava/lang/String;
    //   263: astore 15
    //   265: aload 15
    //   267: astore 5
    //   269: goto -151 -> 118
    //   272: astore_3
    //   273: new 108	org/apache/cordova/GlobalizationError
    //   276: dup
    //   277: ldc_w 364
    //   280: invokespecial 204	org/apache/cordova/GlobalizationError:<init>	(Ljava/lang/String;)V
    //   283: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	284	0	this	Globalization
    //   0	284	1	paramJSONArray	JSONArray
    //   7	223	2	localJSONObject	JSONObject
    //   272	1	3	localException	Exception
    //   17	236	4	localDecimalFormat	DecimalFormat
    //   30	238	5	localObject	Object
    //   85	147	14	str1	String
    //   263	3	15	str2	String
    // Exception table:
    //   from	to	target	type
    //   8	118	272	java/lang/Exception
    //   118	229	272	java/lang/Exception
    //   231	265	272	java/lang/Exception
  }
  
  private JSONObject getNumberToString(JSONArray paramJSONArray)
    throws GlobalizationError
  {
    JSONObject localJSONObject1 = new JSONObject();
    try
    {
      JSONObject localJSONObject2 = localJSONObject1.put("value", getNumberFormatInstance(paramJSONArray).format(paramJSONArray.getJSONObject(0).get("number")));
      return localJSONObject2;
    }
    catch (Exception localException)
    {
      throw new GlobalizationError("FORMATTING_ERROR");
    }
  }
  
  private JSONObject getPreferredLanguage()
    throws GlobalizationError
  {
    JSONObject localJSONObject = new JSONObject();
    try
    {
      localJSONObject.put("value", Locale.getDefault().getDisplayLanguage().toString());
      return localJSONObject;
    }
    catch (Exception localException)
    {
      throw new GlobalizationError("UNKNOWN_ERROR");
    }
  }
  
  private JSONObject getStringToNumber(JSONArray paramJSONArray)
    throws GlobalizationError
  {
    JSONObject localJSONObject1 = new JSONObject();
    try
    {
      JSONObject localJSONObject2 = localJSONObject1.put("value", getNumberFormatInstance(paramJSONArray).parse((String)paramJSONArray.getJSONObject(0).get("numberString")));
      return localJSONObject2;
    }
    catch (Exception localException)
    {
      throw new GlobalizationError("PARSING_ERROR");
    }
  }
  
  private JSONObject getStringtoDate(JSONArray paramJSONArray)
    throws GlobalizationError
  {
    JSONObject localJSONObject = new JSONObject();
    try
    {
      Date localDate = new SimpleDateFormat(getDatePattern(paramJSONArray).getString("pattern")).parse(paramJSONArray.getJSONObject(0).get("dateString").toString());
      Time localTime = new Time();
      localTime.set(localDate.getTime());
      localJSONObject.put("year", localTime.year);
      localJSONObject.put("month", localTime.month);
      localJSONObject.put("day", localTime.monthDay);
      localJSONObject.put("hour", localTime.hour);
      localJSONObject.put("minute", localTime.minute);
      localJSONObject.put("second", localTime.second);
      localJSONObject.put("millisecond", new Long(0L));
      return localJSONObject;
    }
    catch (Exception localException)
    {
      throw new GlobalizationError("PARSING_ERROR");
    }
  }
  
  public boolean execute(String paramString, JSONArray paramJSONArray, CallbackContext paramCallbackContext)
  {
    new JSONObject();
    try
    {
      if (paramString.equals("getLocaleName")) {
        localObject = getLocaleName();
      }
      for (;;)
      {
        paramCallbackContext.success((JSONObject)localObject);
        break label305;
        if (paramString.equals("getPreferredLanguage"))
        {
          localObject = getPreferredLanguage();
        }
        else if (paramString.equalsIgnoreCase("dateToString"))
        {
          localObject = getDateToString(paramJSONArray);
        }
        else if (paramString.equalsIgnoreCase("stringToDate"))
        {
          localObject = getStringtoDate(paramJSONArray);
        }
        else
        {
          if (!paramString.equalsIgnoreCase("getDatePattern")) {
            break;
          }
          localObject = getDatePattern(paramJSONArray);
        }
      }
      if (paramString.equalsIgnoreCase("getDateNames")) {
        if (Build.VERSION.SDK_INT < 9) {
          throw new GlobalizationError("UNKNOWN_ERROR");
        }
      }
    }
    catch (GlobalizationError localGlobalizationError)
    {
      for (;;)
      {
        paramCallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, localGlobalizationError.toJson()));
        break label305;
        Object localObject = getDateNames(paramJSONArray);
        continue;
        if (paramString.equalsIgnoreCase("isDayLightSavingsTime"))
        {
          localObject = getIsDayLightSavingsTime(paramJSONArray);
        }
        else if (paramString.equalsIgnoreCase("getFirstDayOfWeek"))
        {
          localObject = getFirstDayOfWeek(paramJSONArray);
        }
        else if (paramString.equalsIgnoreCase("numberToString"))
        {
          localObject = getNumberToString(paramJSONArray);
        }
        else if (paramString.equalsIgnoreCase("stringToNumber"))
        {
          localObject = getStringToNumber(paramJSONArray);
        }
        else if (paramString.equalsIgnoreCase("getNumberPattern"))
        {
          localObject = getNumberPattern(paramJSONArray);
        }
        else
        {
          if (!paramString.equalsIgnoreCase("getCurrencyPattern")) {
            break;
          }
          JSONObject localJSONObject = getCurrencyPattern(paramJSONArray);
          localObject = localJSONObject;
        }
      }
      return false;
    }
    catch (Exception localException)
    {
      paramCallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.JSON_EXCEPTION));
    }
    label305:
    return true;
  }
}


/* Location:           C:\Users\Monitor\Videos\app\classes_dex2jar.jar
 * Qualified Name:     org.apache.cordova.Globalization
 * JD-Core Version:    0.7.0.1
 */