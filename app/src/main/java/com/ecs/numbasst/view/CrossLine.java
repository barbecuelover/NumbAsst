package com.ecs.numbasst.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ecs.numbasst.R;

public class CrossLine extends FrameLayout {
    public CrossLine(@NonNull Context context) {
        super(context);
    }

    public CrossLine(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CrossLine);
        String str = typedArray.getString(R.styleable.CrossLine_cross_line_title);
        typedArray.recycle();
        LayoutInflater.from(context).inflate(R.layout.view_cross_line, this, true);
        TextView title =  findViewById(R.id.tv_cross_line_title);
        title.setText(str);
    }

}
