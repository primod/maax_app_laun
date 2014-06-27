package org.apache.cordova;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.Images.Media;
import android.webkit.MimeTypeMap;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.channels.FileChannel;
import org.apache.commons.codec.binary.Base64;
import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaInterface;
import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.PluginResult;
import org.apache.cordova.api.PluginResult.Status;
import org.apache.cordova.file.EncodingException;
import org.apache.cordova.file.FileExistsException;
import org.apache.cordova.file.InvalidModificationException;
import org.apache.cordova.file.NoModificationAllowedException;
import org.apache.cordova.file.TypeMismatchException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FileUtils
  extends CordovaPlugin
{
  public static int ABORT_ERR = 0;
  public static int APPLICATION = 3;
  public static int ENCODING_ERR = 0;
  public static int INVALID_MODIFICATION_ERR = 0;
  public static int INVALID_STATE_ERR = 0;
  private static final String LOG_TAG = "FileUtils";
  public static int NOT_FOUND_ERR = 1;
  public static int NOT_READABLE_ERR = 0;
  public static int NO_MODIFICATION_ALLOWED_ERR = 0;
  public static int PATH_EXISTS_ERR = 0;
  public static int PERSISTENT = 0;
  public static int QUOTA_EXCEEDED_ERR = 0;
  public static int RESOURCE = 0;
  public static int SECURITY_ERR = 2;
  public static int SYNTAX_ERR = 0;
  public static int TEMPORARY = 0;
  public static int TYPE_MISMATCH_ERR = 0;
  private static final String _DATA = "_data";
  FileReader f_in;
  FileWriter f_out;
  
  static
  {
    ABORT_ERR = 3;
    NOT_READABLE_ERR = 4;
    ENCODING_ERR = 5;
    NO_MODIFICATION_ALLOWED_ERR = 6;
    INVALID_STATE_ERR = 7;
    SYNTAX_ERR = 8;
    INVALID_MODIFICATION_ERR = 9;
    QUOTA_EXCEEDED_ERR = 10;
    TYPE_MISMATCH_ERR = 11;
    PATH_EXISTS_ERR = 12;
    TEMPORARY = 0;
    PERSISTENT = 1;
    RESOURCE = 2;
  }
  
  private boolean atRootDirectory(String paramString)
  {
    String str = getRealPathFromURI(Uri.parse(paramString), this.cordova);
    return (str.equals(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + this.cordova.getActivity().getPackageName() + "/cache")) || (str.equals(Environment.getExternalStorageDirectory().getAbsolutePath())) || (str.equals("/data/data/" + this.cordova.getActivity().getPackageName()));
  }
  
  private void copyAction(File paramFile1, File paramFile2)
    throws FileNotFoundException, IOException
  {
    FileInputStream localFileInputStream = new FileInputStream(paramFile1);
    FileOutputStream localFileOutputStream = new FileOutputStream(paramFile2);
    FileChannel localFileChannel1 = localFileInputStream.getChannel();
    FileChannel localFileChannel2 = localFileOutputStream.getChannel();
    try
    {
      localFileChannel1.transferTo(0L, localFileChannel1.size(), localFileChannel2);
      return;
    }
    finally
    {
      localFileInputStream.close();
      localFileOutputStream.close();
      localFileChannel1.close();
      localFileChannel2.close();
    }
  }
  
  private JSONObject copyDirectory(File paramFile1, File paramFile2)
    throws JSONException, IOException, NoModificationAllowedException, InvalidModificationException
  {
    if ((paramFile2.exists()) && (paramFile2.isFile())) {
      throw new InvalidModificationException("Can't rename a file to a directory");
    }
    if (isCopyOnItself(paramFile1.getAbsolutePath(), paramFile2.getAbsolutePath())) {
      throw new InvalidModificationException("Can't copy itself into itself");
    }
    if ((!paramFile2.exists()) && (!paramFile2.mkdir())) {
      throw new NoModificationAllowedException("Couldn't create the destination directory");
    }
    File[] arrayOfFile = paramFile1.listFiles();
    int i = arrayOfFile.length;
    int j = 0;
    if (j < i)
    {
      File localFile = arrayOfFile[j];
      if (localFile.isDirectory()) {
        copyDirectory(localFile, paramFile2);
      }
      for (;;)
      {
        j++;
        break;
        copyFile(localFile, new File(paramFile2.getAbsoluteFile() + File.separator + localFile.getName()));
      }
    }
    return getEntry(paramFile2);
  }
  
  private JSONObject copyFile(File paramFile1, File paramFile2)
    throws IOException, InvalidModificationException, JSONException
  {
    if ((paramFile2.exists()) && (paramFile2.isDirectory())) {
      throw new InvalidModificationException("Can't rename a file to a directory");
    }
    copyAction(paramFile1, paramFile2);
    return getEntry(paramFile2);
  }
  
  private File createDestination(String paramString, File paramFile1, File paramFile2)
  {
    if (("null".equals(paramString)) || ("".equals(paramString))) {
      paramString = null;
    }
    if (paramString != null) {
      return new File(paramFile2.getAbsolutePath() + File.separator + paramString);
    }
    return new File(paramFile2.getAbsolutePath() + File.separator + paramFile1.getName());
  }
  
  private File createFileObject(String paramString)
  {
    return new File(getRealPathFromURI(Uri.parse(paramString), this.cordova));
  }
  
  private File createFileObject(String paramString1, String paramString2)
  {
    if (paramString2.startsWith("/")) {
      return new File(paramString2);
    }
    String str = getRealPathFromURI(Uri.parse(paramString1), this.cordova);
    return new File(str + File.separator + paramString2);
  }
  
  private JSONObject getEntry(String paramString)
    throws JSONException
  {
    return getEntry(new File(paramString));
  }
  
  private JSONObject getFile(String paramString1, String paramString2, JSONObject paramJSONObject, boolean paramBoolean)
    throws FileExistsException, IOException, TypeMismatchException, EncodingException, JSONException
  {
    boolean bool1 = false;
    boolean bool2 = false;
    if (paramJSONObject != null)
    {
      bool1 = paramJSONObject.optBoolean("create");
      bool2 = false;
      if (bool1) {
        bool2 = paramJSONObject.optBoolean("exclusive");
      }
    }
    if (paramString2.contains(":")) {
      throw new EncodingException("This file has a : in it's name");
    }
    File localFile = createFileObject(paramString1, paramString2);
    if (bool1)
    {
      if ((bool2) && (localFile.exists())) {
        throw new FileExistsException("create/exclusive fails");
      }
      if (paramBoolean) {
        localFile.mkdir();
      }
      while (!localFile.exists())
      {
        throw new FileExistsException("create fails");
        localFile.createNewFile();
      }
    }
    if (!localFile.exists()) {
      throw new FileNotFoundException("path does not exist");
    }
    if (paramBoolean)
    {
      if (localFile.isFile()) {
        throw new TypeMismatchException("path doesn't exist or is file");
      }
    }
    else if (localFile.isDirectory()) {
      throw new TypeMismatchException("path doesn't exist or is directory");
    }
    return getEntry(localFile);
  }
  
  private JSONObject getFileMetadata(String paramString)
    throws FileNotFoundException, JSONException
  {
    File localFile = createFileObject(paramString);
    if (!localFile.exists()) {
      throw new FileNotFoundException("File: " + paramString + " does not exist.");
    }
    JSONObject localJSONObject = new JSONObject();
    localJSONObject.put("size", localFile.length());
    localJSONObject.put("type", getMimeType(paramString));
    localJSONObject.put("name", localFile.getName());
    localJSONObject.put("fullPath", paramString);
    localJSONObject.put("lastModifiedDate", localFile.lastModified());
    return localJSONObject;
  }
  
  private long getMetadata(String paramString)
    throws FileNotFoundException
  {
    File localFile = createFileObject(paramString);
    if (!localFile.exists()) {
      throw new FileNotFoundException("Failed to find file in getMetadata");
    }
    return localFile.lastModified();
  }
  
  public static String getMimeType(String paramString)
  {
    if (paramString != null)
    {
      String str1 = paramString.replace(" ", "%20").toLowerCase();
      MimeTypeMap localMimeTypeMap = MimeTypeMap.getSingleton();
      String str2 = MimeTypeMap.getFileExtensionFromUrl(str1);
      if (str2.toLowerCase().equals("3ga")) {
        return "audio/3gpp";
      }
      return localMimeTypeMap.getMimeTypeFromExtension(str2);
    }
    return "";
  }
  
  private JSONObject getParent(String paramString)
    throws JSONException
  {
    String str = getRealPathFromURI(Uri.parse(paramString), this.cordova);
    if (atRootDirectory(str)) {
      return getEntry(str);
    }
    return getEntry(new File(str).getParent());
  }
  
  private InputStream getPathFromUri(String paramString)
    throws FileNotFoundException
  {
    if (paramString.startsWith("content"))
    {
      Uri localUri = Uri.parse(paramString);
      return this.cordova.getActivity().getContentResolver().openInputStream(localUri);
    }
    return new FileInputStream(getRealPathFromURI(Uri.parse(paramString), this.cordova));
  }
  
  protected static String getRealPathFromURI(Uri paramUri, CordovaInterface paramCordovaInterface)
  {
    String str = paramUri.getScheme();
    if (str == null) {
      return paramUri.toString();
    }
    if (str.compareTo("content") == 0)
    {
      String[] arrayOfString = { "_data" };
      Cursor localCursor = paramCordovaInterface.getActivity().managedQuery(paramUri, arrayOfString, null, null, null);
      int i = localCursor.getColumnIndexOrThrow("_data");
      localCursor.moveToFirst();
      return localCursor.getString(i);
    }
    if (str.compareTo("file") == 0) {
      return paramUri.getPath();
    }
    return paramUri.toString();
  }
  
  private boolean isCopyOnItself(String paramString1, String paramString2)
  {
    return (paramString2.startsWith(paramString1)) && (paramString2.indexOf(File.separator, -1 + paramString1.length()) != -1);
  }
  
  private JSONObject moveDirectory(File paramFile1, File paramFile2)
    throws IOException, JSONException, InvalidModificationException, NoModificationAllowedException, FileExistsException
  {
    if ((paramFile2.exists()) && (paramFile2.isFile())) {
      throw new InvalidModificationException("Can't rename a file to a directory");
    }
    if (isCopyOnItself(paramFile1.getAbsolutePath(), paramFile2.getAbsolutePath())) {
      throw new InvalidModificationException("Can't move itself into itself");
    }
    if ((paramFile2.exists()) && (paramFile2.list().length > 0)) {
      throw new InvalidModificationException("directory is not empty");
    }
    if (!paramFile1.renameTo(paramFile2))
    {
      copyDirectory(paramFile1, paramFile2);
      if (paramFile2.exists()) {
        removeDirRecursively(paramFile1);
      }
    }
    else
    {
      return getEntry(paramFile2);
    }
    throw new IOException("moved failed");
  }
  
  private JSONObject moveFile(File paramFile1, File paramFile2)
    throws IOException, JSONException, InvalidModificationException
  {
    if ((paramFile2.exists()) && (paramFile2.isDirectory())) {
      throw new InvalidModificationException("Can't rename a file to a directory");
    }
    if (!paramFile1.renameTo(paramFile2))
    {
      copyAction(paramFile1, paramFile2);
      if (paramFile2.exists()) {
        paramFile1.delete();
      }
    }
    else
    {
      return getEntry(paramFile2);
    }
    throw new IOException("moved failed");
  }
  
  private void notifyDelete(String paramString)
  {
    String str = getRealPathFromURI(Uri.parse(paramString), this.cordova);
    try
    {
      this.cordova.getActivity().getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "_data = ?", new String[] { str });
      return;
    }
    catch (UnsupportedOperationException localUnsupportedOperationException) {}
  }
  
  private JSONArray readEntries(String paramString)
    throws FileNotFoundException, JSONException
  {
    File localFile = createFileObject(paramString);
    if (!localFile.exists()) {
      throw new FileNotFoundException();
    }
    JSONArray localJSONArray = new JSONArray();
    if (localFile.isDirectory())
    {
      File[] arrayOfFile = localFile.listFiles();
      for (int i = 0; i < arrayOfFile.length; i++) {
        if (arrayOfFile[i].canRead()) {
          localJSONArray.put(getEntry(arrayOfFile[i]));
        }
      }
    }
    return localJSONArray;
  }
  
  private boolean remove(String paramString)
    throws NoModificationAllowedException, InvalidModificationException
  {
    File localFile = createFileObject(paramString);
    if (atRootDirectory(paramString)) {
      throw new NoModificationAllowedException("You can't delete the root directory");
    }
    if ((localFile.isDirectory()) && (localFile.list().length > 0)) {
      throw new InvalidModificationException("You can't delete a directory that is not empty.");
    }
    return localFile.delete();
  }
  
  private boolean removeDirRecursively(File paramFile)
    throws FileExistsException
  {
    if (paramFile.isDirectory())
    {
      File[] arrayOfFile = paramFile.listFiles();
      int i = arrayOfFile.length;
      for (int j = 0; j < i; j++) {
        removeDirRecursively(arrayOfFile[j]);
      }
    }
    if (!paramFile.delete()) {
      throw new FileExistsException("could not delete: " + paramFile.getName());
    }
    return true;
  }
  
  private boolean removeRecursively(String paramString)
    throws FileExistsException
  {
    File localFile = createFileObject(paramString);
    if (atRootDirectory(paramString)) {
      return false;
    }
    return removeDirRecursively(localFile);
  }
  
  private JSONObject requestFileSystem(int paramInt)
    throws IOException, JSONException
  {
    JSONObject localJSONObject = new JSONObject();
    if (paramInt == TEMPORARY)
    {
      localJSONObject.put("name", "temporary");
      if (Environment.getExternalStorageState().equals("mounted"))
      {
        new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + this.cordova.getActivity().getPackageName() + "/cache/").mkdirs();
        localJSONObject.put("root", getEntry(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + this.cordova.getActivity().getPackageName() + "/cache/"));
        return localJSONObject;
      }
      new File("/data/data/" + this.cordova.getActivity().getPackageName() + "/cache/").mkdirs();
      localJSONObject.put("root", getEntry("/data/data/" + this.cordova.getActivity().getPackageName() + "/cache/"));
      return localJSONObject;
    }
    if (paramInt == PERSISTENT)
    {
      localJSONObject.put("name", "persistent");
      if (Environment.getExternalStorageState().equals("mounted"))
      {
        localJSONObject.put("root", getEntry(Environment.getExternalStorageDirectory()));
        return localJSONObject;
      }
      localJSONObject.put("root", getEntry("/data/data/" + this.cordova.getActivity().getPackageName()));
      return localJSONObject;
    }
    throw new IOException("No filesystem of type requested");
  }
  
  private JSONObject resolveLocalFileSystemURI(String paramString)
    throws IOException, JSONException
  {
    String str = URLDecoder.decode(paramString, "UTF-8");
    File localFile;
    if (str.startsWith("content:"))
    {
      Cursor localCursor = this.cordova.getActivity().managedQuery(Uri.parse(str), new String[] { "_data" }, null, null, null);
      int j = localCursor.getColumnIndexOrThrow("_data");
      localCursor.moveToFirst();
      localFile = new File(localCursor.getString(j));
    }
    while (!localFile.exists())
    {
      throw new FileNotFoundException();
      new URL(str);
      if (str.startsWith("file://"))
      {
        int i = str.indexOf("?");
        if (i < 0) {
          localFile = new File(str.substring(7, str.length()));
        } else {
          localFile = new File(str.substring(7, i));
        }
      }
      else
      {
        localFile = new File(str);
      }
    }
    if (!localFile.canRead()) {
      throw new IOException();
    }
    return getEntry(localFile);
  }
  
  public static String stripFileProtocol(String paramString)
  {
    if (paramString.startsWith("file://")) {
      paramString = paramString.substring(7);
    }
    return paramString;
  }
  
  private JSONObject transferTo(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
    throws JSONException, NoModificationAllowedException, IOException, InvalidModificationException, EncodingException, FileExistsException
  {
    String str1 = getRealPathFromURI(Uri.parse(paramString1), this.cordova);
    String str2 = getRealPathFromURI(Uri.parse(paramString2), this.cordova);
    if ((paramString3 != null) && (paramString3.contains(":"))) {
      throw new EncodingException("Bad file name");
    }
    File localFile1 = new File(str1);
    if (!localFile1.exists()) {
      throw new FileNotFoundException("The source does not exist");
    }
    File localFile2 = new File(str2);
    if (!localFile2.exists()) {
      throw new FileNotFoundException("The source does not exist");
    }
    File localFile3 = createDestination(paramString3, localFile1, localFile2);
    if (localFile1.getAbsolutePath().equals(localFile3.getAbsolutePath())) {
      throw new InvalidModificationException("Can't copy a file onto itself");
    }
    JSONObject localJSONObject;
    if (localFile1.isDirectory()) {
      if (paramBoolean) {
        localJSONObject = moveDirectory(localFile1, localFile3);
      }
    }
    do
    {
      return localJSONObject;
      return copyDirectory(localFile1, localFile3);
      if (!paramBoolean) {
        break;
      }
      localJSONObject = moveFile(localFile1, localFile3);
    } while (!paramString1.startsWith("content://"));
    notifyDelete(paramString1);
    return localJSONObject;
    return copyFile(localFile1, localFile3);
  }
  
  /* Error */
  private long truncateFile(String paramString, long paramLong)
    throws FileNotFoundException, IOException, NoModificationAllowedException
  {
    // Byte code:
    //   0: aload_1
    //   1: ldc_w 530
    //   4: invokevirtual 242	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   7: ifeq +14 -> 21
    //   10: new 171	org/apache/cordova/file/NoModificationAllowedException
    //   13: dup
    //   14: ldc_w 536
    //   17: invokespecial 197	org/apache/cordova/file/NoModificationAllowedException:<init>	(Ljava/lang/String;)V
    //   20: athrow
    //   21: new 538	java/io/RandomAccessFile
    //   24: dup
    //   25: aload_1
    //   26: invokestatic 78	android/net/Uri:parse	(Ljava/lang/String;)Landroid/net/Uri;
    //   29: aload_0
    //   30: getfield 82	org/apache/cordova/FileUtils:cordova	Lorg/apache/cordova/api/CordovaInterface;
    //   33: invokestatic 86	org/apache/cordova/FileUtils:getRealPathFromURI	(Landroid/net/Uri;Lorg/apache/cordova/api/CordovaInterface;)Ljava/lang/String;
    //   36: ldc_w 540
    //   39: invokespecial 543	java/io/RandomAccessFile:<init>	(Ljava/lang/String;Ljava/lang/String;)V
    //   42: astore 4
    //   44: aload 4
    //   46: invokevirtual 544	java/io/RandomAccessFile:length	()J
    //   49: lload_2
    //   50: lcmp
    //   51: iflt +20 -> 71
    //   54: aload 4
    //   56: invokevirtual 545	java/io/RandomAccessFile:getChannel	()Ljava/nio/channels/FileChannel;
    //   59: lload_2
    //   60: invokevirtual 549	java/nio/channels/FileChannel:truncate	(J)Ljava/nio/channels/FileChannel;
    //   63: pop
    //   64: aload 4
    //   66: invokevirtual 550	java/io/RandomAccessFile:close	()V
    //   69: lload_2
    //   70: lreturn
    //   71: aload 4
    //   73: invokevirtual 544	java/io/RandomAccessFile:length	()J
    //   76: lstore 6
    //   78: lload 6
    //   80: lstore_2
    //   81: goto -17 -> 64
    //   84: astore 5
    //   86: aload 4
    //   88: invokevirtual 550	java/io/RandomAccessFile:close	()V
    //   91: aload 5
    //   93: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	94	0	this	FileUtils
    //   0	94	1	paramString	String
    //   0	94	2	paramLong	long
    //   42	45	4	localRandomAccessFile	java.io.RandomAccessFile
    //   84	8	5	localObject	Object
    //   76	3	6	l	long
    // Exception table:
    //   from	to	target	type
    //   44	64	84	finally
    //   71	78	84	finally
  }
  
  public boolean execute(String paramString, JSONArray paramJSONArray, CallbackContext paramCallbackContext)
    throws JSONException
  {
    try
    {
      if (paramString.equals("testSaveLocationExists"))
      {
        boolean bool3 = DirectoryManager.testSaveLocationExists();
        PluginResult localPluginResult5 = new PluginResult(PluginResult.Status.OK, bool3);
        paramCallbackContext.sendPluginResult(localPluginResult5);
      }
      else if (paramString.equals("getFreeDiskSpace"))
      {
        long l4 = DirectoryManager.getFreeDiskSpace(false);
        paramCallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, (float)l4));
      }
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      paramCallbackContext.error(NOT_FOUND_ERR);
      break label990;
      if (paramString.equals("testFileExists"))
      {
        boolean bool2 = DirectoryManager.testFileExists(paramJSONArray.getString(0));
        PluginResult localPluginResult4 = new PluginResult(PluginResult.Status.OK, bool2);
        paramCallbackContext.sendPluginResult(localPluginResult4);
      }
    }
    catch (FileExistsException localFileExistsException)
    {
      paramCallbackContext.error(PATH_EXISTS_ERR);
      break label990;
      if (paramString.equals("testDirectoryExists"))
      {
        boolean bool1 = DirectoryManager.testFileExists(paramJSONArray.getString(0));
        PluginResult localPluginResult3 = new PluginResult(PluginResult.Status.OK, bool1);
        paramCallbackContext.sendPluginResult(localPluginResult3);
      }
    }
    catch (NoModificationAllowedException localNoModificationAllowedException)
    {
      paramCallbackContext.error(NO_MODIFICATION_ALLOWED_ERR);
      break label990;
      if (paramString.equals("readAsText"))
      {
        int i = 2147483647;
        int j = paramJSONArray.length();
        int k = 0;
        if (j >= 3) {
          k = paramJSONArray.getInt(2);
        }
        if (paramJSONArray.length() >= 4) {
          i = paramJSONArray.getInt(3);
        }
        String str1 = readAsText(paramJSONArray.getString(0), paramJSONArray.getString(1), k, i);
        PluginResult localPluginResult1 = new PluginResult(PluginResult.Status.OK, str1);
        paramCallbackContext.sendPluginResult(localPluginResult1);
      }
    }
    catch (InvalidModificationException localInvalidModificationException)
    {
      paramCallbackContext.error(INVALID_MODIFICATION_ERR);
      break label990;
      if (paramString.equals("readAsDataURL"))
      {
        int m = 2147483647;
        int n = paramJSONArray.length();
        int i1 = 0;
        if (n >= 2) {
          i1 = paramJSONArray.getInt(1);
        }
        if (paramJSONArray.length() >= 3) {
          m = paramJSONArray.getInt(2);
        }
        String str2 = readAsDataURL(paramJSONArray.getString(0), i1, m);
        PluginResult localPluginResult2 = new PluginResult(PluginResult.Status.OK, str2);
        paramCallbackContext.sendPluginResult(localPluginResult2);
      }
    }
    catch (MalformedURLException localMalformedURLException)
    {
      paramCallbackContext.error(ENCODING_ERR);
      break label990;
      if (paramString.equals("write"))
      {
        long l3 = write(paramJSONArray.getString(0), paramJSONArray.getString(1), paramJSONArray.getInt(2));
        paramCallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, (float)l3));
      }
    }
    catch (IOException localIOException)
    {
      paramCallbackContext.error(INVALID_MODIFICATION_ERR);
      break label990;
      if (paramString.equals("truncate"))
      {
        long l2 = truncateFile(paramJSONArray.getString(0), paramJSONArray.getLong(1));
        paramCallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, (float)l2));
      }
    }
    catch (EncodingException localEncodingException)
    {
      paramCallbackContext.error(ENCODING_ERR);
      break label990;
      if (!paramString.equals("requestFileSystem")) {
        break label612;
      }
      long l1 = paramJSONArray.optLong(1);
      if ((l1 != 0L) && (l1 > 1024L * DirectoryManager.getFreeDiskSpace(true))) {
        paramCallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, QUOTA_EXCEEDED_ERR));
      }
    }
    catch (TypeMismatchException localTypeMismatchException)
    {
      paramCallbackContext.error(TYPE_MISMATCH_ERR);
    }
    paramCallbackContext.success(requestFileSystem(paramJSONArray.getInt(0)));
    break label990;
    label612:
    if (paramString.equals("resolveLocalFileSystemURI")) {
      paramCallbackContext.success(resolveLocalFileSystemURI(paramJSONArray.getString(0)));
    } else if (paramString.equals("getMetadata")) {
      paramCallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, (float)getMetadata(paramJSONArray.getString(0))));
    } else if (paramString.equals("getFileMetadata")) {
      paramCallbackContext.success(getFileMetadata(paramJSONArray.getString(0)));
    } else if (paramString.equals("getParent")) {
      paramCallbackContext.success(getParent(paramJSONArray.getString(0)));
    } else if (paramString.equals("getDirectory")) {
      paramCallbackContext.success(getFile(paramJSONArray.getString(0), paramJSONArray.getString(1), paramJSONArray.optJSONObject(2), true));
    } else if (paramString.equals("getFile")) {
      paramCallbackContext.success(getFile(paramJSONArray.getString(0), paramJSONArray.getString(1), paramJSONArray.optJSONObject(2), false));
    } else if (paramString.equals("remove"))
    {
      if (remove(paramJSONArray.getString(0)))
      {
        notifyDelete(paramJSONArray.getString(0));
        paramCallbackContext.success();
      }
      else
      {
        paramCallbackContext.error(NO_MODIFICATION_ALLOWED_ERR);
      }
    }
    else if (paramString.equals("removeRecursively"))
    {
      if (removeRecursively(paramJSONArray.getString(0))) {
        paramCallbackContext.success();
      } else {
        paramCallbackContext.error(NO_MODIFICATION_ALLOWED_ERR);
      }
    }
    else if (paramString.equals("moveTo")) {
      paramCallbackContext.success(transferTo(paramJSONArray.getString(0), paramJSONArray.getString(1), paramJSONArray.getString(2), true));
    } else if (paramString.equals("copyTo")) {
      paramCallbackContext.success(transferTo(paramJSONArray.getString(0), paramJSONArray.getString(1), paramJSONArray.getString(2), false));
    } else if (paramString.equals("readEntries")) {
      paramCallbackContext.success(readEntries(paramJSONArray.getString(0)));
    } else {
      return false;
    }
    label990:
    return true;
  }
  
  public JSONObject getEntry(File paramFile)
    throws JSONException
  {
    JSONObject localJSONObject = new JSONObject();
    localJSONObject.put("isFile", paramFile.isFile());
    localJSONObject.put("isDirectory", paramFile.isDirectory());
    localJSONObject.put("name", paramFile.getName());
    localJSONObject.put("fullPath", "file://" + paramFile.getAbsolutePath());
    return localJSONObject;
  }
  
  public boolean isSynch(String paramString)
  {
    if (paramString.equals("testSaveLocationExists")) {}
    while ((paramString.equals("getFreeDiskSpace")) || (paramString.equals("testFileExists")) || (paramString.equals("testDirectoryExists"))) {
      return true;
    }
    return false;
  }
  
  public String readAsDataURL(String paramString, int paramInt1, int paramInt2)
    throws FileNotFoundException, IOException
  {
    int i = paramInt2 - paramInt1;
    byte[] arrayOfByte1 = new byte[1000];
    BufferedInputStream localBufferedInputStream = new BufferedInputStream(getPathFromUri(paramString), 1024);
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    if (paramInt1 > 0) {
      localBufferedInputStream.skip(paramInt1);
    }
    while (i > 0)
    {
      int j = localBufferedInputStream.read(arrayOfByte1, 0, Math.min(1000, i));
      if (j < 0) {
        break;
      }
      i -= j;
      localByteArrayOutputStream.write(arrayOfByte1, 0, j);
    }
    Uri localUri;
    if (paramString.startsWith("content:")) {
      localUri = Uri.parse(paramString);
    }
    for (String str = this.cordova.getActivity().getContentResolver().getType(localUri);; str = getMimeType(paramString))
    {
      byte[] arrayOfByte2 = Base64.encodeBase64(localByteArrayOutputStream.toByteArray());
      return "data:" + str + ";base64," + new String(arrayOfByte2);
    }
  }
  
  public String readAsText(String paramString1, String paramString2, int paramInt1, int paramInt2)
    throws FileNotFoundException, IOException
  {
    int i = paramInt2 - paramInt1;
    byte[] arrayOfByte = new byte[1000];
    BufferedInputStream localBufferedInputStream = new BufferedInputStream(getPathFromUri(paramString1), 1024);
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    if (paramInt1 > 0) {
      localBufferedInputStream.skip(paramInt1);
    }
    while (i > 0)
    {
      int j = localBufferedInputStream.read(arrayOfByte, 0, Math.min(1000, i));
      if (j < 0) {
        break;
      }
      i -= j;
      localByteArrayOutputStream.write(arrayOfByte, 0, j);
    }
    return new String(localByteArrayOutputStream.toByteArray(), paramString2);
  }
  
  public long write(String paramString1, String paramString2, int paramInt)
    throws FileNotFoundException, IOException, NoModificationAllowedException
  {
    if (paramString1.startsWith("content://")) {
      throw new NoModificationAllowedException("Couldn't write to file given its content URI");
    }
    String str = getRealPathFromURI(Uri.parse(paramString1), this.cordova);
    boolean bool = false;
    if (paramInt > 0)
    {
      truncateFile(str, paramInt);
      bool = true;
    }
    byte[] arrayOfByte1 = paramString2.getBytes();
    ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte1);
    FileOutputStream localFileOutputStream = new FileOutputStream(str, bool);
    byte[] arrayOfByte2 = new byte[arrayOfByte1.length];
    localByteArrayInputStream.read(arrayOfByte2, 0, arrayOfByte2.length);
    localFileOutputStream.write(arrayOfByte2, 0, arrayOfByte1.length);
    localFileOutputStream.flush();
    localFileOutputStream.close();
    return arrayOfByte1.length;
  }
}


/* Location:           C:\Users\Monitor\Videos\app\classes_dex2jar.jar
 * Qualified Name:     org.apache.cordova.FileUtils
 * JD-Core Version:    0.7.0.1
 */