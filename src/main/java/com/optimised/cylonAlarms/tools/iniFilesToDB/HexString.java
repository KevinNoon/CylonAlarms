package com.optimised.cylonAlarms.tools.iniFilesToDB;

public class HexString {
    public static String ByteArrayToString(byte[] array, int start) {
        if (array.length <= start) return "";
        StringBuilder result = new StringBuilder("0x");
        for (int x = start; x < array.length; x++) {
            result.append(String.format("%02x", array[x]));
        }
        return result.toString();
    }

    public static char[] GetAlarmTime(byte[] alarmChar) {
        char[] timeInfo = new char[5];
        if (alarmChar.length < 14) return timeInfo;

        if ((alarmChar[13] & 0xFF) == 1) {
            if (alarmChar.length < 32) return timeInfo;
            timeInfo[0] = (char) (alarmChar[25] & 0xFF);
            timeInfo[1] = (char) (alarmChar[26] & 0xFF);
            timeInfo[2] = (char) (alarmChar[27] & 0xFF);
            timeInfo[3] = (char) (alarmChar[28] & 0xFF);
        } else if ((alarmChar[13] & 0xFF) == 2) {
            if (alarmChar.length < 27) return timeInfo;
            timeInfo[0] = (char) (alarmChar[29] & 0xFF);
            timeInfo[1] = (char) (alarmChar[30] & 0xFF);
            timeInfo[2] = (char) (alarmChar[31] & 0xFF);
            timeInfo[3] = (char) (alarmChar[32] & 0xFF);
        } else {
            if (alarmChar.length < 18) return timeInfo;
            timeInfo[0] = (char) (alarmChar[15] & 0xFF);
            timeInfo[1] = (char) (alarmChar[16] & 0xFF);
            timeInfo[2] = (char) (alarmChar[17] & 0xFF);
            timeInfo[3] = (char) (alarmChar[18] & 0xFF);
        }

        timeInfo[4] = (char) (alarmChar[alarmChar.length - 2] & 0xFF);
        return timeInfo;
    }
    public static char[] HexToCharArray(String hex, int offset){
        char[] c = new char[hex.length()/2];
        for (int i = offset; i < hex.length(); i+=2) {
            String str = hex.substring(i, i+2);
            c[i/2] = (char)Integer.parseInt(str, 16);
        }
        return c;
    }

    public static int[] HexToIntArray(String hex, int offset){
        int[] c = new int[hex.length()/2];
        for (int i = offset; i < hex.length(); i+=2) {
            String str = hex.substring(i, i+2);
            c[i/2] = Integer.parseInt(str, 16);
        }
        return c;
    }

    public static String CharArrayToHexStr(char[] c){
        String hex = "";
        for (char x:c
        ) {
            hex += String.format("%02X", (int) x);
        }
        return hex;
    }
}
