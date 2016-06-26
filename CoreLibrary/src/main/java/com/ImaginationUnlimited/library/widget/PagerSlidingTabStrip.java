package com.ImaginationUnlimited.library.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ImaginationUnlimited.library.R;

import java.util.Locale;

/**
 * 配合ViewPager 使用的indicator，支持ViewPager Scroll
 *
 * 设置TabBackground的时候，将无法显示Indicator(Bug,待解决)
 *
 * @date: 2014-11-14 上午10:38:46 <br/>
 */
public class PagerSlidingTabStrip extends HorizontalScrollView {

    public interface IconTabProvider {

        int getPageIconResId(int position);
    }

    // @formatter:off
    private static final int[] ATTRS = new int[]{
            android.R.attr.textSize,
            android.R.attr.textColor};

    // @formatter:on

    private LinearLayout.LayoutParams defaultTabLayoutParams;

    private LinearLayout.LayoutParams expandedTabLayoutParams;

    private final PageListener pageListener = new PageListener();

    public OnPageChangeListener delegatePageListener;

    private LinearLayout tabsContainer;

    private ViewPager pager;

    private int tabCount;

    private int currentPosition = 0;

    private float currentPositionOffset = 0f;

    private Paint rectPaint;

    private Paint dividerPaint;

    private int indicatorColor = 0xFF666666;

    private int underlineColor = 0x1A000000;

    private int dividerColor = 0x1A000000;

    private boolean shouldExpand = false;

    private boolean textAllCaps = true;

    private int scrollOffset = 52;

    private int indicatorHeight = 8;

    private int underlineHeight = 2;

    private int dividerPadding = 12;

    private int tabPadding = 0;

    private int dividerWidth = 1;

    private int tabTextSize = 10;

    private int tabTextColor = 0xFF666666;

    private Typeface tabTypeface = null;

    private int tabTypefaceStyle = Typeface.NORMAL;

    private int lastScrollX = 0;

    private int tabBackgroundResId = R.drawable.background_tab;

    private static final int INVALID_RESOURCE_ID = -1;

    private int tabSelectedBackgroundId = INVALID_RESOURCE_ID;
    private int tabSelectedFirstBackgroundId = INVALID_RESOURCE_ID;
    private int tabSelectedLastBackgroundId = INVALID_RESOURCE_ID;
    private int tabDefaultBackgroundId = INVALID_RESOURCE_ID;

    private Locale locale;

    /**
     * 底线是否和文字宽度保持一致
     */
    private boolean shouldScale = false;

    /**
     * 选中tab颜色
     */
    private int selectedtabTextColor = 0xFF666666;

    /**
     * 选中tab位置
     */
    private int selectedtab = 0;

    public PagerSlidingTabStrip(Context context) {
        this(context, null);
    }

    public PagerSlidingTabStrip(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagerSlidingTabStrip(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setFillViewport(true);
        setWillNotDraw(false);

        DisplayMetrics dm = getResources().getDisplayMetrics();

        scrollOffset = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scrollOffset, dm);
        indicatorHeight = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorHeight, dm);
        underlineHeight = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, underlineHeight, dm);
        dividerPadding = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerPadding, dm);
        tabPadding = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tabPadding, dm);
        dividerWidth = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerWidth, dm);
        tabTextSize = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, tabTextSize, dm);

        // get system attrs (android:textSize and android:textColor)

        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);

        tabTextSize = a.getDimensionPixelSize(0, tabTextSize);
        tabTextColor = a.getColor(1, tabTextColor);

        selectedtabTextColor = a.getColor(1, selectedtabTextColor);

        a.recycle();

        // get custom attrs

        a = context.obtainStyledAttributes(attrs, R.styleable.PagerSlidingTabStrip);

        indicatorColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsIndicatorColor, indicatorColor);
        underlineColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsUnderlineColor, underlineColor);
        dividerColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsDividerColor, dividerColor);
        indicatorHeight = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsIndicatorHeight, indicatorHeight);
        underlineHeight = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsUnderlineHeight, underlineHeight);
        dividerPadding = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsDividerPadding, dividerPadding);
        tabPadding = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsTabPaddingLeftRight, tabPadding);
        tabBackgroundResId = a.getResourceId(R.styleable.PagerSlidingTabStrip_pstsTabBackground, tabBackgroundResId);
        shouldExpand = a.getBoolean(R.styleable.PagerSlidingTabStrip_pstsShouldExpand, shouldExpand);
        shouldScale = a.getBoolean(R.styleable.PagerSlidingTabStrip_pstsShouldScale, shouldScale);
        scrollOffset = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsScrollOffset, scrollOffset);
        textAllCaps = a.getBoolean(R.styleable.PagerSlidingTabStrip_pstsTextAllCaps, textAllCaps);

        tabSelectedBackgroundId = a.getResourceId(R.styleable.PagerSlidingTabStrip_pstsTabSelectedBackground, INVALID_RESOURCE_ID);
        tabSelectedBackgroundId = a.getResourceId(R.styleable.PagerSlidingTabStrip_pstsTabSelectedBackground, INVALID_RESOURCE_ID);
        tabSelectedFirstBackgroundId = a.getResourceId(R.styleable.PagerSlidingTabStrip_pstsTabSelectedFirstBackground, INVALID_RESOURCE_ID);
        tabSelectedLastBackgroundId = a.getResourceId(R.styleable.PagerSlidingTabStrip_pstsTabSelectedLastBackground, INVALID_RESOURCE_ID);
        tabDefaultBackgroundId = a.getResourceId(R.styleable.PagerSlidingTabStrip_pstsTabDefaultBackground, INVALID_RESOURCE_ID);
        dividerWidth = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsDividerWidth, dividerWidth);


        int selectColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsSelectedTextColor, -1);
