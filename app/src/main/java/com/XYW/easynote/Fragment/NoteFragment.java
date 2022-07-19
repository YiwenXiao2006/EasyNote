package com.XYW.easynote.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.XYW.easynote.R;
import com.XYW.easynote.activity.CreateFile;
import com.XYW.easynote.activity.NoteDoc;
import com.XYW.easynote.ui.DetailViewPager;
import com.XYW.easynote.ui.RoundImageView;
import com.XYW.easynote.util.IOManager;
import com.XYW.easynote.util.UIManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class NoteFragment extends Fragment implements UIManager.HideScrollListener {

    private static final String TAG = "NoteFragment";

    private DetailViewPager ViewPager_templateNote;
    private RecyclerView RecyclerView_notes;
    private ScrollView ScrollView_fragment_note;
    private FloatingActionButton FAB_createNote;

    private ViewGroup container;
    private Context context;
    private Activity activity;
    private View NoteFragment_Layout;

    private int AllCount_viewpager = 0, CountNow_viewpager = 0;
    private static int SavedCount_viewpager, ScrollView_fragment_note_ScrollY;
    private boolean isMax_viewpager;
    private Timer Timer_viewpager = new Timer();
    private Handler.Callback Hander_viewpager;

    private static List<NoteTag> NOTE_TAGS = new ArrayList<>();

    LocalBroadcastManager broadcastManager_refresh_noteList;
    BroadcastReceiver refresh_noteList = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            initRecyclerView(NoteFragment_Layout);
        }
    };

    public NoteFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, container, false);
        NoteFragment_Layout = view;
        this.container = container;
        init(view);
        return view;
    }

    @Override
    public void onPause () {
        super.onPause();
        SavedCount_viewpager = CountNow_viewpager;
        if (ScrollView_fragment_note != null) {
            ScrollView_fragment_note_ScrollY = ScrollView_fragment_note.getScrollY();
        }
    }

    @Override
    public void onResume () {
        super.onResume();
        setLayoutManager(RecyclerView_notes);
        if (ViewPager_templateNote != null) {
            CountNow_viewpager = SavedCount_viewpager;
            ViewPager_templateNote.setCurrentItem(SavedCount_viewpager);
        }
        if (ScrollView_fragment_note != null) {
            ScrollView_fragment_note.setScrollY(ScrollView_fragment_note_ScrollY);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(refresh_noteList);
        } catch (Exception e) {e.printStackTrace();}
    }

    private void setLayoutManager (RecyclerView recyclerView) {
        UIManager.FullyLinearLayoutManager layoutManager;
        layoutManager = new UIManager.FullyLinearLayoutManager(context);
        layoutManager.setmCanVerticalScroll(false);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (recyclerView.getAdapter() == null ||recyclerView.getLayoutManager() == null) {
                    return;
                }
                if (!(recyclerView.getAdapter().getItemCount() > 0))
                    return;
                View Item = recyclerView.getLayoutManager().findViewByPosition(0);
                ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
                params.height = recyclerView.getAdapter().getItemCount() * (Item != null ? Item.getHeight() : 0);
                recyclerView.setLayoutParams(params);
            }
        });
    }

    private void init (View view){
        initViewPager(view);
        initRecyclerView(view);
        initScrollView(view);
        initFAB(view);


        broadcastManager_refresh_noteList = LocalBroadcastManager.getInstance(activity);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.XYW.EasyNote.activity.CreateFile.refresh_noteList");
        broadcastManager_refresh_noteList.registerReceiver(refresh_noteList, intentFilter);
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

        File Notes_Contents = new File(context.getFilesDir().getPath() + File.separator + "Notes_Contents.ctt");
        if (!Notes_Contents.exists()) {
            RecyclerView_notes.setAdapter(null);
            Log.d(TAG, "initRecyclerView: not exists");
            return;
        }
        List<String> notesContentsReader = IOManager.readFileByLine(Notes_Contents);
        if (notesContentsReader.isEmpty() || !Objects.equals(notesContentsReader.get(0), "#EasyNote")) {
            RecyclerView_notes.setAdapter(null);
            Log.d(TAG, "initRecyclerView: empty");
            return;
        }

        int mode = 0, //0:read Tag 1:read Note
            offset_Pos = 0;
        List<Note> notes = new ArrayList<>();
        List<String> notereader = new ArrayList<>();
        String tagTitle = "";
        List<Integer> firstItem = new ArrayList<>();
        List<Float> offset = new ArrayList<>();
        boolean endNote = true, endTag = true;

        for (int i = 0; i < NOTE_TAGS.size(); i++) {
            firstItem.add(NOTE_TAGS.get(i).getRecyclerView_notes_firstItem());
            offset.add(NOTE_TAGS.get(i).getRecyclerView_notes_leftOffset());
        }

        NOTE_TAGS = new ArrayList<>();

        for (int i = 2; i < notesContentsReader.size(); i++) {
            if (Objects.equals(notesContentsReader.get(i), IOManager.NOTE_TAG) && endTag && endNote) {
                mode = 0;
                tagTitle = "";
                endTag = false;
            } else if (Objects.equals(notesContentsReader.get(i), IOManager.NOTE_NOTE) && endNote) {
                mode = 1;
                endNote = false;
            } else if (Objects.equals(notesContentsReader.get(i), IOManager.NOTE_ENDNOTE)) {
                if (notereader.size() < 1) {
                    continue;
                }
                Log.d(TAG, "initRecyclerView: " + notes.size());
                if (notereader.size() == 5) {
                    notes.add(new Note(Objects.equals(notereader.get(0), "null") ? null : notereader.get(0),
                            Objects.equals(notereader.get(1), "null") ? null : notereader.get(1),
                            Objects.equals(notereader.get(2), "null") ? null : notereader.get(2),
                            notereader.get(3),
                            Objects.equals(notereader.get(4), "null") ? null : notereader.get(4)));
                } else if (notereader.size() == 7) {
                    notes.add(new Note(Objects.equals(notereader.get(0), "null") ? null : notereader.get(0),
                            Objects.equals(notereader.get(1), "null") ? null : notereader.get(1),
                            Objects.equals(notereader.get(2), "null") ? null : notereader.get(2),
                            notereader.get(3),
                            Objects.equals(notereader.get(4), "null") ? null : notereader.get(4),
                            Objects.equals(notereader.get(5), "0") ? 0 : Integer.parseInt(notereader.get(5)),
                            Objects.equals(notereader.get(6), "null") ? null : notereader.get(6)));
                }
                endNote = true;
                mode = -1;
                notereader.clear();
            } else if (Objects.equals(notesContentsReader.get(i), IOManager.NOTE_ENDTAG)) {
                if (!firstItem.isEmpty() && firstItem.size() > offset_Pos) {
                    NOTE_TAGS.add(new NoteTag(notes, tagTitle, firstItem.get(offset_Pos), offset.get(offset_Pos)));
                    offset_Pos ++;
                } else {
                    NOTE_TAGS.add(new NoteTag(notes, tagTitle));
                }
                notes.clear();
                endTag = true;
                mode = -1;
            } else {
                switch (mode) {
                    case 0:
                        if (Objects.equals(notesContentsReader.get(i), IOManager.NOTE_DEFAULT_TAG_NAME)) {
                            tagTitle = getString(R.string.text_fragment_note_mynotes_title);
                        } else {
                            tagTitle = notesContentsReader.get(i);
                        }
                        break;
                    case 1:
                        notereader.add(notesContentsReader.get(i));
                        break;
                }
            }
        }
        List<NoteTag> tags = new ArrayList<>(NOTE_TAGS);
        NoteTagAdapter adapter = new NoteTagAdapter(tags, context);
        RecyclerView_notes.setAdapter(adapter);
    }

    private void initScrollView(View view) {
        ScrollView_fragment_note = view.findViewById(R.id.ScrollView_fragment_note);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            UIManager.ViewScrollListener listener = new UIManager.ViewScrollListener(this, 20);
            ScrollView_fragment_note.setOnScrollChangeListener(listener);
        }
    }

    private void initFAB(View view) {
        FAB_createNote = view.findViewById(R.id.FAB_createNote);
        FAB_createNote.setOnClickListener(view1 -> {
            Intent intent = new Intent(activity, CreateFile.class);
            activity.startActivity(intent);
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

    @Override
    public void onHide() {
        RelativeLayout.LayoutParams FAB_modifyExam_LayoutParams = (RelativeLayout.LayoutParams) FAB_createNote.getLayoutParams();
        FAB_createNote.animate().translationY(FAB_createNote.getHeight() + FAB_modifyExam_LayoutParams.bottomMargin)
                .setInterpolator(new AccelerateInterpolator(3));
    }

    @Override
    public void onShow() {
        FAB_createNote.animate().translationY(0).setInterpolator(new DecelerateInterpolator(3));
    }

    public static class NoteTag {

        private int RecyclerView_notes_firstItem;
        private float RecyclerView_notes_leftOffset;

        private List<Note> notes = new ArrayList<>();
        private String title;

        public NoteTag(List<Note> notes, String title) {
            this.notes.addAll(notes);
            this.title = title;
        }

        public NoteTag(List<Note> notes, String title, int RecyclerView_notes_firstItem, float RecyclerView_notes_leftOffset) {
            this.notes.addAll(notes);
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

        private String File_Path, File_Name, File_End, title, background_Path, describe_Path;
        private int icon_ID;

        public Note(String file_Path, String file_Name, String file_End, String title, String describe_Path) {
            this.File_Path = file_Path;
            this.File_Name = file_Name;
            this.File_End = file_End;
            this.title = title;
            this.describe_Path = describe_Path;
            this.icon_ID = 0;
            this.background_Path = null;
        }

        public Note(String file_Path, String file_Name, String file_End, String title, String describe_Path, int icon, String background) {
            this.File_Path = file_Path;
            this.File_Name = file_Name;
            this.File_End = file_End;
            this.title = title;
            this.describe_Path = describe_Path;
            this.icon_ID = icon;
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

        public void setDescribe_Path(String describe_Path) {
            this.describe_Path = describe_Path;
        }

        public void setIcon_ID(int icon) {
            this.icon_ID = icon;
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

        public String getDescribe_Path() {
            return describe_Path;
        }

        public int getIcon_ID() {
            return icon_ID;
        }

        public String getBackground_Path() {
            return background_Path;
        }

        protected Note(Parcel in) {
            this.File_Path = in.readString();
            this.File_Name = in.readString();
            this.File_End = in.readString();
            this.title = in.readString();
            this.describe_Path = in.readString();
            this.icon_ID = in.readInt();
            this.background_Path = in.readString();
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
            parcel.writeString(describe_Path);
            parcel.writeInt(icon_ID);
            parcel.writeString(background_Path);
        }
    }

    private static class NoteTagAdapter extends RecyclerView.Adapter<NoteTagAdapter.ViewHolder> {

        private final List<NoteTag> noteTags = new ArrayList<>();
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

        public NoteTagAdapter(List<NoteTag> noteTags, Context context) {
            this.noteTags.addAll(noteTags);
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
        public void onBindViewHolder(NoteTagAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int pos) {
            NoteTag noteTag = noteTags.get(pos);
            //holder.setIsRecyclable(false);
            holder.TextView_NoteKind.setText(noteTag.getTitle());
            LinearLayoutManager layoutManager;
            layoutManager = holder.RecyclerView_Notes.getLayoutManager() == null ? new LinearLayoutManager(context) : (LinearLayoutManager) holder.RecyclerView_Notes.getLayoutManager();
            layoutManager.scrollToPositionWithOffset(noteTag.getRecyclerView_notes_firstItem(), (int) noteTag.getRecyclerView_notes_leftOffset());
            layoutManager.setOrientation(RecyclerView.HORIZONTAL);
            holder.RecyclerView_Notes.setLayoutManager(layoutManager);

            NoteAdapter adapter = new NoteAdapter(noteTag.getNotes(), context);

            holder.RecyclerView_Notes.setAdapter(adapter);
            holder.RecyclerView_Notes.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    noteTag.setRecyclerView_notes_firstItem(manager != null ? manager.findFirstVisibleItemPosition() : 0);
                    View firstItemView = manager != null ? manager.findViewByPosition(noteTag.getRecyclerView_notes_firstItem()) : null;
                    noteTag.setRecyclerView_notes_leftOffset(firstItemView != null ? firstItemView.getLeft() : 0);
                }
            });
        }

        @Override
        public int getItemCount() {
            return this.noteTags.size();
        }

        private static class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

            private final List<Note> mNotes;
            private final Context context;

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

            public NoteAdapter(List<Note> mNotes, Context context) {
                this.mNotes = mNotes;
                this.context = context;
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
                //holder.setIsRecyclable(false);
                holder.TextView_noteTitle.setText(note.getTitle());
                holder.RoundImageView_noteImg.setImageResource(R.drawable.img_note_defult_cover);
                if (note.getBackground_Path() != null) {
                    holder.RoundImageView_noteImg.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            holder.RoundImageView_noteImg.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            holder.RoundImageView_noteImg.setImageBitmap(IOManager.decodeBitmap(note.getBackground_Path(),
                                    holder.RoundImageView_noteImg.getWidth(), holder.RoundImageView_noteImg.getHeight()));
                        }
                    });
                }
                if (note.getIcon_ID() != 0) {
                    holder.RoundImageView_noteIcon.setImageResource(note.getIcon_ID());
                } else {
                    holder.RoundImageView_noteIcon.setImageResource(R.drawable.general_drive_file);
                }
                holder.itemView.setOnClickListener(view -> {
                    Intent intent = new Intent(context, NoteDoc.class);
                    intent.putExtra("title", note.getTitle());
                    intent.putExtra("filePath", note.getFile_Path());
                    intent.putExtra("fileName", note.getFile_Name());
                    intent.putExtra("fileEnd", note.getFile_End());
                    intent.putExtra("EditMode", false);
                    context.startActivity(intent);
                });
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

        @Override
        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) {
                view.setAlpha(0);

            } else if (position <= 1) { //a页滑动至b页 ； a页从 0.0 -1 ；b页从1 ~ 0.0
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
            } else {
                view.setAlpha(0);
            }
        }
    }
}
