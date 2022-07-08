package com.XYW.easynote.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.XYW.easynote.R;
import com.XYW.easynote.ui.DetailViewPager;
import com.XYW.easynote.ui.RoundImageView;
import com.XYW.easynote.util.UIManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NoteFragment extends Fragment {

    private static final String TAG = "NoteFragment";

    private DetailViewPager ViewPager_templateNote;
    private RecyclerView RecyclerView_notes;

    private ViewGroup container;
    private Context context;
    private Activity activity;

    private int AllCount_viewpager = 0, CountNow_viewpager = 0;
    private static int SavedCount_viewpager, RecyclerView_notes_firstItem = 0;
    private static float RecyclerView_notes_Offset;
    private boolean isMax_viewpager;
    private Timer Timer_viewpager = new Timer();
    private Handler.Callback Hander_viewpager;

    private static List<NoteKind> noteKinds = new ArrayList<>();

    public NoteFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, container, false);
        this.container = container;

        if (savedInstanceState != null) {
            RecyclerView_notes_firstItem = savedInstanceState.getInt("ARGS_SCROLL_POS_NOTES");
            RecyclerView_notes_Offset = savedInstanceState.getFloat("ARGS_SCROLL_OFFSET_NOTES");
        }
        init(view);
        return view;
    }

    @Override
    public void onPause () {
        super.onPause();
        SavedCount_viewpager = CountNow_viewpager;
        getLayoutManager(RecyclerView_notes);
    }

    @Override
    public void onResume () {
        super.onResume();
        setLayoutManager(RecyclerView_notes);
        if (ViewPager_templateNote != null) {
            CountNow_viewpager = SavedCount_viewpager;
            ViewPager_templateNote.setCurrentItem(SavedCount_viewpager);
        }
    }

    @Override
    public void onSaveInstanceState (@NonNull Bundle outState){
        super.onSaveInstanceState(outState);
        getLayoutManager(RecyclerView_notes);
        outState.putInt("ARGS_SCROLL_POS_NOTES", RecyclerView_notes_firstItem);
        outState.putFloat("ARGS_SCROLL_OFFSET_NOTES", RecyclerView_notes_Offset);
    }

    private void getLayoutManager (RecyclerView recyclerView) {
        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        RecyclerView_notes_firstItem = manager != null ? manager.findFirstVisibleItemPosition() : 0;
        View firstItemView = manager != null ? manager.findViewByPosition(RecyclerView_notes_firstItem) : null;
        RecyclerView_notes_Offset = firstItemView != null ? firstItemView.getTop() : 0;
    }

    private void setLayoutManager (RecyclerView recyclerView) {
        LinearLayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(context);
        layoutManager.scrollToPositionWithOffset(RecyclerView_notes_firstItem, (int) RecyclerView_notes_Offset);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                Log.d(TAG, "onGlobalLayout: " + recyclerView.getItemDecorationCount());
                //if (recyclerView.getItemDecorationCount())
                //View Item = recyclerView.getLayoutManager().findViewByPosition(0);
                ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
                params.height = recyclerView.getMeasuredHeight();
                Log.d(TAG, "onGlobalLayout: " + recyclerView.getMeasuredHeight());
                recyclerView.setLayoutParams(params);
            }
        });
    }

    private void init (View view){
        initViewPager(view);
        initRecyclerView(view);
    }

    private void initViewPager (View view){
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

    private void initRecyclerView (View view){
        RecyclerView_notes = view.findViewById(R.id.RecyclerView_notekinds);
        setLayoutManager(RecyclerView_notes);

        List<Note> notes = new ArrayList<>();
        notes.add(new Note(null, null, null, "test"));
        notes.add(new Note(null, null, null, "test2"));
        notes.add(new Note(null, null, null, "test3"));
        notes.add(new Note(null, null, null, "test4"));
        notes.add(new Note(null, null, null, "test5"));

        List<Integer> firstItem = new ArrayList<>();
        List<Float> offset = new ArrayList<>();
        for (int i = 0; i < noteKinds.size(); i++) {
            firstItem.add(noteKinds.get(i).getRecyclerView_notes_firstItem());
            offset.add(noteKinds.get(i).getRecyclerView_notes_leftOffset());
        }
        noteKinds.clear();
        if (!firstItem.isEmpty()) {
            noteKinds.add(new NoteKind(notes, context.getResources().getString(R.string.text_fragment_note_mynotes_title), firstItem.get(0), offset.get(0)));
            noteKinds.add(new NoteKind(notes, context.getResources().getString(R.string.text_fragment_note_mynotes_title), firstItem.get(1), offset.get(1)));
            noteKinds.add(new NoteKind(notes, context.getResources().getString(R.string.text_fragment_note_mynotes_title), firstItem.get(2), offset.get(2)));
        } else {
            noteKinds.add(new NoteKind(notes, context.getResources().getString(R.string.text_fragment_note_mynotes_title)));
            noteKinds.add(new NoteKind(notes, context.getResources().getString(R.string.text_fragment_note_mynotes_title)));
            noteKinds.add(new NoteKind(notes, context.getResources().getString(R.string.text_fragment_note_mynotes_title)));
        }
        NoteKindAdapter adapter = new NoteKindAdapter(noteKinds, context);
        RecyclerView_notes.setAdapter(adapter);
    }

    private void getTheViewPagerRoll () {
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
    public void onAttach (@NonNull Activity activity){
        super.onAttach(activity);
        this.activity = activity;
        Hander_viewpager = message -> {
            activity.runOnUiThread(() -> ViewPager_templateNote.setCurrentItem(CountNow_viewpager));
            return false;
        };
    }

    @Override
    public void onAttach (@NonNull Context context){
        super.onAttach(context);
        this.context = context;
    }

    public static class NoteKind {

        private int RecyclerView_notes_firstItem;
        private float RecyclerView_notes_leftOffset;

        private List<Note> notes;
        private String title;

        public NoteKind(List<Note> notes, String title) {
            this.notes = notes;
            this.title = title;
        }

        public NoteKind(List<Note> notes, String title, int RecyclerView_notes_firstItem, float RecyclerView_notes_leftOffset) {
            this.notes = notes;
            this.title = title;
            this.RecyclerView_notes_firstItem = RecyclerView_notes_firstItem;
            this.RecyclerView_notes_leftOffset = RecyclerView_notes_leftOffset;
        }

        public void setNotes(List<Note> notes) {
            this.notes = notes;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<Note> getNotes() {
            return notes;
        }

        public String getTitle() {
            return title;
        }

        public void setRecyclerView_notes_firstItem(int recyclerView_notes_firstItem) {
            RecyclerView_notes_firstItem = recyclerView_notes_firstItem;
        }

        public void setRecyclerView_notes_leftOffset(float recyclerView_notes_leftOffset) {
            RecyclerView_notes_leftOffset = recyclerView_notes_leftOffset;
        }

        public int getRecyclerView_notes_firstItem() {
            return RecyclerView_notes_firstItem;
        }

        public float getRecyclerView_notes_leftOffset() {
            return RecyclerView_notes_leftOffset;
        }
    }

    public static class Note implements Parcelable {

        private String File_Path, File_Name, File_End, title, icon_Path, background_Path;

        public Note(String file_Path, String file_Name, String file_End, String title) {
            this.File_Path = file_Path;
            this.File_Name = file_Name;
            this.File_End = file_End;
            this.title = title;
            this.icon_Path = null;
            this.background_Path = null;
        }

        public Note(String file_Path, String file_Name, String file_End, String title, String img, String background) {
            this.File_Path = file_Path;
            this.File_Name = file_Name;
            this.File_End = file_End;
            this.title = title;
            this.icon_Path = img;
            this.background_Path = background;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setFile_Path(String file_Path) {
            File_Path = file_Path;
        }

        public void setFile_Name(String file_Name) {
            File_Name = file_Name;
        }

        public void setFile_End(String file_End) {
            File_End = file_End;
        }

        public void setIcon_Path(String icon_Path) {
            this.icon_Path = icon_Path;
        }

        public void setBackground_Path(String background_Path) {
            this.background_Path = background_Path;
        }

        public String getTitle() {
            return title;
        }

        public String getFile_Path() {
            return File_Path;
        }

        public String getFile_Name() {
            return File_Name;
        }

        public String getFile_End() {
            return File_End;
        }

        public String getIcon_Path() {
            return icon_Path;
        }

        public String getBackground_Path() {
            return background_Path;
        }

        protected Note(Parcel in) {
            this.File_Path = in.readString();
            this.File_Name = in.readString();
            this.File_End = in.readString();
            this.title = in.readString();
            this.icon_Path = in.readString();
            this.background_Path = in.readString();
            //BitmapFactory.decodeFile();
        }

        public static final Creator<Note> CREATOR = new Creator<Note>() {
            @Override
            public Note createFromParcel(Parcel in) {
                return new Note(in);
            }

            @Override
            public Note[] newArray(int size) {
                return new Note[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(File_Path);
            parcel.writeString(File_Name);
            parcel.writeString(File_End);
            parcel.writeString(title);
            parcel.writeString(icon_Path);
            parcel.writeString(background_Path);
        }
    }

    private static class NoteKindAdapter extends RecyclerView.Adapter<NoteKindAdapter.ViewHolder> {

        private final List<NoteKind> noteKinds;
        private final Context context;

        static class ViewHolder extends RecyclerView.ViewHolder {
            RecyclerView RecyclerView_Notes;
            TextView TextView_NoteKind;

            public ViewHolder(View view) {
                super(view);
                RecyclerView_Notes = view.findViewById(R.id.RecyclerView_notes);
                TextView_NoteKind = view.findViewById(R.id.TextView_noteKinds_title);
            }
        }

        public NoteKindAdapter(List<NoteKind> noteKinds, Context context) {
            this.noteKinds = noteKinds;
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_fragment_note_recyclerview_notekinds, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(NoteKindAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int pos) {
            NoteKind noteKind = noteKinds.get(pos);
            holder.setIsRecyclable(false);
            holder.TextView_NoteKind.setText(noteKind.getTitle());
            LinearLayoutManager layoutManager;
            layoutManager = holder.RecyclerView_Notes.getLayoutManager() == null ? new LinearLayoutManager(context) : (LinearLayoutManager) holder.RecyclerView_Notes.getLayoutManager();
            layoutManager.scrollToPositionWithOffset(noteKind.getRecyclerView_notes_firstItem(), (int) noteKind.getRecyclerView_notes_leftOffset());
            layoutManager.setOrientation(RecyclerView.HORIZONTAL);
            holder.RecyclerView_Notes.setLayoutManager(layoutManager);

            NoteAdapter adapter = new NoteAdapter(noteKind.getNotes());
            holder.RecyclerView_Notes.setAdapter(adapter);
            holder.RecyclerView_Notes.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    Log.d(TAG, "onScrollStateChanged: ");
                    LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    noteKind.setRecyclerView_notes_firstItem(manager != null ? manager.findFirstVisibleItemPosition() : 0);
                    View firstItemView = manager != null ? manager.findViewByPosition(RecyclerView_notes_firstItem) : null;
                    noteKind.setRecyclerView_notes_leftOffset(firstItemView != null ? firstItemView.getLeft() : 0);
                }
            });
        }

        @Override
        public int getItemCount() {
            return this.noteKinds.size();
        }

        private static class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

            private final List<Note> mNotes;

            static class ViewHolder extends RecyclerView.ViewHolder {

                TextView TextView_noteTitle;
                RoundImageView RoundImageView_noteIcon, RoundImageView_noteImg;

                public ViewHolder(View view) {
                    super(view);
                    TextView_noteTitle = view.findViewById(R.id.TextView_noteTitle);
                    RoundImageView_noteIcon = view.findViewById(R.id.RoundImageView_noteIcon);
                    RoundImageView_noteImg = view.findViewById(R.id.RoundImageView_noteImg);
                }
            }

            public NoteAdapter(List<Note> mNotes) {
                this.mNotes = mNotes;
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_fragment_note_recyclerview_notes, parent, false);
                return new ViewHolder(view);
            }

            @Override
            public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int pos) {
                Note note = mNotes.get(pos);
                holder.setIsRecyclable(false);
                holder.TextView_noteTitle.setText(note.getTitle());
                if (note.getBackground_Path() != null) {
                    holder.RoundImageView_noteImg.setImageBitmap(BitmapFactory.decodeFile(note.getBackground_Path()));
                } else {
                    holder.RoundImageView_noteImg.setImageResource(R.drawable.img_note_defult_cover);
                }
                if (note.getIcon_Path() != null) {
                    holder.RoundImageView_noteIcon.setImageBitmap(BitmapFactory.decodeFile(note.getIcon_Path()));
                } else {
                    holder.RoundImageView_noteIcon.setImageResource(R.drawable.general_drive_file);
                }
            }

            @Override
            public int getItemCount() {
                return this.mNotes.size();
            }
        }
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
