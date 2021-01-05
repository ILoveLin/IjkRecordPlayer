package com.company.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    //     public static final String path = "http://121.18.168.149/cache.ott.ystenlive.itv.cmvideo.cn:80/000000001000/1000000001000010606/1.m3u8?stbId=005301FF001589101611549359B92C46&channel-id=ystenlive&Contentid=1000000001000010606&mos=jbjhhzstsl&livemode=1&version=1.0&owaccmark=1000000001000010606&owchid=ystenlive&owsid=5474771579530255373&AuthInfo=2TOfGIahP4HrGWrHbpJXVOhAZZf%2B%2BRvFCOimr7PCGr%2Bu3lLj0NrV6tPDBIsVEpn3QZdNn969VxaznG4qedKIxPvWqo6nkyvxK0SnJLSEP%2FF4Wxm5gCchMH9VO%2BhWyofF";
//    public static final String path = "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov";
    public static final String path = "http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8";
//    public static final String path = "rtsp://root:root@192.168.129.39:7788/session0.mpg";
    //    public static final String path = "rtmp://58.200.131.2:1935/livetv/jxhd";
    //    public String path = "rtmp://58.200.131.2:1935/livetv/jxhd";
    //    public String path = "rtsp://username:password@ip：port/session1.mpg";
    //public static final String path = "http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8";
    //private String path = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4";
    private SurfaceView surfaceView;
    private TextureView textureView;
    private Surface mSurface;
    private IjkMediaPlayer mPlayer;
    private Button btn_end_record;
    private Button btn_frame;
    private Button btn_start_record;
    private String mRecordPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textureView = findViewById(R.id.texture_view);
        btn_start_record = findViewById(R.id.btn_start_record);
        btn_end_record = findViewById(R.id.btn_end_record);
        btn_frame = findViewById(R.id.btn_frame);
        textureView.setSurfaceTextureListener(listener);
        btn_start_record.setOnClickListener(this);
        btn_end_record.setOnClickListener(this);
        btn_frame.setOnClickListener(this);
        requestPermission();

    }


    public static void scanFile(Context context, String filePath) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(new File(filePath)));
        context.sendBroadcast(intent);
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

        mRecordPath = s;
        Log.e(TAG, "===mRecordPath===" + mRecordPath);

        mPlayer.startRecord(mRecordPath);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_record: //开始
                Toast.makeText(this, "开始录像", Toast.LENGTH_SHORT).show();
                record();
                break;
            case R.id.btn_end_record:   //结束
                mPlayer.stopRecord();
                scanFile(this, mRecordPath);
                Toast.makeText(this, "结束录像", Toast.LENGTH_SHORT).show();

                break;
            case R.id.btn_frame:        //截图

                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        Bitmap srcBitmap = Bitmap.createBitmap(1920,
                                1080, Bitmap.Config.ARGB_8888);
                        boolean currentFrame = mPlayer.getCurrentFrame(srcBitmap);
                        //插入相册 解决了华为截图显示问题
                        MediaStore.Images.Media.insertImage(getContentResolver(), srcBitmap, "", "");

                    }
                }.start();

                Toast.makeText(this, "截图", Toast.LENGTH_SHORT).show();


                break;


        }
    }

    private void createPlayer() {
        if (mPlayer == null) {
            mPlayer = new IjkMediaPlayer();
            // 设置倍速，应该是0-2的float类型，可以测试一下
            mPlayer.setSpeed(1.0f);
            // 设置调用prepareAsync不自动播放，即调用start才开始播放
            mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
//                mPlayer.setDataSource("http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4");
                mPlayer.setDataSource(path);
                mPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mPlayer.prepareAsync();
        }
    }

    private void release() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        IjkMediaPlayer.native_profileEnd();
    }

    //
    private TextureView.SurfaceTextureListener listener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            createPlayer();
            mSurface = new Surface(surface);
            mPlayer.setSurface(mSurface);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            textureView.setSurfaceTextureListener(null);
            textureView = null;
            mSurface = null;
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        release();
    }


    public void handleTextureView(View view) {
        mPlayer.setSurface(mSurface);
        if (!mPlayer.isPlaying())
            mPlayer.start();
    }

    private void requestPermission() {
        XXPermissions.with(MainActivity.this)
                // 不适配 Android 11 可以这样写
                //.permission(Permission.Group.STORAGE)
                // 适配 Android 11 需要这样写，这里无需再写 Permission.Group.STORAGE
//                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .permission(Permission.WRITE_EXTERNAL_STORAGE)
                .permission(Permission.READ_EXTERNAL_STORAGE)
                .request(new OnPermission() {

                    @Override
                    public void hasPermission(List<String> granted, boolean all) {
                        if (all) {
//                            showToast("获取存储权限成功");
                        }
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean never) {
                        if (never) {
//                            showToast("被永久拒绝授权，请手动授予存储权限");
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(MainActivity.this, denied);
                        } else {
//                            showToast("获取存储权限失败");
                        }
                    }
                });

    }
//    //截图
//    public Bitmap capture() {
//        Bitmap srcBitmap = Bitmap.createBitmap(1920,
//                1080, Bitmap.Config.ARGB_8888);
//        mPlayer.getCurrentFrame(srcBitmap);
//        return srcBitmap;
//    }
}
