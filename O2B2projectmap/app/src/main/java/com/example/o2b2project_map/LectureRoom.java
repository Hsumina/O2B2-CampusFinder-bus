package com.example.o2b2project_map;

public class LectureRoom {
    private String roomNumber;
    private int floor;
    private String location;

    public LectureRoom(String roomNumber, int floor, String location) {
        this.roomNumber = roomNumber;
        this.floor = floor;
        this.location = location;
    }

    // Getter 메서드들
    public String getRoomNumber() {
        return roomNumber;
    }

    public int getFloor() {
        return floor;
    }

    public String getLocation() {
        return location;
    }

    // toString() 메서드 재정의
    @Override
    public String toString() {
        return "강의실 번호: " + roomNumber + ", 층: " + floor + "F , 위치: " + location;
    }
}

