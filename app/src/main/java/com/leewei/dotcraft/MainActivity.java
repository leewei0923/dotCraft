/*
 * date: 2022.01.18
 * author: leewei
 * content:
 * */
package com.leewei.dotcraft;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

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

        // 2022.01.22 leewei 创建toolbar返回按钮事件

        backBtn.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
        initContainerViews();
        initDotViews();

    }


    private void startGame() {
        startBtn.setVisibility(View.GONE);
        reStartBtn.setVisibility(View.VISIBLE);

        level = new Level1();
        refresh();
    }

    private void reStartGame() {
        startBtn.setVisibility(View.VISIBLE);
        reStartBtn.setVisibility(View.GONE);
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
                containerViewList[i].setBackgroundResource(R.drawable.shape_ring_white);
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

        switch (event.getAction()) {
            // 手指按下
            case MotionEvent.ACTION_DOWN:
                Log.d("点击事件", "onTouchEvent:点击 是" + touchIndex);
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
                Log.d("点击事件", "onTouchEvent:滑动 是" + touchSlop);
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
                Log.d("点击事件", "onTouchEvent:抬起 是" + touchIndex);
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

        if (backupDot.getVisibility() != View.VISIBLE) {
            backupDot.setVisibility(View.VISIBLE);
        }
        if (translationX > 0) {
            // 向右滑，backup在左边出现
            backupDot.setTranslationX(translationX - backupDot.getWidth());
            backupDot.setImageDrawable(rightDot.getDrawable());
        } else {
            // 向左滑，backup在右边出现
            backupDot.setTranslationX(backupDot.getWidth() * 3 + translationX);
            backupDot.setImageDrawable(leftDot.getDrawable());
        }
        backupDot.setTranslationY(backupDot.getHeight() * rowIndex);
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
//        if (LevelUtils.hasSuccess(level)) {
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
        if (backupDot.getVisibility() != View.VISIBLE) {
            backupDot.setVisibility(View.VISIBLE);
        }
        backupDot.setTranslationX(backupDot.getWidth() * columnIndex);
        if (translationY > 0) {
            // 向下滑，backup在上边出现
            backupDot.setTranslationY(translationY - backupDot.getHeight());
            backupDot.setImageDrawable(bottomDot.getDrawable());
        } else {
            // 向上滑，backup在下边出现
            backupDot.setTranslationY(backupDot.getHeight() * 3 + translationY);
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
        if (LevelUtil.hasSuccess(level)) {
            congratulations();
        }
    }

    // leewei 22.01.24 限制一次只能滑动一格

    private float getValidTranslation(float translation) {
        return Math.max(backupDot.getWidth() * -1, Math.min(translation, backupDot.getWidth()));
    }

    private void congratulations() {
        Toast.makeText(this, "恭喜过关", Toast.LENGTH_SHORT).show();
    }
}