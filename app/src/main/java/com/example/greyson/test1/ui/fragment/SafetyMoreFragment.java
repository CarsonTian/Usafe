package com.example.greyson.test1.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.greyson.test1.R;
import com.example.greyson.test1.ui.base.BaseFragment;

/**
 * Created by greyson on 28/3/17.
 */

public class SafetyMoreFragment extends BaseFragment {
        @Override
        protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.frag_safetymore, container, false);
            return view;
        }

        @Override
        protected void initData() {

        }

        @Override
        protected void initEvent() {

        }

        @Override
        protected void destroyView() {

        }
}
