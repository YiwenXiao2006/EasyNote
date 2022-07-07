package com.XYW.easynote.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.XYW.easynote.R;
import com.XYW.easynote.ui.DetailViewPager;
import com.XYW.easynote.ui.MessageBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NoteFragment extends Fragment {

    private static final String TAG = "NoteFragment";

    private DetailViewPager ViewPager_templateNote;

    private ViewGroup container;
    private Context context;
    private Activity activity;

    private int AllCount_viewpager = 0, CountNow_viewpager = 0;
    private boolean isMax_viewpager;
    private Timer Timer_viewpager = new Timer();
    private Handler.Callback Hander_viewpager;

    public NoteFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, container, false);
        this.container = container;

        init(view);
        return view;
    }

    private void init(View view) {
        initViewPager(view);
    }

    private void initViewPager(View view) {
        ViewPager_templateNote = view.findViewById(R.id.ViewPager_templateNote);
        int[] drawables = new int[]{R.drawable.shape_gradient_red, R.drawable.shape_gradient_lemon},
                img = new int[]{R.drawable.img_note_template_gift, R.drawable.img_note_template_leaves},
                subimg = new int[]{R.drawable.img_note_template_trees, R.drawable.img_note_template_lemon},
                title = new int[]{R.string.text_fragment_note_template_holiday_title, R.string.text_fragment_note_template_summer_title},
                subtitle = new int[]{R.string.text_fragment_note_template_holiday_subtitle, R.string.text_fragment_note_template_summer_subtitle},
                textcolor = new int[]{R.color.white, R.color.text};
        List<View> mViews = new ArrayList<>();
        for (int i = 0; i < drawables.length; i++) {
            View templateLayout = LayoutInflater.from(context).inflate(R.layout.content_template, container, false);
            RelativeLayout relativeLayout = templateLayout.findViewById(R.id.RelativeLayout_template_background);
            ImageView Img = templateLayout.findViewById(R.id.ImageView_template_Img),
                        subImg = templateLayout.findViewById(R.id.ImageView_template_subImg);
            TextView Title = templateLayout.findViewById(R.id.TextView_template_title),
                        subTitle = templateLayout.findViewById(R.id.TextView_template_subtitle);
            relativeLayout.setBackground(ContextCompat.getDrawable(activity, drawables[i]));
            Img.setImageResource(img[i]);
            subImg.setImageResource(subimg[i]);
            Title.setText(title[i]);
            Title.setTextColor(getResources().getColor(textcolor[i]));
            subTitle.setText(subtitle[i]);
            subTitle.setTextColor(getResources().getColor(textcolor[i]));
            mViews.add(templateLayout);
        }
        AllCount_viewpager = mViews.size();

        ViewPagerAdapter adapter = new ViewPagerAdapter(mViews);
        ViewPager_templateNote.setAdapter(adapter);
        ViewPager_templateNote.setCanCurrent(false);
        ViewPager_templateNote.setCanSwipe(true);
        ViewPager_templateNote.setPageTransformer(true, new ZoomOutPageTransformer());
        getTheViewPagerRoll();
        ViewPager_templateNote.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case 0:
                        CountNow_viewpager = ViewPager_templateNote.getCurrentItem();
                        getTheViewPagerRoll();
                        break;
                    case 1:
                        Timer_viewpager.cancel();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void getTheViewPagerRoll() {
        TimerTask timerTask_Viewpager = new TimerTask() {
            @Override
            public void run() {
                if (CountNow_viewpager == AllCount_viewpager - 1) {
                    isMax_viewpager = true;
                }
                if (CountNow_viewpager == 0) {
                    isMax_viewpager = false;
                }
                if (isMax_viewpager) {
                    Hander_viewpager.handleMessage(null);
                    CountNow_viewpager--;
                } else {
                    Hander_viewpager.handleMessage(null);
                    CountNow_viewpager++;
                }
            }
        };
        Timer_viewpager = new Timer();
        Timer_viewpager.schedule(timerTask_Viewpager, 5000, 5000);
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        Hander_viewpager = message -> {
            activity.runOnUiThread(() -> ViewPager_templateNote.setCurrentItem(CountNow_viewpager));
            return false;
        };
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public static class ViewPagerAdapter extends PagerAdapter {

        private final List<View> mViews;

        public ViewPagerAdapter(List<View> mViews) {
            this.mViews = mViews;
        }

        @Override
        public int getCount() {
            return mViews.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViews.get(position));
            return mViews.get(position % mViews.size());
        }

        @Override
        public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
            container.removeView(mViews.get(position % mViews.size()));
        }
    }

    public static class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;
        @Override
        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) {
                // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { //a页滑动至b页 ； a页从 0.0 -1 ；b页从1 ~ 0.0
                // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                //view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE)
                //        / (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }
}
