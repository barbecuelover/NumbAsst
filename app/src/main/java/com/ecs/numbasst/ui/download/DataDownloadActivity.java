package com.ecs.numbasst.ui.download;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ecs.numbasst.R;
import com.ecs.numbasst.base.BaseActivity;


public class DataDownloadActivity extends BaseActivity {

    private final static int START_TIME = 1;
    private final static int END_TIME = 2;

    private TextView tvStartTime;
    private TextView tvEndTime;
    private Button btnDownload;
    private ProgressBar progressBar;
    private TextView tvProgressPercent;
    private TextView tvStatus;
    private TextView tvTitle;
    private ImageButton btnBack;

    DialogDatePickerSelect datePickerSelect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int initLayout() {
        return R.layout.activity_data_download;
    }

    @Override
    protected void initView() {
        tvStartTime = findViewById(R.id.btn_download_start_time);
        tvEndTime = findViewById(R.id.btn_download_end_time);
        btnDownload = findViewById(R.id.btn_set_car_number);
        progressBar = findViewById(R.id.progress_bar_data_download);
        tvProgressPercent = findViewById(R.id.tv_progress_percent);
        tvStatus =  findViewById(R.id.tv_data_download_status);
        tvTitle = findViewById(R.id.action_bar_title);
        btnBack= findViewById(R.id.ib_device_scan_back);

    }

    @Override
    protected void initData() {

        tvTitle.setText(getTitle());
    }

    @Override
    protected void initEvent() {
        btnBack.setOnClickListener(this);
        tvStartTime.setOnClickListener(this);
        tvEndTime.setOnClickListener(this);
        btnDownload.setOnClickListener(this);
        datePickerSelect = new DialogDatePickerSelect(this, new DialogDatePickerSelect.OnDateSelectCallBack() {
            @Override
            public void onDateSelected(int flag, int year, int month, int day, long time, String dateString) {
                switch (flag) {
                    //年审到期时间
                    case START_TIME:
                        tvStartTime.setText(dateString);
                        break;
                    //保险到期时间
                    case END_TIME:
                        tvEndTime.setText(dateString);
                        break;
                }
            }

        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.btn_download_start_time :
            datePickerSelect.showDatePickView("开始时间", START_TIME);
            break;
            case R.id.btn_download_end_time:
                datePickerSelect.showDatePickView("结束时间", END_TIME);
                break;
            case R.id.ib_device_scan_back:
                finish();
                break;
        }
    }
}