package com.optimised.cylonAlarms.test.alarmsToIPQueue;


import com.optimised.cylonAlarms.tools.alarmsToIPQueue.GetAlarm;

import static com.optimised.cylonAlarms.tools.iniFilesToDB.HexString.ByteArrayToString;

public class getAlarmTest {
    public static void main(String[] args) {
        char[] time = {1, 0, 0, 0, 0};

        System.out.println(ByteArrayToString(GetAlarm.getRawAlarm("192.168.16.211",time,4950),3));
    }


}
