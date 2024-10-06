package com.example.o2b2project_map;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private LectureRoomData lectureRoomData; // 강의실 데이터 관리 객체
    private EditText editTextRoomNumber; // 사용자 입력을 받는 필드
    private TextView textViewResult; // 검색 결과 표시

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // XML과 연결

        // XML 파일에서 UI 요소들 연결
        editTextRoomNumber = findViewById(R.id.editTextRoomNumber);
        Button buttonSearch = findViewById(R.id.buttonSearch);
        textViewResult = findViewById(R.id.textViewResult);

        // 강의실 데이터 초기화
        lectureRoomData = new LectureRoomData();

        // 버튼 클릭 시 검색 기능 실행
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EditText에서 입력받은 강의실 번호 가져오기
                String roomNumber = editTextRoomNumber.getText().toString();

                // 강의실 정보 검색
                LectureRoom result = lectureRoomData.searchRoom(roomNumber);

                // 검색 결과를 TextView에 표시
                if (result != null) {
                    textViewResult.setText(result.toString());
                } else {
                    textViewResult.setText("Room not found.");
                }
            }
        });
    }
}
