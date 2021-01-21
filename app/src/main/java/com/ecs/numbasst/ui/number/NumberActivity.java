package com.ecs.numbasst.ui.number;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.ecs.numbasst.R;
import com.ecs.numbasst.base.BaseActionBarActivity;
import com.ecs.numbasst.manager.msg.CarNumberMsg;
import com.ecs.numbasst.ui.download.DialogDatePicker;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NumberActivity extends BaseActionBarActivity {

    private final int DAY_TIME = 0x11;
    ImageButton btnRefresh;
    ImageButton btnNumberLogout;

    Button btnSetCarNumber;
    EditText etNewNumber;
    TextView tvCarName;
    TextView tvNumberStatus;
    TextView btnTimeDay;
    TextView btnTimeHour;
    private DialogDatePicker datePickerSelect;
    private TimePickerDialog timePickerDialog;
    DateFormat formatter ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_car_number;
    }

    @Override
    protected void initView() {

        btnRefresh = findViewById(R.id.ib_get_car_number_refresh);
        btnNumberLogout = findViewById(R.id.ib_number_logo_out);
        btnSetCarNumber = findViewById(R.id.btn_set_car_number);
        etNewNumber = findViewById(R.id.et_new_numb);
        tvCarName = findViewById(R.id.car_number_current);
        tvNumberStatus = findViewById(R.id.tv_car_numb_status);

        btnTimeDay = findViewById(R.id.btn_time_day);
        btnTimeHour = findViewById(R.id.btn_time_hour);

    }

    @Override
    protected void initData() {
        Calendar calendar = Calendar.getInstance();
        DateFormat formatterDay=new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        DateFormat formatterHour=new SimpleDateFormat("HH:mm", Locale.getDefault());

        formatter =new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        datePickerSelect = new DialogDatePicker(this, new DialogDatePicker.OnDateSelectCallBack() {
            @Override
            public void onDateSelected(int flag, int year, int month, int day, long time, String dateString) {
                switch (flag) {
                    case DAY_TIME:
                        btnTimeDay.setText(dateString);
                        break;
                }
            }
        });

        timePickerDialog = new TimePickerDialog(NumberActivity.this, AlertDialog.THEME_HOLO_LIGHT,new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                c.set(Calendar.MINUTE,minute);
                c.getTime();
                btnTimeHour.setText(formatterHour.format(c.getTime()));
            }
        },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true);

        btnTimeDay.setText(formatterDay.format(calendar.getTime()));
        btnTimeHour.setText(formatterHour.format(calendar.getTime()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCarNumber(CarNumberMsg msg) {
        long state = msg.getState();
        if (state == CarNumberMsg.UNSUBSCRIBE_NUMBER_SUCCEED) {
            updateStatus("注销车号成功！");
        } else if (state == CarNumberMsg.UNSUBSCRIBE_NUMBER_FAILED) {
            updateStatus("注销车号失败！");
        } else if (state == CarNumberMsg.SET_NUMBER_SUCCEED) {
            updateStatus("设置车号成功");
        } else if (state == CarNumberMsg.SET_NUMBER_FAILED) {
            updateStatus("设置车号失败");
        } else if (state == CarNumberMsg.GET_NUMBER) {
            String number = msg.getCarNumber();
            tvCarName.setText(number);
            updateStatus("获取车号为：" + number);
        }
    }

    @Override
    protected void initEvent() {
        btnRefresh.setOnClickListener(this);
        btnSetCarNumber.setOnClickListener(this);
        btnNumberLogout.setOnClickListener(this);
        btnTimeDay.setOnClickListener(this);
        btnTimeHour.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.ib_action_back) {
            finish();
        } else if (id == R.id.ib_get_car_number_refresh) {
            if (!manager.isConnected()) {
                tvNumberStatus.setText(getString(R.string.check_device_connection));
                showToast(getString(R.string.check_device_connection));
                return;
            }
            if (inProgressing()) {
                tvNumberStatus.setText("获取或设置车号中，请稍后再试");

            } else {
                manager.getCarNumber();
                tvNumberStatus.setText("获取车号中...");
                showProgressBar();
            }
        } else if (id == R.id.btn_set_car_number) {
            if (etNewNumber.getText().toString().trim().equals("")) {
                updateStatus("车号不能为空！");
            } else {
                if (!manager.isConnected()) {
                    updateStatus(getString(R.string.check_device_connection));
                    return;
                }
                String y = btnTimeDay.getText().toString().trim();
                String h = btnTimeHour.getText().toString().trim();
                String carNumber = etNewNumber.getText().toString().trim();
                Date date = new Date();
                try {
                     date = formatter.parse(y+" "+h);
                } catch (ParseException e) {
                    showToast("日期格式不正确");
                    e.printStackTrace();
                }
                manager.setCarNumber(carNumber,date);
                showProgressBar();
            }
        } else if (id == R.id.ib_number_logo_out) {
            if (!manager.isConnected()) {
                updateStatus(getString(R.string.check_device_connection));
                return;
            }
            updateStatus("注销车号中！");
            manager.logoutCarNumber();
        }else if (id ==R.id.btn_time_hour){
            timePickerDialog.show();
        }else if (id == R.id.btn_time_day){
            datePickerSelect.showDatePickView("授时年月日", DAY_TIME);
        }
    }

    @Override
    public void onRefreshAll() {

    }

    private void updateStatus(String msg) {
        tvNumberStatus.setText(msg);
        hideProgressBar();
    }

}