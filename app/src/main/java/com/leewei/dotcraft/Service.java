package com.leewei.dotcraft;


import java.util.Map;
import java.util.Random;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.SoundPool;

/**
 * 声音控制类
 * @author wyf
 *
 */
class LeeSoundPlayer {

    private static MediaPlayer music;
    private static SoundPool soundPool;

    private static boolean musicSt = true; //音乐开关

    private static Context context;


    /**
     * 初始化方法
     * @param c
     */
    public static void init(Context c, int musicId)
    {
        context = c;

        initMusic(musicId);

    }


    //初始化音乐播放器
    private static void initMusic(int musicId)
    {

        music = MediaPlayer.create(context,musicId);
        music.setLooping(true);
    }



    /**
     * 暂停音乐
     */
    public static void pauseMusic()
    {
        if(music.isPlaying())
            music.pause();
    }

    /**
     * 播放音乐
     */
    public static void startMusic()
    {
        if(musicSt)
            music.start();
    }



    /**
     * 获得音乐开关状态
     * @return
     */
    public static boolean isMusicSt() {
        return musicSt;
    }

    /**
     * 设置音乐开关
     * @param musicSt
     */
    public static void setMusicSt(boolean musicSt) {
        LeeSoundPlayer.musicSt = musicSt;
        if(musicSt)
            music.start();
        else
            music.stop();
    }



}