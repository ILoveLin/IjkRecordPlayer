package com.company.ijkpalyer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.company.ijkpalyer.ijk.IjkPlayerActivity;
import com.company.ijkpalyer.ijk.MyIjkPlayerActivity;

/**
 * 原版ijk,aar的录像,没有集成DK播放器
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    //苹果公司点播的的流地址
//    public static final String path = "http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/gear2/prog_index.m3u8";
    //我自己公司rtsp的流地址
//    public static final String path = "rtsp://root:root@192.168.66.31:7788/session0.mpg";
//    public static String path = "http://219.151.31.38/liveplay-kk.rtxapp.com/live/program/live/hnwshd/4000000/mnf.m3u8";
//    public static String path = "http://192.168.67.210:3333/api/stream/video?session=123456";
    public static String path = "http://220.161.87.62:8800/hls/0/index.m3u8";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //跳转DKPlayer播放器--切换成自己自定义可以录像的ijk内核
        findViewById(R.id.go_record_ijkdkplaeyr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, IjkPlayerActivity.class);
                startActivity(intent);
            }
        });
        //自己写的ijk测试播放器
        findViewById(R.id.go_my_ijkdkplaeyr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyIjkPlayerActivity.class);
                startActivity(intent);
            }
        });


    }

}
