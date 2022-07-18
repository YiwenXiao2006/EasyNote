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

public class ViewDocFragment extends Fragment {

    private Context context;
    private Activity activity;

    public ViewDocFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_doc, container, false);
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
}
