package com.XYW.easynote.activity;

import static com.google.android.material.bottomnavigation.LabelVisibilityMode.LABEL_VISIBILITY_LABELED;
import static com.google.android.material.bottomnavigation.LabelVisibilityMode.LABEL_VISIBILITY_SELECTED;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.UiModeManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.XYW.easynote.Fragment.NoteFragment;
import com.XYW.easynote.Fragment.TodoFragment;
import com.XYW.easynote.R;
import com.XYW.easynote.util.ActivityManager;
import com.XYW.easynote.util.WindowManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.suke.widget.SwitchButton;

public class MainUI extends AppCompatActivity {

    private static final String TAG = "MainUI";

    private TextView TextView_toolbarTitle;
    private DrawerLayout DrawerLayout_MainUI;

    private SharedPreferences.Editor editor;
    private boolean darkMode = false, drawerOpen = false;
    private int system_ui_mode = UiModeManager.MODE_NIGHT_NO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainui);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager.addActivity(this);
        } else {
            ActivityManager.setActivity(this, findViewById(R.id.status_bar));
        }

        SharedPreferences preferences = getSharedPreferences("Settings", Context.MODE_MULTI_PROCESS);
        editor = getSharedPreferences("Settings", Context.MODE_MULTI_PROCESS).edit();
        darkMode = preferences.getBoolean("darkMode", false);

        if (savedInstanceState != null) {
            system_ui_mode = savedInstanceState.getInt("system_ui_mode", UiModeManager.MODE_NIGHT_NO);
            drawerOpen = savedInstanceState.getBoolean("drawerOpen", false);
        }

        init();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("system_ui_mode", system_ui_mode);
        outState.putBoolean("drawerOpen", drawerOpen);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.removeActivity(this);
    }

    private void init() {
        initDrawerLayout();
        initNavigationView();
        initToolbar();
        initTextView();
        initImageButton();
        initBottomNavigationView();
        replaceFragment(new NoteFragment());
    }

    private void initToolbar() {
        Toolbar toolbar_MainUI = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar_MainUI);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
        }
    }

    private void initTextView() {
        TextView_toolbarTitle = findViewById(R.id.TextView_toolbarTitle);
        TextView_toolbarTitle.setText(getString(R.string.title_note));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initImageButton() {
        ImageButton imageButton_toolbarHomeButton = findViewById(R.id.ImageButton_toolbarHomeButton);
        imageButton_toolbarHomeButton.setImageDrawable(getResources().getDrawable(R.drawable.general_menu));
        imageButton_toolbarHomeButton.setVisibility(View.VISIBLE);
        imageButton_toolbarHomeButton.setOnClickListener(view -> {
            if (!DrawerLayout_MainUI.isOpen()) {
                DrawerLayout_MainUI.open();
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    private void initBottomNavigationView() {
        BottomNavigationView bottomNavigationView_MainUI = findViewById(R.id.BottomNavigationView_MainUI);
        bottomNavigationView_MainUI.getMenu().getItem(0).setChecked(true);
        bottomNavigationView_MainUI.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_note:
                    TextView_toolbarTitle.setText(getString(R.string.title_note));
                    replaceFragment(new NoteFragment());
                    return true;
                case R.id.navigation_todo:
                    TextView_toolbarTitle.setText(getString(R.string.title_todo));
                    replaceFragment(new TodoFragment());
                    return true;
            }
            return false;
        });

        if (WindowManager.isScreenChange(this)) {
            bottomNavigationView_MainUI.setLabelVisibilityMode(LABEL_VISIBILITY_SELECTED);
        } else {
            bottomNavigationView_MainUI.setLabelVisibilityMode(LABEL_VISIBILITY_LABELED);
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
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
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
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                recreate();
            }
            switchButton_darkMode.setOnCheckedChangeListener((view, isChecked) -> {
                if (switchButton_darkMode.isChecked()) {
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor.putBoolean("darkMode", true).commit();
                } else {
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor.putBoolean("darkMode", false).commit();
                }
                recreate();
            });
        }

        switchButton_darkMode.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    DrawerLayout_MainUI.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN); //关闭手势滑动
                    break;
                case MotionEvent.ACTION_UP:
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
                    WindowManager.showToast(MainUI.this, getString(R.string.title_settings));
                    break;
                case R.id.navigation_darkmode:
                    switchButton_darkMode.setChecked(!switchButton_darkMode.isChecked());
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
                WindowManager.setWhiteStatusBar(window);
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
            WindowManager.setWhiteStatusBar(window);
            DrawerLayout_MainUI.open();
        } else {
            DrawerClosed(window);
            DrawerLayout_MainUI.close();
        }
    }

    private void DrawerClosed(Window window) {
        if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            WindowManager.setWhiteStatusBar(window);
        } else {
            WindowManager.setBlackStatusBar(window);
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.FrameLayout_MainUI, fragment);
        transaction.commitNow();
    }
}