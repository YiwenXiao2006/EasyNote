package com.XYW.easynote.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.percentlayout.widget.PercentRelativeLayout;

import com.XYW.easynote.Fragment.NoteFragment;
import com.XYW.easynote.R;
import com.XYW.easynote.ui.MessageBox;
import com.XYW.easynote.util.ActivityManager;
import com.XYW.easynote.util.IOManager;
import com.XYW.easynote.util.PermissionManager;
import com.XYW.easynote.util.UIManager;
import com.XYW.easynote.util.WindowManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CreateFile extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CreateFile";

    private ImageView ImageView_noteCover;
    private PercentRelativeLayout Layout_content_createfile_selecimage, Layout_content_create_notecover;
    private EditText EditText_createFile_theme, EditText_createFile_describe;
    private TextView TextView_createFile_clearcover;

    private PermissionManager permissionManager;
    private String theme, describe, coverPath, describePath, file_Path, file_Name, file_End;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_file);
        ActivityManager.setActivity(this, findViewById(R.id.status_bar));
        ActivityManager.setDarkMode(this);
        init(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("theme", EditText_createFile_theme.getText().toString());
        outState.putString("describe", EditText_createFile_describe.getText().toString());
        outState.putString("coverPath", coverPath);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.removeActivity(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionManager.PERMISSION_REQUEST) {
            if (!permissionManager.checkPermission()) {
                permissionManager.onResult(grantResults, permissions, getString(R.string.message_permission_denied_camera_2),
                        getString(R.string.message_permission_denied_camera_1), null);
            } else {
                openCamera();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (!permissionManager.checkPermission())
                permissionManager.checkPermissionWithRequest();
            else {
                openCamera();
            }
        }

        if (requestCode == IOManager.PHOTO_REQUEST_GALLERY) {
            if (data != null) {
                Uri uri = data.getData();
                saveCover(uri);
            }
        } else if (requestCode == IOManager.PHOTO_REQUEST_CAREMA) {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                if (resultCode == RESULT_OK) {
                    Uri uri;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        // Android 10 使用图片uri加载
                        uri = IOManager.imageFromCamera.getImageUriFromCamera();
                    } else {
                        // 使用图片路径加载
                        Bitmap bitmap = IOManager.decodeBitmap(IOManager.imageFromCamera.getImagePathFromCamera(), 1920, 1080);
                        uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null,null));
                    }
                    saveCover(uri);
                }
            }
        }
    }

    private void saveCover(Uri uri) {
        Layout_content_createfile_selecimage.setVisibility(View.GONE);
        Layout_content_create_notecover.setVisibility(View.VISIBLE);
        TextView_createFile_clearcover.setVisibility(View.VISIBLE);
        ImageView_noteCover.setImageURI(uri);
        coverPath = new File(getExternalCacheDir(), "tempCover.jpg").getPath();
        IOManager.writeFileWithUri(this, uri, coverPath);
    }

    private void init(Bundle bundle) {
        if (bundle != null) {
            theme = bundle.getString("theme", getString(R.string.text_activity_createfile_theme));
            describe = bundle.getString("describe", getString(R.string.text_activity_createfile_describe));
            coverPath = bundle.getString("coverPath", null);
        }
        permissionManager = new PermissionManager(this, this, new String[]{Manifest.permission.CAMERA});

        WindowManager windowManager = new WindowManager();
        windowManager.KeyBoardListen(this, this);

        initTextView();
        initImageButton();
        initEditText();
        initPercentRelativeLayout();
        initImageView();

        if (coverPath != null && new File(coverPath).exists()) {
            Layout_content_createfile_selecimage.setVisibility(View.GONE);
            Layout_content_create_notecover.setVisibility(View.VISIBLE);
            TextView_createFile_clearcover.setVisibility(View.VISIBLE);
            ImageView_noteCover.setImageBitmap(IOManager.decodeBitmap(coverPath, 1920, 1080));
        }
    }

    private void initTextView() {
        TextView TextView_toolbarTitle = findViewById(R.id.TextView_toolbarTitle);
        TextView_toolbarTitle.setText(getString(R.string.title_new_file));

        TextView_createFile_clearcover = findViewById(R.id.TextView_createFile_clearcover);
        TextView_createFile_clearcover.setVisibility(View.GONE);
        TextView_createFile_clearcover.setOnClickListener(this);
    }

    private void initImageButton() {
        ImageButton imageButton_toolbarHomeButton = findViewById(R.id.ImageButton_toolbarHomeButton);
        imageButton_toolbarHomeButton.setImageDrawable(ContextCompat.getDrawable(CreateFile.this, R.drawable.direction_left));
        imageButton_toolbarHomeButton.setVisibility(View.VISIBLE);
        imageButton_toolbarHomeButton.setOnClickListener(this);

        ImageButton imageButton_toolbarDoneButton = findViewById(R.id.ImageButton_toolbarDoneButton);
        imageButton_toolbarDoneButton.setImageDrawable(ContextCompat.getDrawable(CreateFile.this, R.drawable.tips_check));
        imageButton_toolbarDoneButton.setVisibility(View.VISIBLE);
        imageButton_toolbarDoneButton.setOnClickListener(this);

        ImageButton imageButton_selectCoverImage = findViewById(R.id.ImageButton_selectcoverimage);
        imageButton_selectCoverImage.setOnClickListener(this);
    }

    private void initEditText() {
        char[] filter = new char[]{'<', '>'};
        EditText_createFile_theme = findViewById(R.id.EditText_createFile_theme);
        EditText_createFile_theme.addTextChangedListener(new UIManager.TextWatcher(filter, EditText_createFile_theme, CreateFile.this));
        EditText_createFile_theme.setFilters(new UIManager.TextFilter[]{new UIManager.TextFilter(CreateFile.this, 20)});
        EditText_createFile_theme.setText((theme != null && !theme.equals("")) ? theme : "");

        EditText_createFile_describe = findViewById(R.id.EditText_createFile_describe);
        EditText_createFile_describe.addTextChangedListener(new UIManager.TextWatcher(filter, EditText_createFile_describe, CreateFile.this));
        EditText_createFile_describe.setFilters(new UIManager.TextFilter[]{new UIManager.TextFilter(CreateFile.this, 100)});
        EditText_createFile_describe.setText((describe != null && !describe.equals("")) ? describe : "");
    }

    private void initPercentRelativeLayout() {
        Layout_content_create_notecover = findViewById(R.id.Layout_content_create_notecover);
        Layout_content_createfile_selecimage = findViewById(R.id.Layout_content_createfile_selecimage);
    }

    private void initImageView() {
        ImageView_noteCover = findViewById(R.id.ImageView_noteCover);
    }

    private void create() {
        theme = EditText_createFile_theme.getText().length() > 0 ? EditText_createFile_theme.getText().toString() : EditText_createFile_theme.getHint().toString();
        describe = EditText_createFile_describe.getText().toString();
        String dir = getFilesDir() + File.separator + "Notes" + File.separator + theme;

        File path = new File(dir);
        file_Path = path.getPath();
        file_Name = theme;
        file_End = IOManager.NOTE_FILE_END;

        if (path.exists()) {
            new MessageBox.CreateMessageBox.Builder(this)
                    .setTitle(getString(R.string.title_messagebox_file_exists))
                    .setMessage(getString(R.string.message_file_exists))
                    .setIcon(R.drawable.general_face_meh_fill)
                    .setCancelable(true)
                    .setCanceledOnTouchOutside(true)
                    .setPositiveButton(getString(R.string.text_button_positive_default), () -> {
                        IOManager.deleteDir(path);
                        IOManager.deleteNoteInCtt(CreateFile.this, theme);
                        IOManager.mkdir(path);
                        if (IOManager.fileExists(new File(getExternalCacheDir(), "tempCover.jpg"))) {
                            IOManager.moveFile(new File(getExternalCacheDir(), "tempCover.jpg"), new File(path.getPath(), theme + "_cover.jpg"), true);
                            coverPath = new File(path.getPath(), theme + "_cover.jpg").getPath();
                        } else {
                            coverPath = null;
                        }

                        if (!Objects.equals(describe, "") && describe != null) {
                            IOManager.writeFile(new File(path.getPath(), theme + "_describe.dsb"), describe, false);
                            describePath = new File(path.getPath(), theme + "_describe.dsb").getPath();
                        } else {
                            describePath = null;
                        }

                        create_writeFile(file_Path, file_Name, file_End, theme, describePath, coverPath);
                    })
                    .setNegativeButton(getString(R.string.text_button_negative_default), null)
                    .create()
                    .show();
        } else {
            IOManager.mkdir(path);
            if (IOManager.fileExists(new File(getExternalCacheDir(), "tempCover.jpg"))) {
                IOManager.moveFile(new File(getExternalCacheDir(), "tempCover.jpg"), new File(path.getPath(), theme + "_cover.jpg"), true);
                coverPath = new File(path.getPath(), theme + "_cover.jpg").getPath();
            } else {
                coverPath = null;
            }

            if (!Objects.equals(describe, "") && describe != null) {
                IOManager.writeFile(new File(path.getPath(), theme + "_describe.dsb"), describe, false);
                describePath = new File(path.getPath(), theme + "_describe.dsb").getPath();
            } else {
                describePath = null;
            }
            create_writeFile(file_Path, file_Name, file_End, theme, describePath, coverPath);
        }
    }

    private void create_writeFile(String File_Path, String File_Name, String File_End, String theme, String describePath, String coverPath) {
        File Notes_Contents = new File(getFilesDir().getPath() + File.separator + "Notes_Contents.ctt");

        List<NoteFragment.NoteTag> Tags = new ArrayList<>(IOManager.readNoteCtt(this, Notes_Contents));

        Notes_Contents.delete();
        IOManager.createNewFile(Notes_Contents);
        IOManager.writeFile(Notes_Contents, "#EasyNote\n" +
                ActivityManager.getAppVersionCode(this) + '\n', false);
        if (Tags.size() == 0) {
            IOManager.writeFile(Notes_Contents, IOManager.NOTE_TAG + '\n' +
                    IOManager.NOTE_DEFAULT_TAG_NAME + '\n' +
                    IOManager.NOTE_NOTE + '\n' +
                    File_Path + '\n' +
                    File_Name + '\n' +
                    File_End + '\n' +
                    theme + '\n' +
                    describePath + '\n' +
                    "0\n" +
                    coverPath + '\n' +
                    IOManager.NOTE_ENDNOTE + '\n' +
                    IOManager.NOTE_ENDTAG + '\n', true);
        } else {
            for (int i = 0; i < Tags.size(); i++) {
                int note_num = 0;
                StringBuilder str = new StringBuilder();

                str.append(IOManager.NOTE_TAG + '\n').append(Tags.get(i).getTitle()).append('\n');
                for (int j = 0; j < Tags.get(i).getNotes().size(); j++) {
                    str.append(IOManager.NOTE_NOTE + '\n');
                    str.append(Tags.get(i).getNotes().get(j).getFile_Path()).append('\n');
                    str.append(Tags.get(i).getNotes().get(j).getFile_Name()).append('\n');
                    str.append(Tags.get(i).getNotes().get(j).getFile_End()).append('\n');
                    str.append(Tags.get(i).getNotes().get(j).getTitle()).append('\n');
                    str.append(Tags.get(i).getNotes().get(j).getDescribe_Path()).append('\n');
                    str.append(Tags.get(i).getNotes().get(j).getIcon_ID()).append('\n');
                    str.append(Tags.get(i).getNotes().get(j).getBackground_Path()).append('\n');
                    str.append(IOManager.NOTE_ENDNOTE + '\n');
                }
                if (Objects.equals(Tags.get(i).getTitle(), IOManager.NOTE_DEFAULT_TAG_NAME) ||
                        Objects.equals(Tags.get(i).getTitle(), getString(R.string.text_fragment_note_mynotes_title))) {
                    str.append(IOManager.NOTE_NOTE + '\n');
                    str.append(File_Path).append('\n');
                    str.append(File_Name).append('\n');
                    str.append(File_End).append('\n');
                    str.append(theme).append('\n');
                    str.append(describePath).append('\n');
                    str.append(0).append('\n');
                    str.append(coverPath).append('\n');
                    str.append(IOManager.NOTE_ENDNOTE + '\n');
                    note_num ++;
                }
                if (note_num + Tags.get(i).getNotes().size() == 0) {
                    continue;
                }
                str.append(IOManager.NOTE_ENDTAG + '\n');
                IOManager.writeFile(Notes_Contents, str.toString(), true);
            }
        }

        File Note_File = new File(file_Path , file_Name + "." + file_End);
        IOManager.createNewFile(Note_File);

        Intent intent = new Intent("com.XYW.EasyNote.activity.CreateFile.refresh_noteList");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        intent = new Intent(CreateFile.this, NoteDoc.class);
        NoteFragment.Note note = new NoteFragment.Note(file_Path, file_Name, file_End, theme, describePath);
        intent.putExtra("note", note);
        intent.putExtra("EditMode", true);
        intent.putExtra("text_HTML", "");
        startActivity(intent);
        finish();
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,
                IOManager.PHOTO_REQUEST_GALLERY);
    }

    private void openCamera() {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        int cameraNum = Camera.getNumberOfCameras();
        // 判断是否有相机
        if (cameraNum > 0) {
            File photoFile = null;
            Uri photoUri = null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // 适配android 10
                photoUri = IOManager.createImageUri(this);
            } else {
                try {
                    photoFile = IOManager.createImageFile(this);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (photoFile != null) {
                    IOManager.imageFromCamera.setImagePathFromCamera(photoFile.getAbsolutePath());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        //适配Android 7.0文件权限，通过FileProvider创建一个content类型的Uri
                        photoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
                    } else {
                        photoUri = Uri.fromFile(photoFile);
                    }
                }
            }

            IOManager.imageFromCamera.setImageUriFromCamera(photoUri);
            if (photoUri != null) {
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(captureIntent, IOManager.PHOTO_REQUEST_CAREMA);
            }
        } else {
            WindowManager.showToast(this, getString(R.string.toast_camera_not_found));
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.ImageButton_toolbarHomeButton:
                intent = new Intent("com.XYW.EasyNote.activity.CreateFile.refresh_noteList");
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                finish();
                break;
            case R.id.ImageButton_toolbarDoneButton:
                create();
                break;
            case R.id.ImageButton_selectcoverimage:
                new MessageBox.CreateMessageBox.Builder(this)
                        .setTitle(getString(R.string.title_messagebox_select_cover))
                        .setMessage(getString(R.string.message_select_cover))
                        .setCancelable(true)
                        .setCanceledOnTouchOutside(true)
                        .setPositiveButton(getString(R.string.text_button_positive_camera), () -> {
                            if (!permissionManager.checkPermission()) {
                                permissionManager.checkPermissionWithRequest();
                            } else {
                                openCamera();
                            }
                        })
                        .setNegativeButton(getString(R.string.text_button_negative_album), this::selectImage)
                        .create()
                        .show();
                break;
            case R.id.TextView_createFile_clearcover:
                new MessageBox.CreateMessageBox.Builder(this)
                        .setTitle(getString(R.string.text_activity_createfile_clear))
                        .setMessage(getString(R.string.message_clear_cover))
                        .setCancelable(true)
                        .setCanceledOnTouchOutside(true)
                        .setPositiveButton(getString(R.string.text_button_positive_default), () -> {
                            Layout_content_create_notecover.setVisibility(View.GONE);
                            Layout_content_createfile_selecimage.setVisibility(View.VISIBLE);
                            TextView_createFile_clearcover.setVisibility(View.GONE);
                        })
                        .setNegativeButton(getString(R.string.text_button_negative_default), null)
                        .create()
                        .show();
                break;
        }
    }
}