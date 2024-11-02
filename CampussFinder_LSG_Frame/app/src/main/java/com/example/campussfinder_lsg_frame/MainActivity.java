package com.example.campussfinder_lsg_frame;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // 강의실 검색 화면으로 이동
    public void openSearch(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    // 강의실 정보 화면으로 이동
    public void openRoomInfo(View view) {
        Intent intent = new Intent(this, RoomInfoActivity.class);
        startActivity(intent);
    }

    // 학사 일정 화면으로 이동
    public void openAcademicSchedule(View view) {
        Intent intent = new Intent(this, AcademicScheduleActivity.class);
        startActivity(intent);
    }
}
