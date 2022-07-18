package com.XYW.easynote.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.FileProvider;
import androidx.core.os.EnvironmentCompat;

import com.XYW.easynote.Fragment.NoteFragment;
import com.XYW.easynote.R;

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

    public static final String NOTE_TAG = "<tag>";
    public static final String NOTE_ENDTAG = "<endtag>";
    public static final String NOTE_NOTE = "<note>";
    public static final String NOTE_ENDNOTE = "<endnote>";
    public static final String NOTE_DEFAULT_TAG_NAME = "<default_tag_name>";
    public static final String NOTE_FILE_END = "note";

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

    public static void mkdir(File file) {
        file.mkdirs();
    }

    public static void createNewFile(File file) {
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteDir(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            return;
        } else {
            for (File file : Objects.requireNonNull(dir.listFiles())) {
                if (!file.delete()) {
                    return;
                } else if (file.isDirectory()) {
                    deleteDir(file);
                }
            }
        }
        dir.delete();
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

     * 高效加载Bitmap
     * @param filePath 文件路径
     * @param requestWidth 所需宽度
     * @param requestHeight 所需高度
     */

    public static Bitmap decodeBitmap(String filePath, int requestWidth, int requestHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;  //第一次解析图片原始宽高信息，不会真正去加载图片
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, requestWidth, requestHeight);  //计算采样率inSampleSize
        options.inJustDecodeBounds = false;  //第二次解析图片，真正加载图片
        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 计算采样率
     *
     * @param options 图片信息
     * @param reqWidth 所需宽度
     * @param reqHeight 所需高度
     * @return 采样率
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int width = options.outWidth;
        final int height = options.outHeight;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {  //计算图片原始宽高和所需宽高比例值
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = Math.max(heightRatio, widthRatio);  //取宽高比例值中的较大值作为inSampleSize
        }
        return inSampleSize;
    }

    public static List<NoteFragment.NoteTag> readNoteCtt(Context context, File Notes_Contents) {
        List<String> notesContentsReader = IOManager.readFileByLine(Notes_Contents);
        int mode = 0; //0:read Tag 1:read Note
        List<NoteFragment.Note> notes = new ArrayList<>();
        List<String> notereader = new ArrayList<>();
        List<NoteFragment.NoteTag> NOTE_TAGS = new ArrayList<>();
        String tagTitle = "";
        boolean endNote = true, endTag = true;

        for (int i = 2; i < notesContentsReader.size(); i++) {
            if (Objects.equals(notesContentsReader.get(i), NOTE_TAG) && endTag && endNote) {
                mode = 0;
                tagTitle = "";
                endTag = false;
            } else if (Objects.equals(notesContentsReader.get(i), NOTE_NOTE) && endNote) {
                mode = 1;
                endNote = false;
            } else if (Objects.equals(notesContentsReader.get(i), NOTE_ENDNOTE)) {
                if (notereader.size() < 1) {
                    continue;
                }
                if (notereader.size() == 5) {
                    notes.add(new NoteFragment.Note(Objects.equals(notereader.get(0), "null") ? null : notereader.get(0),
                            Objects.equals(notereader.get(1), "null") ? null : notereader.get(1),
                            Objects.equals(notereader.get(2), "null") ? null : notereader.get(2),
                            notereader.get(3),
                            Objects.equals(notereader.get(4), "null") ? null : notereader.get(4)));
                } else if (notereader.size() == 7) {
                    notes.add(new NoteFragment.Note(Objects.equals(notereader.get(0), "null") ? null : notereader.get(0),
                            Objects.equals(notereader.get(1), "null") ? null : notereader.get(1),
                            Objects.equals(notereader.get(2), "null") ? null : notereader.get(2),
                            notereader.get(3),
                            Objects.equals(notereader.get(4), "null") ? null : notereader.get(4),
                            Objects.equals(notereader.get(5), "0") ? 0 : Integer.parseInt(notereader.get(5)),
                            Objects.equals(notereader.get(6), "null") ? null : notereader.get(6)));
                }
                endNote = true;
                mode = -1;
                notereader.clear();
            } else if (Objects.equals(notesContentsReader.get(i), NOTE_ENDTAG)) {
                NOTE_TAGS.add(new NoteFragment.NoteTag(notes, tagTitle));
                notes.clear();
                endTag = true;
                mode = -1;
            } else {
                switch (mode) {
                    case 0:
                        if (Objects.equals(notesContentsReader.get(i), NOTE_DEFAULT_TAG_NAME)) {
                            tagTitle = context.getString(R.string.text_fragment_note_mynotes_title);
                        } else {
                            tagTitle = notesContentsReader.get(i);
                        }
                        break;
                    case 1:
                        notereader.add(notesContentsReader.get(i));
                        break;
                }
            }
        }

        return NOTE_TAGS;
    }

    public static void deleteNoteInCtt(Context context, String name) {
        File Notes_Contents = new File(context.getFilesDir().getPath() + File.separator + "Notes_Contents.ctt");
        List<NoteFragment.NoteTag> Tags = new ArrayList<>(IOManager.readNoteCtt(context, Notes_Contents));

        IOManager.writeFile(Notes_Contents, "#EasyNote\n" +
                ActivityManager.getAppVersionCode(context) + '\n', false);

        for (int i = 0; i < Tags.size(); i++) {
            StringBuilder str = new StringBuilder();
            boolean c = false;
            str.append(IOManager.NOTE_TAG + '\n').append(Tags.get(i).getTitle()).append('\n');
            for (int j = 0; j < Tags.get(i).getNotes().size(); j++) {
                if (!Objects.equals(Tags.get(i).getNotes().get(j).getTitle(), name)) {
                    str.append(IOManager.NOTE_NOTE + '\n');
                    str.append(Tags.get(i).getNotes().get(j).getFile_Path()).append('\n');
                    str.append(Tags.get(i).getNotes().get(j).getFile_Name()).append('\n');
                    str.append(Tags.get(i).getNotes().get(j).getFile_End()).append('\n');
                    str.append(Tags.get(i).getNotes().get(j).getTitle()).append('\n');
                    str.append(Tags.get(i).getNotes().get(j).getDescribe_Path()).append('\n');
                    str.append(Tags.get(i).getNotes().get(j).getIcon_ID()).append('\n');
                    str.append(Tags.get(i).getNotes().get(j).getBackground_Path()).append('\n');
                    str.append(IOManager.NOTE_ENDNOTE + '\n');
                } else {
                    if (Tags.size() == 1 && Tags.get(i).getNotes().size() == 1) {
                        Notes_Contents.delete();
                        return;
                    } else if (Tags.get(i).getNotes().size() == 1) {
                        c = true;
                    }
                }
            }
            if (c) {
                continue;
            }
            str.append(IOManager.NOTE_ENDTAG + '\n');
            IOManager.writeFile(Notes_Contents, str.toString(), true);
        }
    }

    /**
     * 获取图片的内容提供器
     */
    public static class fileProvider extends FileProvider {
    }
}
