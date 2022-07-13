package com.XYW.easynote.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ColorMatrixColorFilter;
import android.os.Build;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.XYW.easynote.R;

public class UIManager {

    private static final String TAG = "UIManager";

    public static final View.OnTouchListener TouchLight = new View.OnTouchListener() {

        public final float[] BT_SELECTED = new float[] {1,0,0,0,50,0,1,0,0,50,0,0,1,0,50,0,0,0,1,0};
        public final float[] BT_NOT_SELECTED = new float[] {1,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,1,0};

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.getBackground().setColorFilter(
                        new ColorMatrixColorFilter(BT_SELECTED));
                v.setBackgroundDrawable(v.getBackground());
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                v.getBackground().setColorFilter(
                        new ColorMatrixColorFilter(BT_NOT_SELECTED));
                v.setBackgroundDrawable(v.getBackground());
            }
            return false;
        }
    };

    public static final View.OnTouchListener TouchDark = new View.OnTouchListener() {

        public final float[] BT_SELECTED = new float[] {1,0,0,0,-50,0,1,0,0,-50,0,0,1,0,-50,0,0,0,1,0};
        public final float[] BT_NOT_SELECTED = new float[] {1,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,1,0};

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.getBackground().setColorFilter(
                        new ColorMatrixColorFilter(BT_SELECTED));
                v.setBackgroundDrawable(v.getBackground());
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                v.getBackground().setColorFilter(
                        new ColorMatrixColorFilter(BT_NOT_SELECTED));
                v.setBackgroundDrawable(v.getBackground());
            }
            return false;
        }
    };

    public static class FullyLinearLayoutManager extends LinearLayoutManager {

        public FullyLinearLayoutManager(Context context) {
            super(context);
        }

        private final int[] mMeasuredDimension = new int[2];
        private boolean mCanVerticalScroll = true;

        @Override
        public boolean canScrollVertically() {
            if (!mCanVerticalScroll){
                return false;
            }else {
                return super.canScrollVertically();
            }
        }

        public void setmCanVerticalScroll(boolean b) {
            mCanVerticalScroll = b;
        }

        @Override
        public void onMeasure(@NonNull RecyclerView.Recycler recycler, @NonNull RecyclerView.State state,
                              int widthSpec, int heightSpec) {

            final int widthMode = View.MeasureSpec.getMode(widthSpec);
            final int heightMode = View.MeasureSpec.getMode(heightSpec);
            final int widthSize = View.MeasureSpec.getSize(widthSpec);
            final int heightSize = View.MeasureSpec.getSize(heightSpec);

            int width = 0;
            int height = 0;
            for (int i = 0; i < getItemCount(); i++) {
                measureScrapChild(recycler, i,
                        View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
                        mMeasuredDimension);

                if (getOrientation() == HORIZONTAL) {
                    width = width + mMeasuredDimension[0];
                    if (i == 0) {
                        height = mMeasuredDimension[1];
                    }
                } else {
                    height = height + mMeasuredDimension[1];
                    if (i == 0) {
                        width = mMeasuredDimension[0];
                    }
                }
            }
            switch (widthMode) {
                case View.MeasureSpec.EXACTLY:
                    width = widthSize;
                case View.MeasureSpec.AT_MOST:
                case View.MeasureSpec.UNSPECIFIED:
            }

            switch (heightMode) {
                case View.MeasureSpec.EXACTLY:
                    height = heightSize;
                case View.MeasureSpec.AT_MOST:
                case View.MeasureSpec.UNSPECIFIED:
            }
            setMeasuredDimension(width, height);
        }

        private void measureScrapChild(RecyclerView.Recycler recycler, int position, int widthSpec,
                                       int heightSpec, int[] measuredDimension) {
            try {
                View view = recycler.getViewForPosition(0);//fix 动态添加时报IndexOutOfBoundsException

                RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) view.getLayoutParams();

                int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec,
                        getPaddingLeft() + getPaddingRight(), p.width);

                int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec,
                        getPaddingTop() + getPaddingBottom(), p.height);

                view.measure(childWidthSpec, childHeightSpec);
                measuredDimension[0] = view.getMeasuredWidth() + p.leftMargin + p.rightMargin;
                measuredDimension[1] = view.getMeasuredHeight() + p.bottomMargin + p.topMargin;
                recycler.recycleView(view);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface HideScrollListener {
        public void onHide();
        public void onShow();
    }

    public static class RecyclerViewScrollListener extends RecyclerView.OnScrollListener{

        private static final int THRESHOLD = 20;
        private int distance = 0;
        private final HideScrollListener hideListener;
        private boolean visible = true;

        public RecyclerViewScrollListener(HideScrollListener hideScrollListener) {
            this.hideListener = hideScrollListener;
        }
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (distance > THRESHOLD && visible) {
                hideListener.onHide();
                visible = false;
                distance = 0;
            } else if (distance < -20 && !visible) {
                hideListener.onShow();
                visible = true;
                distance = 0;
            }
            if (visible && dy > 0 || (!visible && dy < 0)) {
                distance += dy;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static class ViewScrollListener implements View.OnScrollChangeListener {

        private final HideScrollListener hideListener;
        private final int THRESHOLD;

        public ViewScrollListener(HideScrollListener hideListener) {
            this.hideListener = hideListener;
            this.THRESHOLD = 20;
        }

        public ViewScrollListener(HideScrollListener hideListener, int THRESHOLD) {
            this.hideListener = hideListener;
            this.THRESHOLD = THRESHOLD;
        }

        @Override
        public void onScrollChange(View view, int i, int i1, int i2, int i3) {
            if (i1 - i3 > THRESHOLD) {
                // hide
                hideListener.onHide();
            } else if (i1 - i3 < -THRESHOLD) {
                // show
                hideListener.onShow();
            }
        }
    }

    public static class TextWatcher implements android.text.TextWatcher {

        char[] Filter;
        EditText editText;
        Context context;

        public TextWatcher(char[] Filter, EditText editText, Context context) {
            this.Filter = Filter;
            this.editText = editText;
            this.context = context;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            for (char mFilter : Filter) {
                for (int j = 0; j < charSequence.length(); j++) {
                    if (charSequence.charAt(j) == mFilter) {
                        editText.setText(charSequence.subSequence(0, charSequence.length() - 1));
                        editText.setSelection(editText.getText().toString().length());
                        WindowManager.showToastWithGravity(context, context.getString(R.string.text_util_not_allowed_characters) + " \"" + mFilter + "\"",
                                Gravity.TOP, 0, 0, Toast.LENGTH_LONG);
                        return;
                    }
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    public static class TextFilter implements InputFilter {

        private final Context context;
        private final int mMaxLength;

        public  TextFilter(Context context, int max) {
            this.context = context;
            mMaxLength = max;
        }

        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            int keep = mMaxLength - (dest.length() - (dend - dstart));
            if (keep < (end - start)) {
                WindowManager.showToastWithGravity(context, context.getString(R.string.text_util_cannot_input_more_1) + " " + mMaxLength + " " +
                                context.getString(R.string.text_util_cannot_input_more_2),
                        Gravity.TOP, 0, 0, Toast.LENGTH_LONG);
            }
            if (keep <= 0) {
                return "";
            } else if (keep >= end - start) {
                return null;
            } else {
                return source.subSequence(start, start + keep);
            }
        }
    }
}
