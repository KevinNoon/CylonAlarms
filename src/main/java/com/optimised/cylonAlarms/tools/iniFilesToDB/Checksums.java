package com.optimised.cylonAlarms.tools.iniFilesToDB;

public class Checksums {
    public static char calCheckSum(char[] cs){
        char sum = 0;
        for (int i = 0; i < cs.length - 1; i++) {
            sum += cs[i];
            //      System.out.println(sum & 0xFF);
        }
        return sum;
    }
    public static int crc16 ( final byte[] bytes){
        int crc = 0x0000;

        for (int j = 0; j < bytes.length; j++) {
            crc = ((crc >>> 8) | (crc << 8)) & 0xffff;
            crc ^= (bytes[j] & 0xff);// byte to int, trunc sign
            crc ^= ((crc & 0xff) >> 4);
            crc ^= (crc << 12) & 0xffff;
            crc ^= ((crc & 0xFF) << 5) & 0xffff;
        }/*w ww  .j av a 2  s  .com*/
        crc &= 0xffff;
        return crc;
    }
}
