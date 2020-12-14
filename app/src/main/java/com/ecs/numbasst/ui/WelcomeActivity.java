package com.ecs.numbasst.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ecs.numbasst.R;
import com.ecs.numbasst.base.BaseActivity;
import com.ecs.numbasst.ui.login.LoginActivity;

public class WelcomeActivity extends BaseActivity {

    private Handler mHandler;
    private final int WELCOME_DELAY_TIME = 2000;
    ImageView imgWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setShowStatusBar(false);
        setShowTitle(false);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void initView() {
        imgWelcome = findViewById(R.id.iv_welcome);
        Glide.with(this).load(R.drawable.img_welcome).into(imgWelcome);
    }

    @Override
    protected void initData() {
        mHandler = new Handler();
    }

    @Override
    protected void initEvent() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                goMainPage();
            }
        },WELCOME_DELAY_TIME);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler!=null){
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    private void goMainPage(){
        Intent intent  =new Intent( WelcomeActivity.this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onClick(View v) {

    }
}