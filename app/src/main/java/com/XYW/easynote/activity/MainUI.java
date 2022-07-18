package com.XYW.easynote.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.XYW.easynote.Fragment.NoteFragment;
import com.XYW.easynote.Fragment.TodoFragment;
import com.XYW.easynote.R;
import com.XYW.easynote.ui.DetailViewPager;
import com.XYW.easynote.ui.MessageBox;
import com.XYW.easynote.ui.adapter.ListPopupItem;
import com.XYW.easynote.ui.adapter.ListPopupWindowAdapter;
import com.XYW.easynote.util.ActivityManager;
import com.XYW.easynote.util.PermissionManager;
import com.XYW.easynote.util.WindowManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.suke.widget.SwitchButton;

import java.util.ArrayList;
import java.util.List;

public class MainUI extends AppCompatActivity {

    private static final String TAG = "MainUI";

    private TextView TextView_toolbarTitle;
    private DrawerLayout DrawerLayout_MainUI;
    private BottomNavigationView BottomNavigationView_MainUI;
    private DetailViewPager DetailViewPager_MainUI;
    private ListPopupWindow ListPopupWindow_MainUI_menu;

    private PermissionManager permissionManager;
    private SharedPreferences.Editor editor;
    private boolean darkMode = false, drawerOpen = false;
    private int system_ui_mode = UiModeManager.MODE_NIGHT_NO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainui);
        ActivityManager.setActivity(this, findViewById(R.id.status_bar));

        SharedPreferences preferences = getSharedPreferences("Settings", Context.MODE_MULTI_PROCESS);
        editor = getSharedPreferences("Settings", Context.MODE_MULTI_PROCESS).edit();
        darkMode = preferences.getBoolean("darkMode", false);

        if (savedInstanceState != null) {
            system_ui_mode = savedInstanceState.getInt("system_ui_mode", UiModeManager.MODE_NIGHT_NO);
            drawerOpen = savedInstanceState.getBoolean("drawerOpen", false);
        }

        init();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onResume () {
        super.onResume();
        initTextView();
        if (BottomNavigationView_MainUI != null && DetailViewPager_MainUI != null) {
            switch (BottomNavigationView_MainUI.getSelectedItemId()) {
                case R.id.navigation_note:
                    DetailViewPager_MainUI.setCurrentItem(0);
                    TextView_toolbarTitle.setText(getString(R.string.title_note));
                    break;
                case R.id.navigation_todo:
                    DetailViewPager_MainUI.setCurrentItem(1);
                    TextView_toolbarTitle.setText(getString(R.string.title_todo));
                    break;
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("system_ui_mode", system_ui_mode);
        outState.putBoolean("drawerOpen", drawerOpen);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (ListPopupWindow_MainUI_menu != null) {
            ListPopupWindow_MainUI_menu.dismiss();
        }
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
                permissionManager.onResult(grantResults, permissions, getString(R.string.message_permission_denied_storage_2),
                        getString(R.string.message_permission_denied_storage_1), ActivityManager::finishAllActivity);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            permissionManager.checkPermissionWithRequest();
        }
    }

    private void init() {
        permissionManager = new PermissionManager(this, this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE});
        permissionManager.checkPermissionWithRequest();

        initDrawerLayout();
        initNavigationView();
        initToolbar();
        initTextView();
        initImageButton();
        initBottomNavigationView();
        initViewPager();
    }

    private void initToolbar() {
        Toolbar toolbar_MainUI = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar_MainUI);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    private void initTextView() {
        TextView_toolbarTitle = findViewById(R.id.TextView_toolbarTitle);
        TextView_toolbarTitle.setText(getString(R.string.title_note));
    }

    @SuppressLint({"RestrictedApi", "NonConstantResourceId"})
    private void initImageButton() {
        ImageButton imageButton_toolbarHomeButton = findViewById(R.id.ImageButton_toolbarHomeButton),
                    imageButton_toolbarDoneButton = findViewById(R.id.ImageButton_toolbarDoneButton);
        imageButton_toolbarHomeButton.setImageDrawable(ContextCompat.getDrawable(MainUI.this, R.drawable.general_menu));
        imageButton_toolbarHomeButton.setVisibility(View.VISIBLE);
        imageButton_toolbarHomeButton.setOnClickListener(view -> {
            if (!DrawerLayout_MainUI.isOpen()) {
                DrawerLayout_MainUI.open();
            }
        });

        imageButton_toolbarDoneButton.setImageDrawable(ContextCompat.getDrawable(MainUI.this, R.drawable.interactive_more_vertical));
        imageButton_toolbarDoneButton.setVisibility(View.VISIBLE);
        imageButton_toolbarDoneButton.setOnClickListener(view -> {
            List<ListPopupItem> listPopupItems = new ArrayList<>();
            switch (BottomNavigationView_MainUI.getSelectedItemId()) {
                case R.id.navigation_note:
                    listPopupItems.add(new ListPopupItem(getString(R.string.title_menu_recently), R.drawable.interactive_history));
                    listPopupItems.add(new ListPopupItem(getString(R.string.title_menu_newnote), R.drawable.general_file));
                    listPopupItems.add(new ListPopupItem(getString(R.string.title_menu_tags), R.drawable.general_tags));
                    break;
                case R.id.navigation_todo:
                    break;
            }
            showListMenu(view, listPopupItems);
        });
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
        ListPopupWindow_MainUI_menu = createListPopupWindow(anchor, listPopupItems);
        ListPopupWindow_MainUI_menu.setOnItemClickListener((adapterView, view, i, l) -> {
            switch (BottomNavigationView_MainUI.getSelectedItemId()) {
                case R.id.navigation_note:
                    switch (i) {
                        case 0:
                            WindowManager.showToast(MainUI.this, getString(R.string.title_menu_recently));
                            break;
                        case 1:
                            Intent intent = new Intent(MainUI.this, CreateFile.class);
                            startActivity(intent);
                            break;
                        case 2:WindowManager.showToast(MainUI.this, getString(R.string.title_menu_tags));
                            break;
                    }
                    ListPopupWindow_MainUI_menu.dismiss();
                    break;
                case R.id.navigation_todo:
                    break;
            }
        }); // the callback for when a list item is selected
        ListPopupWindow_MainUI_menu.show();
    }

    @SuppressLint("NonConstantResourceId")
    private void initBottomNavigationView() {
        BottomNavigationView_MainUI = findViewById(R.id.BottomNavigationView_MainUI);
        BottomNavigationView_MainUI.getMenu().getItem(0).setChecked(true);
        BottomNavigationView_MainUI.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_note:
                    TextView_toolbarTitle.setText(getString(R.string.title_note));
                    DetailViewPager_MainUI.setCurrentItem(0);
                    return true;
                case R.id.navigation_todo:
                    TextView_toolbarTitle.setText(getString(R.string.title_todo));
                    DetailViewPager_MainUI.setCurrentItem(1);
                    return true;
            }
            return false;
        });

        if (WindowManager.isScreenChange(this)) {
            BottomNavigationView_MainUI.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_SELECTED);
        } else {
            BottomNavigationView_MainUI.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
        }
    }

    private void initViewPager() {
        DetailViewPager_MainUI = findViewById(R.id.DetailViewPager_MainUI);
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        DetailViewPager_MainUI.setAdapter(adapter);
        DetailViewPager_MainUI.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                BottomNavigationView_MainUI.getMenu().getItem(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        DetailViewPager_MainUI.setCanCurrent(false);
        DetailViewPager_MainUI.setCanSwipe(false);
    }

    private static class PagerAdapter extends FragmentPagerAdapter {
        private static final int NUM_PAGES = 2;
        Fragment noteFragment;
        Fragment todoFragment;

        PagerAdapter(FragmentManager fm) {
            super(fm);
            noteFragment = new NoteFragment();
            todoFragment = new TodoFragment();
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return noteFragment;
                default:
                    return todoFragment;
            }
        }

        int getTitle(int position) {
            switch (position) {
                case 0:
                    return R.string.title_note;
                default:
                    return R.string.title_todo;
            }
        }
    }

    @SuppressLint({"NonConstantResourceId", "ClickableViewAccessibility"})
    private void initNavigationView() {
        NavigationView navigationView_drawerlayout = findViewById(R.id.NavigationView_drawerlayout);

        Menu menu = navigationView_drawerlayout.getMenu();
        MenuItem item = menu.findItem(R.id.navigation_darkmode);
        LinearLayout linearLayout = (LinearLayout) item.getActionView();
        SwitchButton switchButton_darkMode = linearLayout.findViewById(R.id.SwitchButton_darkmode);

        UiModeManager uiModeManager = (UiModeManager) getSystemService(UI_MODE_SERVICE);
        if (system_ui_mode != uiModeManager.getNightMode() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES) {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
            system_ui_mode = uiModeManager.getNightMode();
            recreate();
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1 || uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES) {
            item.setEnabled(false);
            switchButton_darkMode.setChecked(!(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1));
            switchButton_darkMode.setEnabled(false);
        } else {
            switchButton_darkMode.setChecked(darkMode);
            if (darkMode && (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) != Configuration.UI_MODE_NIGHT_YES) {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                recreate();
            } else if (!darkMode && (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                recreate();
            }
            switchButton_darkMode.setOnCheckedChangeListener((view, isChecked) -> {
                if (switchButton_darkMode.isChecked()) {
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor.putBoolean("darkMode", true).commit();
                } else {
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    editor.putBoolean("darkMode", false).commit();
                }
                recreate();
            });
        }

        switchButton_darkMode.setOnTouchListener((view, motionEvent) -> {
            Log.d(TAG, "initNavigationView: " + motionEvent.getAction());
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    DrawerLayout_MainUI.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN); //关闭手势滑动
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    DrawerLayout_MainUI.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);    //开启手势滑动
                    break;
                default:
                    break;
            }
            return false;
        });

        navigationView_drawerlayout.setNavigationItemSelectedListener(item1 -> {
            switch (item1.getItemId()) {
                case R.id.navigation_settings:
                    WindowManager.showToast(MainUI.this, getString(R.string.title_nav_settings));
                    break;
                case R.id.navigation_darkmode:
                    switchButton_darkMode.setChecked(!switchButton_darkMode.isChecked());
                    break;
                case R.id.navigation_exit:
                    new MessageBox.CreateMessageBox.Builder(MainUI.this)
                            .setTitle(getString(R.string.title_nav_exit))
                            .setMessage(getString(R.string.message_exit))
                            .setCancelable(true)
                            .setCanceledOnTouchOutside(true)
                            .setPositiveButton(getString(R.string.text_button_positive_default), this::finish)
                            .setNegativeButton(getString(R.string.text_button_negative_default), null)
                            .create()
                            .show();
                break;
                default:
                    break;
            }
            return false;
        });
        View View_nav_status_bar = navigationView_drawerlayout.inflateHeaderView(R.layout.content_nav_drawerlayout_header).findViewById(R.id.nav_status_bar);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            View_nav_status_bar.setVisibility(View.GONE);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isInMultiWindowMode()) {
                View_nav_status_bar.setVisibility(View.GONE);
            }
            ViewGroup.LayoutParams params = View_nav_status_bar.getLayoutParams();
            params.height = WindowManager.getStatusBarHeight(this, this);
            View_nav_status_bar.setLayoutParams(params);
        }
    }

    private void initDrawerLayout() {
        Window window = getWindow();
        DrawerLayout_MainUI = findViewById(R.id.DrawerLayout_MainUI);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        DrawerLayout_MainUI.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                WindowManager.setWhiteStatusBar(window, Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP);
                drawerOpen = true;
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                DrawerClosed(window);
                drawerOpen = false;
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        if (drawerOpen) {
            WindowManager.setWhiteStatusBar(window, Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP);
            DrawerLayout_MainUI.open();
        } else {
            DrawerClosed(window);
            DrawerLayout_MainUI.close();
        }
    }

    private void DrawerClosed(Window window) {
        if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            WindowManager.setWhiteStatusBar(window, Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP);
        } else {
            WindowManager.setBlackStatusBar(window, Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP);
        }
    }
}