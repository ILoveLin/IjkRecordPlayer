package com.company.ijkpalyer.ijk;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.company.ijkpalyer.R;
import com.company.ijkpalyer.dk.IjkPlayerFactory;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import xyz.doikki.videoplayer.player.BaseVideoView;
import xyz.doikki.videoplayer.player.VideoView;

/**
 * DK播放器,切换成自己的ijk可以录像的播放器  arr内核+jniLibs.so 文件
 */
public class IjkPlayerActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "IjkPlayerActivity";
    //苹果公司点播的的流地址
    public static final String path = "http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/gear2/prog_index.m3u8";
    //我自己公司rtsp的流地址
//    public static final String path = "rtsp://root:root@192.168.66.31:7788/session0.mpg";
//    public static String path = "http://219.151.31.38/liveplay-kk.rtxapp.com/live/program/live/hnwshd/4000000/mnf.m3u8";
//    public static String path = "http://220.161.87.62:8800/hls/0/index.m3u8";
//    public static String path = "http://192.168.67.210:3333/api/stream/video?session=123456";

    private Surface mSurface;
    private IjkMediaPlayer mPlayer;
    private Button btn_end_record;  
    private Button btn_frame;
    private Button btn_start_record;
    private String mRecordPath;
    private Button start_live;
    private VideoView mVideoView;
    private IjkPlayerFactory ijkPlayerFactory;
    private EditText et_get_path;
    private String currentPath;
    private ImageView imageview;
    private Button btn_16_9;
    private Button btn_default;
    private Button btn_yuanshi;
    private Button btn_no_voice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ijk);
        btn_start_record = findViewById(R.id.btn_start_record);
        btn_end_record = findViewById(R.id.btn_end_record);
        btn_frame = findViewById(R.id.btn_frame);
        start_live = findViewById(R.id.start_live);
        et_get_path = findViewById(R.id.et_get_path);
        imageview = findViewById(R.id.imageview);
        mVideoView = findViewById(R.id.player);
        btn_16_9 = findViewById(R.id.btn_16_9);
        btn_default = findViewById(R.id.btn_default);
        btn_yuanshi = findViewById(R.id.btn_yuanshi);
        btn_no_voice = findViewById(R.id.btn_no_voice);
        btn_16_9.setOnClickListener(this);
        btn_default.setOnClickListener(this);
        btn_yuanshi.setOnClickListener(this);
        btn_no_voice.setOnClickListener(this);
        btn_start_record.setOnClickListener(this);
        btn_end_record.setOnClickListener(this);
        btn_frame.setOnClickListener(this);
        start_live.setOnClickListener(this);
        //使用IjkPlayer解码
        ijkPlayerFactory = IjkPlayerFactory.create();
        mVideoView.setPlayerFactory(ijkPlayerFactory);
        requestPermission();
        mVideoView.setUrl(path); //设置视频地址
        mVideoView.start(); //开始播放，不调用则不自动播放
        responseListener();

    }

    private void responseListener() {
        mVideoView.addOnStateChangeListener(new BaseVideoView.OnStateChangeListener() {
            @Override
            public void onPlayerStateChanged(int playerState) {
            }

            @Override
            public void onPlayStateChanged(int playState) {
                switch (playState) {
                    case VideoView.STATE_BUFFERING://缓存中
                        Log.e(TAG, "==DKPlayer===缓存中");
                        break;
                    case VideoView.STATE_BUFFERED://缓存完毕
                        Log.e(TAG, "==DKPlayer===缓存完毕");
                        break;
                    case VideoView.STATE_PLAYING://播放
                        Log.e(TAG, "==DKPlayer===播放");
                        break;
                    case VideoView.STATE_PAUSED://播放暂停
                        Log.e(TAG, "==DKPlayer===播放暂停");
                        break;
                    case VideoView.STATE_ERROR://播放失败
                        Log.e(TAG, "==DKPlayer===播放失败");

                        break;
                }
            }
        });

    }

    //刷新相册，出现刚刚录像视频
    public static void scanFile(Context context, String filePath) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(new File(filePath)));
        context.sendBroadcast(intent);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_default: //默认
                mVideoView.setScreenScaleType(VideoView.SCREEN_SCALE_DEFAULT);
                break;
            case R.id.btn_yuanshi: //原始
                mVideoView.setScreenScaleType(VideoView.SCREEN_SCALE_ORIGINAL);
                break;
            case R.id.btn_16_9: //16:9
                mVideoView.setScreenScaleType(VideoView.SCREEN_SCALE_16_9);
                break;
            case R.id.btn_no_voice: //静音
                if (mVideoView.isMute()) {
                    mVideoView.setMute(false);
                    Toast.makeText(IjkPlayerActivity.this, "开启-声音", Toast.LENGTH_SHORT).show();
                } else {
                    mVideoView.setMute(true);
                    Toast.makeText(IjkPlayerActivity.this, "开启-静音", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.start_live: //开始播放
                mVideoView.release();
                currentPath = et_get_path.getText().toString().trim();
                mVideoView.setUrl(currentPath); //设置视频地址
                mVideoView.start(); //开始播放，不调用则不自动播放
                /**
                 * 必须播放之后 才能拿到IjkMediaPlayer==mPlayer;
                 */
                break;
            case R.id.btn_start_record: //开始录像
                //获取可以录像的IjkMediaPlayer
                mPlayer = IjkPlayerFactory.ijkPlayer.getIjkMediaPlayer();
                Toast.makeText(this, "开始录像", Toast.LENGTH_SHORT).show();
                record();
                break;
            case R.id.btn_end_record:   //结束录像
                //获取可以录像的IjkMediaPlayer
                mPlayer = IjkPlayerFactory.ijkPlayer.getIjkMediaPlayer();
                mPlayer.stopRecord();
                scanFile(this, mRecordPath);
                Toast.makeText(this, "结束录像", Toast.LENGTH_SHORT).show();

                break;
            case R.id.btn_frame:        //截图
                //1,DK播放器自带截图功能
                Bitmap bitmap = mVideoView.doScreenShot();
                imageview.setImageBitmap(bitmap);
                Toast.makeText(this, "截图", Toast.LENGTH_SHORT).show();
                break;


        }
    }

    private void record() {
        File file = new File(Environment.getExternalStorageDirectory() + "/MyMovies");
//        /storage/emulated/0/RecordVideos
        Log.e(TAG, "===file.exists()===" + file.exists());
        Log.e(TAG, "===file.mkdirs()===" + file.mkdirs());
        Log.e(TAG, "===file.mkdirs()=file==" + file.getAbsolutePath());
        if (!file.exists()) {
            file.mkdirs();
            Log.e(TAG, "===000===" + file.exists());
        }
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(System.currentTimeMillis()));
        String s = file.getAbsolutePath() + "/" + date + ".mp4";
        Log.e(TAG, "===s===" + s);
        String replace = s.replace("-", "_");
        Log.e(TAG, "===s==replace=" + replace);
        String replace1 = replace.replace(" ", "_");
        Log.e(TAG, "===s==replace1=" + replace1);
        String replace2 = replace1.replace(":", "_");
        Log.e(TAG, "===s==replace2=" + replace2);
        mRecordPath = replace2;
        Log.e(TAG, "===mRecordPath===" + mRecordPath);

        mPlayer.startRecord(mRecordPath);
    }


    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.resume();
    }


    @Override
    public void onBackPressed() {
        if (!mVideoView.onBackPressed()) {
            super.onBackPressed();
        }
    }

    private void requestPermission() {

        XXPermissions.with(this)
                // 不适配 Android 11 可以这样写
                //.permission(Permission.Group.STORAGE)
                // 适配 Android 11 需要这样写，这里无需再写 Permission.Group.STORAGE
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (all) {
                            Toast.makeText(IjkPlayerActivity.this, "获取存储权限成功", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (never) {
                            Toast.makeText(IjkPlayerActivity.this, "被永久拒绝授权，请手动授予存储权限", Toast.LENGTH_SHORT).show();

                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(IjkPlayerActivity.this, permissions);
                        } else {
                            Toast.makeText(IjkPlayerActivity.this, "获取存储权限失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


//        XXPermissions.with(MainActivity.this)
//                // 不适配 Android 11 可以这样写
//                //.permission(Permission.Group.STORAGE)
//                // 适配 Android 11 需要这样写，这里无需再写 Permission.Group.STORAGE
////                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
//                .permission(Permission.WRITE_EXTERNAL_STORAGE)
//                .permission(Permission.READ_EXTERNAL_STORAGE)
//                .request(new OnPermission() {
//
//                    @Override
//                    public void hasPermission(List<String> granted, boolean all) {
//                        if (all) {
////                            showToast("获取存储权限成功");
//                        }
//                    }
//
//                    @Override
//                    public void noPermission(List<String> denied, boolean never) {
//                        if (never) {
////                            showToast("被永久拒绝授权，请手动授予存储权限");
//                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
//                            XXPermissions.startPermissionActivity(MainActivity.this, denied);
//                        } else {
////                            showToast("获取存储权限失败");
//                        }
//                    }
//                });

    }
//    //截图
//    public Bitmap capture() {
//        Bitmap srcBitmap = Bitmap.createBitmap(1920,
//                1080, Bitmap.Config.ARGB_8888);
//        mPlayer.getCurrentFrame(srcBitmap);
//        return srcBitmap;
//    }
}
