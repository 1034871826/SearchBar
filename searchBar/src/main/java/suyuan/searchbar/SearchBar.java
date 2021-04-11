package suyuan.searchbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;


/**
 * @author suyuan
 */
public class SearchBar extends androidx.appcompat.widget.AppCompatEditText {
    public static final String TAG = "SearchBar";
    private int leftImageWidth = 20;
    private int leftImageHeight = 20;
    private int rightImageWidth = 20;
    private int rightImageHeight = 20;
    private int windowMarginTop = 0;
    private int windowMarginStart = 0;
    private int windowMarginEnd = 0;
    private int drawableMarginStart = 0;
    private int drawableMarginEnd = 0;
    private float windowAlpha = 1f;
    private Drawable left;
    private Drawable right;
    private PopupWindow searchRecordWindow;
    private RecyclerView searchRecordRecyclerView;
    private RecyclerView.Adapter adapter;
    private SearchRecordContent searchRecordContent;

    public SearchBar(@NonNull Context context) {
        this(context, null, 0);
    }

    public SearchBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        Log.d(TAG, "init: 开始");
        getAttrsProperties(context, attrs, defStyleAttr);

        setClickable(true);
        setEnabled(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        createSearchRecordWindow(context);

        Log.d(TAG, "init: 结束");
    }

    /**
     * 从xml文件中获取自定义的属性
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private void getAttrsProperties(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SearchBar, defStyleAttr, 0);
        Log.d(TAG, "getAttrsProperties: 开始");
        drawableMarginStart = typedArray.getDimensionPixelSize(R.styleable.SearchBar_drawable_margin_start, 0);
        drawableMarginEnd = typedArray.getDimensionPixelSize(R.styleable.SearchBar_drawable_margin_end, 0);
        windowMarginTop = typedArray.getDimensionPixelSize(R.styleable.SearchBar_window_margin_top, 0);
        windowMarginStart = typedArray.getDimensionPixelSize(R.styleable.SearchBar_window_margin_start, 0);
        windowMarginEnd = typedArray.getDimensionPixelSize(R.styleable.SearchBar_window_margin_end, 0);
        leftImageWidth = typedArray.getDimensionPixelSize(R.styleable.SearchBar_left_drawable_width, 20);
        leftImageHeight = typedArray.getDimensionPixelSize(R.styleable.SearchBar_left_drawable_height, 20);
        rightImageWidth = typedArray.getDimensionPixelSize(R.styleable.SearchBar_right_drawable_width, 20);
        rightImageHeight = typedArray.getDimensionPixelSize(R.styleable.SearchBar_right_drawable_height, 20);
        windowAlpha = typedArray.getFloat(R.styleable.SearchBar_window_alpha, 1f);
        //这个方法首先是在父类的构造方法中执行，当时还未获取宽高，所以在获取完宽高以后再执行一次
        setCompoundDrawablesWithIntrinsicBounds(left, null, right, null);
        //使用完记得循环掉
        typedArray.recycle();
        Log.d(TAG, "getAttrsProperties: 结束");

    }


    private void createSearchRecordWindow(Context context) {
        //这里初始化了popupWindow以及内部的recyclerView，但是适配器还没有进行初始化，需要数据
        View view = LayoutInflater.from(context).inflate(R.layout.item_recycler_view, null);
        searchRecordRecyclerView = view.findViewById(R.id.popupWindow_recyclerView);
        searchRecordWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        searchRecordWindow.setTouchable(true);
        searchRecordWindow.setOutsideTouchable(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        searchRecordRecyclerView.setLayoutManager(linearLayoutManager);
        searchRecordRecyclerView.setAlpha(windowAlpha);

    }

    public void setWindowBackgroundResource(int resourceId) {
        searchRecordRecyclerView.setBackgroundResource(resourceId);
    }

    public void setWindowBackground(Drawable drawable) {
        searchRecordRecyclerView.setBackground(drawable);
    }


    public RecyclerView.Adapter getAdapter() {
        return adapter;
    }

    /**
     * 该方法用于设置默认适配器的数据以及对应的监听
     * 根据内部类SearchRecordContent实现，可以直接new一个出来, 实现抽象类即可
     */
    public void setDefaultAdapter(@NotNull SearchRecordContent searchRecordContent) {
        this.searchRecordContent = searchRecordContent;
        adapter = new SearchRecordItemAdapter(searchRecordContent);
        searchRecordRecyclerView.setAdapter(adapter);
    }


    /**
     * 设置适配器, 该方法在需要自定义搜索记录内容的时候使用
     * 如果使用该方法，那么所有的监听由自己自定义的适配器本身实现
     * 此时代码中与SearchRecordContent相关的方法都不可调用
     *
     * @param adapter
     */
    public void setCustomAdapter(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
        searchRecordRecyclerView.setAdapter(adapter);
    }


    /**
     * 获取当前的SearchRecordContent
     *
     * @return
     */
    public SearchRecordContent getSearchRecordContent() {
        return searchRecordContent;
    }

