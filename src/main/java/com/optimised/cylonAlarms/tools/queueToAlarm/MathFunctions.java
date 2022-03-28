package com.optimised.cylonAlarms.tools.queueToAlarm;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.sql.Timestamp;

public class MathFunctions {
    public static Timestamp getTime(int a, int b, int c, int d) {
        a = a & 0xFF;
        b = b & 0xFF;
        c = c & 0xFF;
        d = d & 0xFF;
        int timeOffset = ((((a * 256 ) + b) * 256) + c) * 256 + d;
        if (timeOffset == 0) return null;
        Timestamp original = Timestamp.valueOf("1988-1-1 00:00:00");
        return new Timestamp(original.getTime() + (timeOffset * 1000L));
    }

    public static Float getValue(int a, int b, int c, int d) {
        byte[] array = {(byte) a, (byte) b, (byte) c, (byte) d};
        if ((a & 0xFF) == 255) return 0.0F;
        return ByteBuffer.wrap(array).order(ByteOrder.BIG_ENDIAN).getFloat();
    }

    public static int get2ByteInt(int a, int b) {
        a = a & 0xFF;
        b = b & 0xFF;
        return (a * 256 + b);
    }

    public static  int get4ByteInt(Byte a,Byte b, Byte c, Byte d){
        return ((((a & 0xFF) * 256 + (b & 0xFF)) * 256 + (c & 0xFF)) * 256 + (d & 0xFF));
    }
}
