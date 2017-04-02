package com.example.greyson.test1.ui.base;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.example.greyson.test1.config.WSAppContext;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Retrofit;

/**
 * Created by greyson on 28/3/17.
 */

public abstract class BaseActivity extends RxAppCompatActivity{

    protected Retrofit mRetrofit = WSAppContext.getInstance().getRetrofit();
    protected Resources mResources = WSAppContext.getInstance().getmResources();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutRes());
        initView();
        initData();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initEvent();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    protected  <T extends View> T findView(int id) {
        return (T) findViewById(id);
    }

    protected abstract int getLayoutRes();

    protected abstract void initView();

    protected abstract void initData();

    protected abstract void initEvent();

    protected abstract void destroyView();
}
