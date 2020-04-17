package com.lishang.tablayout;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * LSTabLayout
 */
public class LSTabLayout extends HorizontalScrollView {
    /**
     * 平分填充屏幕
     */
    public static final int MODE_FIXED = 0;
    /**
     * 滑动
     */
    public static final int MODE_SCROLL = 1;

    /**
     * 模式
     */
    private int mode = MODE_FIXED;

    /**
     * mode = MODE_SCROLL tab个数不超过屏幕时左对齐
     */
    public static final int MODE_SCROLL_NO = 0;
    /**
     * mode = MODE_SCROLL tab个数不超过屏幕时居中对齐
     */
    public static final int MODE_SCROLL_CENTER = 1;

    /**
     * mode = MODE_SCROLL 时 对齐方式
     */
    private int modeScrollGravity = MODE_SCROLL_NO;

    /**
     * tab最小长度
     */
    private int tabMinWidth = 72;

    /**
     * tab 字体默认颜色
     */
    private int textColor = Color.GRAY;
    /**
     * tab 字体选择颜色
     */
    private int textSelectColor = Color.BLACK;
    /**
     * 默认字体大小 单位：sp
     */
    private int textSize = 14;
    /**
     * 指示器
     */
    private GradientDrawable mIndicator;
    /**
     * 指示器区域
     */
    private Rect mIndicatorRect;
    /**
     * 指示器默认高度 dp
     */
    private int mIndicatorHeight = 2;//默认高度
    /**
     * 指示器的高度
     */
    private int mIndicatorWidth = -1;
    /**
     * 指示器的样式
     */
    private int mIndicatorStyle = 0;
    /**
     * 指示器的样式 长度和tab的长度一样
     */
    public static final int INDICATOR_STYLE_FILL = 0;
    /**
     * 指示器的样式 长度和文本长度一致
     */
    public static final int INDICATOR_STYLE_TEXT = 1;
    /**
     * 指示器的样式 长度和文本长度一半一致
     */
    public static final int INDICATOR_STYLE_TEXT_HALF = 2;

    /**
     * 指示器默认颜色
     */
    private int mIndicatorColor = Color.RED;
    /**
     * 指示器是否显示
     */
    private boolean isIndicatorShow = true;

    /**
     * 指示器动画
     */
    private ValueAnimator mIndicatorAnimator;
    /**
     * 小红点 只显示红点
     */
    public static final int BADGE_STYLE_POINT = 1;
    /**
     * 小红点 带数字
     */
    public static final int BADGE_STYLE_TEXT = 2;
    /**
     * 小红点显示在icon上
     */
    public static final int BADGE_ANCHOR_ICON = 1;
    /**
     * 小红点显示在文字上
     */
    public static final int BADGE_ANCHOR_TEXT = 2;
    /**
     * tab 集合
     */
    private List<Tab> tabs = new ArrayList<>();
    /**
     * tabView 容器
     */
    private TabViewContainer container;
    /**
     * 选中的tab
     */
    private Tab selectTab;
    /**
     * tab 选中监听
     */
    private OnTabSelectListener onTabSelectListener;
    /**
     * 关联的ViewPager
     */
    private ViewPager viewPager;

    public LSTabLayout(Context context) {
        this(context, null);
    }

    public LSTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LSTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        setFillViewport(true);
        setOverScrollMode(OVER_SCROLL_NEVER);
        setHorizontalScrollBarEnabled(false);

        mIndicator = new GradientDrawable();
        mIndicatorRect = new Rect(mIndicator.getBounds());
        mIndicatorAnimator = new ValueAnimator();

        container = new TabViewContainer(context);
        addView(container, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LSTabLayout);

