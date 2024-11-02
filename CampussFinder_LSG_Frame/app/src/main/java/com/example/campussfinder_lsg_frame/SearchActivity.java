package com.example.campussfinder_lsg_frame;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

public class SearchActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }

    // 메인 페이지로 돌아가기
    public void backToMain(View view) {
        finish();
    }
}
