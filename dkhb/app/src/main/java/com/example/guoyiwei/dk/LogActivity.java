package com.example.guoyiwei.dk;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.guoyiwei.dk.services.PreferencesService;

/**
 * Created by guoyiwei on 2017/3/6.
 */
public class LogActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log);
        TextView log = (TextView)findViewById(R.id.logText);
        PreferencesService service = new PreferencesService(this);
        log.setText(service.getValue("log"));
    }
}
