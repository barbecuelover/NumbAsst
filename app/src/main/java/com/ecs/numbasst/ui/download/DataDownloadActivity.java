package com.ecs.numbasst.ui.download;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ecs.numbasst.R;
import com.ecs.numbasst.base.BaseActivity;
import com.ecs.numbasst.manager.BleServiceManager;
import com.ecs.numbasst.manager.callback.StatusCallback;

import java.text.SimpleDateFormat;
import java.util.Date;


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

    private StatusCallback downloadRequestCallback;

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
        btnDownload = findViewById(R.id.btn_download_data);
        progressBar = findViewById(R.id.progress_bar_data_download);
        tvProgressPercent = findViewById(R.id.tv_progress_percent);
        tvStatus =  findViewById(R.id.tv_data_download_status);
        tvTitle = findViewById(R.id.action_bar_title);
        btnBack= findViewById(R.id.ib_action_back);

    }

    @Override
    protected void initData() {

        tvTitle.setText(getTitle());
        String date = new SimpleDateFormat("yyy-MM-dd").format(new Date());
        tvStartTime.setText(date);
        tvEndTime.setText(date);

        downloadRequestCallback = new StatusCallback() {
            @Override
            public void onSucceed(String msg) {
                //msg 为下载数据大小,询问用户确定要下载数据
                showDownloadConfirmDialog();

            }

            @Override
            public void onFailed(String reason) {

            }
        };
    }

    private void showDownloadConfirmDialog() {

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
        int id = v.getId();
        if (id ==R.id.btn_download_start_time){
            datePickerSelect.showDatePickView("开始时间", START_TIME);
        }else if (id == R.id.btn_download_end_time){
            datePickerSelect.showDatePickView("结束时间", END_TIME);
        }else if( id == R.id.ib_action_back){
            finish();
        }else if( id == R.id.btn_download_data){
            prepareDownloadData();
        }

    }

    private void prepareDownloadData() {
        String startTime = tvStartTime.getText().toString().replace("-","");
        String endTime = tvEndTime.getText().toString().replace("-","");
        BleServiceManager.getInstance().downloadDataRequest(startTime,endTime,downloadRequestCallback);
    }

    private void replyDownloadConfirm(boolean download){

    }

}