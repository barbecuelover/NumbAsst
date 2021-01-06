package com.ecs.numbasst.ui.download;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ecs.numbasst.R;
import com.ecs.numbasst.base.BaseActivity;
import com.ecs.numbasst.base.util.Log;
import com.ecs.numbasst.manager.BleServiceManager;
import com.ecs.numbasst.manager.callback.DownloadCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class DataDownloadActivity extends BaseActivity {

    private final static int START_TIME = 1;
    private final static int END_TIME = 2;

    private TextView tvStartTime;
    private TextView tvEndTime;
    private Button btnDownload;
    private ProgressBar progressBarStatus;
    private ProgressBar progressBarDownload;
    private TextView tvProgressPercent;
    private TextView tvStatus;
    private TextView tvTitle;
    private ImageButton btnBack;

    DialogDatePicker datePickerSelect;

    private BleServiceManager manager;
    private long dataTotalSize;
    private long currentSize;

    private boolean isDownloading;
    private SimpleDateFormat dateFormat;

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
        progressBarStatus = findViewById(R.id.progress_bar_download_status);
        progressBarDownload = findViewById(R.id.progress_bar_data_download_percent);
        tvProgressPercent = findViewById(R.id.tv_progress_percent);
        tvStatus =  findViewById(R.id.tv_data_download_status);
        tvTitle = findViewById(R.id.action_bar_title);
        btnBack= findViewById(R.id.ib_action_back);

    }

    @Override
    protected void initData() {

        tvTitle.setText(getTitle());
        manager = BleServiceManager.getInstance();
        dateFormat = new SimpleDateFormat("yyy-MM-dd", Locale.getDefault());
        String date =dateFormat.format(new Date());
        tvStartTime.setText(date);
        tvEndTime.setText(date);
        Log.d("zwcc"," Unix :" + new Date().getTime());
        DownloadCallback downloadCallback = new DownloadCallback() {
            @Override
            public void onRetryFailed() {

            }

            @Override
            public void onConfirmed(long size) {
                dataTotalSize = size;
                showDownloadConfirmDialog((size/1024)+" kb");
            }

            @Override
            public void onTransferred(byte[] data) {
               if (dataTotalSize == currentSize){
                   isDownloading = false;
                   tvStatus.setText("下载完成!");
               }else {
                   int progress = (int) ((currentSize/dataTotalSize) *100);
                   tvProgressPercent.setText(progress+"%");
                   progressBarDownload.setProgress(progress);
               }
            }
        };

        manager.setDownloadCallback(downloadCallback);
    }



    @Override
    protected void initEvent() {
        btnBack.setOnClickListener(this);
        tvStartTime.setOnClickListener(this);
        tvEndTime.setOnClickListener(this);
        btnDownload.setOnClickListener(this);
        datePickerSelect = new DialogDatePicker(this, new DialogDatePicker.OnDateSelectCallBack() {
            @Override
            public void onDateSelected(int flag, int year, int month, int day, long time, String dateString) {
                switch (flag) {
                    case START_TIME:
                        tvStartTime.setText(dateString);
                        break;
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

        if (!manager.isConnected()){
            showToast(getString(R.string.check_device_connection));
            return;
        }
        if (progressBarStatus.getVisibility()==View.VISIBLE ||isDownloading){
            showToast("下载操作中请勿重复点击下载");
            return;
        }

//        String startTime = tvStartTime.getText().toString().replace("-","");
//        String endTime = tvEndTime.getText().toString().replace("-","");

        try {
            Date dateStart = dateFormat.parse(tvStartTime.getText().toString());
            Date dateEnd = dateFormat.parse(tvEndTime.getText().toString());
            dataTotalSize = 0;
            BleServiceManager.getInstance().downloadDataRequest(dateStart,dateEnd);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private void showDownloadConfirmDialog(String size){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("数据下载");
        builder.setMessage("是否要下载数据？数据大小为"+size);
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                progressBarStatus.setVisibility(View.VISIBLE);
                isDownloading =true;
                BleServiceManager.getInstance().replyDownloadConfirm(true);
            }
        });
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BleServiceManager.getInstance().replyDownloadConfirm(false);
                progressBarStatus.setVisibility(View.GONE);
                isDownloading = false;
                dialog.dismiss();
            }
        });

        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}