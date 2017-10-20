package com.kui.wxvideoeditt;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    String video;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        video= Environment.getExternalStorageDirectory().getPath()+ File.separator
                +"test"+File.separator+"c.mp4";

    }


    public void click(View view){
        Intent intent=new Intent();
        intent.putExtra(EsayVideoEditActivity.PATH,video);
        intent.setClass(this,EsayVideoEditActivity.class);
        startActivity(intent);
    }

}
