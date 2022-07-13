package com.XYW.easynote.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import androidx.percentlayout.widget.PercentRelativeLayout;

import com.XYW.easynote.R;
import com.XYW.easynote.ui.MessageBox;
import com.XYW.easynote.util.ActivityManager;
import com.XYW.easynote.util.IOManager;
import com.XYW.easynote.util.PermissionManager;
import com.XYW.easynote.util.UIManager;
import com.XYW.easynote.util.WindowManager;

import java.io.File;
import java.io.IOException;

public class CreateFile extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CreateFile";

    private ImageView ImageView_createExamSubject;
    private PercentRelativeLayout Layout_content_createfile_selecimage, Layout_content_create_notecover;
    private EditText EditText_createFile_theme, EditText_createFile_describe;
    private TextView TextView_createFile_clearcover;

    private PermissionManager permissionManager;
    private String theme, describe, coverPath;

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
                        Bitmap bitmap = BitmapFactory.decodeFile(IOManager.imageFromCamera.getImagePathFromCamera());
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
        ImageView_createExamSubject.setImageURI(uri);
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
            ImageView_createExamSubject.setImageBitmap(BitmapFactory.decodeFile(coverPath));
        }
    }

    private void initTextView() {
        TextView TextView_toolbarTitle = findViewById(R.id.TextView_toolbarTitle);
        TextView_toolbarTitle.setText(getString(R.string.title_newfile));

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
        ImageView_createExamSubject = findViewById(R.id.ImageView_createExamSubject);
    }

    private boolean create() {
        theme = EditText_createFile_theme.getText().toString();
        describe = EditText_createFile_describe.getText().toString();
        return true;
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,
                IOManager.PHOTO_REQUEST_GALLERY);
    }

    private void openCamera() {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 判断是否有相机
        if (captureIntent.resolveActivity(getPackageManager()) != null) {
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
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ImageButton_toolbarHomeButton:
                finish();
                break;
            case R.id.ImageButton_toolbarDoneButton:
                if (create()) {
                    finish();
                }
                break;
            case R.id.ImageButton_selectcoverimage:
                new MessageBox.CreateMessageBox.Builder(this)
                        .setTitle(getString(R.string.title_messagebox_selectcover))
                        .setMessage(getString(R.string.message_selectcover))
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
                        .setMessage(getString(R.string.message_clearcover))
                        .setCancelable(true)
                        .setCanceledOnTouchOutside(true)
                        .setPositiveButton(getString(R.string.text_button_positive_defult), () -> {
                            Layout_content_create_notecover.setVisibility(View.GONE);
                            Layout_content_createfile_selecimage.setVisibility(View.VISIBLE);
                            TextView_createFile_clearcover.setVisibility(View.GONE);
                        })
                        .setNegativeButton(getString(R.string.text_button_negative_defult), null)
                        .create()
                        .show();
                break;
        }
    }
}