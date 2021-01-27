package com.ecs.numbasst.ui.number;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.ecs.numbasst.R;
import com.ecs.numbasst.base.BaseActionBarActivity;
import com.ecs.numbasst.manager.msg.TimeMsg;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeActivity extends BaseActionBarActivity {


    private TextView tvSetTimeYearSelect;
    private Button btnSetTimeDefault;
    private TextView tvSetTimeHourSelect;
    private Button btnSetTime;
    private TextView tvSetTimeState;
    private TextView tvGetTime;
    private Button btnGetTime;

//    private DialogDatePicker datePickerSelect;
    //private TimePickerDialog timePickerDialog;

    TimePickerView datePickerSelect;
    TimePickerView timePickerDialog;
    DateFormat formatter;

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
        DateFormat formatterDay = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
        DateFormat formatterHour = new SimpleDateFormat("HH:mm:ss",Locale.getDefault());
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());

//        datePickerSelect = new DialogDatePicker(this, new DialogDatePicker.OnDateSelectCallBack() {
//            @Override
//            public void onDateSelected(int flag, int year, int month, int day, long time, String dateString) {
//                switch (flag) {
//                    case DAY_TIME:
//                        tvSetTimeYearSelect.setText(dateString);
//                        break;
//                }
//            }
//        });

        datePickerSelect = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                String dy =formatterDay.format(date);
                tvSetTimeYearSelect.setText(dy);

            }
        }).    setType(new boolean[]{true , true, true,false, false, false})//分别对应年月日时分秒，默认全部显示
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确定")//确认按钮文字
                .setTitleSize(18)//标题文字大小
                .setTitleText("")//标题文字
                .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(true)//是否循环滚动
                .setTitleColor(Color.BLACK)//标题文字颜色
                .setSubmitColor(Color.BLACK)//确定按钮文字颜色
                .setCancelColor(Color.BLACK)//取消按钮文字颜色
                .setTitleBgColor(0xFFFFFFFF)//标题背景颜色 Night mode
                .setBgColor(0xFFFFFFFF)//滚轮背景颜色 Night mode
                .setLabel("年","月","日","时","分","秒")
                .isDialog(true)//是否显示为对话框样式
                .build();


//        datePickerSelect = new TimePickerBuilder(this, new OnTimeSelectListener() {
//            @Override
//            public void onTimeSelect(Date date, View v) {//选中事件回调
//                String dy = formatterDay.format(date);
//                tvSetTimeYearSelect.setText(dy);
//            }
//        })
//
//                .setLayoutRes(R.layout.view_time_picker, new CustomListener() {
//
//                    @Override
//                    public void customLayout(View v) {
//                        final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
//                        ImageView ivCancel = (ImageView) v.findViewById(R.id.iv_cancel);
//                        tvSubmit.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                datePickerSelect.returnData();
//                                datePickerSelect.dismiss();
//                            }
//                        });
//                        ivCancel.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                datePickerSelect.dismiss();
//                            }
//                        });
//                    }
//                })
//                .setContentTextSize(18)
//                .setType(new boolean[]{true, true, true, false, false, false})
//                .setLabel("年", "月", "日", "时", "分", "秒")
//                .setLineSpacingMultiplier(1.2f)
//                .setTextXOffset(0, 0, 0, 40, 0, -40)
//                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
//                .setDividerColor(0xFF24AD9D)
//                .build();


        //时间选择器
        timePickerDialog = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                String hour = formatterHour.format(date);
                tvSetTimeHourSelect.setText(hour);
            }
        }).setType(new boolean[]{false, false, false, true, true, true})//分别对应年月日时分秒，默认全部显示
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确定")//确认按钮文字
                .setTitleSize(18)//标题文字大小
                .setTitleText("")//标题文字
                .setOutSideCancelable(true)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(true)//是否循环滚动
                .setDate(calendar)
                .setTitleColor(Color.BLACK)//标题文字颜色
                .setSubmitColor(Color.BLACK)//确定按钮文字颜色
                .setCancelColor(Color.BLACK)//取消按钮文字颜色
                .setTitleBgColor(0xFFFFFFFF)//标题背景颜色 Night mode
                .setBgColor(0xFFFFFFFF)//滚轮背景颜色 Night mode
                .setLabel("年", "月", "日", "时", "分", "秒")
                .isDialog(true)//是否显示为对话框样式
                .build();


//        timePickerDialog = new TimePickerDialog(TimeActivity.this, AlertDialog.THEME_HOLO_LIGHT,new TimePickerDialog.OnTimeSetListener() {
//            @Override
//            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                Calendar c = Calendar.getInstance();
//                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
//                c.set(Calendar.MINUTE,minute);
//                c.getTime();
//                tvSetTimeHourSelect.setText(formatterHour.format(c.getTime()));
//            }
//        },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true);

        tvSetTimeYearSelect.setText(formatterDay.format(calendar.getTime()));
        tvSetTimeHourSelect.setText(formatterHour.format(calendar.getTime()));

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
            //timePickerDialog.show();
            timePickerDialog.show();
        } else if (id == R.id.tv_set_time_year_select) {
            datePickerSelect.show();
        }
    }


    private void updateStatus(String msg) {
        tvSetTimeState.setText(msg);
        hideProgressBar();
    }

}