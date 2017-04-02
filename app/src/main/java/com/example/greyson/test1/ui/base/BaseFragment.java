package com.example.greyson.test1.ui.base;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.greyson.test1.config.WSAppContext;
import com.trello.rxlifecycle.components.support.RxFragment;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Retrofit;

/**
 * Created by greyson on 28/3/17.
 */

public abstract class BaseFragment extends RxFragment {
    protected Context mContext;
    protected Retrofit mRetrofit = WSAppContext.getInstance().getRetrofit();
    protected Resources mResources = WSAppContext.getInstance().getmResources();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return initView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initEvent();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        destroyView();
    }

    protected abstract View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) ;

    protected abstract void initData();

    protected abstract void initEvent();

    protected abstract void destroyView();

}