//        if(selectColor != -1){
            selectedtabTextColor = selectColor;
//        }

        boolean scrollable = a.getBoolean(R.styleable.PagerSlidingTabStrip_pstsScrollable, true);


        a.recycle();

        rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setStyle(Style.FILL);

        dividerPaint = new Paint();
        dividerPaint.setAntiAlias(true);
        dividerPaint.setStrokeWidth(dividerWidth);

        tabsContainer = new LinearLayout(context);
        tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        tabsContainer.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(tabsContainer);

        if(!scrollable){
            post(new Runnable() {
                @Override
                public void run() {
                    LayoutParams lp = (LayoutParams) tabsContainer.getLayoutParams();
                    lp.width = getWidth();
                    tabsContainer.setLayoutParams(lp);
                }
            });
        }


        defaultTabLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        expandedTabLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);

        if(locale == null) {
            locale = getResources().getConfiguration().locale;
        }
    }

    public void setViewPager(ViewPager pager) {
        this.pager = pager;

        if(pager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }

        pager.setOnPageChangeListener(pageListener);

        notifyDataSetChanged();
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.delegatePageListener = listener;
    }

    public void notifyDataSetChanged() {

        tabsContainer.removeAllViews();

        tabCount = pager.getAdapter().getCount();

        for(int i = 0; i < tabCount; i ++ ) {

            if(pager.getAdapter() instanceof IconTabProvider) {
                addIconTab(i, ((IconTabProvider)pager.getAdapter()).getPageIconResId(i));
            } else {
                addTextTab(i, pager.getAdapter().getPageTitle(i).toString());
            }

        }

        updateTabStyles();

        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @SuppressLint("NewApi")
            @Override
            public void onGlobalLayout() {

                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                currentPosition = pager.getCurrentItem();
                scrollToChild(currentPosition, 0);
            }
        });

    }

    protected TextView newTextView(){
        return new TextView(getContext());
    }

    private void addTextTab(final int position, String title) {

        TextView tab = newTextView();
        tab.setText(title);
        tab.setGravity(Gravity.CENTER);
        tab.setSingleLine();

        addTab(position, tab);
    }

    private void addIconTab(final int position, int resId) {

        ImageButton tab = new ImageButton(getContext());
        tab.setImageResource(resId);

        addTab(position, tab);

    }

    private void addTab(final int position, View tab) {
        tab.setFocusable(true);
        tab.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                pager.setCurrentItem(position);
            }
        });

        tab.setPadding(tabPadding, 0, tabPadding, 0);
        tabsContainer.addView(tab, position,
                shouldExpand ? expandedTabLayoutParams : defaultTabLayoutParams);
    }

    private void updateTabStyles() {

        for(int i = 0; i < tabCount; i ++ ) {

            View v = tabsContainer.getChildAt(i);

            v.setBackgroundResource(tabBackgroundResId);

            if(v instanceof TextView) {

                TextView tab = (TextView)v;
                // tab.setTextSize(TypedValue.COMPLEX_UNIT_SP, tabTextSize);
                tab.setTextSize(tabTextSize);
                tab.setTypeface(tabTypeface, tabTypefaceStyle);
                if(selectedtab == i){
                    tab.setTextColor(selectedtabTextColor);
                    if(i == 0){
                        if(tabSelectedFirstBackgroundId != INVALID_RESOURCE_ID){
                            v.setBackgroundResource(tabSelectedFirstBackgroundId);
                        }
                    }else if(i == tabCount - 1){
                        if(tabSelectedLastBackgroundId != INVALID_RESOURCE_ID){
                            v.setBackgroundResource(tabSelectedLastBackgroundId);
                        }
                    }else{
                        if(tabSelectedBackgroundId != INVALID_RESOURCE_ID){
                            v.setBackgroundResource(tabSelectedBackgroundId);
                        }
                    }
                }else{
                    tab.setTextColor(tabTextColor);
                    if(tabDefaultBackgroundId != INVALID_RESOURCE_ID){
                        v.setBackgroundResource(tabDefaultBackgroundId);
                    }
                }

                // setAllCaps() is only available from API 14, so the upper case
                // is made manually if we are on a
                // pre-ICS-build
                if(textAllCaps) {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        tab.setAllCaps(true);
                    } else {
                        tab.setText(tab.getText().toString().toUpperCase(locale));
                    }
                }
            }
        }

    }

    private void scrollToChild(int position, int offset) {

        if(tabCount == 0) {
            return;
        }

        int newScrollX = tabsContainer.getChildAt(position).getLeft() + offset;

        if(position > 0 || offset > 0) {
            newScrollX -= scrollOffset;
        }

        if(newScrollX != lastScrollX) {
            lastScrollX = newScrollX;
            scrollTo(newScrollX, 0);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(isInEditMode() || tabCount == 0) {
            return;
        }

        final int height = getHeight();

        // draw indicator line

        rectPaint.setColor(indicatorColor);

        // default: line below current tab
        View currentTab = tabsContainer.getChildAt(currentPosition);
        float lineLeft = currentTab.getLeft();
        float lineRight = currentTab.getRight();

        float delta = 0;
        if(currentTab instanceof TextView){
            TextView tv = (TextView)currentTab;
            delta = (tv.getWidth() - tv.getPaint().measureText(tv.getText().toString())) / 2.0f;
        }else{
            delta = tabPadding;
        }

        // if there is an offset, start interpolating left and right coordinates
        // between current and next tab
        if(currentPositionOffset > 0f && currentPosition < tabCount - 1) {

            View nextTab = tabsContainer.getChildAt(currentPosition + 1);
            final float nextTabLeft = nextTab.getLeft();
            final float nextTabRight = nextTab.getRight();

            lineLeft = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset) * lineLeft);
            lineRight = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset) * lineRight);
        }
        if(shouldScale) {
            canvas.drawRect(lineLeft /**+ tabPadding**/ + delta,
                    height - indicatorHeight,
                    lineRight /**- tabPadding**/
                            - delta, height, rectPaint);
        }else {
            canvas.drawRect(lineLeft, height - indicatorHeight, lineRight, height, rectPaint);
        }
        // draw underline

        rectPaint.setColor(underlineColor);
        canvas.drawRect(0, height - underlineHeight, tabsContainer.getWidth(), height, rectPaint);

        // draw divider

        dividerPaint.setColor(dividerColor);
        for(int i = 0; i < tabCount - 1; i ++ ) {
            View tab = tabsContainer.getChildAt(i);
            canvas.drawLine(tab.getRight(), dividerPadding, tab.getRight(), height - dividerPadding, dividerPaint);
        }
    }

    private class PageListener implements OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            currentPosition = position;
            currentPositionOffset = positionOffset;
            View childView = tabsContainer.getChildAt(position);
            if(childView != null) {
                scrollToChild(position, (int)(positionOffset * childView.getWidth()));

                invalidate();

                if(delegatePageListener != null) {
                    delegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if(state == ViewPager.SCROLL_STATE_IDLE) {
                scrollToChild(pager.getCurrentItem(), 0);
            }

            if(delegatePageListener != null) {
                delegatePageListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {
            if(delegatePageListener != null) {
                delegatePageListener.onPageSelected(position);
            }
            selectedtab = position;
            updateTabStyles();
        }

    }

    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
        invalidate();
    }

    public void setIndicatorColorResource(int resId) {
        this.indicatorColor = getResources().getColor(resId);
        invalidate();
    }

    public int getIndicatorColor() {
        return this.indicatorColor;
    }

    public void setIndicatorHeight(int indicatorLineHeightPx) {
        this.indicatorHeight = indicatorLineHeightPx;
        invalidate();
    }

    public int getIndicatorHeight() {
        return indicatorHeight;
    }

    public void setUnderlineColor(int underlineColor) {
        this.underlineColor = underlineColor;
        invalidate();
    }

    public void setUnderlineColorResource(int resId) {
        this.underlineColor = getResources().getColor(resId);
        invalidate();
    }

    public int getUnderlineColor() {
        return underlineColor;
    }

    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
        invalidate();
    }

    public void setDividerColorResource(int resId) {
        this.dividerColor = getResources().getColor(resId);
        invalidate();
    }

    public int getDividerColor() {
        return dividerColor;
    }

    public void setUnderlineHeight(int underlineHeightPx) {
        this.underlineHeight = underlineHeightPx;
        invalidate();
    }

    public int getUnderlineHeight() {
        return underlineHeight;
    }

    public void setDividerPadding(int dividerPaddingPx) {
        this.dividerPadding = dividerPaddingPx;
        invalidate();
    }

    public int getDividerPadding() {
        return dividerPadding;
    }

    public void setScrollOffset(int scrollOffsetPx) {
        this.scrollOffset = scrollOffsetPx;
        invalidate();
    }

    public int getScrollOffset() {
        return scrollOffset;
    }

    public void setShouldExpand(boolean shouldExpand) {
        this.shouldExpand = shouldExpand;
        requestLayout();
    }

    public boolean getShouldExpand() {
        return shouldExpand;
    }

    public boolean isTextAllCaps() {
        return textAllCaps;
    }

    public boolean isShouldScale() {
        return shouldScale;
    }

    /**
     * setShouldScale:设置底线是否和文字宽度保持一致<br/>
     * @author adison
     * @param shouldScale
     */
    public void setShouldScale(boolean shouldScale) {
        this.shouldScale = shouldScale;
        requestLayout();
    }

    public void setAllCaps(boolean textAllCaps) {
        this.textAllCaps = textAllCaps;
    }

    public void setTextSize(int textSizePx) {
        this.tabTextSize = textSizePx;
        updateTabStyles();
    }

    public int getTextSize() {
        return tabTextSize;
    }

    public void setTextColor(int textColor) {
        this.tabTextColor = textColor;
        updateTabStyles();
    }

    public void setTextColorResource(int resId) {
        this.tabTextColor = getResources().getColor(resId);
        updateTabStyles();
    }

    public int getTextColor() {
        return tabTextColor;
    }

    /**************** 设置选中颜色 ******************/
    public void setSelectTextColor(int textColor) {
        this.selectedtabTextColor = textColor;
        updateTabStyles();
    }

    public void setSelectTextColorResource(int resId) {
        this.selectedtabTextColor = getResources().getColor(resId);
        updateTabStyles();
    }

    public int getSelectTextColor() {
        return selectedtabTextColor;
    }

    /************** end ******************/

    public void setTypeface(Typeface typeface, int style) {
        this.tabTypeface = typeface;
        this.tabTypefaceStyle = style;
        updateTabStyles();
    }

    public void setTabBackground(int resId) {
        this.tabBackgroundResId = resId;
    }

    public int getTabBackground() {
        return tabBackgroundResId;
    }

    public void setTabPaddingLeftRight(int paddingPx) {
        this.tabPadding = paddingPx;
        updateTabStyles();
    }

    public int getTabPaddingLeftRight() {
        return tabPadding;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState)state;
        super.onRestoreInstanceState(savedState.getSuperState());
        currentPosition = savedState.currentPosition;
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.currentPosition = currentPosition;
        return savedState;
    }

    static class SavedState extends BaseSavedState {

        int currentPosition;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentPosition = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(currentPosition);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {

            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

}
