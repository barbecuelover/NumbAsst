package com.ecs.numbasst.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * @author zw
 * @time 2020/12/29
 * @description
 */
public abstract class BaseFragment extends Fragment implements  View.OnClickListener {
    public String TAG = "BaseFragment";
    public Context context;
    protected View mRootView;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(setLayoutResourcesId(), container, false);
        initView();
        initData();
        initEvent();
        return mRootView;
    }

    /**
     * 设置跟资源ID
     */
    protected abstract int setLayoutResourcesId();

    /**
     * 初始化View，绑定ID
     */
    protected abstract void initView();

    /**
     * 初始化数据，绑定数据
     */
    protected abstract void initData();


    protected abstract void initEvent();

    /**
     * 绑定布局的ID,不用每次都去强转类型
     *
     * @param id
     * @param <T>
     * @return
     */
    protected <T extends View> T findViewById(int id) {
        if (mRootView == null) {
            return null;
        }
        return (T) mRootView.findViewById(id);
    }


}
