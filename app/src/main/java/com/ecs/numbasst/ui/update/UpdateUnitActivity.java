package com.ecs.numbasst.ui.update;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.ecs.numbasst.R;
import com.ecs.numbasst.base.BaseActivity;
import com.ecs.numbasst.base.util.DataKeeper;
import com.ecs.numbasst.base.util.FileChooseUtil;
import com.ecs.numbasst.base.util.Log;
import com.ecs.numbasst.manager.BleServiceManager;
import com.ecs.numbasst.manager.ProtocolHelper;
import com.ecs.numbasst.manager.callback.UpdateCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class UpdateUnitActivity extends BaseActivity {


    private final int REQUEST_CODE_FILE_EXP = 1001;
    private TextView tvTitle;
    private ImageButton btnBack;
    private Spinner spinnerUnit;
    private Button btnUpdateUnit;
    private Button btnSelectFile;
    private ProgressBar progressBarStatus;
    private ProgressBar progressBarProcess;
    private TextView unitStatus;
    private TextView tvProcess;

    private BleServiceManager manager;
    private String path = "";  // update file path
    //private File dataFile; //test file

    private File  dirMainControl;
    private File  dirStore;
    private File  dirDisplay;

    private File  dirCur;
    private Spinner spinnerFile;

    private ArrayAdapter adapter;
    List<String> fileList = new ArrayList();

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
        btnBack = findViewById(R.id.ib_action_back);
        spinnerUnit = findViewById(R.id.spinner_select_unit);
        spinnerFile = findViewById(R.id.spinner_select_file);
        btnUpdateUnit = findViewById(R.id.btn_update_unit);
        progressBarStatus = findViewById(R.id.progress_bar_update_unit_status);
        progressBarProcess = findViewById(R.id.progress_bar_unit_update);
        unitStatus = findViewById(R.id.tv_data_download_status);
        tvProcess = findViewById(R.id.tv_progress_percent);
        btnSelectFile = findViewById(R.id.btn_select_file);
    }

    @Override
    protected void initData() {
        tvTitle.setText(getTitle());
        manager = BleServiceManager.getInstance();
        //testFile();
        dirMainControl = new File(DataKeeper.unit_main_control);
        dirDisplay = new File(DataKeeper.unit_display);
        dirStore = new File (DataKeeper.unit_store);

        adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, fileList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFile.setAdapter(adapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        manager.setUpdateCallback(updateCallback);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        manager.setUpdateCallback(null);
    }

    private final UpdateCallback updateCallback = new UpdateCallback() {

        @Override
        public void onRetryFailed() {
            updateUnitStatus("多次尝试与主机通讯失败！");
        }

        @Override
        public void onRequestSucceed() {
            updateUnitStatus("更新单元请求成功！开始传输文件。");
            sendFile2Device();
        }

        @Override
        public void onUpdateCompleted(int unitType, int status) {
            Log.d("zwcc", "Activity onUpdateCompleted ");
            if (status == ProtocolHelper.STATE_SUCCEED){
                updateUnitStatus(spinnerUnit.getItemAtPosition(unitType - 1).toString() + "固件升级完成！");
            }else {
                updateUnitStatus(spinnerUnit.getItemAtPosition(unitType - 1).toString() + "固件升级失败！");
            }
        }

        @Override
        public void onUpdateProgressChanged(int progress) {
            Log.d("zwcc","Activity onUpdateProgressChanged progress= " +progress);
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
        btnSelectFile.setOnClickListener(this);
        spinnerUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG,"view  position= " +position + " view = "+ spinnerUnit.getSelectedItem().toString());
                adapterSelectFileSpinner(position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void adapterSelectFileSpinner(int type) {
        fileList.clear();
        spinnerFile.setEnabled(true);
        if (type == ProtocolHelper.UNIT_MAIN_CONTROL){
            dirCur = dirMainControl;
        }else if (type == ProtocolHelper.UNIT_INDICATE){
            dirCur = dirDisplay;
        }else {
            dirCur = dirStore;
        }
        File[] files = dirCur.listFiles();
        if (files==null || files.length == 0){
            fileList.add("请检查"+spinnerUnit.getSelectedItem().toString()+"软件文件夹");
            spinnerFile.setEnabled(false);
            showToast("未找到升级所需软件！");
        }else {
            for (File file:files){
                fileList.add(file.getName());
            }
        }
//        adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, fileList);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerFile.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ib_action_back) {
            finish();
        } else if (id == R.id.btn_update_unit) {
            prepareUnitUpdate();
        }else if (id == R.id.btn_select_file){
            openFileExplorer();
        }
    }

    private void sendFile2Device() {
        progressBarStatus.setVisibility(View.VISIBLE);
        BleServiceManager.getInstance().updateUnitTransfer(path);
    }

    private void prepareUnitUpdate() {

        if (!manager.isConnected()) {
            showToast("请先连接设备");
            return;
        }
        if (progressBarStatus.getVisibility() == View.VISIBLE) {
            showToast("更新操作中请勿重复点击");
            return;
        }
        if (!spinnerFile.isEnabled()){
            showToast("请先选择正确的升级固件！");
            return;
        }

        path = dirCur.getAbsolutePath()+ "/" + spinnerFile.getSelectedItem().toString();
        Log.d(TAG,"prepareUnitUpdate path="+path);
        File file = new File(path);
        if (!file.exists()){
            showToast("请先选择正确的升级固件！");
            return;
        }

        int unitType = spinnerUnit.getSelectedItemPosition() + 1;

        progressBarStatus.setVisibility(View.VISIBLE);
        unitStatus.setText("更新 " + spinnerUnit.getSelectedItem().toString() + " 请求中...");
        BleServiceManager.getInstance().updateUnitRequest(unitType , file);
        //BleServiceManager.getInstance().updateUnitRequest(unitType , dataFile);
    }

    private void updateUnitStatus(String msg) {
        progressBarStatus.setVisibility(View.GONE);
        unitStatus.setText(msg);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {//是否选择，没选择就不会继续
            if (requestCode == REQUEST_CODE_FILE_EXP) {
                Uri uri = data.getData();
                String pathTemp = FileChooseUtil.getPath(this, uri);
                Log.d(TAG, "uri path= " + pathTemp);
                //|| !pathTemp.contains(".bin")
                if (pathTemp == null ) {
                    showToast("选择的文件路径或者类型不正确");
                    //path = null;
                } else {
                    int index = pathTemp.lastIndexOf("/");
                    if (index >0){
                        String name = pathTemp.substring(index+1);
                        //btnSelectFile.setText(name);
                        path = pathTemp;
                    }else {
                        showToast("选择的文件路径或者类型不正确");
                        //path = null;
                    }
                }
            }
        }
    }

    private void openFileExplorer() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        String explorerPath =  URLDecoder.decode(dirCur.getAbsolutePath(),"UTF-8");
//        intent.setDataAndType(Uri.parse(explorerPath), "*/*");

        intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE_FILE_EXP);
    }


//    private void testFile() {
//        copyAssetAndWrite("ble.bin");
//        dataFile = new File(getCacheDir(), "ble.bin");
//        path = dataFile.getAbsolutePath();
//    }
//
//
//    private boolean copyAssetAndWrite(String fileName) {
//        try {
//            File cacheDir = getCacheDir();
//            if (!cacheDir.exists()) {
//                cacheDir.mkdirs();
//            }
//            File outFile = new File(cacheDir, fileName);
//            if (!outFile.exists()) {
//                boolean res = outFile.createNewFile();
//                if (!res) {
//                    return false;
//                }
//            } else {
//                if (outFile.length() > 10) {//表示已经写入一次
//                    return true;
//                }
//            }
//            InputStream is = getAssets().open(fileName);
//            FileOutputStream fos = new FileOutputStream(outFile);
//            byte[] buffer = new byte[1024];
//            int byteCount;
//            while ((byteCount = is.read(buffer)) != -1) {
//                fos.write(buffer, 0, byteCount);
//            }
//            fos.flush();
//            is.close();
//            fos.close();
//            return true;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return false;
//    }
}