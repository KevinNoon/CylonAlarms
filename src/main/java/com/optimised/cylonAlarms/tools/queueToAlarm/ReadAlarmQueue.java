package com.optimised.cylonAlarms.tools.queueToAlarm;

import com.optimised.cylonAlarms.model.alarmsToIPQueue.queue.AlarmQueue;
import com.optimised.cylonAlarms.tools.common.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class ReadAlarmQueue {
     public static ArrayList<AlarmQueue> ReadAlarmQueue(){
         Connection conn = null;
         ArrayList<AlarmQueue> alarmQueues = new ArrayList<>();
        try {
            InputStream inputStream;
            String propFileName = "config.properties";
            File external = new File("./" + propFileName);
            if (external.exists()) {
                inputStream = new FileInputStream("./config.properties");
            } else {
                inputStream = ReadAlarmQueue.class.getClassLoader().getResourceAsStream(propFileName);
            }
            Properties prop = new Properties();
            prop.load(inputStream);
            String dbURL = prop.getProperty("db.urlQueue");
            String user = prop.getProperty("db.user");
            String pass = prop.getProperty("db.password");
            Integer maxAlarms = Integer.valueOf(prop.getProperty("alarms.max"));
            conn = DriverManager.getConnection(dbURL, user, pass);

            assert conn != null;
            Statement statement = conn.createStatement();
            ResultSet result = statement.executeQuery(Constants.GET_ALL_ALARM_QUEUE);

            int Count = 0;
            while (result.next() && Count < maxAlarms) {
                AlarmQueue alarmQueue = new AlarmQueue();
                alarmQueue.setSiteName(result.getString("SiteName"));
                alarmQueue.setSiteNumber(result.getInt("SiteNumber"));
                alarmQueue.setSubmitted(result.getTimestamp("Submitted"));
                alarmQueue.setAlarmPacket(result.getString("AlarmPacket"));
                alarmQueues.add(alarmQueue);
               Count++;
            }
            System.out.println("Number of alarms in queue " + Count);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return alarmQueues;
    }
}
