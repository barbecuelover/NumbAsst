package com.ecs.numbasst.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ecs.numbasst.R;

public class TopActionBar extends FrameLayout {
    TextView tvTitle;
    ImageButton btnRefresh;
    ImageButton btnBack;
    public TopActionBar(@NonNull Context context) {
        super(context);
    }

    public TopActionBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TopActionBar);
        String title = typedArray.getString(R.styleable.TopActionBar_title);
        boolean refreshEnable = typedArray.getBoolean(R.styleable.TopActionBar_refreshEnable,false);
        typedArray.recycle();
        LayoutInflater.from(context).inflate(R.layout.view_action_bar, this, true);
        tvTitle =  findViewById(R.id.tv_action_bar_title);
        btnRefresh =  findViewById(R.id.ib_action_refresh_all);
        btnBack = findViewById(R.id.ib_action_back);
        if (refreshEnable){
            btnRefresh.setVisibility(VISIBLE);
        }else {
            btnRefresh.setVisibility(GONE);
        }
        tvTitle.setText(title);
    }

    public void setTitle(CharSequence title){
        tvTitle.setText(title);
    }

    public void setOnClickBackListener(OnClickListener clickBackListener){
        btnBack.setOnClickListener(clickBackListener);
    }

    public void setOnClickRefreshListener(OnClickListener refreshListener){
        btnRefresh.setOnClickListener(refreshListener);
    }

    public int getViewBackID(){
        return btnBack.getId();
    }

    public int getViewRefreshID(){
        return btnRefresh.getId();
    }

    public void clearBackFocus(){
        btnBack.clearFocus();
    }

    public void requestBackFocus(){
        btnBack.requestFocus();
    }

    public void setBackOnKeyListener(OnKeyListener listener){
        btnBack.setOnKeyListener(listener);
    }


}
