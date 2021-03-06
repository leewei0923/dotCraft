/*
 * date: 2022.01.18
 * author: leewei
 * content:
 * */
package com.leewei.dotcraft;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import androidx.appcompat.widget.Toolbar;

import android.os.Vibrator;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private Button startBtn;
    private Button reStartBtn;
    private static Toolbar backBtn;
    // leewei 2022.01.24 定义手指最后运动的坐标
    private Float lastMotionX;
    private Float lastMotionY;
    private int touchIndex;

    //    leewei 22.01.23 定义游戏containerViewList 和 dotViewList
    private final ImageView[] containerViewList = new ImageView[9];
    private final ImageView[] dotViewList = new ImageView[9];

    //    leewei 22.01.24 定义游戏游戏状态 空闲, 等待滑动, 水平滑动, 垂直滑动
    private static final int STATE_IDLE = 0;
    private static final int STATE_WATING_DRAG = 1;
    private static final int STATE_HORIZONTAL_DRAG = 2;
    private static final int STATE_VERTICAL_DRAG = 3;

    private int state = STATE_IDLE;
    private int touchSlop;

    // leewei 22.01.24 定义关卡接口

    private Level level;
    private ImageView backupDot;

    //    leewei 22.01.26 通过按钮
    private Button passBtn;// 定义通过按钮
    private int score = 0; // 定义分数
    private TextView passText;
    private boolean isStartGame = false; // 定义游戏状态，避免未点击开始引发界面奔溃

    //    leewei 22.01.27
    // 用于按钮反馈
    private SoundPool sp;
    private int keyPress; // 普通按钮的提示音
    private int effectTick; // 重要按钮提示音
    // 用于反馈振动
    private Vibrator vib;
    // 实现秒表
    private int levelCount = 1;
    private TextView levelView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // leewei 01.24 获取
        //  获取touchSlop （系统 滑动距离的最小值，大于该值可以认为滑动）
        touchSlop = ViewConfiguration.get(this).getScaledTouchSlop();
        startBtn = findViewById(R.id.start);
        reStartBtn = findViewById(R.id.restart);
        // 2022.01.22 leewei 创建toolbar返回按钮
        backBtn = findViewById(R.id.backBtn);

        // leewei 22.01.26
        passBtn = findViewById(R.id.viaBtn); // 用于获取按钮 id viaBtn
        passText = findViewById(R.id.passText); // 获取 TextView passtext

        levelView = findViewById(R.id.levelCount);


        // leewei 22.01.22 振动
        vib = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);
        // 2022.01.22 leewei 创建toolbar返回按钮事件

        backBtn.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame();
            }
        });

        reStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reStartGame();
            }
        });
        //    leewei 22.01.26 判断是否通过游戏 viaBtn 事件

        passBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isStartGame) nextGame();
            }
        });
        // 渲染 dot 和 container
        initContainerViews();
        initDotViews();
        // SoundPool 初始化加载
        initSound();

    }

    // leewei 22.01.26 开始游戏 事件
    private void startGame() {
        startBtn.setVisibility(View.GONE);
        reStartBtn.setVisibility(View.VISIBLE);
        passBtn.setBackgroundResource(R.drawable.shape_bg);
        playSound(keyPress); // 音效


        vib.vibrate(50); // 振动
        isStartGame = true; // 游戏开始为true
        level = new Level1(); // 初始关卡
        refresh();
    }

    // 游戏重置按钮
    private void reStartGame() {
        startBtn.setVisibility(View.VISIBLE); // 开始按钮 显示
        reStartBtn.setVisibility(View.GONE); // 重置按钮 隐藏
        passBtn.setBackgroundResource(R.drawable.shape_bg_unuse);
        score = 0; // 游戏结束分数重置为 0
        passText.setText(String.valueOf(score));
        playSound(effectTick); // 音效
        vib.vibrate(200); // 振动
        isStartGame = false; // 游戏结束为 flase
        levelCount = 1;
        levelView.setText("第"+String.valueOf(levelCount)+"关");
        level = new Level1(); // 初始关卡

        refresh();
    }

