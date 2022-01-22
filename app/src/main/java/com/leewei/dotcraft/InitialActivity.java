package com.leewei.dotcraft;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class InitialActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
    }

    // 2022.01.21 @leewei 进入 Activity_main
    public void startGame(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    // 退出

    public boolean exitGame() {
        finish();
        return true;
    }
}
