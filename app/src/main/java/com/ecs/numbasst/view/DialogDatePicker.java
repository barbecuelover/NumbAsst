package com.ecs.numbasst.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.core.content.ContextCompat;


import com.ecs.numbasst.R;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @CreateDate: 2020/3/20
 * @Author:lp
 * @Description: 时间选择弹出框
 */
public class DialogDatePicker {
    private Context context;
    private OnDateSelectCallBack callBack;
    private AlertDialog dialog;

    public DialogDatePicker(Context context, OnDateSelectCallBack dateSelectCallBack) {
        this.context = context;
        this.callBack = dateSelectCallBack;
    }


    /**
     * 显示时间选择框
     *
     * @param title 弹出框title
     * @param flag  区分多次使用
     */
    public void showDatePickView(String title, int flag) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_date_picker, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        DatePicker datePicker = view.findViewById(R.id.dds_datepicker);
        setDatePickerDividerColor(datePicker);
        TextView titleTV = view.findViewById(R.id.dds_title);
        titleTV.setText(title);
        view.findViewById(R.id.dds_cancel_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.dds_confirm_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat simpleFormatter = new SimpleDateFormat("yyy-MM-dd");
                Calendar calendar = Calendar.getInstance();
                calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                callBack.onDateSelected(flag, datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth(), calendar.getTimeInMillis(), simpleFormatter.format(calendar.getTime()));
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.setView(view);
        dialog.show();
    }

    /**
     * 设置时间选择器的分割线颜色
     *
     * @param datePicker
     */
    private void setDatePickerDividerColor(DatePicker datePicker) {
        // 获取 mSpinners
        LinearLayout llFirst = (LinearLayout) datePicker.getChildAt(0);

        // 获取 NumberPicker
        LinearLayout mSpinners = (LinearLayout) llFirst.getChildAt(0);
        for (int i = 0; i < mSpinners.getChildCount(); i++) {
            NumberPicker picker = (NumberPicker) mSpinners.getChildAt(i);

            Field[] pickerFields = NumberPicker.class.getDeclaredFields();
            for (Field pf : pickerFields) {
                if (pf.getName().equals("mSelectionDivider")) {
                    pf.setAccessible(true);
                    try {
                        pf.set(picker, new ColorDrawable(ContextCompat.getColor(context, R.color.gray_8f)));
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    break;
                }

            }
        }
    }

    /**
     * 时间选择器选择回调
     */
    public interface OnDateSelectCallBack {
        /**
         * 选择时间回调
         * @param flag 标识
         * @param year 年
         * @param month 月
         * @param day 日
         * @param time 选择的时间long
         * @param dateString 选择的时间字符串格式:yyy-MM-dd
         */
        void onDateSelected(int flag, int year, int month, int day, long time, String dateString);
    }
}