//    leewei 22.01.26 游戏下一关

    private void nextGame() {

        if (LevelUtil.hasSuccess(level)) {
            score++;
            levelCount++;
            levelView.setText("第"+String.valueOf(levelCount)+"关");
            vib.vibrate(70);
            level = new LevelNext();
            refresh();
            playSound(keyPress);
        } else {
            playSound(effectTick);
            congratulations("不成功诶, 再试试?");
            // leewei 22.01.27 提交不成功 分数减
            score--;
            vib.vibrate(100);
            vib.vibrate(50);
        }

        passText.setText(String.valueOf(score < 0 ? 0 : score));
    }


    private void initDotViews() {
        dotViewList[0] = findViewById(R.id.dot0);
        dotViewList[3] = findViewById(R.id.dot1);
        dotViewList[6] = findViewById(R.id.dot2);
        dotViewList[1] = findViewById(R.id.dot3);
        dotViewList[4] = findViewById(R.id.dot4);
        dotViewList[7] = findViewById(R.id.dot5);
        dotViewList[2] = findViewById(R.id.dot6);
        dotViewList[5] = findViewById(R.id.dot7);
        dotViewList[8] = findViewById(R.id.dot8);
        backupDot = findViewById(R.id.backup_dot);
    }

    private void initContainerViews() {
        containerViewList[0] = findViewById(R.id.container0);
        containerViewList[3] = findViewById(R.id.container1);
        containerViewList[6] = findViewById(R.id.container2);
        containerViewList[1] = findViewById(R.id.container3);
        containerViewList[4] = findViewById(R.id.container4);
        containerViewList[7] = findViewById(R.id.container5);
        containerViewList[2] = findViewById(R.id.container6);
        containerViewList[5] = findViewById(R.id.container7);
        containerViewList[8] = findViewById(R.id.container8);
    }


    // leewei 01.24 更新视图

    private void refresh() {
        // setBackgroundResouce() 获取imageView的背景图片
        int[] containerArr = level.getContainerArray();
        int[] dotArr = level.getDotArray();
        for (int i = 0; i < 9; i++) {
            if (containerArr[i] == 1) {
                containerViewList[i].setBackgroundResource(R.drawable.shape_ring_white);
            } else {
                containerViewList[i].setBackgroundResource(0);
            }
        }

        for (int i = 0; i < 9; i++) {
            if (dotArr[i] == 1) {
                dotViewList[i].setBackgroundResource(R.drawable.shape_dot_white);
            } else {
                dotViewList[i].setBackgroundResource(R.drawable.shape_dot_black);
            }
        }
    }

    // leewei 01.24 手势事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isStartGame) {
            switch (event.getAction()) {
                // 手指按下
                case MotionEvent.ACTION_DOWN:
//                Log.d("点击事件", "onTouchEvent:点击 是" + touchIndex);
                    state = STATE_IDLE;
                    lastMotionX = event.getRawX(); //触摸点相对于屏幕左边的坐标
                    lastMotionY = event.getRawY(); // 触摸点相对于屏幕顶部的坐标
                    touchIndex = getTochImageviewIndex(lastMotionX, lastMotionY);

                    if (touchIndex != -1) {
                        state = STATE_WATING_DRAG;
                    }
                    break;
                // 手指滑动
                case MotionEvent.ACTION_MOVE:
//                Log.d("点击事件", "onTouchEvent:滑动 是" + touchSlop);
                    float deltaX = event.getRawX() - lastMotionX; // 偏移的距离
                    float deltaY = event.getRawY() - lastMotionY; //

                    if (state == STATE_WATING_DRAG) {
                        if (Math.abs(deltaX) >= touchSlop || Math.abs(deltaY) >= touchSlop) {
                            state = Math.abs(deltaX) > Math.abs(deltaY) ? STATE_HORIZONTAL_DRAG : STATE_VERTICAL_DRAG;
                        }
                    }
                    if (state == STATE_HORIZONTAL_DRAG) {

                        horizontalDraging(touchIndex / 3, deltaX);
                    }

                    if (state == STATE_VERTICAL_DRAG) {
                        verticalDraging(touchIndex % 3, deltaY);
                    }

                    lastMotionX = event.getRawX();
                    lastMotionY = event.getRawY();
                    break;
                // 手指抬起
                case MotionEvent.ACTION_UP:
//                Log.d("点击事件", "onTouchEvent:抬起 是" + touchIndex);
                    if (state == STATE_HORIZONTAL_DRAG) {
                        // 横向滑动
                        horizontalDragEnd(touchIndex / 3); // 0 第一排, 1 第二排, 2 第三排
                    } else if (state == STATE_VERTICAL_DRAG) {
                        // 纵向滑动
                        verticalDragEnd(touchIndex % 3);
                    }
                    touchIndex = -1;
                    state = STATE_IDLE;
                    break;

                default:
                    break;

            }
        } else {
            congratulations("请点击开始游戏");
        }

        return super.onTouchEvent(event);
    }


