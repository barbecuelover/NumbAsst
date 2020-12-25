package com.ecs.numbasst.ui.update;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.ecs.numbasst.R;
import com.ecs.numbasst.base.BaseActivity;
import com.ecs.numbasst.base.util.Log;
import com.ecs.numbasst.manager.BleServiceManager;
import com.ecs.numbasst.manager.callback.UpdateCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class UpdateUnitActivity extends BaseActivity {


    private TextView tvTitle;
    private ImageButton btnBack;
    private Spinner spinnerUnit;
    private Button btnUpdateUnit;
    private ProgressBar progressBarStatus;
    private ProgressBar progressBarProcess;
    private TextView unitStatus;
    private TextView tvProcess;
    Handler  handler;
    private BleServiceManager manager;
    private String path ="file:///android_asset/ble.docx";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_update_unit;
    }

    @Override
    protected void initView() {
        tvTitle = findViewById(R.id.action_bar_title);
        btnBack= findViewById(R.id.ib_action_back);
        spinnerUnit =findViewById(R.id.spinner_select_unit);
        btnUpdateUnit =findViewById(R.id.btn_update_unit);
        progressBarStatus = findViewById(R.id.progress_bar_unit_update_status);
        progressBarProcess = findViewById(R.id.progress_bar_unit_update);
        unitStatus = findViewById(R.id.tv_data_download_status);
        tvProcess = findViewById(R.id.tv_progress_percent);
    }

    @Override
    protected void initData() {
        tvTitle.setText(getTitle());
        handler = new Handler();
        manager = BleServiceManager.getInstance();
    }

    private final UpdateCallback updateCallback = new UpdateCallback() {

        @Override
        public void onRetryFailed() {
            updateUnitStatus("多次尝试与主机通讯失败！");
        }

        @Override
        public void onRequestSucceed() {
            updateUnitStatus("更新单元请求成功！");
            sendFile2Device();
        }

        @Override
        public void onUpdateCompleted(int unitType, int status) {
            updateUnitStatus( spinnerUnit.getItemAtPosition(unitType).toString() + "固件升级完成！");
        }

        @Override
        public void onUpdateProgressChanged(int progress) {
            progressBarProcess.setProgress(progress);
            tvProcess.setText(progress + "%");
        }

        @Override
        public void onUpdateError() {
            updateUnitStatus("升级文件上传失败！");
        }

        @Override
        public void onFailed(String reason) {
            updateUnitStatus("更新单元请求失败！");
        }
    };


    @Override
    protected void initEvent() {
        btnBack.setOnClickListener(this);
        btnUpdateUnit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.ib_action_back){
            finish();
        }else if(id == R.id.btn_update_unit){
           // prepareUnitUpdate();

            copyAssetAndWrite("ble.docx");
            File dataFile=new File(getCacheDir(),"ble.docx");
            path = dataFile.getAbsolutePath();
            sendFile2Device();
        }
    }

    private void sendFile2Device() {
        BleServiceManager.getInstance().updateUnitTransfer(path);
    }

    private void prepareUnitUpdate(){

        if (manager.getConnectedDeviceMac()==null){
            showToast("请先连接设备");
            return;
        }
        if (progressBarStatus.getVisibility()==View.VISIBLE ){
            showToast("更新操作中请勿重复点击");
            return;
        }

        int unitType = spinnerUnit.getSelectedItemPosition();
        //File file = new File("");
        //long fileSize = file.length();
        long fileSize = 0x2A2B;
        progressBarStatus.setVisibility(View.VISIBLE);
        unitStatus.setText("更新 " + spinnerUnit.getSelectedItem().toString() + " 请求中..." );
        BleServiceManager.getInstance().updateUnitRequest(unitType,fileSize, updateCallback);
    }

    private void updateUnitStatus(String msg){
        progressBarStatus.setVisibility(View.GONE);
        unitStatus.setText(msg);
    }





    private boolean copyAssetAndWrite(String fileName){
        try {
            File cacheDir=getCacheDir();
            if (!cacheDir.exists()){
                cacheDir.mkdirs();
            }
            File outFile =new File(cacheDir,fileName);
            if (!outFile.exists()){
                boolean res=outFile.createNewFile();
                if (!res){
                    return false;
                }
            }else {
                if (outFile.length()>10){//表示已经写入一次
                    return true;
                }
            }
            InputStream is=getAssets().open(fileName);
            FileOutputStream fos = new FileOutputStream(outFile);
            byte[] buffer = new byte[1024];
            int byteCount;
            while ((byteCount = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();
            is.close();
            fos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}