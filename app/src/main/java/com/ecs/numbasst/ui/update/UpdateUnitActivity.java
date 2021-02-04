package com.ecs.numbasst.ui.update;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.ecs.numbasst.R;
import com.ecs.numbasst.base.BaseActionBarActivity;
import com.ecs.numbasst.base.util.DataKeeper;
import com.ecs.numbasst.base.util.FileChooseUtil;
import com.ecs.numbasst.base.util.Log;
import com.ecs.numbasst.manager.BleServiceManager;
import com.ecs.numbasst.manager.ProtocolHelper;
import com.ecs.numbasst.manager.msg.UnitUpdateMsg;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UpdateUnitActivity extends BaseActionBarActivity {


    private final int REQUEST_CODE_FILE_EXP = 1001;
    private Spinner spinnerUnit;
    private Button btnUpdateUnit;
    private Button btnSelectFile;

    private ProgressBar progressBarProcess;
    private TextView unitStatus;
    private TextView tvProcess;

    private String path = "";  // update file path
    //private File dataFile; //test file

    private File dirMainControl;
    private File dirStore;
    private File dirDisplay;

    private File dirCur;
    private Spinner spinnerFile;

    private ArrayAdapter adapter;
    List<String> fileList = new ArrayList();
    AlertDialog updateDialog;

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
        spinnerUnit = findViewById(R.id.spinner_select_unit);
        spinnerFile = findViewById(R.id.spinner_select_file);
        btnUpdateUnit = findViewById(R.id.btn_update_unit);

        progressBarProcess = findViewById(R.id.progress_bar_unit_update);
        unitStatus = findViewById(R.id.tv_data_download_status);
        tvProcess = findViewById(R.id.tv_progress_percent);
        btnSelectFile = findViewById(R.id.btn_select_file);
    }

    @Override
    protected void initData() {
        //testFile();
        dirMainControl = new File(DataKeeper.unit_main_control);
        dirDisplay = new File(DataKeeper.unit_display);
        dirStore = new File(DataKeeper.unit_store);
        adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, fileList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFile.setAdapter(adapter);

        AlertDialog.Builder loadingBuilder = new AlertDialog.Builder(this);
        loadingBuilder.setTitle("");
        loadingBuilder.setMessage("取消升级中...");
        loadingBuilder.setCancelable(false);
        AlertDialog cancelDialog = loadingBuilder.create();


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("单元升级");
        builder.setMessage("升级过程中请耐心等待...");
        builder.setNegativeButton("取消升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                manager.cancelAction();
                cancelDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cancelDialog.dismiss();
                        resetUI();
                    }
                },6000);
            }
        });
        builder.setCancelable(false);
        updateDialog = builder.create();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdate(UnitUpdateMsg msg) {
        long state = msg.getState();
        if (state == UnitUpdateMsg.REQUEST_FAILED) {
            updateUnitStatus("更新单元请求失败！");
        } else if (state == UnitUpdateMsg.REQUEST_SUCCEED) {
            updateUnitStatus("更新单元请求成功！开始传输文件。");
            sendFile2Device();
        } else if (state == UnitUpdateMsg.TRANSFER_PROGRESS_CHANGED) {
            int progress = msg.getProgress();
            Log.d("zwcc", "Activity onUpdateProgressChanged progress= " + progress);
            progressBarProcess.setProgress(progress);
            tvProcess.setText(progress + "%");
        } else if (state == UnitUpdateMsg.UPDATE_COMPLETED) {
            int unitType = msg.getUnitType();
            updateUnitStatus(spinnerUnit.getItemAtPosition(unitType - 1).toString() + "固件升级完成！");
        } else if (state == UnitUpdateMsg.UPDATE_FAILED) {
            updateUnitStatus("更新单元失败！");
        }
    }

    @Override
    protected void initEvent() {
        btnUpdateUnit.setOnClickListener(this);
        btnSelectFile.setOnClickListener(this);
        spinnerUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "view  position= " + position + " view = " + spinnerUnit.getSelectedItem().toString());
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
        if (type == ProtocolHelper.UNIT_MAIN_CONTROL) {
            dirCur = dirMainControl;
        } else if (type == ProtocolHelper.UNIT_INDICATE) {
            dirCur = dirDisplay;
        } else {
            dirCur = dirStore;
        }
        File[] files = dirCur.listFiles();
        if (files == null || files.length == 0) {
            fileList.add("请检查" + spinnerUnit.getSelectedItem().toString() + "软件文件夹");
            spinnerFile.setEnabled(false);
            showToast("未找到升级所需软件！");
        } else {
            for (File file : files) {
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
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.ib_action_back) {
            finish();
        } else if (id == R.id.btn_update_unit) {
            prepareUnitUpdate();
            //updateDialog.show();
        } else if (id == R.id.btn_select_file) {
            openFileExplorer();
        }
    }

    @Override
    public void onRefreshAll() {

    }

    private void sendFile2Device() {
        BleServiceManager.getInstance().updateUnitTransfer(path);
        updateDialog.show();
    }

    private void prepareUnitUpdate() {

        if (!manager.isConnected()) {
            showToast("请先连接设备");
            return;
        }
        if (inProgressing()) {
            showToast("更新操作中请勿重复点击");
            return;
        }
        if (!spinnerFile.isEnabled()) {
            showToast("请先选择正确的升级固件！");
            return;
        }

        path = dirCur.getAbsolutePath() + "/" + spinnerFile.getSelectedItem().toString();
        Log.d(TAG, "prepareUnitUpdate path=" + path);
        File file = new File(path);
        if (!file.exists()) {
            showToast("请先选择正确的升级固件！");
            return;
        }

        int unitType = spinnerUnit.getSelectedItemPosition() + 1;
        showProgressBar();
        unitStatus.setText("更新 " + spinnerUnit.getSelectedItem().toString() + " 请求中...");
        BleServiceManager.getInstance().updateUnitRequest(unitType, file);
        //BleServiceManager.getInstance().updateUnitRequest(unitType , dataFile);
    }

    private void updateUnitStatus(String msg) {
        hideProgressBar();
        unitStatus.setText(msg);
        updateDialog.dismiss();
    }

    private void resetUI(){
        progressBarProcess.setProgress(0);
        tvProcess.setText( "0%");
        updateDialog.dismiss();
        hideProgressBar();
        unitStatus.setText("更新状态");
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
                if (pathTemp == null) {
                    showToast("选择的文件路径或者类型不正确");
                    //path = null;
                } else {
                    int index = pathTemp.lastIndexOf("/");
                    if (index > 0) {
                        String name = pathTemp.substring(index + 1);
                        //btnSelectFile.setText(name);
                        path = pathTemp;
                    } else {
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