package com.lishang.lstablayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lishang.tablayout.LSTabLayout;


public class MainActivity extends AppCompatActivity {
    LSTabLayout lsTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lsTabLayout = findViewById(R.id.lsTabLayout);
        lsTabLayout.setMode(LSTabLayout.MODE_FIXED);
        lsTabLayout.setIndicatorStyle(LSTabLayout.INDICATOR_STYLE_TEXT_HALF);
        lsTabLayout.setOnTabSelectListener(new LSTabLayout.SimpleOnTabSelectListener() {
            @Override
            public void onTabSelect(LSTabLayout.Tab tab) {
                Log.e("LSTabLayout", "onTabSelect:" + tab.getPosition());
            }

            @Override
            public void onTabUnSelect(LSTabLayout.Tab tab) {
                Log.e("LSTabLayout", "onTabUnSelect:" + tab.getPosition());
            }

            @Override
            public void onTabReselected(LSTabLayout.Tab tab) {
                Log.e("LSTabLayout", "onTabReselected:" + tab.getPosition());
            }
        });

        init(lsTabLayout);

        lsTabLayout.addTab(lsTabLayout.newTab().setText("订单通知")
                .setBadgeStyle(LSTabLayout.BADGE_STYLE_TEXT)
                .setBadgeAnchor(LSTabLayout.BADGE_ANCHOR_TEXT)
                .setBadgeNum(5));
        lsTabLayout.addTab(lsTabLayout.newTab().setText("退款通知")
                .setBadgeStyle(LSTabLayout.BADGE_STYLE_TEXT)
                .setBadgeAnchor(LSTabLayout.BADGE_ANCHOR_TEXT)
                .setBadgeNum(23));
        lsTabLayout.addTab(lsTabLayout.newTab().setText("评价通知")
                .setBadgeStyle(LSTabLayout.BADGE_STYLE_TEXT)
                .setBadgeAnchor(LSTabLayout.BADGE_ANCHOR_TEXT)
                .setBadgeNum(101));
        lsTabLayout.addTab(lsTabLayout.newTab().setText("其它通知")
                .setBadgeStyle(LSTabLayout.BADGE_STYLE_POINT)
                .setBadgeAnchor(LSTabLayout.BADGE_ANCHOR_TEXT)
                .setBadgeNum(23));


        LSTabLayout tabLayout1 = findViewById(R.id.lsTabLayout1);
        tabLayout1.setMode(LSTabLayout.MODE_SCROLL);
        tabLayout1.setIndicatorStyle(LSTabLayout.INDICATOR_STYLE_FILL);

        init(tabLayout1);

        tabLayout1.addTab(tabLayout1.newTab().setText("订单通知"));
        tabLayout1.addTab(tabLayout1.newTab().setText("退款通知"));
        tabLayout1.addTab(tabLayout1.newTab().setText("评价通知"));
        tabLayout1.addTab(tabLayout1.newTab().setText("其它通知"));

        LSTabLayout tabLayout2 = findViewById(R.id.lsTabLayout2);
        tabLayout2.setMode(LSTabLayout.MODE_SCROLL);
        tabLayout2.setIndicatorStyle(LSTabLayout.INDICATOR_STYLE_TEXT);

        tabLayout2.setModeScrollGravity(LSTabLayout.MODE_SCROLL_CENTER);
        init(tabLayout2);

        tabLayout2.addTab(tabLayout2.newTab().setText("订单通知"));
        tabLayout2.addTab(tabLayout2.newTab().setText("退款通知"));
        tabLayout2.addTab(tabLayout2.newTab().setText("评价通知"));
        tabLayout2.addTab(tabLayout2.newTab().setText("其它通知"));

        final LSTabLayout tabLayout3 = findViewById(R.id.lsTabLayout3);
        tabLayout3.setMode(LSTabLayout.MODE_SCROLL);
        tabLayout3.setIndicatorStyle(LSTabLayout.INDICATOR_STYLE_TEXT_HALF);

        init(tabLayout3);

        tabLayout3.addTab(tabLayout3.newTab().setText("订单通知"));
        tabLayout3.addTab(tabLayout3.newTab().setText("退款通知"));
        tabLayout3.addTab(tabLayout3.newTab().setText("评价通知"));
        tabLayout3.addTab(tabLayout3.newTab().setText("其它通知"));
        tabLayout3.addTab(tabLayout3.newTab().setText("订单通知"));
        tabLayout3.addTab(tabLayout3.newTab().setText("退款通知"));
        tabLayout3.addTab(tabLayout3.newTab().setText("评价通知"));
        tabLayout3.addTab(tabLayout3.newTab().setText("其它通知"));

        LSTabLayout lsTabLayout4 = findViewById(R.id.lsTabLayout4);
        lsTabLayout4.setIndicatorShow(false);
        lsTabLayout4.setTextColor(Color.BLACK);
        lsTabLayout4.setTextSelectColor(Color.RED);
        lsTabLayout4.setTextSize(12);
        lsTabLayout4.addTab(lsTabLayout4.newTab().setText("首页")
                .setIcon(R.drawable.tab_home_icon_selector)
                .setBadgeAnchor(LSTabLayout.BADGE_ANCHOR_ICON)
                .setBadgeStyle(LSTabLayout.BADGE_STYLE_TEXT)
                .setBadgeNum(101));
        lsTabLayout4.addTab(lsTabLayout4.newTab().setText("消息")
                .setIcon(R.drawable.tab_msg_icon_selector)
                .setBadgeAnchor(LSTabLayout.BADGE_ANCHOR_ICON)
                .setBadgeStyle(LSTabLayout.BADGE_STYLE_TEXT)
                .setBadgeNum(9));
        lsTabLayout4.addTab(lsTabLayout4.newTab().setText("我的")
                .setIcon(R.drawable.tab_mine_icon_selector)
                .setBadgeAnchor(LSTabLayout.BADGE_ANCHOR_ICON)
                .setBadgeStyle(LSTabLayout.BADGE_STYLE_TEXT)
                .setBadgeNum(90));


        ViewPager viewPager = findViewById(R.id.viewPager);
        tabLayout3.setupWithViewPager(viewPager);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return tabLayout3.getTabCount();
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {

                TextView textView = new TextView(container.getContext());
                textView.setText("item:" + position);
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(Color.RED);
                textView.setBackgroundColor(position % 2 == 0 ? Color.GRAY : Color.DKGRAY);
                container.addView(textView);
                return textView;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//                super.destroyItem(container, position, object);
                container.removeView((View) object);
            }
        });
    }

    private void init(LSTabLayout tabLayout) {

        tabLayout.setIndicatorColor(Color.RED);
        tabLayout.setIndicatorHeight(5);
        tabLayout.setTextColor(Color.BLACK);
        tabLayout.setTextSelectColor(Color.RED);
        tabLayout.setTextSize(12);

    }


    public void onClick1(View view) {
        lsTabLayout.selectTabPosition(0);
    }

    public void onClick2(View view) {
        lsTabLayout.selectTabPosition(1);
    }

    public void onClick3(View view) {
        lsTabLayout.selectTabPosition(2);
    }

    public void onClick4(View view) {
        lsTabLayout.selectTabPosition(3);
    }


    private int sp2px(int sp) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, this.getResources().getDisplayMetrics());
        return px;
    }

    private int dp2px(int dp) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, this.getResources().getDisplayMetrics());
        return px;
    }

}
