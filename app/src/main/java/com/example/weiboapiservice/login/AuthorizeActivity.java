package com.example.weiboapiservice.login;

import android.os.Bundle;

import com.example.weiboapiservice.BaseActivity;
import com.example.weiboapiservice.R;

public class AuthorizeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorize);

        initEvents();
    }

    @Override
    public void initEvents() {

    }
}
