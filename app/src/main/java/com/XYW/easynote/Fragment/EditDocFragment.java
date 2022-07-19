package com.XYW.easynote.Fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.XYW.easynote.R;
import com.XYW.easynote.util.WindowManager;
import com.ns.yc.yccustomtextlib.edit.inter.OnHyperEditListener;
import com.ns.yc.yccustomtextlib.edit.view.HyperTextEditor;

public class EditDocFragment extends Fragment implements View.OnClickListener {

    private HyperTextEditor HTE_edit_doc;

    private Context context;
    private Activity activity;

    public EditDocFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_doc, container, false);
        init(view);
        return view;
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private void init(View view) {
        WindowManager windowManager = new WindowManager();
        windowManager.KeyBoardListen(context, activity);

        initHyperTextEditor(view);
        initTextView(view);
    }

    private void initHyperTextEditor(View view) {
        HTE_edit_doc = view.findViewById(R.id.HTE_edit_doc);
        //解决点击EditText弹出收起键盘时出现的黑屏闪现现象
        View rootView = HTE_edit_doc.getRootView();
        rootView.setBackgroundColor(Color.WHITE);
        HTE_edit_doc.postDelayed(() -> {
            EditText lastFocusEdit = HTE_edit_doc.getLastFocusEdit();
            lastFocusEdit.requestFocus();
        },300);

        HTE_edit_doc.setOnHyperListener(new OnHyperEditListener() {
            @Override
            public void onImageClick(View view, String imagePath) {
                //图片点击事件
            }

            @Override
            public void onRtImageDelete(String imagePath) {
                //图片删除成功事件
                WindowManager.showToast(context, "图片删除成功");
            }

            @Override
            public void onImageCloseClick(final View view) {
                //图片删除图片点击事件
                //HTE_edit_doc.onImageCloseClick(view);
            }
        });
    }

    private void initTextView(View view) {
        view.findViewById(R.id.TextView_Edit_Bold).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.TextView_Edit_Bold:
                HTE_edit_doc.bold();
                break;
            default:
                break;
        }
    }
}
