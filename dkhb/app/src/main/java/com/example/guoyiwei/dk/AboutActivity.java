package com.example.guoyiwei.dk;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.guoyiwei.dk.services.PreferencesService;
import com.example.guoyiwei.dk.util.LogUtil;

import java.util.Calendar;

/**
 * Created by guoyiwei on 2017/3/6.
 */
public class AboutActivity extends AppCompatActivity {
    public static int num =0;
    Button test =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        Button log = (Button)findViewById(R.id.logButton);
        log.setOnClickListener(new View.OnClickListener(){//创建监听

            public void onClick(View v) {
                num++;


                Intent intent = new Intent();
                intent.setClass(AboutActivity.this, LogActivity.class);
                startActivity(intent);

            }

        });



    }
}
