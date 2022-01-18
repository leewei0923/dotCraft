/*
 * date: 2022.01.18
 * author: leewei
 * content:
 * */
package com.leewei.dotcraft;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button startBtn;
    private Button reStartBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startBtn = findViewById(R.id.start);
        reStartBtn = findViewById(R.id.restart);

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
    }


    private void startGame() {
        startBtn.setVisibility(View.GONE);
        reStartBtn.setVisibility(View.VISIBLE);
    }

    private void reStartGame() {
        startBtn.setVisibility(View.VISIBLE);
        reStartBtn.setVisibility(View.GONE);
    }
}
