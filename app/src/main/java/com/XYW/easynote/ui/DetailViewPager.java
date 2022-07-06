package com.XYW.easynote.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

//处理java.lang.IllegalArgumentException: pointerIndex out of range pointerIndex=-1 pointerCount=1
//项目详情中的viewPager 异常
public class DetailViewPager extends ViewPager {

    private boolean mIsDisallowIntercept = false;

    private boolean canSwipe = true;

    private boolean canCurrent = true;

    public DetailViewPager(Context context) {
        super(context);
    }

    public DetailViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        // keep the info about if the innerViews do
        // requestDisallowInterceptTouchEvent
        mIsDisallowIntercept = disallowIntercept;
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // the incorrect array size will only happen in the multi-touch
        // scenario.
        if (ev.getPointerCount() > 1 && mIsDisallowIntercept) {
            requestDisallowInterceptTouchEvent(false);
            boolean handled = super.dispatchTouchEvent(ev);
            requestDisallowInterceptTouchEvent(true);
            return handled;
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }

    public void setCanSwipe(boolean canSwipe)
    {
        this.canSwipe = canSwipe;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return canSwipe && super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return canSwipe && super.onInterceptTouchEvent(ev);
    }

    public void setCanCurrent(boolean canCurrent) {
        this.canCurrent = canCurrent;
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item,canCurrent);
    }
}