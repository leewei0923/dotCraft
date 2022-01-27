package com.leewei.dotcraft;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import android.os.Vibrator;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.leewei.dotcraft.LeeSoundPlayer;

public class InitialActivity extends AppCompatActivity {

    //    leewei 22.01.27
    // 用于按钮反馈
    private SoundPool sp;
    private int keyPress;
    private int effectTick;
    private int initial;
    // 用于按钮振动
    private Vibrator vib;
    // 音乐开/闭
    private boolean musicState;
    private Button musicSwitchBtn;

    private SharedPreferences spre;

    LeeSoundPlayer spl = new LeeSoundPlayer(); // 音乐服务

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        musicSwitchBtn = findViewById(R.id.musicSwitch);
        spre = getSharedPreferences("MusicSetting", Context.MODE_PRIVATE);
        musicState = spre.getBoolean("musicState", true);
//        leewei 22.01.27
        initSound(); // 播放声音设置


        if (musicState) {
            spl.init(this, R.raw.main);
            spl.startMusic();
        }


        // leewei 22.01.22 振动
        vib = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);// 震动0.5秒
        // leewei 22.01.27 music开关按钮
        musicSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicSwitchEvent();
            }
        });

        musicSwitch(); // 由musicState 控制状态 true 开, false 关
    }

    // 2022.01.21 @leewei 进入 Activity_main
    public void startGame(View view) {
        vib.vibrate(50);
        playSound(keyPress, 1);
        startActivity(new Intent(this, MainActivity.class));
    }

    // 退出

    public void exitGame(View view) {
        playSound(effectTick, 1);
        showDialog();
    }

    // 开 / 闭 音乐


    public void musicSwitchEvent() {
        musicState = !musicState;
        if (musicState == true) {
            musicSwitchBtn.setText("音乐: 开");
            spl.init(this, R.raw.main);
            spl.startMusic();
        } else {
            musicSwitchBtn.setText("音乐: 关");
            spl.pauseMusic();
        }
        spre.edit().putBoolean("musicState", musicState).apply();
    }

    // leewei 22.01.27 控制音乐按钮
    private void musicSwitch() {
        if (musicState == true) {
            musicSwitchBtn.setText("音乐: 开");
        } else {
            musicSwitchBtn.setText("音乐: 关");
        }
    }

    // 关于我们

    public void aboutMe(View view) {
        vib.vibrate(50);
        playSound(keyPress, 1);
        showDialog2(view);
    }


    // leewei 01.27 按钮声音设置
    @SuppressLint("NewApi")
    private void initSound() {
        sp = new SoundPool.Builder().build();
        keyPress = sp.load(this, R.raw.keypress, 1);
        effectTick = sp.load(this, R.raw.effecttick, 2);
        initial = sp.load(this, R.raw.initial, 2);
    }

    private void playSound(int soundId, int loop) {
        sp.play(soundId,
                0.1f,   //左耳道音量【0~1】
                0.5f,   //右耳道音量【0~1】
                0,  //播放优先级【0表示最低优先级】
                loop,  //循环模式【0表示循环一次，-1表示一直循环，其他表示数字+1表示当前数字对应的循环次数】
                1);  //播放速度【1是正常，范围从0~2】
    }

    //
    // leewei 22.01.27 dialog

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher)
                .setTitle("提示框")
                .setMessage("确定要退出?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
    }


    public void showDialog2(View view) {

        View dialogView = getLayoutInflater().inflate(R.layout.about, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher)
                .setTitle("游戏介绍")
                .setMessage("这仅仅是开始!")
                .setPositiveButton("朕已阅", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("送花", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        congratulations("🎉🎉🎉🌷🌷🌷");
                        vib.vibrate(100);
                        vib.vibrate(50);
                        vib.vibrate(80);
                        vib.vibrate(60);
                    }
                })
                .setView(dialogView)
                .create()
                .show();
    }

    private void congratulations(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