        mode = array.getInt(R.styleable.LSTabLayout_mode, MODE_FIXED);
        modeScrollGravity = array.getInt(R.styleable.LSTabLayout_mode_scroll_gravity, MODE_SCROLL_NO);
        tabMinWidth = array.getDimensionPixelOffset(R.styleable.LSTabLayout_tab_min_width, dp2px(tabMinWidth));
        textColor = array.getColor(R.styleable.LSTabLayout_tab_text_color, textColor);
        textSelectColor = array.getColor(R.styleable.LSTabLayout_tab_text_select_color, textSelectColor);
        textSize = array.getDimensionPixelSize(
                R.styleable.LSTabLayout_tab_text_size, sp2px(textSize)
        );
        mIndicatorHeight = array.getDimensionPixelOffset(R.styleable.LSTabLayout_indicator_height, mIndicatorHeight);
        mIndicatorStyle = array.getInt(R.styleable.LSTabLayout_indicator_style, INDICATOR_STYLE_FILL);
        mIndicatorColor = array.getColor(R.styleable.LSTabLayout_indicator_color, mIndicatorColor);
        isIndicatorShow = array.getBoolean(R.styleable.LSTabLayout_indicator_show, isIndicatorShow);


        array.recycle();
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getModeScrollGravity() {
        return modeScrollGravity;
    }

    public void setModeScrollGravity(int modeScrollGravity) {
        this.modeScrollGravity = modeScrollGravity;
        if (mode == MODE_SCROLL && modeScrollGravity == MODE_SCROLL_CENTER) {
            container.setGravity(Gravity.CENTER);
        } else {
            container.setGravity(Gravity.NO_GRAVITY);
        }

    }

    public int getIndicatorStyle() {
        return mIndicatorStyle;
    }

    public void setIndicatorStyle(int indicatorStyle) {
        this.mIndicatorStyle = indicatorStyle;
    }

    public boolean isIndicatorShow() {
        return isIndicatorShow;
    }