//    leewei 22.01.24 获取所需要滑动图片的序号

    private int getTochImageviewIndex(float x, float y) {
        for (int i = 0; i < 9; i++) {
            ImageView dotView = dotViewList[i];
            int[] location = new int[2];
            //getLocationOnScreen()：控件相对于屏幕的左上角为原点的坐标。
            dotView.getLocationOnScreen(location);
            // 勾勒出dotView 的位置, 由距离屏幕 top 和 left 加上每个dotView的宽高推断出位置
            int toLeft = location[0];
            int toTop = location[1];
            int toRight = toLeft + dotView.getWidth();
            int toBottom = toTop + dotView.getHeight();

            if (x >= toLeft && x < toRight && y >= toTop && y <= toBottom) {
                return i;
            }

        }
        return -1;
    }


    // leewei 22.01.24 实现横向滑动
    // rowIndex,
    private void horizontalDraging(int rowIndex, float deltaX) {
        ImageView leftDot = dotViewList[rowIndex * 3];
        ImageView middleDot = dotViewList[rowIndex * 3 + 1];
        ImageView rightDot = dotViewList[rowIndex * 3 + 2];

        float translationX = getValidTranslation(leftDot.getTranslationX() + deltaX);
        leftDot.setTranslationX(translationX);
        middleDot.setTranslationX(translationX);
        rightDot.setTranslationX(translationX);
// 删除 backupDot

        if (backupDot.getVisibility() != View.VISIBLE) {
            backupDot.setVisibility(View.VISIBLE);
        }
        if (translationX > 0) {
            // 向右滑，backup在左边出现
            backupDot.setTranslationX(translationX - backupDot.getWidth() - 90);
//            getDrawable()
            backupDot.setImageDrawable(rightDot.getBackground());
        } else {
            // 向左滑，backup在右边出现
            backupDot.setTranslationX(backupDot.getWidth() * 3 + translationX + 360);
            backupDot.setImageDrawable(leftDot.getBackground());
        }
        backupDot.setTranslationY(backupDot.getHeight() * rowIndex + 115 * rowIndex);
    }


    private void horizontalDragEnd(int rowIndex) {
        ImageView leftDot = dotViewList[rowIndex * 3];
        ImageView middleDot = dotViewList[rowIndex * 3 + 1];
        ImageView rightDot = dotViewList[rowIndex * 3 + 2];
        float targetTranslationX = leftDot.getTranslationX();
        leftDot.setTranslationX(0.0f);
        middleDot.setTranslationX(0.0f);
        rightDot.setTranslationX(0.0f);
        backupDot.setVisibility(View.INVISIBLE);
        backupDot.setTranslationX(0.0f);
        backupDot.setTranslationY(0.0f);
        if (Math.abs(targetTranslationX) < backupDot.getWidth() * 1.0f / 2) {
            return;
        }
        boolean toRight = targetTranslationX > backupDot.getWidth() * 1.0f / 2;
        LevelUtil.horizontalDragLevel(level, toRight, rowIndex);
        refresh();
//        if (LevelUtil.hasSuccess(level)) {
//            congratulations();
//        }
    }

    // leewei 22.01.24 纵向滑动

    private void verticalDraging(int columnIndex, float deltaY) {
        ImageView topDot = dotViewList[columnIndex];
        ImageView middleDot = dotViewList[columnIndex + 3];
        ImageView bottomDot = dotViewList[columnIndex + 6];

        // 获取偏移度
        float translationY = getValidTranslation(topDot.getTranslationY() + deltaY);

        // 让 dot 上移 或 下移
        topDot.setTranslationY(translationY);
        middleDot.setTranslationY(translationY);
        bottomDot.setTranslationY(translationY);
        // 让backupDot 显示
//        ---
        if (backupDot.getVisibility() != View.VISIBLE) {
            backupDot.setVisibility(View.VISIBLE);
        }
        backupDot.setTranslationX(backupDot.getWidth() * columnIndex + 125 * columnIndex);
        if (translationY > 0) {
            // 向下滑，backup在上边出现
            backupDot.setTranslationY(translationY - backupDot.getHeight() - 90);
            backupDot.setImageDrawable(bottomDot.getDrawable());
        } else {
            // 向上滑，backup在下边出现
            backupDot.setTranslationY(backupDot.getHeight() * 3 + translationY + 360);
            backupDot.setImageDrawable(topDot.getDrawable());
        }
    }

    /**
     * 纵向滑动结束，判断滑动距离是否足够触发移动。如果触发移动后，刷新页面展示，并判断关卡是否通过
     */
    private void verticalDragEnd(int columnIndex) {
        ImageView topDot = dotViewList[columnIndex];
        ImageView middleDot = dotViewList[columnIndex + 3];
        ImageView bottomDot = dotViewList[columnIndex + 6];
        float targetTranslationY = topDot.getTranslationY();
        topDot.setTranslationY(0.0f);
        middleDot.setTranslationY(0.0f);
        bottomDot.setTranslationY(0.0f);
        backupDot.setVisibility(View.INVISIBLE);
        backupDot.setTranslationX(0.0f);
        backupDot.setTranslationY(0.0f);
        if (Math.abs(targetTranslationY) < backupDot.getWidth() * 1.0f / 2) {
            return;
        }
        boolean toTop = targetTranslationY < backupDot.getWidth() * -1.0f / 2;
        LevelUtil.verticalDragLevel(level, toTop, columnIndex);
        refresh();
    }

    // leewei 22.01.24 限制一次只能滑动一格

    private float getValidTranslation(float translation) {
        return Math.max(backupDot.getWidth() * -1, Math.min(translation, backupDot.getWidth()));
    }

    private void congratulations(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    // leewei 01.27 按钮声音设置
    @SuppressLint("newmusic")
    private void initSound() {
        sp = new SoundPool.Builder().build();
        keyPress = sp.load(this, R.raw.keypress, 1);
        effectTick = sp.load(this, R.raw.effecttick, 2);
    }

    private void playSound(int soundId) {
        sp.play(soundId,
                0.1f,   //左耳道音量【0~1】
                0.5f,   //右耳道音量【0~1】
                0,  //播放优先级【0表示最低优先级】
                1,  //循环模式【0表示循环一次，-1表示一直循环，其他表示数字+1表示当前数字对应的循环次数】
                1);  //播放速度【1是正常，范围从0~2】
    }

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
                        congratulations("取消");
                    }
                })
                .create()
                .show();
    }

}