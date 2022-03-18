package com.optimised.cylonAlarms.tools.queueToAlarm;


import com.optimised.cylonAlarms.model.queueToAlarm.Alarm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class WriteAlarm {
    public static void WriteAlarmQueue(ArrayList<Alarm> alarms) {
        Connection conn = null;
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
            conn = DriverManager.getConnection(dbURL, user, pass);

            assert conn != null;
            Statement statement = conn.createStatement();

            for (Alarm alarm : alarms
                    ) {
                String sqlCount;
                if (alarm.getStartedAt() == null) {
                    sqlCount = "SELECT COUNT(*) FROM [WN3000SL].[dbo].[Alarm] WHERE [StartedAt] IS NULL " +
                            " AND [SiteName]  =  '" + alarm.getSiteName() + " ' ";
                } else {
                    sqlCount = "SELECT COUNT(*) FROM [WN3000SL].[dbo].[Alarm] WHERE [StartedAt] = '" + alarm.getStartedAt() +
                            "' AND [SiteName]  =  '" + alarm.getSiteName() + " ' ";
                }
                ResultSet count = statement.executeQuery(sqlCount);
                boolean result = true;
                if (count.next()) {
                    result = statement.execute(buildValues(alarm));
                }
                if (!result) {
                    String move = "INSERT INTO [AlarmQueueArchive] " +
                            "([SiteName],[SiteNumber],[Acknowledged],[Submitted],[AlarmPacket])" +
                            "SELECT TOP 1 [SiteName],[SiteNumber],[Acknowledged],[Submitted],[AlarmPacket]" +
                            "FROM [AlarmQueue] " + "WHERE SUBMITTED = '" + alarm.getSummited() + "'" +
                            " AND SITENAME = '" + alarm.getSiteName() + "'";
                    statement.execute(move);

                    System.out.println("Alarm for " + alarm.getSiteName() + " moved to Alarm table" + " with start time " + alarm.getStartedAt());

                    String sqlDelete = "DELETE FROM [ALARMQUEUE] WHERE SUBMITTED = '" + alarm.getSummited() + "'" +
                            " AND SITENUMBER = '" + alarm.getSiteNumber() + "'";
                    conn.commit();
                    statement.executeUpdate(sqlDelete);

                }
            }
        } catch (SQLException | IOException e) {
            System.out.println("Exception");
            e.printStackTrace();
        } finally {
            try {
                System.out.println("Closed");
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static String buildValues(Alarm alarm) {
        String insert = "INSERT INTO [WN3000SL].[DBO].[ALARM] (";
        String values = "VALUES (";

        if (!(alarm.getSiteName() == null)) {
            insert = insert + "SiteName,";
            values = values + "'" + alarm.getSiteName() + "','";
        }
        if (!(alarm.getSiteNumber() == 0)) {
            insert = insert + "SiteNumber,";
            values = values + alarm.getSiteNumber() + "','";
        }
        if (!(alarm.getUCC4Name() == null)) {
            insert = insert + "UCC4Name,";
            values = values + alarm.getUCC4Name() + "','";
        }
        if (!(alarm.getUCC4Number() == 0)) {
            insert = insert + "UCC4Number,";
            values = values + alarm.getUCC4Number() + "','";
        }
        if (!(alarm.getUC16Name() == null)) {
            insert = insert + "UC16Name,";
            values = values + alarm.getUC16Name() + "','";
        }
        if (!(alarm.getUC16Number() == 0)) {
            insert = insert + "UC16Number,";
            values = values + alarm.getUC16Number() + "','";
        }
        if (!(alarm.getPriority() == 0)) {
            insert = insert + "Priority,";
            values = values + alarm.getPriority() + "','";
        }
        if (!(alarm.getStartedAt() == null)) {
            insert = insert + "StartedAt,";
            values = values + alarm.getStartedAt() + "','";
        }
        if (!(alarm.getEndedAt() == null)) {
            insert = insert + "EndedAt,";
            values = values + alarm.getEndedAt() + "','";
        }
        if (!(alarm.getAlarmType() == -1)) {
            insert = insert + "AlarmType,";
            values = values + alarm.getAlarmType() + "','";
        }
        if (!(alarm.getTriggerPointName() == null)) {
            insert = insert + "TriggerPointName,";
            values = values + alarm.getTriggerPointName() + "','";
        }
        if (!(alarm.getTriggerPointNumber() == -1)) {
            insert = insert + "TriggerPointNumber,";
            values = values + alarm.getTriggerPointNumber() + "','";
        }

        insert = insert + "TriggerPointType,";
        values = values + alarm.isTriggerPointType() + "','";

        if (!(alarm.getTriggerPointValue() == null)) {
            insert = insert + "TriggerPointValue,";
            values = values + alarm.getTriggerPointValue() + "','";
        }
        if (!(alarm.getTriggerPointUnit() == null)) {
            insert = insert + "TriggerPointUnit,";
            values = values + alarm.getTriggerPointUnit() + "','";
        }
        if (!(alarm.getAlarmNumber() == 0)) {
            insert = insert + "AlarmNumber,";
            values = values + alarm.getAlarmNumber() + "','";
        }
        if (!(alarm.getProgramModuleNumber() == 0)) {
            insert = insert + "ProgramModuleNumber,";
            values = values + alarm.getProgramModuleNumber() + "','";
        }
        if (!(alarm.getAlarmMessage() == null)) {
            insert = insert + "AlarmMessage,";
            values = values + alarm.getAlarmMessage().replace("'","") + "','";
        }
        if (!(alarm.getUCC4SysStatus() == 0)) {
            insert = insert + "UCC4SysStatus,";
            values = values + alarm.getUCC4SysStatus() + "','";
        }
        if (!(alarm.getUC16SysAlarms() == 0)) {
            insert = insert + "UC16SysAlarms,";
            values = values + alarm.getUC16SysAlarms() + "','";
        }
        if (!(alarm.getStringNumber() == 0)) {
            insert = insert + "StringNumber,";
            values = values + alarm.getStringNumber() + "','";
        }
        if (!(alarm.getExtraBits() == 0)) {
            insert = insert + "ExtraBits,";
            values = values + alarm.getExtraBits() + "','";
        }
        if (!(alarm.getExtraInteger() == 0)) {
            insert = insert + "ExtraInteger,";
            values = values + alarm.getExtraInteger() + "','";
        }
        if (!(alarm.getExtraString() == null)) {
            insert = insert + "ExtraString,";
            values = values + alarm.getExtraString() + "','";
        }
        return insert.substring(0, insert.length() - 1) + ") " + values.substring(0, values.length() - 2) + ")";
    }
}
