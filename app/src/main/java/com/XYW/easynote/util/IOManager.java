package com.XYW.easynote.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.FileProvider;
import androidx.core.os.EnvironmentCompat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class IOManager {

    private static final String TAG = "IOManager";

    public static final int PHOTO_REQUEST_CAREMA = 201;
    public static final int PHOTO_REQUEST_GALLERY = 202;

    public static class imageFromCamera {
        public static Uri imageUriFromCamera = null;
        public static String imagePathFromCamera = "";

        public static void setImagePathFromCamera(String imagePathFromCamera) {
            imageFromCamera.imagePathFromCamera = imagePathFromCamera;
        }

        public static void setImageUriFromCamera(Uri imageUriFromCamera) {
            imageFromCamera.imageUriFromCamera = imageUriFromCamera;
        }

        public static String getImagePathFromCamera() {
            return imagePathFromCamera;
        }

        public static Uri getImageUriFromCamera() {
            return imageUriFromCamera;
        }
    }

    /**
     * 读取本地文件
     * @param file 目标文件
     * @return 文件所有内容
     */
    public static String readFile(File file) {
        StringBuilder str = new StringBuilder();
        if (file.isDirectory() || !file.exists()) {
            Log.d(TAG, "The file doesn't exist.");
        } else {
            try {
                InputStream inputStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line;
                while (( line = bufferedReader.readLine()) != null) {
                    str.append(line).append('\n');
                }
                inputStream.close();
            } catch(Exception e) {
                e.printStackTrace();
                Log.e(TAG, "readFile: ", e);
            }
        }
        return str.toString();
    }

    /**
     * 读取本地文件
     * @param file 目标文件
     * @return 文件每行内容
     */
    public static List<String> readFileByLine(File file) {
        List<String> str = new ArrayList<>();
        if (file.isDirectory() || !file.exists()) {
            Log.d(TAG, "The file doesn't exist.");
        } else {
            try {
                InputStream inputStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line;
                //逐行读取
                while (( line = bufferedReader.readLine()) != null) {
                    str.add(line);
                }
                inputStream.close();
            } catch(Exception e) {
                e.printStackTrace();
                Log.e(TAG, "readFileByLine: ", e);
            }
        }
        return str;
    }

    /**
     * 写入本地文件
     * @param file 目标文件
     * @param str 写入内容
     * @param append true为追加写入, false为覆盖写入
     */
    public static void writeFile(File file, String str, Boolean append) {
        try {
            File path = new File(Objects.requireNonNull(file.getParent()));
            if (!path.exists()) {
                path.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            OutputStream outputStream = new FileOutputStream(file, append);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
            bufferedWriter.write(str);
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "writeFile: ", e);
        }
    }

    /**
     * 判断文件是否存在
     * @param file 目标文件
     * @return 返回true为文件存在,返回false为文件不存在
     */
    public static boolean fileExists(File file) {
        return file.exists();
    }



    /**
     * 创建保存图片的文件
     */
    public static File createImageFile(Context context) throws IOException {
        String imageName = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            storageDir.mkdir();
        }
        File tempFile = new File(storageDir, imageName);
        if (!Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(tempFile))) {
            return null;
        }
        return tempFile;
    }

    /**
     * 创建图片地址uri,用于保存拍照后的照片 Android 10以后使用这种方法
     */
    public static Uri createImageUri(Context context) {
        String status = Environment.getExternalStorageState();
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        } else {
            return context.getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, new ContentValues());
        }
    }

    /**
     * 保存uri文件
     * @param context 活动容器
     * @param uri 源文件uri
     * @param path 保存位置
     */
    public static void writeFileWithUri(Context context, Uri uri, String path) {
        File saveFile = new File(path);
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            if (!saveFile.exists()) {
                saveFile.createNewFile();
            }
            inStream = new FileInputStream(parseFile(uri, context));
            outStream = new FileOutputStream(saveFile);
            FileChannel inChannel = inStream.getChannel();
            FileChannel outChannel = outStream.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 移动文件位置
     * @param file1 源文件
     * @param file2 目标位置文件
     * @param flag 是否删除源文件,true为删除,false为保留
     */
    public static void moveFile(File file1, File file2, boolean flag) {
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            if (!file2.exists()) {
                file2.createNewFile();
            }
            inStream = new FileInputStream(file1);
            outStream = new FileOutputStream(file2);
            FileChannel inChannel = inStream.getChannel();
            FileChannel outChannel = outStream.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            if (flag) {
                file1.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean mkdir(File file) {
        return file.mkdirs();
    }

    public static boolean createNewFile(File file) {
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static File parseFile(Uri uri, Context context) {
        String path = null;
        if ("file".equals(uri.getScheme())) {
            path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = context.getContentResolver();
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA },
                        "(" + MediaStore.Images.ImageColumns.DATA + "=" + "'" + path + "'" + ")",
                        null, null);
                int index = 0;
                int dataIdx;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    index = cur.getInt(index);
                    dataIdx = cur.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    path = cur.getString(dataIdx);
                }
                cur.close();
                if (index != 0) {
                    Uri u = Uri.parse("content://media/external/images/media/" + index);
                    System.out.println("temp uri is :" + u);
                }
            }
            if (path != null) {
                return new File(path);
            }
        } else if ("content".equals(uri.getScheme())) {
            // 4.2.2以后
            String[] proj = { MediaStore.Images.Media.DATA };
            Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(columnIndex);
            }
            cursor.close();

            if (path != null)
                return new File(path);
        } else {
            Log.i(TAG, "Uri Scheme:" + uri.getScheme());
        }
        return null;
    }

    /**
     * 获取图片的内容提供器
     */
    public static class fileProvider extends FileProvider {
    }
}
