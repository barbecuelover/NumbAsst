package com.ecs.numbasst.ui.number;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ecs.numbasst.R;
import com.ecs.numbasst.base.BaseFragment;
import com.ecs.numbasst.manager.BleServiceManager;
import com.ecs.numbasst.manager.ProtocolHelper;
import com.ecs.numbasst.manager.callback.NumberCallback;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CarNumberFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CarNumberFragment extends BaseFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ImageButton btnRefresh;
    ProgressBar progressBar;
    Button btnSetCarNumber;
    EditText etNewNumber;
    TextView tvCarName;
    TextView tvNumberStatus;

    private BleServiceManager manager;
    private NumberCallback numberCallback;
    private NumberActivity activity;

    public CarNumberFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CarNumberFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CarNumberFragment newInstance(String param1, String param2) {
        CarNumberFragment fragment = new CarNumberFragment();
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
        return R.layout.fragment_car_number;
    }

    @Override
    protected void initView() {
        btnRefresh = findViewById(R.id.ib_get_car_number_refresh);
        progressBar = findViewById(R.id.progress_bar_set_car_number);
        btnSetCarNumber = findViewById(R.id.btn_set_car_number);
        etNewNumber = findViewById(R.id.et_new_numb);
        tvCarName = findViewById(R.id.car_number_current);
        tvNumberStatus = findViewById(R.id.tv_car_numb_status);
    }


    @Override
    protected void initData(){
        manager = BleServiceManager.getInstance();
        activity = (NumberActivity) getActivity();
        numberCallback = activity.getNumberCallback();
    }

    @Override
    protected void initEvent() {
        btnRefresh.setOnClickListener(this);
        btnSetCarNumber.setOnClickListener(this);
    }


    private void updateNumberStatus(String msg){
        tvNumberStatus.setText(msg);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        int id  =v.getId();
      if(id == R.id.ib_get_car_number_refresh){
            if (manager.getConnectedDeviceMac()==null){
                tvNumberStatus.setText(getString(R.string.check_device_connection));
                return;
            }
            if (progressBar.getVisibility() == View.VISIBLE){
                tvNumberStatus.setText("获取或设置车号中，请稍后再试");

            }else {
                manager.getCarNumber(numberCallback);
                tvNumberStatus.setText("获取车号中...");
                progressBar.setVisibility(View.VISIBLE);
            }
        }else if (id == R.id.btn_set_car_number){
            if (etNewNumber.getText().toString().trim().equals("")){
                updateNumberStatus("车号不能为空！");
            }else {
                if (manager.getConnectedDeviceMac()==null){
                    updateNumberStatus(getString(R.string.check_device_connection));
                    return;
                }
                manager.setCarNumber(etNewNumber.getText().toString().trim(), numberCallback);
                progressBar.setVisibility(View.VISIBLE);
            }
        }
    }
}