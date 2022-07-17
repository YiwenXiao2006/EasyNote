package com.XYW.easynote.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import com.XYW.easynote.R;

public class RoundImageView extends androidx.appcompat.widget.AppCompatImageView {

    private int mBorderRadius;
    float width,height;
    Path path;

    public RoundImageView(Context context) {
        this(context, null);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView);
        mBorderRadius = typedArray.getDimensionPixelSize(R.styleable.RoundImageView_radius, 10);
        if (Build.VERSION.SDK_INT < 18) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        path = new Path();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (width > mBorderRadius && height > mBorderRadius) {
            path.moveTo(mBorderRadius, 0);
            path.lineTo(width - mBorderRadius, 0);
            path.quadTo(width, 0, width, mBorderRadius);
            path.lineTo(width, height - mBorderRadius);
            path.quadTo(width, height, width - mBorderRadius, height);
            path.lineTo(mBorderRadius, height);
            path.quadTo(0, height, 0, height - mBorderRadius);
            path.lineTo(0, mBorderRadius);
            path.quadTo(0, 0, mBorderRadius, 0);
            canvas.clipPath(path);
        }

        super.onDraw(canvas);
    }

    public void setRadius(int radius) {
        mBorderRadius = radius;
    }
}