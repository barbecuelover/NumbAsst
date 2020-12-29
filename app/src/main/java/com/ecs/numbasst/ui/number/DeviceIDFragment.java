package com.ecs.numbasst.ui.number;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.ecs.numbasst.R;
import com.ecs.numbasst.base.BaseFragment;
import com.ecs.numbasst.manager.BleServiceManager;
import com.ecs.numbasst.manager.ProtocolHelper;
import com.ecs.numbasst.manager.callback.DeviceIDCallback;
import com.ecs.numbasst.manager.callback.NumberCallback;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeviceIDFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeviceIDFragment extends BaseFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView tvCurrentDeviceId;
    private ImageButton ibGetDeviceIdRefresh;
    private EditText etNewDeviceId;
    private Button btnSetDeviceId;
    private TextView tvDeviceIdStatus;


    private BleServiceManager manager;
    private DeviceIDCallback callback;


    public DeviceIDFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DeviceIDFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DeviceIDFragment newInstance(String param1, String param2) {
        DeviceIDFragment fragment = new DeviceIDFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return super.onCreateView(inflater,container,savedInstanceState);

    }

    @Override
    protected int setLayoutResourcesId() {
        return R.layout.fragment_device_id;
    }

    @Override
    protected void initView() {
        tvCurrentDeviceId = findViewById(R.id.tv_current_device_id);
        ibGetDeviceIdRefresh = findViewById(R.id.ib_get_device_id_refresh);
        etNewDeviceId = findViewById(R.id.et_new_device_id);
        btnSetDeviceId = findViewById(R.id.btn_set_device_id);
        tvDeviceIdStatus = findViewById(R.id.tv_device_id_status);
    }

    @Override
    protected void initData() {
        manager = BleServiceManager.getInstance();

        callback = new DeviceIDCallback() {
            @Override
            public void onDeviceIDGot(String number) {
                tvCurrentDeviceId.setText(number);
                updateStatus("获取设备ID为："+number);
            }

            @Override
            public void onDeviceIDSet(int state) {
                String status = state == ProtocolHelper.STATE_SUCCEED ? "成功！" : "失败！";
                String msg = "设置设备ID" + status;
                updateStatus(msg);
            }

            @Override
            public void onRetryFailed() {
                updateStatus("多次连接主机失败");
            }
        };

    }

    @Override
    protected void initEvent() {
        ibGetDeviceIdRefresh.setOnClickListener(this);
        btnSetDeviceId.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ib_get_device_id_refresh) {
            if (manager.getConnectedDeviceMac() == null) {
                updateStatus(getString(R.string.check_device_connection));
                return;
            }
            manager.getDeviceID(callback);
            updateStatus("获取设备ID中...");
            //showLoading
        } else if (id == R.id.btn_set_device_id) {
            String dID = etNewDeviceId.getText().toString().trim();

            if (dID.equals("")) {
                updateStatus("设备ID不能为空！");
            } else {
                if (manager.getConnectedDeviceMac() == null) {
                    updateStatus(getString(R.string.check_device_connection));
                    return;
                }

                if (dID.length() == 6){
                    manager.setDeviceID(dID,callback);
                    //showLoading
                }

            }
        }
    }

    private void updateStatus(String msg) {
        tvDeviceIdStatus.setText(msg);
    }

}