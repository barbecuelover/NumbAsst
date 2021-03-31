package com.ecs.numbasst.ui.number;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ecs.numbasst.R;
import com.ecs.numbasst.base.BaseActionBarActivity;
import com.ecs.numbasst.manager.msg.TimeMsg;
import com.ecs.numbasst.view.DialogDatePicker;
import com.ecs.numbasst.view.DialogTimePicker;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeActivity extends BaseActionBarActivity {


    private Button tvSetTimeYearSelect;
    private Button btnSetTimeDefault;
    private Button tvSetTimeHourSelect;
    private Button btnSetTime;
    private TextView tvSetTimeState;
    private TextView tvGetTime;
    private Button btnGetTime;
    DateFormat formatterDay;
    DateFormat formatterHour;
    private DialogDatePicker datePickerSelect;
    private DialogTimePicker timePickerSelect;

    DateFormat formatter;
    private final int YEAR_TIME = 0X100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int initLayout() {
        return R.layout.activity_time;
    }

    @Override
    protected void initView() {
        tvSetTimeYearSelect = findViewById(R.id.tv_set_time_year_select);
        btnSetTimeDefault = findViewById(R.id.btn_set_time_default);
        tvSetTimeHourSelect = findViewById(R.id.tv_set_time_hour_select);
        btnSetTime = findViewById(R.id.btn_set_time);
        tvSetTimeState = findViewById(R.id.tv_set_time_state);
        tvGetTime = findViewById(R.id.tv_get_time);
        btnGetTime = findViewById(R.id.btn_get_time);
    }

    @Override
    protected void initData() {
        Calendar calendar = Calendar.getInstance();
        formatterDay = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
        formatterHour = new SimpleDateFormat("HH:mm:ss",Locale.getDefault());
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());


        datePickerSelect = new DialogDatePicker(this, new DialogDatePicker.OnDateSelectCallBack() {
            @Override
            public void onDateSelected(int flag, int year, int month, int day, long time, String dateString) {
                switch (flag) {
                    case YEAR_TIME:
                        tvSetTimeYearSelect.setText(dateString);
                        break;
                }
            }
        });

        timePickerSelect =  new DialogTimePicker(this, new DialogTimePicker.OnTimeSelectCallBack() {
            @Override
            public void onTimeSelected(int hour, int minute, int second) {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY, hour);
                c.set(Calendar.MINUTE,minute);
                c.set(Calendar.SECOND,second);
                c.getTime();
                tvSetTimeHourSelect.setText(formatterHour.format(c.getTime()));
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTimeChanged(TimeMsg msg) {
        int state = msg.getState();
        if (state == TimeMsg.TIME_SET_SUCCEED) {
            updateStatus("授时成功");
        } else if (state == TimeMsg.TIME_SET_FAILED) {
            updateStatus("授时失败");
        } else if (state == TimeMsg.TIME_GET) {
            long time = msg.getTimeT();
            Date date = new Date(time);
            String str = formatter.format(date);
            tvGetTime.setText(str);
            hideProgressBar();
        }
    }


    @Override
    protected void initEvent() {
        btnSetTimeDefault.setOnClickListener(this);
        btnSetTime.setOnClickListener(this);
        btnGetTime.setOnClickListener(this);
        tvSetTimeYearSelect.setOnClickListener(this);
        tvSetTimeHourSelect.setOnClickListener(this);
    }

    @Override
    public void onRefreshAll() {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        if (id == R.id.btn_set_time_default) {
            if (!manager.isConnected()) {
                updateStatus(getString(R.string.check_device_connection));
                return;
            }
            Date date = new Date();
            tvSetTimeYearSelect.setText(formatterDay.format(date.getTime()));
            tvSetTimeHourSelect.setText(formatterHour.format(date.getTime()));
            manager.setTime(date);
            showProgressBar();
        } else if (id == R.id.btn_set_time) {
            if (!manager.isConnected()) {
                updateStatus(getString(R.string.check_device_connection));
                return;
            }

            String y = tvSetTimeYearSelect.getText().toString().trim();
            String h = tvSetTimeHourSelect.getText().toString().trim();
            Date date = new Date();
            try {
                date = formatter.parse(y + " " + h);
            } catch (ParseException e) {
                showToast("日期格式不正确");
                e.printStackTrace();
            }
            manager.setTime(date);
            showProgressBar();
        } else if (id == R.id.btn_get_time) {
            if (!manager.isConnected()) {
                updateStatus(getString(R.string.check_device_connection));
                return;
            }
            manager.getTime();
            showProgressBar();

        } else if (id == R.id.tv_set_time_hour_select) {
            timePickerSelect.show("时分秒");
        } else if (id == R.id.tv_set_time_year_select) {
            datePickerSelect.showDatePickView("年月日", YEAR_TIME);
        }
    }


    private void updateStatus(String msg) {
        tvSetTimeState.setText(msg);
        hideProgressBar();
    }

}