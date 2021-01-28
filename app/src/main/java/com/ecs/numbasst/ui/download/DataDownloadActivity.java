package com.ecs.numbasst.ui.download;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ecs.numbasst.R;
import com.ecs.numbasst.base.BaseActionBarActivity;
import com.ecs.numbasst.base.util.Log;
import com.ecs.numbasst.manager.BleServiceManager;
import com.ecs.numbasst.manager.msg.DownloadMsg;
import com.ecs.numbasst.view.DialogDatePicker;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class DataDownloadActivity extends BaseActionBarActivity {

    private final static int START_TIME = 1;
    private final static int END_TIME = 2;

    private TextView tvStartTime;
    private TextView tvEndTime;
    private Button btnDownload;

    private ProgressBar progressBarDownload;
    private TextView tvProgressPercent;
    private TextView tvStatus;

    DialogDatePicker datePickerSelect;

    private long currentSize;

    private boolean isDownloading;
    private SimpleDateFormat dateFormat;

    Date startDate = new Date();
    Date endDate = new Date();
    Calendar calendar;

    private List<Long> allFiles = new ArrayList<>();
    private List<Long> downloadFiles = new ArrayList<>();
    int downloadedSize = 0;
    private SimpleDateFormat nameFormat;
    private ProgressBar progressBarFiles;
    private TextView tvFilesPercent;

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
        progressBarDownload = findViewById(R.id.progress_bar_data_download_percent);
        progressBarFiles = findViewById(R.id.progress_bar_download_file);
        tvProgressPercent = findViewById(R.id.tv_progress_percent);
        tvFilesPercent = findViewById(R.id.tv_progress_file_percent);
        tvStatus = findViewById(R.id.tv_data_download_status);

    }

    @Override
    protected void initData() {
        dateFormat = new SimpleDateFormat("yyy-MM-dd", Locale.getDefault());
        nameFormat = new SimpleDateFormat("yyyMMdd", Locale.getDefault());
        String date = dateFormat.format(new Date());
        tvStartTime.setText(date);
        tvEndTime.setText(date);
        // Log.d("zwcc"," Unix :" + new Date().getTime());

        calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

        zeroDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE));

        startDate = calendar.getTime();
        endDate = calendar.getTime();
        Log.d("Download", "startDate = " + startDate.getTime() / 1000);
    }

    private void zeroDate(int y, int month, int day) {
        calendar.set(Calendar.YEAR, y);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DATE, day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND,0);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownload(DownloadMsg msg) {
        int state = msg.getMsgType();
        if (state == DownloadMsg.DOWNLOAD_ALL_FILES) {
            resetList();
            allFiles = msg.getFiles();
            if (allFiles == null || allFiles.size() == 0) {
                updateState("查询文件不存在");
            } else {
                long start = startDate.getTime();
                long end = endDate.getTime();


                Log.d("zwcc","开始日期Long ：" +start);
                Log.d("zwcc","结束日期Long ：" +end);
                //对比 时间段内 是否存在文件。
                for (Long l : allFiles) {
                    Log.d("zwcc","文件日期："+l);
                    if (l >= start && l <= end) {
                        downloadFiles.add(l);
                        Log.d("zwcc","添加到待下载队列 文件日期："+l);
                    }
                }
                if (downloadFiles.size() == 0) {
                    updateState("选择的日期内不存在文件");
                } else {
                    //开始下载第一个文件。
                    Date firstFile = new Date(downloadFiles.get(0));
                    manager.downloadOneDayData(0, firstFile);

                    updateState("下载文件：" + nameFormat.format(firstFile));
                    //显示文件进度条
                    tvFilesPercent.setText("0/" + downloadFiles.size());
                    progressBarFiles.setProgress(0);
                }
            }

        } else if (state == DownloadMsg.DOWNLOAD_FILE_NULL) {
            updateState("获取文件为空，日期为：" + msg.getDate().toString());
            hideProgressBar();
            for (long file : downloadFiles){
                if (file == msg.getDate().getTime()){
                    //如果想下载目录中 有这个文件，且服务器返回不存在 则下载 下一个文件。
                    ifDownloadCompleted( msg.getDate().getTime());
                    break;
                }
            }

        } else if (state == DownloadMsg.DOWNLOAD_FILE_INFO) {
            updateState("准备下载:" + nameFormat.format(msg.getDate()));
            progressBarDownload.setProgress(0);
        } else if (state == DownloadMsg.DOWNLOAD_FILE_COMPLETED) {
            ifDownloadCompleted( msg.getDate().getTime());
        } else if (state == DownloadMsg.DOWNLOAD_PROGRESS) {

            currentSize = msg.getCurrent();
            Log.d("zwcc", "UI界面收到消息：currentSize =" + currentSize + " totalSize = " + msg.getTotalSize());
            int progress = (int) ((currentSize * 100) / msg.getTotalSize());
            Log.d("zwcc", "UI界面收到消息：progress =" + progress);
            progressBarDownload.setProgress(progress);
            tvProgressPercent.setText(progress + "%");

        } else if (state == DownloadMsg.DOWNLOAD_STOP) {
            showToast("传输已结束，断开连接");
            hideProgressBar();
            resetList();
        }
    }


    private void ifDownloadCompleted(long fileDate){

        downloadedSize += 1;
        tvFilesPercent.setText( downloadedSize +"/" + downloadFiles.size());

        Log.d("zwcc","更新文件进度条：downloadedSize=" +downloadedSize + " 总文件数："+ downloadFiles.size());
        int fileProgress = downloadedSize *100 /downloadFiles.size();
        progressBarFiles.setProgress(fileProgress);

        if (downloadedSize >= downloadFiles.size()) {
            //全部下载完成
            updateState("准备断开连接");
            currentSize = 0;
            manager.stopDownload();
        }else {
            //需要继续下载 下一个文件
            Date nextFile = new Date(downloadFiles.get(downloadedSize));
            manager.downloadOneDayData(downloadedSize, nextFile);
            updateState("开始下载下一个文件："+nameFormat.format(nextFile));
        }
    }

    private void resetList() {
        downloadedSize = 0;
        allFiles.clear();
        downloadFiles.clear();
    }

    private void updateState(String msg) {
        tvStatus.setText(msg);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void initEvent() {

        tvStartTime.setOnClickListener(this);
        tvEndTime.setOnClickListener(this);
        btnDownload.setOnClickListener(this);
        datePickerSelect = new DialogDatePicker(this, new DialogDatePicker.OnDateSelectCallBack() {
            @Override
            public void onDateSelected(int flag, int year, int month, int day, long time, String dateString) {
                switch (flag) {
                    case START_TIME:
                        tvStartTime.setText(dateString);
                        zeroDate(year, month, day);
                        startDate = calendar.getTime();
                        Log.d("zwcc", "开始时间为：" + startDate.getTime() / 1000);
                        break;
                    case END_TIME:
                        tvEndTime.setText(dateString);
                        zeroDate(year, month, day);
                        endDate = calendar.getTime();
                        Log.d("zwcc", "结束时间为：" + endDate.getTime() / 1000);
                        break;
                }
            }

        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.btn_download_start_time) {
            datePickerSelect.showDatePickView("开始时间", START_TIME);
        } else if (id == R.id.btn_download_end_time) {
            datePickerSelect.showDatePickView("结束时间", END_TIME);
        } else if (id == R.id.ib_action_back) {
            finish();
        } else if (id == R.id.btn_download_data) {
            //prepareDownloadData();
            testUdp();

        }

    }

    @Override
    public void onRefreshAll() {

    }


    private void testUdp() {
        manager.downloadDataRequest(startDate, endDate);
        showProgressBar();
    }


    private void prepareDownloadData() {

        if (!manager.isConnected()) {
            showToast(getString(R.string.check_device_connection));
            return;
        }
        if (inProgressing() || isDownloading) {
            showToast("下载操作中请勿重复点击下载");
            return;
        }

//        String startTime = tvStartTime.getText().toString().replace("-","");
//        String endTime = tvEndTime.getText().toString().replace("-","");

        try {
            Date dateStart = dateFormat.parse(tvStartTime.getText().toString());
            Date dateEnd = dateFormat.parse(tvEndTime.getText().toString());

            BleServiceManager.getInstance().downloadDataRequest(dateStart, dateEnd);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private void showDownloadConfirmDialog(String size) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("数据下载");
        builder.setMessage("是否要下载数据？数据大小为" + size);
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                showProgressBar();
                isDownloading = true;
                BleServiceManager.getInstance().replyDownloadConfirm(true);
            }
        });
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BleServiceManager.getInstance().replyDownloadConfirm(false);
                hideProgressBar();
                isDownloading = false;
                dialog.dismiss();
            }
        });

        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}