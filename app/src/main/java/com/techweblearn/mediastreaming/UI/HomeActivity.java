package com.techweblearn.mediastreaming.UI;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;



import com.techweblearn.mediastreaming.EventBus.Events;
import com.techweblearn.mediastreaming.EventBus.GlobalEventBus;

import com.techweblearn.mediastreaming.R;

import com.techweblearn.mediastreaming.Streaming.StreamingService;

import org.greenrobot.eventbus.Subscribe;


public class HomeActivity extends AppCompatActivity {


    public static final String TAG_URI = "uri";
    public static final String TAG = HomeActivity.class.getSimpleName();
    Uri playuri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Test.mp4");
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},9);

        GlobalEventBus.getBus().register(this);




        Intent intent = getIntent();
        if (intent.getData() != null) {
            uri = intent.getData();
        } else {
            uri = intent.getParcelableExtra(TAG_URI);
        }

        if (uri != null) {
            Log.d("URI", uri.toString());
            Intent intent1 = new Intent(this, StreamingService.class);
            intent1.putExtra("uri", uri.toString());

            startService(intent1);

        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalEventBus.getBus().unregister(this);
    }

    @Subscribe
    public void getStreamStatus(Events.StreamStatusBus streamStatusBus)
    {

    }

    @Subscribe
    public void getStreamStarted(Events.StreamStartedBus streamStartedBus)
    {
        Intent intent=new Intent(HomeActivity.this,PlayerActivity.class);
        intent.putExtra("videoinfo",streamStartedBus.getVideoInfo());
        startActivity(intent);
        finish();
    }
}
