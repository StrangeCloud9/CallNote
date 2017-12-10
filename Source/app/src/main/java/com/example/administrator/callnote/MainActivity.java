package com.example.administrator.callnote;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

public class MainActivity extends Activity {
    public static boolean service_state = false;
    public static Button service_control;
    public static Button function1;
    public static String TransString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=57bfe166");


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO))
            {
                Log.d("MainActivity", "shouldSPR");
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO}, 1);
            }
            else
            {

                Log.d("MainActivity", "!shouldSPR");
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO}, 1);
            }
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        service_control = (Button) findViewById(R.id.start_service);
        service_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (service_state)
                {
                    stopService(new Intent(MainActivity.this, MSCService.class));
                    service_control.setText("Start\nservice");
                }
                else
                {
                    startService(new Intent(MainActivity.this, MSCService.class));
                    service_control.setText("Stop\nservice");

                }
                service_state = !service_state;
            }
        });

        function1 = (Button) findViewById(R.id.function1);
        function1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Note.class));

            }
        });

        if (service_state)
        {
            service_control.setText("Stop\nservice");
        }
        else
        {
            service_control.setText("Start\nservice");
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {

                }
                else
                {
                    this.finish();
                }
        }
    }
}
