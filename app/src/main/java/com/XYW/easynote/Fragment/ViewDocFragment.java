package com.XYW.easynote.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.XYW.easynote.R;
import com.ns.yc.yccustomtextlib.edit.view.HyperTextView;

public class ViewDocFragment extends Fragment {

    private HyperTextView HTV_view_doc;

    private Context context;
    private Activity activity;

    public ViewDocFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_doc, container, false);
        init(view);
        return view;
    }

    @Override
    public void onAttach (@NonNull Activity activity){
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onAttach (@NonNull Context context){
        super.onAttach(context);
        this.context = context;
    }

    private void init(View view) {
        initHyperTextView(view);
    }

    private void initHyperTextView(View view) {
        HTV_view_doc = view.findViewById(R.id.HTV_view_doc);
    }
}
