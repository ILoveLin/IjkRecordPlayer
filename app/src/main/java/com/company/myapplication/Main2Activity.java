//package com.company.myapplication;
//
//import android.graphics.SurfaceTexture;
//import android.media.AudioManager;
//import android.os.Bundle;
//import android.view.Surface;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//import android.view.TextureView;
//import android.view.View;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import java.io.IOException;
//
//import tv.danmaku.ijk.media.player.IjkMediaPlayer;
//
//
//public class Main2Activity extends AppCompatActivity {
//
//    private SurfaceView surfaceView;
//
//    private TextureView textureView;
//    private Surface mSurface;
//
//    private IjkMediaPlayer mPlayer;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        surfaceView = findViewById(R.id.surface_view);
//        textureView = findViewById(R.id.texture_view);
//
//        surfaceView.getHolder().addCallback(callback);
//        textureView.setSurfaceTextureListener(listener);
//
//    }
//
//    private void createPlayer() {
//        if (mPlayer == null) {
//            mPlayer = new IjkMediaPlayer();
//            // 设置倍速，应该是0-2的float类型，可以测试一下
//            mPlayer.setSpeed(1.0f);
//            // 设置调用prepareAsync不自动播放，即调用start才开始播放
//            mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);
//            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            try {
//                mPlayer.setDataSource("http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            mPlayer.prepareAsync();
//        }
//    }
//
//    private void release() {
//        if (mPlayer != null) {
//            mPlayer.stop();
//            mPlayer.release();
//            mPlayer = null;
//        }
//        IjkMediaPlayer.native_profileEnd();
//    }
//
//    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
//        @Override
//        public void surfaceCreated(SurfaceHolder holder) {
//            createPlayer();
//            mPlayer.setDisplay(surfaceView.getHolder());
//        }
//
//        @Override
//        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//
//        }
//
//        @Override
//        public void surfaceDestroyed(SurfaceHolder holder) {
//            if (surfaceView != null) {
//                surfaceView.getHolder().removeCallback(callback);
//                surfaceView = null;
//            }
//        }
//    };
//
//    private TextureView.SurfaceTextureListener listener = new TextureView.SurfaceTextureListener() {
//        @Override
//        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
//            createPlayer();
//            mSurface = new Surface(surface);
//            mPlayer.setSurface(mSurface);
//        }
//
//        @Override
//        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
//
//        }
//
//        @Override
//        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
//            textureView.setSurfaceTextureListener(null);
//            textureView = null;
//            mSurface = null;
//            return true;
//        }
//
//        @Override
//        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//
//        }
//    };
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        release();
//    }
//
//    public void handleSurfaceView(View view) {
//        mPlayer.setDisplay(surfaceView.getHolder());
//        if (!mPlayer.isPlaying())
//            mPlayer.start();
//    }
//
//    public void handleTextureView(View view) {
//        mPlayer.setSurface(mSurface);
//        if (!mPlayer.isPlaying())
//            mPlayer.start();
//    }
//}
