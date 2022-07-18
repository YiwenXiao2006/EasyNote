package com.XYW.easynote.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.XYW.easynote.Fragment.EditDocFragment;
import com.XYW.easynote.Fragment.ViewDocFragment;
import com.XYW.easynote.R;
import com.XYW.easynote.ui.MessageBox;
import com.XYW.easynote.ui.adapter.ListPopupItem;
import com.XYW.easynote.ui.adapter.ListPopupWindowAdapter;
import com.XYW.easynote.util.ActivityManager;
import com.XYW.easynote.util.IOManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NoteDoc extends AppCompatActivity implements View.OnClickListener {

    private ListPopupWindow ListPopupWindow_NoteDoc_menu;
    private TextView TextView_toolbarTitle;

    private String title_File_Theme, file_Path, file_Name, file_End;
    private boolean EditMode = false;

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
        outState.putString("title", title_File_Theme);
        outState.putBoolean("EditMode", EditMode);
        outState.putString("filePath", file_Path);
        outState.putString("fileName", file_Name);
        outState.putString("fileEnd", file_End);
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
                    String title;
                    if (EditMode) {
                        changeFragment(new ViewDocFragment(), R.id.FrameLayout_NoteDoc);
                        title = title_File_Theme + " ("  + getString(R.string.text_activity_view_mode) + ")";
                    } else {
                        changeFragment(new EditDocFragment(), R.id.FrameLayout_NoteDoc);
                        title = title_File_Theme + " ("  + getString(R.string.text_activity_edit_mode) + ")";
                    }
                    TextView_toolbarTitle.setText(title);
                    EditMode = !EditMode;
                    break;
                case 1:
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
            title_File_Theme = intent.getStringExtra("title");
            file_Path = intent.getStringExtra("filePath");
            file_Name = intent.getStringExtra("fileName");
            file_End = intent.getStringExtra("fileEnd");
        }
        if (bundle != null) {
            title_File_Theme = bundle.getString("title", getString(R.string.title_note_doc));
            EditMode = bundle.getBoolean("EditMode", false);
            file_Path = bundle.getString("filePath", null);
            file_Name = bundle.getString("fileName", null);
            file_End = bundle.getString("fileEnd", null);
        }
        initTextView();
        initImageButton();
        initFrameLayout();
    }

    private void initTextView() {
        if (Objects.equals(title_File_Theme, "") || title_File_Theme == null) {
            title_File_Theme = getString(R.string.title_note_doc);
        }
        String title = title_File_Theme + " ("  + getString(R.string.text_activity_view_mode) + ")";
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
    }

    private void initFrameLayout() {
        changeFragment(new ViewDocFragment(), R.id.FrameLayout_NoteDoc);
    }

    private void changeFragment(Fragment fragment, int frameLayout) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(frameLayout, fragment);
        fragmentTransaction.commit();
    }

    private void save() {

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