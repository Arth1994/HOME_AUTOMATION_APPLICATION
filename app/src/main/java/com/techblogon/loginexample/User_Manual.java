package com.techblogon.loginexample;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class User_Manual extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            String value = extras.getString("KEY");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manual);


    }
}
