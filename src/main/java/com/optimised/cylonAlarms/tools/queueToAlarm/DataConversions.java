package com.optimised.cylonAlarms.tools.queueToAlarm;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.sql.Timestamp;

public class DataConversions {
    public static Timestamp calcTime(int a, int b, int c, int d) {
        int timeOffset = ((((a * 256) + b) * 256) + c) * 256 + d;
        if (timeOffset == 0) return null;
        Timestamp original = Timestamp.valueOf("1988-1-1 00:00:00");
        return new Timestamp(original.getTime() + (timeOffset * 1000L));
    }

    public static String getText(int[] array, int startIndex, int endIndex) {
        String alarms = "";
        int index = startIndex;
        while (index <= endIndex ) {
            if (array[index] > 31 && array[index] < 126){
                alarms = alarms + (char)array[index];
            } else {
                break;
            }
            index++;
        }
        return alarms;
    }

    public static Float getValue(int a, int b, int c, int d) {
        byte[] array = {(byte) a, (byte) b, (byte) c, (byte) d};
        if ((a & 0xFF) == 255) return 0.0F;
        return ByteBuffer.wrap(array).order(ByteOrder.BIG_ENDIAN).getFloat();
    }

    public static String getAlarmMessage(int[] packet, int start) {
        int end = start;
        while (end < packet.length && packet[end] != 0) {
            end++;
        }
        return getText(packet, start, end);
    }

    public static int get2ByteInt(int a, int b) {
        return (a * 256 + b);
    }

    public static String getOffLineMessage(int[] packet, String prefix) {
        StringBuilder message = new StringBuilder("offline controllers ");
        boolean offline = false;
        int packetIndex = 17 + 1;
        int count = packet[17];
        int ctrlNo = 1;
        int map = 0;
        if (count >= 1) map = 128;
        while (count > 0) {
            if ((map & packet[packetIndex]) < 1) {
                offline = true;
                message.append(prefix).append(ctrlNo);
            }
            map = map / 2;
            ctrlNo++;
            count--;
            if (((ctrlNo - 1) % 8) == 0) {
                packetIndex++;
                map = 128;
            }
        }
        if (!offline) message = new StringBuilder("all controllers online");
        return message.toString();
    }
}
