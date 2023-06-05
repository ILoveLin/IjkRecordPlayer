package com.company.ijkpalyer.dk;

import android.content.Context;
import android.util.Log;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import xyz.doikki.videoplayer.player.PlayerFactory;

public class IjkPlayerFactory extends PlayerFactory<IjkPlayer> {

    private IjkMediaPlayer ijkMediaPlayer;
    public static IjkPlayer ijkPlayer;

    public static IjkPlayerFactory create() {
        return new IjkPlayerFactory();
    }

    @Override
    public IjkPlayer createPlayer(Context context) {
        ijkPlayer = new IjkPlayer(context);
        ijkMediaPlayer = ijkPlayer.getIjkMediaPlayer();
        Log.e("TAG", "===测试=333==" + ijkMediaPlayer);

        return ijkPlayer;
    }

    public IjkMediaPlayer getIjkMediaPlayer() {
        Log.e("TAG", "===测试=444==" + ijkMediaPlayer);

        return ijkMediaPlayer;
    }
}
