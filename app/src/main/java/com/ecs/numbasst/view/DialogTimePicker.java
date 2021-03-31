package com.ecs.numbasst.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.core.content.ContextCompat;

import com.ecs.numbasst.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @CreateDate: 2020/3/20
 * @Author:lp
 * @Description: 时间选择弹出框
 */
public class DialogTimePicker implements View.OnKeyListener ,View.OnFocusChangeListener{
    private Context context;
    private OnTimeSelectCallBack callBack;
    private AlertDialog dialog;
    private Button btnOk;
    private Button btnCancel;
    private List<View> numberViewList = new ArrayList<>();

    public DialogTimePicker(Context context, OnTimeSelectCallBack timeSelectCallBack) {
        this.context = context;
        this.callBack = timeSelectCallBack;
    }


    /**
     * 显示时间选择框
     */
    public void show(String title) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_time_picker, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        NumberPicker hours = view.findViewById(R.id.dds_hour);
        hours.setMaxValue(23);
        hours.setMinValue(0);
        NumberPicker minutes = view.findViewById(R.id.dds_minute);
        minutes.setMaxValue(59);
        minutes.setMinValue(0);
        NumberPicker seconds = view.findViewById(R.id.dds_seconds);
        seconds.setMaxValue(59);
        seconds.setMinValue(0);
        TextView titleTV = view.findViewById(R.id.dds_title);
        titleTV.setText(title);
        Calendar calendar  = Calendar.getInstance();
        hours.setValue(calendar.get(Calendar.HOUR_OF_DAY));
        minutes.setValue(calendar.get(Calendar.MINUTE));
        seconds.setValue(calendar.get(Calendar.SECOND));



        numberViewList.add(hours);
        numberViewList.add(minutes);
        numberViewList.add(seconds);

        hours.setOnKeyListener(this);
        minutes.setOnKeyListener(this);
        seconds.setOnKeyListener(this);

        hours.setOnFocusChangeListener(this);
        minutes.setOnFocusChangeListener(this);
        seconds.setOnFocusChangeListener(this);


        hours.requestFocus();

        btnCancel = view.findViewById(R.id.dds_cancel_tv);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnCancel.setOnKeyListener(this);
        btnOk = view.findViewById(R.id.dds_confirm_tv);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.onTimeSelected( hours.getValue(), minutes.getValue(), seconds.getValue());
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.setView(view);
        dialog.show();
    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        boolean intercept = false;
        if (event.getAction() == KeyEvent.ACTION_DOWN){
            if (keyCode == KeyEvent.KEYCODE_ENTER){
                if (btnOk!=null){
                    btnOk.callOnClick();
                    intercept = true;
                }

            }else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
                if (numberViewList.contains(v) && numberViewList.indexOf(v) == (numberViewList.size()-1)){
                    v.clearFocus();
                    btnCancel.requestFocus();
                    intercept = true;
                }
            }else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
                if (v == btnCancel){
                    if (numberViewList.size()>0){
                        v.clearFocus();
                        numberViewList.get(numberViewList.size()-1).requestFocus();
                        intercept = true;
                    }
                }
            }
        }
        return intercept;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus){
            v.setBackgroundColor(context.getResources().getColor(R.color.blue_deep_sky));
        }else {
            v.setBackgroundColor(context.getResources().getColor(R.color.white));
        }
    }

    /**
     * 时间选择器选择回调
     */
    public interface OnTimeSelectCallBack {
        /**
         * 选择时间回调
         * @param hour 年
         * @param minute 月
         * @param second 日
         */
        void onTimeSelected(int hour, int minute, int second);
    }
}