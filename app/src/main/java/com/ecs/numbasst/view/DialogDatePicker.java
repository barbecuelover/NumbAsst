package com.ecs.numbasst.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;

import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.core.content.ContextCompat;


import com.ecs.numbasst.R;
import com.ecs.numbasst.base.util.Log;

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
public class DialogDatePicker implements View.OnKeyListener{
    private Context context;
    private OnDateSelectCallBack callBack;
    private AlertDialog dialog;
    private Button btnOk;
    private Button btnCancel;
    private List<View> numberViewList = new ArrayList<>();

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
        numberViewList.clear();

        // 获取 mSpinners
        LinearLayout llFirst = (LinearLayout) datePicker.getChildAt(0);

        // 获取 NumberPicker
        LinearLayout mSpinners = (LinearLayout) llFirst.getChildAt(0);
        for (int i = 0; i < mSpinners.getChildCount(); i++) {
            NumberPicker picker = (NumberPicker) mSpinners.getChildAt(i);
            numberViewList.add(picker);
            picker.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus){
                        picker.setBackgroundColor(context.getResources().getColor(R.color.blue_deep_sky));
                    }else {
                        picker.setBackgroundColor(context.getResources().getColor(R.color.white));
                    }
                }
            });
            if (i ==0){
                picker.requestFocus();
            }
            picker.setOnKeyListener(this);
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

//                else if (pf.getName().equals("mInputText")){
//                    pf.setAccessible(true);
//                    try {
//                        Object editTextObject = pf.get(picker);
//                        Class editClass =  Class.forName(editTextObject.getClass().getName());
//                        Method setBackground = getMethod(editClass,"setBackground",new Class[]{Drawable.class});
//                        //Method setBackground = editClass.getDeclaredMethod("setBackground");
//                        setBackground.setAccessible(true);
//                        //调用show()方法
//                        setBackground.invoke(editTextObject,context.getDrawable(R.drawable.selector_btn_normal));
//                       EditText editText = new EditText(context);
//                      // editText.setBackgroundColor();
////                        editText.setBackground(context.getDrawable(R.drawable.selector_btn_normal));
//                        Log.d("zwcc","333333");
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }

            }
        }
    }


    public  Method getMethod(Class clazz, String methodName, final Class[] classes) throws Exception {
        Method method = null;
        try {
            method = clazz.getDeclaredMethod(methodName, classes);
        } catch (NoSuchMethodException e) {
            try {
                method = clazz.getMethod(methodName, classes);
            } catch (NoSuchMethodException ex) {
                if (clazz.getSuperclass() == null) {
                    return method;
                } else {
                    method = getMethod(clazz.getSuperclass(), methodName,classes);
                }
            }
        }
        return method;
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