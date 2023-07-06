package com.company.ijkpalyer;

import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.interfaces.IVLCVout;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//import org.videolan.libvlc.media.MediaPlayer;

/**
 * vlc 播放器 4.0.0 版本还是测试版本
 * 播放视频会卡顿  仅供参考
 */
public class ActivityVLC4 extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    //苹果公司点播的的流地址
//    public static final String path = "http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/gear2/prog_index.m3u8";
    //我自己公司rtsp的流地址
//        public static String path = "http://192.168.67.210:3333/api/stream/audio?session=123456";
    public static String path = "http://219.151.31.38/liveplay-kk.rtxapp.com/live/program/live/hnwshd/4000000/mnf.m3u8";

    //    public static final String path = "rtsp://root:root@192.168.66.31:7788/session0.mpg";
    private SurfaceView texture_video;
    private LibVLC mLibVLC = null;
    private MediaPlayer mMediaPlayer = null;
    private TextureView textureView;
    private Surface mSurface;
    private SurfaceView surfaceView;
    private Button btn_end_record;
    private Button btn_frame;
    private Button btn_start_record;
    private String mRecordPath;
    private Button start_live;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vlc4);
//        textureView  = findViewById(R.id.texture_video);
        textureView = findViewById(R.id.texture_view);
        textureView.setSurfaceTextureListener(listener);

        btn_start_record = findViewById(R.id.btn_start_record);
        btn_end_record = findViewById(R.id.btn_end_record);
        start_live = findViewById(R.id.start_live);
        btn_frame = findViewById(R.id.btn_frame);
        start_live.setOnClickListener(this);
        btn_start_record.setOnClickListener(this);
        btn_end_record.setOnClickListener(this);
        btn_frame.setOnClickListener(this);
        requestPermission();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_live: //开始
                mMediaPlayer.play();
                Toast.makeText(this, "开始-播放", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_start_record: //开始
                ///storage/emulated/0/MyMovies/2023-03-10 14:42:50.mp4
                ///storage/emulated/0/MyMovies/2023-03-10 14:49:33.mp4
                Toast.makeText(this, "开始录像", Toast.LENGTH_SHORT).show();
                File file = new File(Environment.getExternalStorageDirectory() + "/MyMovies");
                // /storage/emulated/0/RecordVideos
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
                //没有找到结束 的方法,所以不知道录像是否成功
                mMediaPlayer.record(mRecordPath);
                break;
            case R.id.btn_end_record:   //结束

                scanFile(this, mRecordPath);
                Toast.makeText(this, "结束录像", Toast.LENGTH_SHORT).show();

                break;
            case R.id.btn_frame:        //截图

                Toast.makeText(this, "截图", Toast.LENGTH_SHORT).show();


                break;


        }
    }

    //刷新相册，出现刚刚录像视频
    public static void scanFile(Context context, String filePath) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(new File(filePath)));
        context.sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaPlayer.release();
        mLibVLC.release();
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onStop() {
        super.onStop();

        mMediaPlayer.stop();
        mMediaPlayer.detachViews();
    }


    private TextureView.SurfaceTextureListener listener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            createPlayer();
//            mSurface = new Surface(surface);
//            mMediaPlayer.setSurface(mSurface);
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

    private void createPlayer() {

        final ArrayList args = new ArrayList<>();//VLC参数

        args.add("--rtsp-tcp");//强制rtsp-tcp，加快加载视频速度

        args.add("--aout=opensles");

        args.add("--audio-time-stretch");


        //args.add("--sub-source=marq{marquee=\"%Y-%m-%d,%H:%M:%S\",position=10,color=0xFF0000,size=40}");//这行是可以再vlc窗口右下角添加当前时间的

        args.add("-vvv");

        mLibVLC = new LibVLC(this, args);
        mMediaPlayer = new MediaPlayer(mLibVLC);
//        Rect surfaceFrame = textureView.getHolder().getSurfaceFrame();

        //设置vlc视频铺满布局
        //
        //mMediaPlayer.getVLCVout().setWindowSize(layout_video.getWidth(), layout_video.getHeight());//宽，高  播放窗口的大小
        //
        //mMediaPlayer.setAspectRatio(layout_video.getWidth()+":"+layout_video.getHeight());//宽，高  画面大小

        mMediaPlayer.setScale(0);//这行必须加，为了让视图填满布局

        //添加视图

        IVLCVout vout = mMediaPlayer.getVLCVout();

        vout.setVideoView(textureView);

        vout.attachViews();

        Uri uri = Uri.parse(path);//rtsp流地址或其他流地址//"https://media.w3.org/2010/05/sintel/trailer.mp4"

        final Media media = new Media(mLibVLC, uri);

        int cache = 1000;

        media.addOption(":network-caching=" + cache);

        media.addOption(":file-caching=" + cache);

        media.addOption(":live-cacheing=" + cache);

        media.addOption(":sout-mux-caching=" + cache);

        media.addOption(":codec=mediacodec,iomx,all");

        mMediaPlayer.setMedia(media);//

        media.setHWDecoderEnabled(false, false);//设置后才可以录制和截屏,这行必须放在mMediaPlayer.setMedia(media)后面，因为setMedia会设置setHWDecoderEnabled为true

        mMediaPlayer.setEventListener(new MediaPlayer.EventListener() {
            @Override
            public void onEvent(MediaPlayer.Event event) {
                if (event.type == MediaPlayer.Event.Playing) {
                    Log.w("main", "正在播放");
                } else {

                }

            }
        });
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
                            Toast.makeText(ActivityVLC4.this, "获取存储权限成功", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (never) {
                            Toast.makeText(ActivityVLC4.this, "被永久拒绝授权，请手动授予存储权限", Toast.LENGTH_SHORT).show();

                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(ActivityVLC4.this, permissions);
                        } else {
                            Toast.makeText(ActivityVLC4.this, "获取存储权限失败", Toast.LENGTH_SHORT).show();
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