    /**
     * 如果你想设置一个全新的聊天记录内容，调用该方法
     * 该方法不会刷新界面的数据，如果要刷新数据，请调用
     * notifyDataSetChanged()
     *
     * @param searchRecordContent
     */
    public void setSearchRecordContent(SearchRecordContent searchRecordContent) {
        this.searchRecordContent = searchRecordContent;
        if (adapter instanceof SearchRecordItemAdapter) {
            ((SearchRecordItemAdapter) adapter).setSearchRecordContent(searchRecordContent);
        }
    }

    public void showSearchRecordWindow() {
        if (!searchRecordWindow.isShowing() && adapter.getItemCount() != 0) {
            Log.d(TAG, "showSearchRecordWindow: 展示下拉菜单");
            searchRecordWindow.showAsDropDown(this, windowMarginStart, windowMarginTop);
        } else {
            Log.d(TAG, "showSearchRecordWindow: 执行该方法，但是菜单已存在");
        }
    }

    public void dismissSearchRecordWindow() {
        if (searchRecordWindow.isShowing()) {
            searchRecordWindow.dismiss();
            Log.d(TAG, "dismissSearchRecordWindow: 关闭下拉菜单");
        } else {
            Log.d(TAG, "dismissSearchRecordWindow: 执行该方法，但是菜单本身就是关闭状态");
        }
    }


    public void setWindowAlpha(float alpha) {
        searchRecordRecyclerView.setAlpha(alpha);
    }

    /**
     * 当需要更新数据的时候，调用该方法，重绘所有的界面
     */
    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

    /**
     * 当需要删除指定位置数据的界面时候，调用该方法
     * @param position
     */
    public void notifyDataRemoved(int position) {
        adapter.notifyItemRemoved(position);
    }

    /**
     * 当需要指定位置插入数据的界面的时候，调用该方法
     * @param position
     */
    public void notifyDataInserted(int position) {
        adapter.notifyItemInserted(position);
    }

    /**
     * 重写onSelectionChanged，保证每次光标都在文字的末尾
     */
    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        if (selStart == selEnd) {
            if (getText() != null) {
                setSelection(getText().length());
            } else {
                setSelection(0);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "onMeasure: 测量开始");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 实际的宽度就是editText的宽度 - margin
        searchRecordWindow.setWidth(getMeasuredWidth() - windowMarginStart - windowMarginEnd);
        searchRecordWindow.update();
        Log.d(TAG, "onMeasure: 测量结束");
    }

    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(@Nullable Drawable left, @Nullable Drawable top, @Nullable Drawable right, @Nullable Drawable bottom) {
        this.left = left;
        this.right = right;
        if (right != null) {
            //因为right是位于右边，所以要根据marginEnd来重新设置bounds的时候，需要-marginEnd
            right.setBounds(-drawableMarginEnd, 0, rightImageWidth - drawableMarginEnd, rightImageHeight);
            //注释见left
            int realDrawablePadding = getCompoundDrawablePadding() + drawableMarginStart + drawableMarginEnd;
            setCompoundDrawablePadding(realDrawablePadding);
        }
        if (left != null) {
            //对left来说，marginStart控制起始的margin，因为整个left宽度多了marginStart，所以right也要多加marginStart来保证图片不变形
            left.setBounds(drawableMarginStart, 0, leftImageWidth + drawableMarginStart, leftImageHeight);
            /*
             对于padding来说，marginEnd要表示的实际上和padding是一个概念，都是drawable和文本之间的距离，所以padding首先需要加上marginEnd
             且marginStart导致图片边界移动，移动的部分会和原本的padding区域重合，为了还原原本padding的距离，padding也需要加上marginStart
             */
            int realDrawablePadding = getCompoundDrawablePadding() + drawableMarginStart + drawableMarginEnd;
            setCompoundDrawablePadding(realDrawablePadding);

        }
        setCompoundDrawables(left, top, right, bottom);
    }


    public abstract static class SearchRecordContent<E> {
        private List<E> dataList;

        public int getDataSize() {
            return dataList.size();
        }

        public SearchRecordContent(@NotNull List<E> dataList) {
            this.dataList = dataList;
        }

        /**
         * 设置搜索记录中每一行显示的文本
         *
         * @param textView 显示的textView
         * @param position 传入数据的position
         */
        public abstract void setContent(TextView textView, int position);

        /**
         * 设置每一个Item的点击事件
         *
         * @param view     被点击的这个item
         * @param position 点击的item的position
         */
        public abstract void onItemClick(View view, int position);

        /**
         * 设置删除按钮的点击事件
         *
         * @param deleteButton 被点击的删除按钮
         * @param position     这个删除按钮所在的item的position
         */
        public abstract void onDelete(Button deleteButton, int position);

        public List<E> getDataList() {
            return dataList;
        }

        /**
         * 想要重新设置显示的内容，调用该方法即可
         * 调用完毕后并不会刷新界面数据，请手动调用notifyDataSetChanged()
         *
         * @param dataList
         */
        public void setDataList(List<E> dataList) {
            this.dataList = dataList;
        }

        public void addData(int index, E data) {
            dataList.add(index, data);
        }

        public void removeData(int index) {
            dataList.remove(index);
        }
    }


}
