package com.XYW.easynote.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.XYW.easynote.R;
import com.XYW.easynote.ui.MessageBox;
import com.XYW.easynote.ui.adapter.ListPopupItem;
import com.XYW.easynote.ui.adapter.ListPopupWindowAdapter;
import com.XYW.easynote.util.ActivityManager;
import com.XYW.easynote.util.IOManager;
import com.XYW.easynote.util.WindowManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jp.wasabeef.richeditor.RichEditor;

public class NoteDoc extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "NoteDoc";

    private RichEditor RichEditor_EditDoc;
    private ListPopupWindow ListPopupWindow_NoteDoc_menu;
    private TextView TextView_toolbarTitle;
    private HorizontalScrollView HorizontalScrollView_EditDoc_Tools;

    private String title_File_Theme, file_Path, file_Name, file_End, text_HTML;
    private boolean EditMode = false;
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (!EditMode) {
                return super.onKeyDown(keyCode, event);
            }
            if((System.currentTimeMillis() - exitTime) > 2000) {
                WindowManager.showToast(this, getString(R.string.toast_one_more_time_back));
                exitTime = System.currentTimeMillis();
            } else {
                save();
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_doc);
        ActivityManager.setActivity(this, findViewById(R.id.status_bar));
        ActivityManager.setDarkMode(this);

        init(getIntent(), savedInstanceState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (ListPopupWindow_NoteDoc_menu != null) {
            ListPopupWindow_NoteDoc_menu.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.removeActivity(this);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("EditMode", EditMode);
        outState.putString("title", title_File_Theme);
        outState.putString("filePath", file_Path);
        outState.putString("fileName", file_Name);
        outState.putString("fileEnd", file_End);
        outState.putString("text_HTML", text_HTML);
    }

    private ListPopupWindow createListPopupWindow(View anchor, List<ListPopupItem> items) {
        final ListPopupWindow popup = new ListPopupWindow(this);
        ListPopupWindowAdapter adapter = new ListPopupWindowAdapter(items);
        popup.setAnchorView(anchor);
        popup.setWidth(getResources().getDimensionPixelSize(R.dimen.popupmenu_width));
        popup.setAdapter(adapter);
        return popup;
    }

    // Call this when you want to show the ListPopupWindow
    @SuppressLint("NonConstantResourceId")
    private void showListMenu(View anchor, List<ListPopupItem> listPopupItems) {
        ListPopupWindow_NoteDoc_menu = createListPopupWindow(anchor, listPopupItems);
        ListPopupWindow_NoteDoc_menu.setOnItemClickListener((adapterView, view, i, l) -> {
            switch (i) {
                case 0:
                    save();
                    break;
                case 1:
                    String title;
                    if (EditMode) {
                        title = title_File_Theme + " ("  + getString(R.string.text_activity_view_mode) + ")";
                    } else {
                        title = title_File_Theme + " ("  + getString(R.string.text_activity_edit_mode) + ")";
                    }
                    TextView_toolbarTitle.setText(title);
                    EditMode = !EditMode;
                    RichEditor_EditDoc.setInputEnabled(EditMode);
                    HorizontalScrollView_EditDoc_Tools.setVisibility(EditMode ? View.VISIBLE : View.GONE);
                    break;
                case 2:
                    if (file_Path != null) {
                        new MessageBox.CreateMessageBox.Builder(this)
                                .setTitle(getString(R.string.title_messagebox_delete))
                                .setMessage(getString(R.string.message_delete_note) + " \"" + title_File_Theme + "\"?")
                                .setIcon(R.drawable.edit_delete)
                                .setCancelable(true)
                                .setCanceledOnTouchOutside(true)
                                .setPositiveButton(getString(R.string.text_button_positive_default), () -> {
                                    File file = new File(file_Path);
                                    IOManager.deleteDir(file);

                                    IOManager.deleteNoteInCtt(NoteDoc.this, title_File_Theme);

                                    Intent intent = new Intent("com.XYW.EasyNote.activity.CreateFile.refresh_noteList");
                                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                                    finish();
                                })
                                .setNegativeButton(getString(R.string.text_button_negative_default), null)
                                .create()
                                .show();
                    }
                    break;
            }
            ListPopupWindow_NoteDoc_menu.dismiss();
        }); // the callback for when a list item is selected
        ListPopupWindow_NoteDoc_menu.show();
    }

    private void init(Intent intent, Bundle bundle) {
        if (intent != null) {
            EditMode = intent.getBooleanExtra("EditMode", false);
            title_File_Theme = intent.getStringExtra("title");
            file_Path = intent.getStringExtra("filePath");
            file_Name = intent.getStringExtra("fileName");
            file_End = intent.getStringExtra("fileEnd");
            text_HTML = intent.getStringExtra("text_HTML");
        }
        if (bundle != null) {
            EditMode = bundle.getBoolean("EditMode", false);
            title_File_Theme = bundle.getString("title", getString(R.string.title_note_doc));
            file_Path = bundle.getString("filePath", null);
            file_Name = bundle.getString("fileName", null);
            file_End = bundle.getString("fileEnd", null);
            text_HTML = bundle.getString("text_HTML", null);
        }

        WindowManager windowManager = new WindowManager();
        windowManager.KeyBoardListen(this, this);

        initTextView();
        initImageButton();
        initTextEditor();
    }

    private void initTextView() {
        if (Objects.equals(title_File_Theme, "") || title_File_Theme == null) {
            title_File_Theme = getString(R.string.title_note_doc);
        }
        String title;
        if (EditMode) {
            title = title_File_Theme + " ("  + getString(R.string.text_activity_edit_mode) + ")";
        } else {
            title = title_File_Theme + " ("  + getString(R.string.text_activity_view_mode) + ")";
        }
        TextView_toolbarTitle = findViewById(R.id.TextView_toolbarTitle);
        TextView_toolbarTitle.setText(title);
    }

    private void initImageButton() {
        ImageButton imageButton_toolbarHomeButton = findViewById(R.id.ImageButton_toolbarHomeButton);
        imageButton_toolbarHomeButton.setImageDrawable(ContextCompat.getDrawable(NoteDoc.this, R.drawable.direction_left));
        imageButton_toolbarHomeButton.setVisibility(View.VISIBLE);
        imageButton_toolbarHomeButton.setOnClickListener(this);

        ImageButton imageButton_toolbarDoneButton = findViewById(R.id.ImageButton_toolbarDoneButton);
        imageButton_toolbarDoneButton.setImageDrawable(ContextCompat.getDrawable(NoteDoc.this, R.drawable.interactive_more_vertical));
        imageButton_toolbarDoneButton.setVisibility(View.VISIBLE);
        imageButton_toolbarDoneButton.setOnClickListener(this);



        findViewById(R.id.action_undo).setOnClickListener(v -> RichEditor_EditDoc.undo());

        findViewById(R.id.action_redo).setOnClickListener(v -> RichEditor_EditDoc.redo());

        findViewById(R.id.action_bold).setOnClickListener(v -> RichEditor_EditDoc.setBold());

        findViewById(R.id.action_italic).setOnClickListener(v -> RichEditor_EditDoc.setItalic());

        findViewById(R.id.action_strikethrough).setOnClickListener(v -> RichEditor_EditDoc.setStrikeThrough());

        findViewById(R.id.action_underline).setOnClickListener(v -> RichEditor_EditDoc.setUnderline());

        findViewById(R.id.action_heading1).setOnClickListener(v -> RichEditor_EditDoc.setHeading(1));

        findViewById(R.id.action_heading2).setOnClickListener(v -> RichEditor_EditDoc.setHeading(2));

        findViewById(R.id.action_heading3).setOnClickListener(v -> RichEditor_EditDoc.setHeading(3));

        findViewById(R.id.action_heading4).setOnClickListener(v -> RichEditor_EditDoc.setHeading(4));

        findViewById(R.id.action_heading5).setOnClickListener(v -> RichEditor_EditDoc.setHeading(5));

        findViewById(R.id.action_heading6).setOnClickListener(v -> RichEditor_EditDoc.setHeading(6));

        findViewById(R.id.action_txt_color).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override
            public void onClick(View v) {
                RichEditor_EditDoc.setTextColor(isChanged ? Color.BLACK : Color.RED);
                isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_bg_color).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override
            public void onClick(View v) {
                RichEditor_EditDoc.setTextBackgroundColor(isChanged ? Color.TRANSPARENT : Color.YELLOW);
                isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_align_left).setOnClickListener(v -> RichEditor_EditDoc.setAlignLeft());

        findViewById(R.id.action_align_center).setOnClickListener(v -> RichEditor_EditDoc.setAlignCenter());

        findViewById(R.id.action_align_right).setOnClickListener(v -> RichEditor_EditDoc.setAlignRight());

        findViewById(R.id.action_blockquote).setOnClickListener(v -> RichEditor_EditDoc.setBlockquote());

        findViewById(R.id.action_insert_bullets).setOnClickListener(v -> RichEditor_EditDoc.setBullets());

        findViewById(R.id.action_insert_numbers).setOnClickListener(v -> RichEditor_EditDoc.setNumbers());

        findViewById(R.id.action_insert_image).setOnClickListener(v -> {
            RichEditor_EditDoc.insertImage("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg", "dachshund", 320);
        });

        findViewById(R.id.action_insert_audio).setOnClickListener(v -> {
            RichEditor_EditDoc.insertAudio("https://file-examples-com.github.io/uploads/2017/11/file_example_MP3_5MG.mp3");
        });

        findViewById(R.id.action_insert_video).setOnClickListener(v -> {
            RichEditor_EditDoc.insertVideo("https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/1080/Big_Buck_Bunny_1080_10s_10MB.mp4", 360);
        });

        findViewById(R.id.action_insert_link).setOnClickListener(v -> RichEditor_EditDoc.insertLink("https://www.baidu.com/", "Baidu"));
        findViewById(R.id.action_insert_checkbox).setOnClickListener(v -> RichEditor_EditDoc.insertTodo());
    }

    private void initTextEditor() {
        RichEditor_EditDoc = (RichEditor) findViewById(R.id.RichEditor_edit);

        //初始化编辑高度
        RichEditor_EditDoc.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                RichEditor_EditDoc.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                RichEditor_EditDoc.setEditorHeight(RichEditor_EditDoc.getHeight());
            }
        });
        //初始化字体大小
        RichEditor_EditDoc.setEditorFontSize(22);
        //初始化字体颜色
        RichEditor_EditDoc.setEditorFontColor(Color.BLACK);
        //mEditor.setEditorBackgroundColor(Color.BLUE);

        //初始化内边距
        RichEditor_EditDoc.setPadding(10, 10, 10, 10);
        //设置编辑框背景，可以是网络图片
        // mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
        RichEditor_EditDoc.setBackgroundColor(Color.TRANSPARENT);
        // 设置默认显示语句
        if (EditMode)
            RichEditor_EditDoc.setPlaceholder(getString(R.string.text_activity_edit_here));
        //设置编辑器是否可用
        RichEditor_EditDoc.setInputEnabled(EditMode);
        if (text_HTML != null && !Objects.equals(text_HTML, "")) {
            RichEditor_EditDoc.setHtml(text_HTML);
        }
        RichEditor_EditDoc.setOnTextChangeListener(text -> text_HTML = RichEditor_EditDoc.getHtml());

        HorizontalScrollView_EditDoc_Tools = findViewById(R.id.HorizontalScrollView_EditDoc_Tools);
        HorizontalScrollView_EditDoc_Tools.setVisibility(EditMode ? View.VISIBLE : View.GONE);
    }

    private void save() {
        File data = new File(file_Path, file_Name + "." + file_End);
        if (!IOManager.fileExists(data)) {
            IOManager.createNewFile(data);
        }
        IOManager.writeFile(data, text_HTML, false);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ImageButton_toolbarHomeButton:
                save();
                finish();
                break;
            case R.id.ImageButton_toolbarDoneButton:
                List<ListPopupItem> listPopupItems = new ArrayList<>();
                listPopupItems.add(new ListPopupItem(getString(R.string.title_menu_save), R.drawable.interactive_save));
                if (EditMode) {
                    listPopupItems.add(new ListPopupItem(getString(R.string.title_menu_view_notes), R.drawable.interactive_eye));
                } else {
                    listPopupItems.add(new ListPopupItem(getString(R.string.title_menu_edit), R.drawable.edit_edit));
                }
                listPopupItems.add(new ListPopupItem(getString(R.string.title_menu_delete), R.drawable.edit_delete));
                showListMenu(view, listPopupItems);
                break;
        }
    }
}