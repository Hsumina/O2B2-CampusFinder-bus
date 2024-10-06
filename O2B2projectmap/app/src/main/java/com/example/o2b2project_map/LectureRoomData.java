package com.example.o2b2project_map;
import java.util.HashMap;

public class LectureRoomData {
    private HashMap<String, LectureRoom> lectureRooms;

    public LectureRoomData() {
        lectureRooms = new HashMap<>();
        addRooms();
    }

    private void addRooms() {
        lectureRooms.put("101", new LectureRoom("101", 1, "West side"));
        lectureRooms.put("102", new LectureRoom("102", 1, "East side"));
        lectureRooms.put("201", new LectureRoom("201", 2, "West side"));
        lectureRooms.put("202", new LectureRoom("202", 2, "East side"));
        // 필요한 강의실을 계속 추가
    }

    public LectureRoom searchRoom(String roomNumber) {
        return lectureRooms.get(roomNumber);
    }
}