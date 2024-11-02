package com.example.campussfinder_lsg_frame;

import android.os.AsyncTask;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import android.util.Log;

public class DatabaseHelper extends AsyncTask<Void, Void, String> {

    private static final String DB_URL = "jdbc:mysql://yourdb.xxxxxxxxxxxx.region.rds.amazonaws.com:3306/your_database_name";
    private static final String DB_USER = "your_db_username";
    private static final String DB_PASSWORD = "your_db_password";

    @Override
    protected String doInBackground(Void... voids) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM your_table_name");

            // 예시로 결과를 출력하는 방식
            StringBuilder result = new StringBuilder();
            while (resultSet.next()) {
                String roomName = resultSet.getString("room_name");  // 칼럼명에 맞춰서 수정하세요.
                result.append("Room: ").append(roomName).append("\n");
            }
            return result.toString();

        } catch (Exception e) {
            Log.e("DatabaseError", "Error connecting to database", e);
            return "Connection Failed: " + e.getMessage();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                Log.e("DatabaseError", "Error closing connection", e);
            }
        }
    }

    @Override
    protected void onPostExecute(String result) {
        // 결과를 UI에 표시하거나 사용할 수 있습니다.
        Log.d("DB Result", result);
    }
}