    public void setIndicatorShow(boolean indicatorShow) {
        isIndicatorShow = indicatorShow;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getTextSelectColor() {
        return textSelectColor;
    }

    public void setTextSelectColor(int textSelectColor) {
        this.textSelectColor = textSelectColor;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = sp2px(textSize);
    }

    public OnTabSelectListener getOnTabSelectListener() {
        return onTabSelectListener;
    }

    public void setOnTabSelectListener(OnTabSelectListener onTabSelectListener) {
        this.onTabSelectListener = onTabSelectListener;
    }

    public int getIndicatorHeight() {
        return mIndicatorHeight;
    }

    public void setIndicatorHeight(int mIndicatorHeight) {
        this.mIndicatorHeight = mIndicatorHeight;
    }

    public int getIndicatorColor() {
        return mIndicatorColor;
    }

    public void setIndicatorColor(int mIndicatorColor) {
        this.mIndicatorColor = mIndicatorColor;
    }

    public int getTabCount() {
        return tabs.size();
    }

    /**
     * 获取当前选中tab
     *
     * @return
     */
    public Tab getSelectTab() {
        return selectTab;
    }

    /**
     * 获取tab
     *
     * @param position
     * @return
     */
    public Tab getTabByPosition(int position) {
        if (position < 0 || position >= tabs.size()) return null;
        return tabs.get(position);
    }

    /**
     * 设置tab 红点数量
     *
     * @param position
     * @param num
     */
    public void setTabBadgeNum(int position, int num) {
        if (position < 0 || position >= tabs.size()) return;
        Tab tab = tabs.get(position);
        tab.setBadgeNum(num);
    }

    /**
     * 根据下标选择tab
     *
     * @param position
     */
    public void selectTabPosition(int position) {
        if (position < 0 || position >= tabs.size()) return;
        Tab tab = tabs.get(position);
        selectTab(tab);
    }

    /**
     * 选中Tab
     *
     * @param tab
     */
    public void selectTab(Tab tab) {
        updateSelectTab(tab, true);
    }


    private void updateSelectTab(Tab tab, boolean updateIndicator) {
        final Tab currentSelectTab = selectTab;

        if (currentSelectTab == tab) {
            if (currentSelectTab != null) {
                dispatchOnTabSelectListener(tab, 2);
            }
        } else {

            int newPosition = tab != null ? tab.position : -1;

            setSelectedTabView(newPosition);

            selectTab = tab;

            if (updateIndicator) {
                container.updateIndicator();
            }

            dispatchOnTabSelectListener(currentSelectTab, 0);
            dispatchOnTabSelectListener(tab, 1);

            if (viewPager != null) {
                viewPager.setCurrentItem(selectTab.position, false);
            }

        }
    }

    /**
     * 新建Tab
     *
     * @return
     */
    public Tab newTab() {
        Tab tab = new Tab();
        tab.parent = this;
        tab.view = createTabView(tab);
        return tab;
    }

    /**
     * 添加tab
     *
     * @param tab
     */
    public void addTab(Tab tab) {
        addTab(tab, tabs.isEmpty());
    }

    /**
     * 添加Tab
     *
     * @param tab
     * @param setSelected true 选中
     */
    public void addTab(Tab tab, boolean setSelected) {

        if (tab.parent != this) {
            throw new IllegalArgumentException("Tab belongs to a different TabLayout.");
        }

        tab.position = tabs.size();
        tabs.add(tab);

        tab.view.setSelected(false);
        tab.view.setActivated(false);
        container.addView(tab.view);

        if (setSelected) {
            tab.select();
        }

    }

    /**
     * 创建TabView
     *
     * @param tab
     * @return
     */
    private TabView createTabView(Tab tab) {
        TabView tabView = new TabView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (mode == MODE_FIXED) {
            params.weight = 1;
            params.width = 0;
        }
        tabView.setMinimumWidth((tabMinWidth));
        tabView.tab = tab;
        tabView.setLayoutParams(params);
        return tabView;
    }

    /**
     * 设置选中的TabView
     *
     * @param position
     */
    private void setSelectedTabView(int position) {
        if (position == -1) return;
        int tabCount = container.getChildCount();
        if (position < tabCount) {
            for (int i = 0; i < tabCount; i++) {

                View child = container.getChildAt(i);
                child.setSelected(position == i);
                child.setActivated(position == i);

            }
        }
    }

    /**
     * 分发事件
     *
     * @param tab
     * @param type 0:未选中 1：已选中 2：再次选中
     */
    private void dispatchOnTabSelectListener(Tab tab, int type) {
        if (tab == null) return;
        if (onTabSelectListener != null) {
            switch (type) {
                case 0:
                    onTabSelectListener.onTabUnSelect(tab);
                    break;
                case 1:
                    onTabSelectListener.onTabSelect(tab);
                    break;
                case 2:
                    onTabSelectListener.onTabReselected(tab);
                    break;
            }
        }
    }


    /**
     * 关联ViewPager
     *
     * @param viewPager
     */
    public void setupWithViewPager(final ViewPager viewPager) {
        this.viewPager = viewPager;

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.e("viewPager", "onPageScrolled---position:" + position + "  positionOffset:" + positionOffset + "  positionOffsetPixels:" + positionOffsetPixels);

                Tab tab = getTabByPosition(position);
                Tab nextTab = getTabByPosition(position + 1);

                if (tab == null) {
                    return;
                }

                if (nextTab == null) {
                    //滑动到最后一个tab了
                    return;
                }


                Rect rect = container.calculationIndicatorBounds(tab);
                Rect nextRect = container.calculationIndicatorBounds(nextTab);

                float leftOffset = (nextRect.left - rect.left) * positionOffset;
                float rightOffset = (nextRect.right - rect.right) * positionOffset;

                mIndicatorRect.left = (int) (rect.left + leftOffset);
                mIndicatorRect.right = (int) (rect.right + rightOffset);


                ViewCompat.postInvalidateOnAnimation(container);

            }

            @Override
            public void onPageSelected(int position) {
                Log.e("viewPager", "onPageSelected:" + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.e("viewPager", "onPageScrollStateChanged:" + state);

                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    int position = viewPager.getCurrentItem();

                    if (selectTab != null && selectTab.position != position) {
                        Tab tab = getTabByPosition(position);
                        updateSelectTab(tab,false);
                    }

                }

            }
        });

    }


    public class Tab {
        /**
         * tab 位置
         */
        private int position;
        /**
         * tabLayout
         */
        private LSTabLayout parent;
        /**
         * tab 依附的TabView
         */
        private TabView view;
        /**
         * tab title
         */
        private CharSequence text;
        /**
         * icon
         */
        private Drawable icon;

        /**
         * 红点半径
         */
        private int badgeRadius = 4;
        /**
         * 红点 文字大小
         */
        private int badgeTextSize = 12;
        /**
         * 红点 背景颜色
         */
        private int badgeBgColor = Color.RED;
        /**
         * 红点文字颜色
         */
        private int badgeTextColor = Color.WHITE;
        /**
         * 红点文字显示最大数量 超过显示max+
         */
        private int badgeMaxNum = 99;
        /**
         * 红点文字数
         */
        private int badgeNum = 0;
        /**
         * 红点类型 点 or 数字
         */
        private int badgeStyle = BADGE_STYLE_POINT;
        /**
         * 红点显示位置 默认不添加
         */
        private int badgeAnchor;


        public int getPosition() {
            return position;
        }

        public Tab setText(CharSequence text) {
            this.text = text;
            update();
            return this;
        }

        public Tab setIcon(Drawable drawable) {
            this.icon = drawable;
            update();
            return this;
        }

        public Tab setIcon(int resId) {
            return setIcon(ContextCompat.getDrawable(parent.getContext(), resId));
        }

        public Tab setBadgeAnchor(int anchor) {
            this.badgeAnchor = anchor;
            update();
            return this;
        }

        public Tab setBadgeStyle(int badgeStyle) {
            this.badgeStyle = badgeStyle;
            update();
            return this;
        }

        public Tab setBadgeNum(int num) {
            this.badgeNum = num;
            update();
            return this;
        }


        public Tab setBadgeRadius(int badgeRadius) {
            this.badgeRadius = badgeRadius;
            update();
            return this;
        }

        public Tab setBadgeTextSize(int badgeTextSize) {
            this.badgeTextSize = badgeTextSize;
            update();
            return this;
        }

        public Tab setBadgeBgColor(int badgeBgColor) {
            this.badgeBgColor = badgeBgColor;
            update();
            return this;
        }

        public Tab setBadgeTextColor(int badgeTextColor) {
            this.badgeTextColor = badgeTextColor;
            update();
            return this;
        }

        public Tab setBadgeMaxNum(int badgeMaxNum) {
            this.badgeMaxNum = badgeMaxNum;
            update();
            return this;
        }

        /**
         * 更新tabView
         */
        private void update() {

            if (view != null) {
                view.update();
            }
        }

        /**
         * 选中tab
         */
        public void select() {

            if (parent == null) {
                throw new IllegalArgumentException("Tab not attached to a TabLayout");
            }
            parent.selectTab(this);
        }

    }

    /**
     * tabView 显示title和icon 以及 小红点
     */
    private class TabView extends LinearLayout {
        /**
         * tab
         */
        private Tab tab;
        /**
         * 文本
         */
        private TextView textView;
        /**
         * 图片
         */
        private ImageView imageView;
        /**
         * 画笔 小红点
         */
        private Paint badgePaint;

        public TabView(Context context) {
            super(context);
            setOrientation(VERTICAL);
            setGravity(Gravity.CENTER);
            //设置为true 表示可以点击view
            setClickable(true);
            setWillNotDraw(false);

            badgePaint = new Paint();
            badgePaint.setAntiAlias(true);
            badgePaint.setTextAlign(Paint.Align.CENTER);

        }


        /**
         * 更新文本信息
         */
        private void updateTextView() {
            if (TextUtils.isEmpty(tab.text)) return;
            if (textView == null) {
                textView = new TextView(getContext());
                textView.setMaxLines(1);
                textView.setIncludeFontPadding(false);
                addView(textView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            }
            textView.setText(tab.text);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        }

        /**
         * 更新图片信息
         */
        private void updateImageView() {
            if (tab.icon == null) return;
            if (imageView == null) {
                imageView = new ImageView(getContext());
                addView(imageView, 0, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            imageView.setImageDrawable(tab.icon);
        }

        /**
         * 更新tabView
         */
        public void update() {
            updateTextView();
            updateImageView();
        }

        /**
         * 点击tab
         *
         * @return
         */
        @Override
        public boolean performClick() {
            final boolean handled = super.performClick();
            if (tab != null) {
                //tab 选中
                tab.select();
            }
            return handled;
        }

        /**
         * 选中状态
         *
         * @param selected
         */
        @Override
        public void setSelected(boolean selected) {
            super.setSelected(selected);
            //更改选中的字体颜色
            if (textView != null) {
                if (selected) {
                    textView.setTextColor(textSelectColor);
                } else {
                    textView.setTextColor(textColor);
                }
            }

            if (imageView != null) {
                imageView.setSelected(selected);
            }

        }

        /**
         * child内容绘制完后调用 保证小红点在最上面显示
         *
         * @param canvas
         */
        @Override
        protected void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            //小红点在icon右上角
            if (tab.badgeAnchor == BADGE_ANCHOR_ICON && (imageView != null)) {
                drawBadgeBg(imageView, canvas);
                drawBadgeText(imageView, canvas);
            }
            //小红点在文字右上角
            if (tab.badgeAnchor == BADGE_ANCHOR_TEXT && (textView != null)) {
                drawBadgeBg(textView, canvas);
                drawBadgeText(textView, canvas);
            }

        }

        /**
         * 画badge背景
         *
         * @param view
         * @param canvas
         */
        private void drawBadgeBg(View view, Canvas canvas) {

            if (view != null && tab.badgeNum > 0) {
                /**
                 * 获取View 右上角位置
                 */
                int right = view.getRight();
                int top = view.getTop();

                badgePaint.setColor(tab.badgeBgColor);
                if (tab.badgeStyle == BADGE_STYLE_POINT) {


                    drawBadgeCircle(canvas, right, top, dp2px(tab.badgeRadius));

                } else {
                    int size = sp2px(tab.badgeTextSize);

                    if (tab.badgeNum < 10) {

                        size = size / 2 + size / 10;

                        drawBadgeCircle(canvas, right, top, size);


                    } else {
                        String text = tab.badgeNum + "";
                        if (tab.badgeNum > tab.badgeMaxNum) {
                            text = tab.badgeMaxNum + "+";
                        }
                        drawBadgeRoundRect(canvas, text, top, right, true);
                    }
                }
            }

        }

        /**
         * 背景为圆  point or num<10
         *
         * @param cx
         * @param cy
         * @param radius
         */
        private void drawBadgeCircle(Canvas canvas, int cx, int cy, int radius) {

            if (cy - radius < 0) {
                //超过父类头部了
                cy += Math.abs(cy - radius);
            }

            if (cx + radius > getRight()) {
                cx -= Math.abs(cx + radius - getRight());
            }

            canvas.drawCircle(cx, cy, radius, badgePaint);

        }

        /**
         * 画圆角矩形
         *
         * @param canvas
         * @param text
         * @param top
         * @param right
         * @param isCanvas
         * @return
         */
        private RectF drawBadgeRoundRect(Canvas canvas, String text, int top, int right, boolean isCanvas) {

            badgePaint.setTextSize(sp2px(tab.badgeTextSize));

            Rect rect = new Rect();
            badgePaint.getTextBounds(text, 0, text.length(), rect);

            int halfWidth = rect.width() / 2;
            int halfHeight = rect.height() / 2;

            int radius = rect.height() / 2;
            int halfRadius = radius / 2;

            if (top - halfHeight - halfRadius < 0) {
                top += Math.abs(top - halfHeight - halfRadius);
            }

            if (right + halfWidth + halfRadius > getRight()) {
                //超过右边框
                right -= Math.abs(right + halfWidth + halfRadius - getRight());
            }


            RectF rectF = new RectF(right - halfWidth - radius, top - halfHeight - halfRadius, right + halfWidth + radius, top + halfHeight + halfRadius);

            if (isCanvas) {
                canvas.drawRoundRect(rectF, rect.height(), rect.height(), badgePaint);
            }

            return rectF;

        }

        /**
         * 画文字
         *
         * @param view
         * @param canvas
         */
        private void drawBadgeText(View view, Canvas canvas) {
            if (view != null && tab.badgeNum > 0) {
                int right = view.getRight();
                int top = view.getTop();

                badgePaint.setColor(tab.badgeBgColor);

                int size = sp2px(tab.badgeTextSize);
                badgePaint.setTextSize(size);
                badgePaint.setColor(tab.badgeTextColor);

                if (tab.badgeStyle == BADGE_STYLE_POINT) {
                    //点 模式 不用画文字
                } else {

                    Rect rect = new Rect();

                    String text = tab.badgeNum + "";

                    if (tab.badgeNum < 10) {

                        size = size / 2 + size / 10;
                        if (top - size < 0) {
                            //超过父类头部了
                            top += Math.abs(top - size);
                        }

                        if (right + size > getRight()) {
                            right -= Math.abs(right + size - getRight());
                        }

                        rect.set(right - size, top - size, right + size, top + size);


                        Paint.FontMetrics fontMetrics = badgePaint.getFontMetrics();
                        /**
                         * 文字基线中心点位置
                         */
                        int baseLineY = (int) (rect.centerY() - fontMetrics.top / 2 - fontMetrics.bottom / 2);//基线中间点的y轴计算公式
                        /**
                         * (fontMetrics.top - fontMetrics.ascent) / 2 说明：由于 top 和 ascent 有可能有空隙 所以中心位置有可能有偏移 ，计算top-ascent的一半再次进行偏移
                         */
                        canvas.drawText(text, rect.centerX(), baseLineY + (fontMetrics.top - fontMetrics.ascent) / 2, badgePaint);

                    } else {

                        if (tab.badgeNum > tab.badgeMaxNum) {
                            text = tab.badgeMaxNum + "+";
                        }

                        Paint.FontMetrics fontMetrics = badgePaint.getFontMetrics();


                        RectF rectF = drawBadgeRoundRect(canvas, text, top, right, false);

                        int baseLineY = (int) (rectF.centerY() - fontMetrics.top / 2 - fontMetrics.bottom / 2);//基线中间点的y轴计算公式

                        canvas.drawText(text, rectF.centerX(), baseLineY + (fontMetrics.top - fontMetrics.ascent) / 2, badgePaint);

                    }


                }
            }

        }

    }


    /**
     * tabView 容器
     */
    private class TabViewContainer extends LinearLayout {

        public TabViewContainer(Context context) {
            super(context);
            setOrientation(HORIZONTAL);
            setWillNotDraw(false);
            if (mode == MODE_SCROLL && modeScrollGravity == MODE_SCROLL_CENTER) {
                setGravity(Gravity.CENTER);
            } else {
                setGravity(Gravity.NO_GRAVITY);
            }

        }


        @Override
        public void draw(Canvas canvas) {

            super.draw(canvas);
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);


            if (!isIndicatorShow) {
//                mIndicator.setVisible(false, true);
            } else {

                if (mIndicatorRect.left == 0 && mIndicatorRect.right == 0) {
                    //左右都为0，重新计算 解决tab默认选中时还没有绘制到布局上计算导致位置有误
                    Rect rect = calculationIndicatorBounds(selectTab);

                    mIndicatorRect.left = rect.left;
                    mIndicatorRect.right = rect.right;
                }

                mIndicator.setBounds(mIndicatorRect);
                mIndicator.setColor(mIndicatorColor);
                mIndicator.draw(canvas);
            }

        }

        /**
         * 计算指示器的位置
         *
         * @return
         */
        private Rect calculationIndicatorBounds(Tab tab) {
            Rect rect = new Rect();
            if (tab == null) return rect;

            TabView tabView = tab.view;

            int left = tabView.getLeft();
            int right = tabView.getRight();
            int top = tabView.getTop();
            int bottom = tabView.getBottom();


            if (left == 0 && right == 0 && top == 0 && bottom == 0) {
                //tabView 都为0 说明还没有绘制到布局上
                return rect;
            }

            mIndicatorRect.top = bottom - mIndicatorHeight;
            mIndicatorRect.bottom = bottom;

            switch (mIndicatorStyle) {
                case INDICATOR_STYLE_FILL:
                    break;
                case INDICATOR_STYLE_TEXT: {
                    int width = measureText(tabView.textView);
                    if (width != 0) {

                        left += (right - left - width) / 2;

                        right = left + width;

                    }
                }

                break;
                case INDICATOR_STYLE_TEXT_HALF: {
                    int width = measureText(tabView.textView);
                    if (width != 0) {
                        left += (right - left - width) / 2;
                        left += width / 4;
                        right = left + width / 2;
                    }
                }

                break;
            }

            rect.set(left, mIndicatorRect.top, right, bottom);
            return rect;

        }

        /**
         * 更新指示器位置
         */
        private void updateIndicator() {
            if (!isIndicatorShow) return;

            Rect rect = calculationIndicatorBounds(selectTab);

            if (mIndicatorRect.left == 0 && mIndicatorRect.right == 0) {
                mIndicatorRect.left = rect.left;
                mIndicatorRect.right = rect.right;
                ViewCompat.postInvalidateOnAnimation(this);
            } else {

                if (rect.left == mIndicatorRect.left && rect.right == mIndicatorRect.right) return;

                if (mIndicatorAnimator.isRunning()) {
                    mIndicatorAnimator.cancel();
                }

                mIndicatorAnimator.setIntValues(mIndicatorRect.left, rect.left);

                mIndicatorAnimator.removeAllUpdateListeners();
                mIndicatorAnimator.removeAllListeners();

                mIndicatorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (int) animation.getAnimatedValue();

                        mIndicatorRect.right = value + mIndicatorRect.width();
                        mIndicatorRect.left = value;

                        ViewCompat.postInvalidateOnAnimation(TabViewContainer.this);

                    }
                });


                mIndicatorAnimator.setDuration(200);
                mIndicatorAnimator.start();

            }


        }

        /**
         * 测量文本长度
         *
         * @param textView
         * @return
         */
        private int measureText(TextView textView) {
            if (textView == null) return 0;
            return (int) textView.getPaint().measureText(textView.getText().toString());
        }

    }

    private int sp2px(int sp) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getContext().getResources().getDisplayMetrics());
        return px;
    }

    private int dp2px(int dp) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
        return px;
    }


    public interface OnTabSelectListener {
        void onTabSelect(Tab tab);

        void onTabUnSelect(Tab tab);

        void onTabReselected(Tab tab);
    }

    public static class SimpleOnTabSelectListener implements OnTabSelectListener {
        @Override
        public void onTabSelect(Tab tab) {

        }

        @Override
        public void onTabUnSelect(Tab tab) {

        }

        @Override
        public void onTabReselected(Tab tab) {

        }
    }

}
