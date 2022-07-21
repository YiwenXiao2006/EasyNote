package com.XYW.easynote.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.XYW.easynote.R;

public class ColorView extends View {

    private int mColor;
    private int width, height;
    private int mDrawShape;
    private int mRadius;

    private Paint mPaint;
    private RectF mRect;

    public ColorView(Context context) {
        super(context);
    }

    public ColorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ColorView, defStyleAttr, 0);
        mColor = array.getColor(R.styleable.ColorView_cv_color, Color.WHITE);
        mDrawShape = array.getInteger(R.styleable.ColorView_cv_shape, 0);
        mRadius = array.getDimensionPixelSize(R.styleable.ColorView_cv_radius, 0);
        array.recycle();

        init();
    }

    private void init() {
        mPaint = new Paint();
        mRect = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);

        mRect.left = 0;
        mRect.top = 0;
        mRect.right = width;
        mRect.bottom = height;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setAntiAlias(true);
//        给画笔设置颜色
        mPaint.setColor(mColor);
//        设置画笔属性
        mPaint.setStyle(Paint.Style.FILL);//画笔属性是实心圆
        //paint.setStyle(Paint.Style.STROKE);//画笔属性是空心圆
        mPaint.setStrokeWidth(1);//设置画笔粗细

        switch (mDrawShape) {
            case 0:
                canvas.drawCircle((int) (getWidth() / 2), (int) (getHeight() / 2), (int) (Math.min(width, height) / 2), mPaint);
                break;
            case 1:
                canvas.drawRect(0, 0, width, height, mPaint);
                break;
            case 2:
                canvas.drawRoundRect(mRect, mRadius, mRadius, mPaint);
                break;
            case 3:
                int shape_width = Math.min(width, height) / 2;
                canvas.drawRect((int) (width / 2 - shape_width), (int) (height / 2 - shape_width),
                        (int) (width / 2 + shape_width), (int) (height / 2 + shape_width), mPaint);
                break;
        }
    }

    public void setColor(int color) {
        mColor = color;
        invalidate();
    }

    public int getColor() {
        return mColor;
    }
}
