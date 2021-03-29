package com.ecs.numbasst.ui.about;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ecs.numbasst.R;
import com.ecs.numbasst.base.BaseActionBarActivity;
import com.ecs.numbasst.base.BaseActivity;
import com.ecs.numbasst.base.util.SystemUtils;

public class AboutActivity extends BaseActionBarActivity {

    TextView tvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_about;
    }

    @Override
    protected void initView() {
        tvVersion =  findViewById(R.id.version_name);
    }

    @Override
    protected void initData() {
        String version = SystemUtils.getVersionName(this);
        if (! "".equals(version)){
            tvVersion.setText(version);
        }
    }

    @Override
    protected void initEvent() {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    @Override
    public void onRefreshAll() {

    }
}